package com.osh.rvs.service.partial;

import static com.osh.rvs.service.UploadService.getCellStringValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.partial.PartialSupplyEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.mapper.partial.PartialSupplyMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;


/**
 * 零件发放
 * 
 * @author gonglm
 * 
 */
public class PartialSupplyService {

	private static Logger _logger = Logger.getLogger("PartialSupplyService");

	private static final int IDENTIFICATION_UNKNOWN = 0;
	private static final int IDENTIFICATION_OGZ = 2;
	private static final int IDENTIFICATION_WH2P = 4;
	private static final int IDENTIFICATION_SORCWH = 1;

	private static final int COULMN_ORDER_CODE = 0;// 订单编号
	private static final int COULMN_ORDER_DATE = 5;// 订单日期
	private static final int COULMN_PARTIAL_CODE = 8;// 零件编号
	private static final int COULMN_ORDER_QUANTITY = 10;// 订单数量
	@SuppressWarnings("unused")
	private static final int COULMN_SUPPLY_QUANTITY = 12;// 入库数量

	/**
	 * 读取文件，取得文件内容放到session，返回日期列表
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param dateList
	 */
	public void readUploadFile(String fileName, SqlSessionManager conn, List<MsgInfo> dateList) {
		InputStream in = null;

		try {
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet

			Map<String, String> supplyDates = new TreeMap<String, String>();

			String supplyDate = "";
//			String shos = "";
//			String shosn = "";

			PartialSupplyMapper psMapper = conn.getMapper(PartialSupplyMapper.class);

			Map<String, PartialSupplyEntity> entries = new HashMap<String, PartialSupplyEntity>();

			for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {

				try {
					HSSFRow row = sheet.getRow(iRow);
					if (row == null) {
						continue;
					}

					HSSFCell cell = null;
					// 订单编号
					cell = row.getCell(COULMN_ORDER_CODE);
					if (getCellStringValue(cell) == "") {// 单元格为空
						continue;
					}

					String orderCode = getCellStringValue(cell);// 更改基准量设置
					int identification = IDENTIFICATION_UNKNOWN;
					if (orderCode.startsWith("OGZ")) {
						identification = IDENTIFICATION_OGZ;
					} else if (orderCode.startsWith("OSH")) {
						identification = IDENTIFICATION_WH2P;
					} else if (orderCode.startsWith("SORC")) {
						identification = IDENTIFICATION_SORCWH;
					}

					cell = row.getCell(COULMN_ORDER_DATE);
					String partialDate = getCellStringValue(cell);// 订单日期
					Date supply_date = cell.getDateCellValue();
					if (!supplyDate.equals(partialDate)) {
						if (!supplyDates.containsKey(partialDate)) {
							psMapper.deletePartialSupplyOfDate(supply_date);
							// TODO show
						} else {
						}
						// 判断是否导入过
						supplyDate = partialDate;
						supplyDates.put(supplyDate, "1");
					}

					cell = row.getCell(COULMN_PARTIAL_CODE);
					String partialCode = getCellStringValue(cell);// 零件code
//					String partialID = ReverseResolution.getPartialByCode(partialCode, conn);

					PartialEntity partial = ReverseResolution.getPartialEntityByCode(partialCode, conn);
					String partialID = null;
					if (partial != null) {
						partialID = partial.getPartial_id();
						if (partial.getIs_exists() != null && partial.getIs_exists() == 0) {
							identification = 9;
						}
					}

					cell = row.getCell(COULMN_ORDER_QUANTITY);
					Integer quantity = (int) cell.getNumericCellValue();

					PartialSupplyEntity entity = new PartialSupplyEntity();

					entity.setSupply_date(supply_date);
					entity.setCode(partialCode);
					entity.setPartial_id(partialID);
					entity.setIdentification(identification);
					entity.setQuantity(quantity);

					if (partialID == null) {
//						shosn += entity + "\r\n";
					} else {
//						shos += entity + "\r\n";
						String keyString = supplyDate + "-" + partialCode + "-" + identification;
						if (entries.containsKey(keyString)) {
							PartialSupplyEntity fentity = entries.get(keyString);
							fentity.setQuantity(fentity.getQuantity() + entity.getQuantity());
						} else {
							entries.put(keyString, entity);
						}
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
				}
			}

			for (String key : entries.keySet()) {
				PartialSupplyEntity entity = entries.get(key);
				psMapper.insertPartialSupply(entity);
			}

//			System.out.println(shos);
//			System.out.println("=========================");
//			System.out.println(shosn);

			MsgInfo info = new MsgInfo();
			info.setErrmsg("上传完毕");
			dateList.add(info);
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public List<PartialSupplyEntity> searchByDate(PartialSupplyEntity entity, SqlSession conn){
		PartialSupplyMapper psMapper = conn.getMapper(PartialSupplyMapper.class);
		return psMapper.getPartialSupplyOfDate(entity.getSupply_date());
	}

	public void updateQuantity(ActionForm form,SqlSessionManager conn){
		PartialSupplyEntity entity=new PartialSupplyEntity();
		//赋值表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialSupplyMapper dao=conn.getMapper(PartialSupplyMapper.class);
		dao.updatePartialSupplyOfQuantity(entity);
	}

	public void delete(PartialSupplyEntity condition, HttpServletRequest req, SqlSessionManager conn) throws Exception {
		List<PartialSupplyEntity> paritals = new AutofillArrayList<PartialSupplyEntity>(PartialSupplyEntity.class);

		Map<String, String[]> parameterMap = req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("delete".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("partial_id".equals(column)) {
						paritals.get(icounts).setPartial_id(value[0]);
						paritals.get(icounts).setSupply_date(condition.getSupply_date());
					} else if ("identification".equals(column)) {
						paritals.get(icounts).setIdentification(Integer.parseInt(value[0]));
					}
				}
			}
		}

		PartialSupplyMapper psMapper = conn.getMapper(PartialSupplyMapper.class);

		for (PartialSupplyEntity parital : paritals) {
			psMapper.deletePartialSupply(parital);
		}
	}

}

package com.osh.rvs.service.partial;

import static com.osh.rvs.service.UploadService.getCellStringValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.osh.rvs.bean.partial.PartialStorageEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.mapper.partial.PartialStorageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 零件库存
 */
public class PartialStorageService {

	private static Logger _logger = Logger.getLogger("PartialStorageService");

	private static final int COULMN_PARTIAL_CODE = 0;// 零件code
	private static final int COULMN_QUANTITY = 1;// 数量

	/**
	 * 读取文件，取得文件内容放到session，返回错误信息
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 */
	public void readUploadFile(String fileName, PartialStorageEntity condEntity, SqlSessionManager conn,
			List<MsgInfo> errors) {
		InputStream in = null;

		try {
			in = new FileInputStream(fileName);// 读取文件
			HSSFWorkbook work = new HSSFWorkbook(in);// 创建Excel
			HSSFSheet sheet = work.getSheetAt(0);// 获取Sheet

			PartialStorageMapper psMapper = conn.getMapper(PartialStorageMapper.class);

			Map<String, PartialStorageEntity> entries = new HashMap<String, PartialStorageEntity>();

			for (int iRow = 0; iRow <= sheet.getLastRowNum(); iRow++) {
				PartialStorageEntity entity = new PartialStorageEntity();

				try {
					HSSFRow row = sheet.getRow(iRow);
					if (row == null) {
						continue;
					}

					HSSFCell cell = null;
					cell = row.getCell(COULMN_PARTIAL_CODE);
					String partialCode = getCellStringValue(cell);// 零件code

					PartialEntity partial = ReverseResolution.getPartialEntityByCode(partialCode, conn);
					if (partial == null) {
						MsgInfo error = new MsgInfo();
						error.setErrmsg("零件code[" + partialCode + "]不存在");
						errors.add(error);
						continue;
					}

					cell = row.getCell(COULMN_QUANTITY);
					Integer quantity = 0;
					try {
						quantity = (int) cell.getNumericCellValue();// 数量
					} catch (Exception e) {}
					if (quantity > 0) {
						entity.setQuantity(quantity);
					} else {
						MsgInfo error = new MsgInfo();
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(
								"validator.invalidParam.invalidNumberValue", "零件code[" + partialCode + "]的数量"));
						errors.add(error);
						continue;
					}

					entity.setPartial_id(partial.getPartial_id());
					entity.setStorage_date(condEntity.getStorage_date());
					entity.setIdentification(condEntity.getIdentification());
					String keyString = partial.getPartial_id();
					if (entries.containsKey(keyString)) {
						PartialStorageEntity tmpEntity = entries.get(keyString);
						tmpEntity.setQuantity(tmpEntity.getQuantity() + quantity);
					} else {
						entries.put(keyString, entity);
					}

				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
				}
			}

			if (entries.size() > 0) {
				psMapper.deleteByDateAndIdentification(condEntity);
				for (String key : entries.keySet()) {
					PartialStorageEntity entity = entries.get(key);
					psMapper.insert(entity);
				}
			}

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

	public List<PartialStorageEntity> searchByDate(PartialStorageEntity entity, SqlSession conn){
		PartialStorageMapper psMapper = conn.getMapper(PartialStorageMapper.class);
		return psMapper.getPartialStorageByDate(entity.getStorage_date());
	}

	public void updateQuantity(ActionForm form,SqlSessionManager conn){
		PartialStorageEntity entity=new PartialStorageEntity();
		//赋值表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialStorageMapper psMapper = conn.getMapper(PartialStorageMapper.class);
		psMapper.updateQuantity(entity);
	}

	public void delete(HttpServletRequest req, SqlSessionManager conn) throws Exception {
		List<PartialStorageEntity> paritals = new AutofillArrayList<PartialStorageEntity>(PartialStorageEntity.class);

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

					if ("partial_id".equals(column)) {
						paritals.get(icounts).setPartial_id(value[0]);
					} else if ("storage_date".equals(column)) {
						paritals.get(icounts).setStorage_date(DateUtil.toDate(value[0], DateUtil.DATE_PATTERN));
					} else if ("identification".equals(column)) {
						paritals.get(icounts).setIdentification(Integer.parseInt(value[0]));
					}
				}
			}
		}

		PartialStorageMapper psMapper = conn.getMapper(PartialStorageMapper.class);

		for (PartialStorageEntity parital : paritals) {
			psMapper.delete(parital);
		}
	}

}

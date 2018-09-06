package com.osh.rvs.service.partial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.partial.ConsumableOrderEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.partial.ConsumableOrderMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class ConsumableOrderService {

	/**
	 * 订购记录一览
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableManageForm> searchOrderList(ConsumableOrderEntity entity, SqlSession conn) throws Exception {

		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);

		List<ConsumableOrderEntity> orderList = dao.searchOrderList(entity);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(orderList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/**
	 * 订购记录详细
	 * 
	 * @param consumable_order_key
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableManageForm> searchOrderDetail(ConsumableManageForm detailForm, SqlSession conn)
			throws Exception {

		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);

		ConsumableOrderEntity entity = new ConsumableOrderEntity();
		BeanUtil.copyToBean(detailForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<ConsumableOrderEntity> orderList = dao.searchOrderDetail(entity);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(orderList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/** 零件集合 **/
	public List<ConsumableManageForm> getPartial(String code, SqlSession conn) throws Exception {
		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);

		List<ConsumableOrderEntity> resultList = dao.getPartialByCode(code);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(resultList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/**
	 * 修改订购单
	 * 
	 * @param formList
	 * @param conn
	 * @return
	 */
	public void updateOrder(List<ConsumableManageForm> formList, SqlSessionManager conn) throws Exception {
		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);

		String report_flg = "";
		Integer consumable_order_key = null;
		List<ConsumableOrderEntity> entityList = new ArrayList<ConsumableOrderEntity>();
		for (ConsumableManageForm cmform : formList) {
			ConsumableOrderEntity entity = new ConsumableOrderEntity();
			BeanUtil.copyToBean(cmform, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			entityList.add(entity);

			report_flg = entity.getReport_flg();
			consumable_order_key = entity.getConsumable_order_key();
		}

		// 数据库更新
		for (ConsumableOrderEntity entity : entityList) {
			// 更新的场合
			if ("update".equals(entity.getDb_flg())) {
				dao.updateOrderDetail(entity);
			// 删除的场合
			} else if ("delete".equals(entity.getDb_flg())) {
				dao.deleteOrderDetailById(entity);				
			// 新规的场合
			} else {
				dao.insertOrderDetail(entity);
			}
		}

		// 确认并导出的场合
		if ("1".equals(report_flg)) {
			// 将create_time更新成当前时间, sent更新成1
			ConsumableOrderEntity tmp_entity = new ConsumableOrderEntity();
			tmp_entity.setConsumable_order_key(consumable_order_key);
			dao.updateOrder(tmp_entity);

			for (ConsumableOrderEntity entity : entityList) {
				// 每个零件订购的数量, 要加到consumable_manage表中相应的on_passage上
				dao.updateConsumableManage(entity);
			}
		}
	}

	/**
	 * 删除订购单
	 * 
	 * @param consumable_order_key
	 * @param conn
	 * @return
	 */
	public void deleteOrder(String consumable_order_key, SqlSessionManager conn) throws Exception {
		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);
		ConsumableOrderEntity entity = new ConsumableOrderEntity();
		entity.setConsumable_order_key(Integer.parseInt(consumable_order_key));
		dao.deleteOrder(entity);
		dao.deleteOrderDetail(entity);
	}

	/**
	 * 消耗品导出
	 * @param consumableManageForm
	 * @return
	 */
	public String report(ConsumableManageForm consumableManageForm,SqlSession conn){
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "消耗品订购模板.xls";
		String cacheName ="消耗品订购" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName;
		
		ConsumableOrderEntity entity = new ConsumableOrderEntity();
		entity.setConsumable_order_key(Integer.valueOf(consumableManageForm.getConsumable_order_key()));
		
		ConsumableOrderMapper dao = conn.getMapper(ConsumableOrderMapper.class);
		List<ConsumableOrderEntity> list = dao.searchConsumableOrderDetailById(entity);
		
		
		PartialMapper partailDao = conn.getMapper(PartialMapper.class);
		
		try {
			FileUtils.copyFile(new File(path), new File(cachePath));
		} catch (IOException e) {
			return null;
		}
		
		if(list == null) return cacheName;
		
		OutputStream out = null;
		InputStream in = null;
		
		try{
			in = new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建xls文件
			HSSFSheet sheet=work.getSheetAt(0);//取得第一个Sheet
			
			HSSFRow row;
			HSSFCell cell;
			
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)10);
			font.setFontName("微软雅黑");
			
			HSSFCellStyle baseStyle = work.createCellStyle();
			baseStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			baseStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			baseStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			baseStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			baseStyle.setFont(font);
			
			HSSFCellStyle codeStyle = work.createCellStyle();
			codeStyle.cloneStyleFrom(baseStyle);
			codeStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
			codeStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
			
			for(int i=0;i<list.size();i++){
				
				entity = list.get(i);
				
				PartialEntity  partialEntity = partailDao.getPartialByID(String.valueOf(entity.getPartial_id()));
				String code = partialEntity.getCode();
				String name = partialEntity.getName();
				if (name == null) name = ""; 
				
				row = sheet.createRow(i+1);
				
				cell = row.createCell(0);//消耗品代码
				cell.setCellValue(code);
				cell.setCellStyle(codeStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				
				
				cell = row.createCell(1);//消耗品代码
				cell.setCellValue(name);
				cell.setCellStyle(baseStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				

				cell = row.createCell(2);//订购数量
				cell.setCellValue(entity.getOrder_quantity());
				cell.setCellStyle(baseStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				
			}
			
			out= new FileOutputStream(cachePath);
			work.write(out);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return cacheName;
		
	}
}

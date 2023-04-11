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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.PartialBomEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.master.PartialBomForm;
import com.osh.rvs.mapper.master.PartialBomMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.copy.IntegerConverter;

public class PartialBomService {
	/**
	 * 零件BOM信息一览
	 * 
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<PartialBomForm> searchPartialBom(ActionForm form, SqlSession conn) throws Exception {
		PartialBomEntity partialBomEntity = new PartialBomEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, partialBomEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PartialBomForm> returnFormList = new ArrayList<PartialBomForm>();

		PartialBomMapper dao = conn.getMapper(PartialBomMapper.class);
		List<PartialBomEntity> responseList = dao.searchPartialBom(partialBomEntity);
		//复制数据到表单对象
		BeanUtil.copyToFormList(responseList, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialBomForm.class);
		if (returnFormList.size() > 0) {
			return returnFormList;
		} else {
			return null;
		}
	}

	public List<String> searchPartialBomEntity(ActionForm form, SqlSession conn) {
		PartialBomEntity partialBomEntity = new PartialBomEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, partialBomEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		PartialBomMapper mapper = conn.getMapper(PartialBomMapper.class);
		List<String> responseList = new ArrayList<String>();
		List<PartialBomEntity> searchList = mapper.searchPartialBom(partialBomEntity);
		for (PartialBomEntity entity : searchList) {
			responseList.add(entity.getPartial_id());
		}
		return responseList;
	}

	public List<PartialBomForm> searchRankBomForm(ActionForm form, SqlSession conn) {

		List<PartialBomForm> responseList = new ArrayList<PartialBomForm>();

		PartialBomEntity partialBomEntity = new PartialBomEntity();
		// 复制表单到数据对象
		CopyOptions cos = new CopyOptions();
		cos.include("model_id", "level");
		cos.converter(IntegerConverter.getInstance(), "level");
		BeanUtil.copyToBean(form, partialBomEntity, cos);

		PartialBomMapper mapper = conn.getMapper(PartialBomMapper.class);
		List<PartialBomEntity> list = mapper.searchRankBomView(partialBomEntity);

		if (list != null) {
			BeanUtil.copyToFormList(list, responseList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialBomForm.class);
			return responseList;
		}
		return null;
	}

	public List<PartialBomEntity> searchRankBom(ActionForm form, SqlSession conn) {
		PartialBomEntity partialBomEntity = new PartialBomEntity();
		// 复制表单到数据对象
		CopyOptions cos = new CopyOptions();
		cos.include("model_id", "level");
		cos.converter(IntegerConverter.getInstance(), "level");
		BeanUtil.copyToBean(form, partialBomEntity, cos);

		PartialBomMapper mapper = conn.getMapper(PartialBomMapper.class);
		List<PartialBomEntity> responseList = mapper.searchRankBom(partialBomEntity);
		return responseList;
	}

	/**
	 * 导出BOM表
	 * @param form
	 * @param conn
	 * @return
	 */
	public String dowloadPartialBom(ActionForm form,SqlSession conn){
		String path = PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\" + "Line-BOM模板.xls";
		String cacheName ="Line-BOM" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		PartialBomEntity partialBomEntity = new PartialBomEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, partialBomEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		PartialBomMapper dao = conn.getMapper(PartialBomMapper.class);
		List<PartialBomEntity> responseList = dao.searchRankBomView(partialBomEntity);
		
		try{
			FileUtils.copyFile(new File(path), new File(cachePath));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		OutputStream out=null;
		InputStream in=null;
		
		try{
			in=new FileInputStream(cachePath);//读取文件 
			HSSFWorkbook work=new HSSFWorkbook(in);//创建Excel文件
			HSSFSheet sheet=work.getSheet("BOM-Line");
			
			PartialBomEntity entity=null;
			int index=1;//索引
			
			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)10);
			font.setFontName("微软雅黑");
			
			HSSFCellStyle borderStyle = work.createCellStyle();
			borderStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			borderStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			borderStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			borderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			borderStyle.setFont(font);
			
			for(int i=0;i<responseList.size();i++){
				entity=responseList.get(i);
				HSSFRow row=sheet.createRow(index);
				index++;
				
				String model_name=entity.getModel_name();
				String level_name=CodeListUtils.getValue("material_level_inline", entity.getLevel()+"" );
				row.createCell(0).setCellValue(CommonStringUtil.fillChar(level_name, ' ', 2, false)+model_name);
				row.getCell(0).setCellStyle(borderStyle);

				row.createCell(1).setCellValue(entity.getCode());
				row.getCell(1).setCellStyle(borderStyle);
				
				row.createCell(2).setCellStyle(borderStyle);
				
				row.createCell(3).setCellValue(entity.getQuantity());
				row.getCell(3).setCellStyle(borderStyle);
				
				row.createCell(4).setCellStyle(borderStyle);
				row.createCell(5).setCellStyle(borderStyle);
				row.createCell(6).setCellStyle(borderStyle);
				row.createCell(7).setCellStyle(borderStyle);
				row.createCell(8).setCellStyle(borderStyle);
				row.createCell(9).setCellStyle(borderStyle);
				row.createCell(10).setCellStyle(borderStyle);
				row.createCell(11).setCellStyle(borderStyle);
				row.createCell(12).setCellStyle(borderStyle);
			}
			
			out= new FileOutputStream(cachePath);
			work.write(out);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(out!=null){
				try{
					out.close();
				}catch(IOException e){
					e.printStackTrace();	
				}
			}
			if(in!=null){
				try{
					in.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
		return cacheName;
	}
}

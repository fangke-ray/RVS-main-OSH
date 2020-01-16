package com.osh.rvs.service.report;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.report.WaittingTimeReportEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReportUtils;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.report.WaittingTimeReportMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class WaittingTimeReportService {
	private Logger log = Logger.getLogger(getClass());
	
	public void search(ActionForm form,Map<String, Object> listResponse, HttpServletRequest req,SqlSession conn) throws Exception{
		WaittingTimeReportEntity entity = new WaittingTimeReportEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(entity.getOutline_time_start()==null){
			// 2015-01-01
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			entity.setOutline_time_start(cal.getTime());
		}
		
		WaittingTimeReportMapper dao = conn.getMapper(WaittingTimeReportMapper.class);
		List<WaittingTimeReportEntity> list = dao.getMaterailIds(entity);
		
		List<String> material_ids = new ArrayList<String>();
		
		HttpClient httpclient = null;
		HttpGet request = null;
		try {
			for(int i = 0;i<list.size();i++){
				entity = list.get(i);
				Integer isExists = entity.getIsExists();
				String material_id = entity.getMaterial_id();
				material_ids.add(material_id);
				
				if(isExists == 0){
					httpclient = new DefaultHttpClient();
					request = new HttpGet("http://localhost:8080/rvspush/trigger/waitting_time/" + material_id + "/1");
					request.setHeader("Connection", "close");
					log.info("finger:"+request.getURI());
					httpclient.execute(request);
					httpclient.getConnectionManager().shutdown();
				}
			}
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
		} finally {
		}


	
		List<WaittingTimeReportEntity> rList = new ArrayList<WaittingTimeReportEntity>();
		int length = material_ids.size();
		int limit = 10000;
		if(length > 0){
			if(length > limit){
				Map<Integer,WaittingTimeReportEntity> cacheMap = new HashMap<Integer, WaittingTimeReportEntity>();
				
				int times = length % limit == 0 ? length / limit : length / limit + 1;
				for(int index = 1;index <= times;index++){
					int fromIndex = limit * (index - 1);
					int toIndex = 0;
					if(index == times){
						toIndex = length;
					}else{
						toIndex = limit * index;
					}
					
					List<String> subList = material_ids.subList(fromIndex, toIndex);
					List<WaittingTimeReportEntity> cacheList = dao.search(subList);
					for(WaittingTimeReportEntity cacheEntity : cacheList){
						Integer level = cacheEntity.getLevel();
						
						if(cacheMap.containsKey(level)){
							WaittingTimeReportEntity mergeEntity = merge(cacheMap.get(level), cacheEntity);
							cacheMap.put(level, mergeEntity);
						}else{
							cacheMap.put(level, cacheEntity);
						}
					}
				}
				
				Iterator<Integer> iterator =  cacheMap.keySet().iterator();
				while (iterator.hasNext()) {
					rList.add(cacheMap.get(iterator.next()));
				}
			}else{
				rList = dao.search(material_ids);
			}
			req.getSession().setAttribute("material_ids", material_ids);
		}
		
		//S1 等待零件发放时间
		String series1_1 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S1 分解作业时间
		String series1_2 = "[{x:0,y:#value#},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}}]";
		//S1 分解等待时间
		String series1_3 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S1 总组作业时间
		String series1_4 = "[{x:0,y:#value#},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}}]";
		//S1 总组等待时间
		String series1_5 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S1 分解等烘干时间
		String series1_6 = "[{x:0,y:#value#,borderColor:'#0B6E48',dataLabels:{y:150,x:10}},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false},borderColor:'rgba(255,255,255,0)'}]";
		//S1 总组等烘干时间
		String series1_7 = "[{x:0,y:#value#,borderColor:'#F2753F',dataLabels:{y:150,x:10}},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false},borderColor:'rgba(255,255,255,0)'}]";
				
		//S1 等待BO零件时间
		String series2_1 = "[{y:#value#}]";
		//S1 异常中断时间
		String series2_2 = "[{y:#value#}]";
		//S1 总维修周期
		String series2_3 = "#value#";
		
		//S2+S3 等待零件发放时间
		String series3_1 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S2+S3 分解作业时间
		String series3_2 = "[{x:0,y:#value#},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}}]";
		//S2+S3 分解等待时间
		String series3_3 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		///S2+S3 NS作业时间
		String series3_4 = "[{x:0,y:#value#},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}}]";
		//S2+S3 NS等待时间
		String series3_5 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S2+S3 总组作业时间
		String series3_6 = "[{x:0,y:#value#},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}}]";
		//S2+S3 总组等待时间
		String series3_7 = "[{x:0,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false}},{x:1,y:#value#}]";
		//S2+S 分解等烘干时间
		String series3_8 = "[{x:0,y:#value#,borderColor:'#0B6E48',dataLabels:{y:130,x:10}},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false},borderColor:'rgba(255,255,255,0)'}]";
		//S2+S NS等烘干时间
		String series3_9 = "[{x:0,y:#value#,borderColor:'#500073',dataLabels:{y:130,x:10}},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false},borderColor:'rgba(255,255,255,0)'}]";
		//S2+S 总组等烘干时间
		String series3_10 = "[{x:0,y:#value#,borderColor:'#F2753F',dataLabels:{y:130,x:10}},{x:1,y:#value#,color:'rgba(255,255,255,0)',dataLabels:{enabled:false},borderColor:'rgba(255,255,255,0)'}]";						
		
		//S2+S3 等待BO零件时间
		String series4_1 = "[{y:#value#}]";
		//S2+S3 异常中断时间
		String series4_2 = "[{y:#value#}]";
		//S2+S3 总维修周期
		String series4_3 = "#value#";
		
		//S1数量
		int S1num = 0;
		//S2+S3数量
		int S2S3num = 0;
		int s2s3_wait_partial_distrubute_time = 0;
		int s2s3_desc_work_time = 0;
		int s2s3_desc_wait_time = 0;
		int s2s3_ns_work_time = 0;
		int s2s3_ns_wait_time = 0;
		int s2s3_com_work_time = 0;
		int s2s3_com_wait_time = 0;
		int s2s3_wait_bo_partial_time = 0;
		int s2s3_exception_break_time = 0;
		int s2s3_total_work_time = 0;
		int s2s3_desc_drying_time = 0;
		int s2s3_ns_drying_time = 0;
		int s2s3_com_drying_time = 0;
		
		for(int i = 0;i<rList.size();i++){
			entity = rList.get(i);
			Integer level = entity.getLevel();//等级
			Integer number = entity.getNumber();//数量
			
			Integer wait_partial_distrubute_time = entity.getWait_partial_distrubute_time();//等待零件发放时间
			if(wait_partial_distrubute_time == null) wait_partial_distrubute_time = 0;
			
			Integer desc_work_time = entity.getDesc_work_time();//分解作业时间
			if(desc_work_time == null) desc_work_time = 0;
			
			Integer desc_wait_time = entity.getDesc_wait_time();//分解等待时间
			if(desc_wait_time == null) desc_wait_time = 0;
			
			Integer desc_drying_time = entity.getDesc_drying_time();//分解烘干时间
			if(desc_drying_time == null) desc_drying_time = 0;
			
			Integer ns_work_time = entity.getNs_work_time();//NS作业时间
			if(ns_work_time == null) ns_work_time = 0;
			
			Integer ns_wait_time = entity.getNs_wait_time();//NS等待时间
			if(ns_wait_time == null) ns_wait_time = 0;
			
			Integer ns_drying_time = entity.getNs_drying_time();//NS烘干时间
			if(ns_drying_time == null) ns_drying_time = 0;
			
			Integer com_work_time = entity.getCom_work_time();//总组作业时间
			if(com_work_time == null) com_work_time = 0;
			
			Integer com_wait_time = entity.getCom_wait_time();//总组等待时间
			if(com_wait_time == null) com_wait_time = 0;
			
			Integer com_drying_time = entity.getCom_drying_time();//总组干时间
			if(com_drying_time == null) com_drying_time = 0;
			
			Integer wait_bo_partial_time = entity.getWait_bo_partial_time();//等待BO零件时间
			if(wait_bo_partial_time == null) wait_bo_partial_time = 0;
			
			Integer exception_break_time = entity.getException_break_time();//异常中断时间
			if(exception_break_time == null) exception_break_time = 0;
			
			Integer total_work_time = entity.getTotal_work_time();//总维修周期
			if(total_work_time == null) total_work_time = 0;
				
			if(level == 1){ //S1
				S1num = number;
				if(S1num!=0){
					series1_1 = series1_1.replaceAll("#value#", new BigDecimal(wait_partial_distrubute_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series1_2 = series1_2.replaceAll("#value#", new BigDecimal(desc_work_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series1_3 = series1_3.replaceAll("#value#", new BigDecimal(desc_wait_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series1_4 = series1_4.replaceAll("#value#", new BigDecimal(com_work_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series1_5 = series1_5.replaceAll("#value#", new BigDecimal(com_wait_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					if(desc_drying_time == 0){
						series1_6 = series1_6.replaceAll("#value#","null");
					}else{
						series1_6 = series1_6.replaceAll("#value#", new BigDecimal(desc_drying_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					}
					if(com_drying_time == 0){
						series1_7 = series1_7.replaceAll("#value#","null");
					}else{
						series1_7 = series1_7.replaceAll("#value#", new BigDecimal(com_drying_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					}
					
					series2_1 = series2_1.replaceAll("#value#", new BigDecimal(wait_bo_partial_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series2_2 = series2_2.replaceAll("#value#", new BigDecimal(exception_break_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
					series2_3 = series2_3.replaceAll("#value#", new BigDecimal(total_work_time).divide(new BigDecimal(number),0,BigDecimal.ROUND_HALF_UP).toString());
				}
			}else{
				S2S3num = S2S3num + number;
				s2s3_wait_partial_distrubute_time = s2s3_wait_partial_distrubute_time + wait_partial_distrubute_time;
				s2s3_desc_work_time = s2s3_desc_work_time + desc_work_time;
				s2s3_desc_wait_time = s2s3_desc_wait_time + desc_wait_time;
				s2s3_ns_work_time = s2s3_ns_work_time + ns_work_time;
				s2s3_ns_wait_time = s2s3_ns_wait_time + ns_wait_time;
				s2s3_com_work_time = s2s3_com_work_time + com_work_time;
				s2s3_com_wait_time = s2s3_com_wait_time + com_wait_time;
				s2s3_wait_bo_partial_time = s2s3_wait_bo_partial_time + wait_bo_partial_time;
				s2s3_exception_break_time = s2s3_exception_break_time + exception_break_time;
				s2s3_total_work_time = s2s3_total_work_time + total_work_time;
				s2s3_desc_drying_time = s2s3_desc_drying_time + desc_drying_time;
				s2s3_ns_drying_time = s2s3_ns_drying_time + ns_drying_time;
				s2s3_com_drying_time = s2s3_com_drying_time + com_drying_time;
			}
		}
		
		if(S2S3num!=0){
			series3_1 = series3_1.replaceAll("#value#", new BigDecimal(s2s3_wait_partial_distrubute_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_2 = series3_2.replaceAll("#value#", new BigDecimal(s2s3_desc_work_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_3 = series3_3.replaceAll("#value#", new BigDecimal(s2s3_desc_wait_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_4 = series3_4.replaceAll("#value#", new BigDecimal(s2s3_ns_work_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_5 = series3_5.replaceAll("#value#", new BigDecimal(s2s3_ns_wait_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_6 = series3_6.replaceAll("#value#", new BigDecimal(s2s3_com_work_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series3_7 = series3_7.replaceAll("#value#", new BigDecimal(s2s3_com_wait_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			if(s2s3_desc_drying_time == 0){
				series3_8 = series3_8.replaceAll("#value#", "null");
			}else{
				series3_8 = series3_8.replaceAll("#value#", new BigDecimal(s2s3_desc_drying_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			}
			if(s2s3_ns_drying_time == 0){
				series3_9 = series3_9.replaceAll("#value#", "null");
			}else{
				series3_9 = series3_9.replaceAll("#value#", new BigDecimal(s2s3_ns_drying_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			}
			if(s2s3_com_drying_time == 0){
				series3_10 = series3_10.replaceAll("#value#", "null");
			}else{
				series3_10 = series3_10.replaceAll("#value#", new BigDecimal(s2s3_com_drying_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			}
			
			series4_1 = series4_1.replaceAll("#value#", new BigDecimal(s2s3_wait_bo_partial_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series4_2 = series4_2.replaceAll("#value#", new BigDecimal(s2s3_exception_break_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
			series4_3 = series4_3.replaceAll("#value#", new BigDecimal(s2s3_total_work_time).divide(new BigDecimal(S2S3num),0,BigDecimal.ROUND_HALF_UP).toString());
		}
		
		
		// 清0
		series1_1 = series1_1.replaceAll("#\\w*#","0");
		series1_2 = series1_2.replaceAll("#\\w*#","0");
		series1_3 = series1_3.replaceAll("#\\w*#","0");
		series1_4 = series1_4.replaceAll("#\\w*#","0");
		series1_5 = series1_5.replaceAll("#\\w*#","0");
		series1_6 = series1_6.replaceAll("#\\w*#","0");
		series1_7 = series1_7.replaceAll("#\\w*#","0");
		series2_1 = series2_1.replaceAll("#\\w*#","0");
		series2_2 = series2_2.replaceAll("#\\w*#","0");
		series2_3 = series2_3.replaceAll("#\\w*#","0");
		
		series3_1 = series3_1.replaceAll("#\\w*#","0");
		series3_2 = series3_2.replaceAll("#\\w*#","0");
		series3_3 = series3_3.replaceAll("#\\w*#","0");
		series3_4 = series3_4.replaceAll("#\\w*#","0");
		series3_5 = series3_5.replaceAll("#\\w*#","0");
		series3_6 = series3_6.replaceAll("#\\w*#","0");
		series3_7 = series3_7.replaceAll("#\\w*#","0");
		series3_8 = series3_8.replaceAll("#\\w*#","0");
		series3_9 = series3_9.replaceAll("#\\w*#","0");
		series3_10 = series3_10.replaceAll("#\\w*#","0");
		series4_1 = series4_1.replaceAll("#\\w*#","0");
		series4_2 = series4_2.replaceAll("#\\w*#","0");
		series4_3 = series4_3.replaceAll("#\\w*#","0");
		
		listResponse.put("S1num", S1num);
		listResponse.put("S2S3num", S2S3num);
		listResponse.put("series1_1", series1_1);
		listResponse.put("series1_2", series1_2);
		listResponse.put("series1_3", series1_3);
		listResponse.put("series1_4", series1_4);
		listResponse.put("series1_5", series1_5);
		listResponse.put("series1_6", series1_6);
		listResponse.put("series1_7", series1_7);
		listResponse.put("series2_1", series2_1);
		listResponse.put("series2_2", series2_2);
		listResponse.put("series2_3", series2_3);
		listResponse.put("series3_1", series3_1);
		listResponse.put("series3_2", series3_2);
		listResponse.put("series3_3", series3_3);
		listResponse.put("series3_4", series3_4);
		listResponse.put("series3_5", series3_5);
		listResponse.put("series3_6", series3_6);
		listResponse.put("series3_7", series3_7);
		listResponse.put("series3_8", series3_8);
		listResponse.put("series3_9", series3_9);
		listResponse.put("series3_10", series3_10);
		listResponse.put("series4_1", series4_1);
		listResponse.put("series4_2", series4_2);
		listResponse.put("series4_3", series4_3);
	
	}

	private WaittingTimeReportEntity merge(WaittingTimeReportEntity prev,WaittingTimeReportEntity next){
		WaittingTimeReportEntity entity = new WaittingTimeReportEntity();
		
		Integer prev_number = prev.getNumber();//数量
		
		Integer prev_wait_partial_distrubute_time = prev.getWait_partial_distrubute_time();//等待零件发放时间
		if(prev_wait_partial_distrubute_time == null) prev_wait_partial_distrubute_time = 0;
		
		Integer prev_desc_work_time = prev.getDesc_work_time();//分解作业时间
		if(prev_desc_work_time == null) prev_desc_work_time = 0;
		
		Integer prev_desc_wait_time = prev.getDesc_wait_time();//分解等待时间
		if(prev_desc_wait_time == null) prev_desc_wait_time = 0;
		
		Integer prev_desc_drying_time = prev.getDesc_drying_time();//分解烘干时间
		if(prev_desc_drying_time == null) prev_desc_drying_time = 0;
		
		Integer prev_ns_work_time = prev.getNs_work_time();//NS作业时间
		if(prev_ns_work_time == null) prev_ns_work_time = 0;
		
		Integer prev_ns_wait_time = prev.getNs_wait_time();//NS等待时间
		if(prev_ns_wait_time == null) prev_ns_wait_time = 0;
		
		Integer prev_ns_drying_time = prev.getNs_drying_time();//NS烘干时间
		if(prev_ns_drying_time == null) prev_ns_drying_time = 0;
		
		Integer prev_com_work_time = prev.getCom_work_time();//总组作业时间
		if(prev_com_work_time == null) prev_com_work_time = 0;
		
		Integer prev_com_wait_time = prev.getCom_wait_time();//总组等待时间
		if(prev_com_wait_time == null) prev_com_wait_time = 0;
		
		Integer prev_com_drying_time = prev.getCom_drying_time();//总组干时间
		if(prev_com_drying_time == null) prev_com_drying_time = 0;
		
		Integer prev_wait_bo_partial_time = prev.getWait_bo_partial_time();//等待BO零件时间
		if(prev_wait_bo_partial_time == null) prev_wait_bo_partial_time = 0;
		
		Integer prev_exception_break_time = prev.getException_break_time();//异常中断时间
		if(prev_exception_break_time == null) prev_exception_break_time = 0;
		
		Integer prev_total_work_time = prev.getTotal_work_time();//总维修周期
		if(prev_total_work_time == null) prev_total_work_time = 0;
		
		Integer next_number = next.getNumber();//数量
		
		Integer next_wait_partial_distrubute_time = next.getWait_partial_distrubute_time();//等待零件发放时间
		if(next_wait_partial_distrubute_time == null) next_wait_partial_distrubute_time = 0;
		
		Integer next_desc_work_time = next.getDesc_work_time();//分解作业时间
		if(next_desc_work_time == null) next_desc_work_time = 0;
		
		Integer next_desc_wait_time = next.getDesc_wait_time();//分解等待时间
		if(next_desc_wait_time == null) next_desc_wait_time = 0;
		
		Integer next_desc_drying_time = next.getDesc_drying_time();//分解烘干时间
		if(next_desc_drying_time == null) next_desc_drying_time = 0;
		
		Integer next_ns_work_time = next.getNs_work_time();//NS作业时间
		if(next_ns_work_time == null) next_ns_work_time = 0;
		
		Integer next_ns_wait_time = next.getNs_wait_time();//NS等待时间
		if(next_ns_wait_time == null) next_ns_wait_time = 0;
		
		Integer next_ns_drying_time = next.getNs_drying_time();//NS烘干时间
		if(next_ns_drying_time == null) next_ns_drying_time = 0;
		
		Integer next_com_work_time = next.getCom_work_time();//总组作业时间
		if(next_com_work_time == null) next_com_work_time = 0;
		
		Integer next_com_wait_time = next.getCom_wait_time();//总组等待时间
		if(next_com_wait_time == null) next_com_wait_time = 0;
		
		Integer next_com_drying_time = next.getCom_drying_time();//总组干时间
		if(next_com_drying_time == null) next_com_drying_time = 0;
		
		Integer next_wait_bo_partial_time = next.getWait_bo_partial_time();//等待BO零件时间
		if(next_wait_bo_partial_time == null) next_wait_bo_partial_time = 0;
		
		Integer next_exception_break_time = next.getException_break_time();//异常中断时间
		if(next_exception_break_time == null) next_exception_break_time = 0;
		
		Integer next_total_work_time = next.getTotal_work_time();//总维修周期
		if(next_total_work_time == null) next_total_work_time = 0;
		
		entity.setLevel(prev.getLevel());
		entity.setNumber(prev_number + next_number);
		entity.setWait_partial_distrubute_time(prev_wait_partial_distrubute_time + next_wait_partial_distrubute_time);
		entity.setDesc_work_time(prev_desc_work_time + next_desc_work_time);
		entity.setDesc_wait_time(prev_desc_wait_time + next_desc_wait_time);
		entity.setDesc_drying_time(prev_desc_drying_time + next_desc_drying_time);
		entity.setNs_work_time(prev_ns_work_time + next_ns_work_time);
		entity.setNs_wait_time(prev_ns_wait_time + next_ns_wait_time);
		entity.setNs_drying_time(prev_ns_drying_time + next_ns_drying_time);
		entity.setCom_work_time(prev_com_work_time + next_com_work_time);
		entity.setCom_wait_time(prev_com_wait_time + next_com_wait_time);
		entity.setCom_drying_time(prev_com_drying_time + next_com_drying_time);
		entity.setWait_bo_partial_time(prev_wait_bo_partial_time + next_wait_bo_partial_time);
		entity.setException_break_time(prev_exception_break_time + next_exception_break_time);
		entity.setTotal_work_time(prev_total_work_time + next_total_work_time);
		
		return entity;
	}
	
	public String createExcel(ActionForm form,HttpServletRequest req,SqlSession conn) throws Exception{
		WaittingTimeReportEntity entity = new WaittingTimeReportEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//Excel临时文件
		String cacheName ="等待时间、中断时间统计" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName;
		
		@SuppressWarnings("unchecked")
		List<String> material_ids = (List<String>)req.getSession().getAttribute("material_ids");
		
		WaittingTimeReportMapper dao = conn.getMapper(WaittingTimeReportMapper.class);
		List<WaittingTimeReportEntity> rList  = dao.searchDetails(material_ids);
		
		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			HSSFWorkbook work=new HSSFWorkbook();
			HSSFSheet sheet = work.createSheet("等待时间、中断时间统计");
			HSSFSheet sheet2 = work.createSheet("等待时间、中断时间统计明细");
			
			HSSFRow row = null;
			HSSFCell cell = null;

			sheet.setColumnWidth(0, 12*256);
			sheet.setColumnWidth(1, 22*256);
			
			sheet2.setColumnWidth(0, 6*256);	//序号
			sheet2.setColumnWidth(1, 11*256);	//日期
			sheet2.setColumnWidth(2, 18*256);	//修理单号
			sheet2.setColumnWidth(3, 24*256);	//内镜型号
			sheet2.setColumnWidth(4, 10*256);	//等级
			sheet2.setColumnWidth(5, 17*256);	//投线时间
			sheet2.setColumnWidth(6, 17*256);	//产出时间
			sheet2.setColumnWidth(7, 16*256);	//等待零件发放时间
			sheet2.setColumnWidth(8, 14*256);	//分解作业时间
			sheet2.setColumnWidth(9, 14*256);	//分解作业时间
			sheet2.setColumnWidth(10, 14*256);	//NS作业时间
			sheet2.setColumnWidth(11, 14*256);	//NS等待时间
			sheet2.setColumnWidth(12, 14*256);	//总组作业时间
			sheet2.setColumnWidth(13, 14*256);	//总组等待时间
			sheet2.setColumnWidth(14, 8*256);	//是否BO
			sheet2.setColumnWidth(15, 9*256);	//是否返工
			sheet2.setColumnWidth(16, 17*256);	//等待BO零件时间
			sheet2.setColumnWidth(17, 15*256);	//异常中断时间
			sheet2.setColumnWidth(18, 16*256);	//PA时间合计
			sheet2.setColumnWidth(19, 14*256);	//等待时间合计
			sheet2.setColumnWidth(20, 14*256);	//总维修周期
			sheet2.setColumnWidth(21, 13*256);	//中断时间占比
			sheet2.setColumnWidth(22, 14*256);	//非作业时间占比
			
			sheet2.createFreezePane(0, 1);

			HSSFFont font=work.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName("微软雅黑");
			
			HSSFFont whiteFont=work.createFont();
			whiteFont.setFontHeightInPoints((short)9);
			whiteFont.setColor(HSSFColor.WHITE.index);
			whiteFont.setFontName("微软雅黑");
			
			HSSFFont titlefont=work.createFont();
			titlefont.setFontHeightInPoints((short)10);
			titlefont.setFontName("微软雅黑");
			titlefont.setColor(HSSFColor.WHITE.index);
			
			HSSFFont boldTitlefont=work.createFont();
			boldTitlefont.setFontHeightInPoints((short)11);
			boldTitlefont.setFontName("微软雅黑");
			boldTitlefont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			boldTitlefont.setColor(HSSFColor.WHITE.index);
			
			HSSFCellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); 
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(true);
			cellStyle.setFont(font);
			
			HSSFCellStyle titleStyle = work.createCellStyle();
			titleStyle.cloneStyleFrom(cellStyle);
			titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			titleStyle.setFont(titlefont);
			
			HSSFCellStyle detailTitleStyle = work.createCellStyle();
			detailTitleStyle.cloneStyleFrom(cellStyle);
			detailTitleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			detailTitleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			detailTitleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			detailTitleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			detailTitleStyle.setWrapText(true);
			detailTitleStyle.setFont(titlefont);
			
			HSSFCellStyle detailBoldTitleStyle = work.createCellStyle();
			detailBoldTitleStyle.cloneStyleFrom(cellStyle);
			detailBoldTitleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			detailBoldTitleStyle.setFillForegroundColor(HSSFColor.GREEN.index);
			detailBoldTitleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			detailBoldTitleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			detailBoldTitleStyle.setWrapText(true);
			detailBoldTitleStyle.setFont(boldTitlefont);
			
			HSSFCellStyle percentageStyle = work.createCellStyle();
			percentageStyle.cloneStyleFrom(cellStyle);
			percentageStyle.setDataFormat(work.createDataFormat().getFormat("0.0%")); 
			
			HSSFCellStyle timeStyle = work.createCellStyle();
			timeStyle.cloneStyleFrom(cellStyle);
			timeStyle.setDataFormat(work.createDataFormat().getFormat("yyyy/mm/dd hh:mm")); 
			
			HSSFCellStyle averageStyle = work.createCellStyle();
			averageStyle.cloneStyleFrom(cellStyle);
			averageStyle.setDataFormat(work.createDataFormat().getFormat("0"));
			
			HSSFCellStyle redBackgroundStyle = work.createCellStyle();
			redBackgroundStyle.cloneStyleFrom(cellStyle);
			redBackgroundStyle.setFont(whiteFont);
			redBackgroundStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			redBackgroundStyle.setFillForegroundColor(HSSFColor.RED.index);
			
			HSSFCellStyle timeRedBackgroundStyle = work.createCellStyle();
			timeRedBackgroundStyle.cloneStyleFrom(cellStyle);
			timeRedBackgroundStyle.setFont(whiteFont);
			timeRedBackgroundStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			timeRedBackgroundStyle.setFillForegroundColor(HSSFColor.RED.index);
			timeRedBackgroundStyle.setDataFormat(work.createDataFormat().getFormat("yyyy/mm/dd hh:mm"));
			
			HSSFCellStyle percentageRedBackgroundStyle = work.createCellStyle();
			percentageRedBackgroundStyle.cloneStyleFrom(cellStyle);
			percentageRedBackgroundStyle.setFont(whiteFont);
			percentageRedBackgroundStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			percentageRedBackgroundStyle.setFillForegroundColor(HSSFColor.RED.index);
			percentageRedBackgroundStyle.setDataFormat(work.createDataFormat().getFormat("0.0%")); 
			
			setConditionContent(entity, sheet, titleStyle, cellStyle);
		
			insertImage(entity, sheet, work);
			
			setDetailTitle(sheet2, detailTitleStyle,detailBoldTitleStyle);
			
			int startIndex = 1;
			int endIndex = 1;
			for(int i = 0;i<rList.size();i++){
				entity = rList.get(i);
				row = sheet2.createRow(endIndex);
				
				cell = row.createCell(0);//序号
				cell.setCellValue(i+1);
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(1);//日期
				cell.setCellValue(DateUtil.toString(entity.getOutline_date(),DateUtil.DATE_PATTERN));
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(2);//修理单号
				cell.setCellValue(entity.getOmr_notifi_no());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(3);//内镜型号
				cell.setCellValue(entity.getModel_name());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(4);//等级
				cell.setCellValue(CodeListUtils.getValue("material_level_heavy",String.valueOf(entity.getLevel())));
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(5);//投线时间
				cell.setCellValue(entity.getInline_time());
				cell.setCellStyle(timeStyle);
				
				cell = row.createCell(6);//产出时间
				cell.setCellValue(entity.getOutline_time());
				cell.setCellStyle(timeStyle);
				
				cell = row.createCell(7);//等待零件发放时间
				cell.setCellValue(entity.getWait_partial_distrubute_time());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(8);//分解作业时间
				if(entity.getDesc_work_time() != null){
					cell.setCellValue(entity.getDesc_work_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(9);//分解等待时间
				if(entity.getDesc_wait_time() != null){
					cell.setCellValue(entity.getDesc_wait_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(10);//NS作业时间
				if(entity.getNs_work_time() != null){
					cell.setCellValue(entity.getNs_work_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(11);//NS等待时间
				if(entity.getNs_wait_time() != null){
					cell.setCellValue(entity.getNs_wait_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(12);//总组作业时间
				if(entity.getCom_work_time()!= null){
					cell.setCellValue(entity.getCom_work_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(13);//总组等待时间
				if(entity.getCom_wait_time()!= null){
					cell.setCellValue(entity.getCom_wait_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(14);//是否BO
				if(entity.getBo_flg() == 1){
					cell.setCellValue("是");
				}else{
					cell.setCellValue("否");
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(15);//是否返工
				if(entity.getRework() == 1){
					cell.setCellValue("是");
				}else{
					cell.setCellValue("否");
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(16);//等待BO零件时间
				cell.setCellValue(entity.getWait_bo_partial_time());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(17);//异常中断时间
				cell.setCellValue(entity.getException_break_time());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(18);//PA时间合计
				cell.setCellFormula("SUM(Q" + (endIndex+1) + ",R" + (endIndex+1) + ")");
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(19);//等待时间合计
				if(entity.getTotal_wait_time() !=null){
					cell.setCellValue(entity.getTotal_wait_time());
				}
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(20);//总维修周期
				cell.setCellValue(entity.getTotal_work_time());
				cell.setCellStyle(cellStyle);
				
				cell = row.createCell(21);//中断时间占比
				cell.setCellFormula("S" + (endIndex+1) + "/U" + (endIndex+1));
				cell.setCellStyle(percentageStyle);
				
				cell = row.createCell(22);//非作业时间占比
				cell.setCellFormula("IF(ISBLANK(T" +  (endIndex+1)+ "),\"\",IF(OR(T" + (endIndex+1) + "/U" + (endIndex+1)+ "<0,T" + (endIndex+1) + "/U" + (endIndex+1) + ">1),\"\","+ "T" + (endIndex+1) + "/U" + (endIndex+1)+"))");
				cell.setCellStyle(percentageStyle);
				
				if(entity.getRework() == 1){//返工
					for(int j = 0;j <= 22;j++){
						if(j == 5 || j == 6){
							 row.getCell(j).setCellStyle(timeRedBackgroundStyle);
						}else if(j == 21 || j == 22){
							 row.getCell(j).setCellStyle(percentageRedBackgroundStyle);
						}else{
							 row.getCell(j).setCellStyle(redBackgroundStyle);
						}
					}
				}
				
				endIndex ++;
			}
			
			//设置平均数
			setAverage(sheet2, startIndex ,endIndex, cellStyle,averageStyle,percentageStyle);
			
			out= new FileOutputStream(file);
			work.write(out);
		}catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}finally{
			if(out!=null){
				out.close();
			}
		}
		
		return cacheName;
	}
	
	// 设置检索条件内容
	private void setConditionContent(WaittingTimeReportEntity entity,HSSFSheet sheet,HSSFCellStyle titleStyle,HSSFCellStyle cellStyle){
		int index = 0;
		
		if(!CommonStringUtil.isEmpty(entity.getSection_name())){//课室
			setCellValue(index, "课室", entity.getSection_name(), sheet, titleStyle,cellStyle);
			index++;
		}
		if(!CommonStringUtil.isEmpty(entity.getOmr_notifi_no())){//修理单号
			setCellValue(index, "修理单号", entity.getOmr_notifi_no(), sheet, titleStyle,cellStyle);
			index++;
		}
		if(!CommonStringUtil.isEmpty(entity.getSerial_no())){//机身号
			setCellValue(index, "机身号", entity.getSerial_no(), sheet, titleStyle,cellStyle);
			index++;
		}
		if(!CommonStringUtil.isEmpty(entity.getModel_name())){//型号
			setCellValue(index, "型号", entity.getModel_name(), sheet, titleStyle,cellStyle);
			index++;
		}
		if(!CommonStringUtil.isEmpty(entity.getCategory_name())){//机种
			setCellValue(index, "机种", entity.getCategory_name(), sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getLevel()!=null){//等级
			setCellValue(index, "等级", CodeListUtils.getValue("material_level_heavy",String.valueOf(entity.getLevel())), sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getBo_flg()!=null){//零件BO
			setCellValue(index, "零件BO", CodeListUtils.getValue("bo_flg",String.valueOf(entity.getBo_flg())), sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getScheduled_expedited() == 1){//加急
			setCellValue(index, "加急", "是", sheet, titleStyle,cellStyle);
			index++;
		}else if(entity.getScheduled_expedited() == 2){
			setCellValue(index, "加急", "否", sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getRework() == 1){//是否包含返工
			setCellValue(index, "是否包含返工", "是", sheet,titleStyle, cellStyle);
			index++;
		}else if(entity.getRework() == 2){
			setCellValue(index, "是否包含返工", "否", sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getDirect_flg()!=null){//直送
			setCellValue(index, "直送", CodeListUtils.getValue("material_direct",String.valueOf(entity.getDirect_flg())), sheet, titleStyle,cellStyle);
			index++;
		}
		
		if(entity.getOutline_time_start() == null){//完成时间
			// 2015-01-01
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			entity.setOutline_time_start(cal.getTime());
		}
		
		String itemValue = DateUtil.toString(entity.getOutline_time_start(),DateUtil.DATE_PATTERN);
		itemValue = itemValue + "~";
		if(entity.getOutline_time_end() != null){
			itemValue = itemValue + DateUtil.toString(entity.getOutline_time_end(),DateUtil.DATE_PATTERN);
		}
		setCellValue(index, "完成时间", itemValue, sheet, titleStyle,cellStyle);
		index++;
		
		if(entity.getOrder_date_start() != null){//零件订购日
			itemValue = DateUtil.toString(entity.getOrder_date_start(),DateUtil.DATE_PATTERN);
			itemValue = itemValue + "~";
			if(entity.getOrder_date_end() != null){
				itemValue = itemValue + DateUtil.toString(entity.getOrder_date_end(),DateUtil.DATE_PATTERN);
			}
			setCellValue(index, "零件订购日", itemValue, sheet, titleStyle,cellStyle);
			index++;
		}else{
			if(entity.getOrder_date_end() != null){
				itemValue = "~" + DateUtil.toString(entity.getOrder_date_end(),DateUtil.DATE_PATTERN);
				setCellValue(index, "零件订购日", itemValue, sheet, titleStyle,cellStyle);
				index++;
			}
		}
		
		if(entity.getArrival_date_start() != null){//零件发放日
			itemValue = DateUtil.toString(entity.getArrival_date_start(),DateUtil.DATE_PATTERN);
			itemValue = itemValue + "~";
			if(entity.getArrival_date_end() != null){
				itemValue = itemValue + DateUtil.toString(entity.getArrival_date_end(),DateUtil.DATE_PATTERN);
			}
			setCellValue(index, "零件发放日", itemValue, sheet, titleStyle,cellStyle);
			index++;
		}else{
			if(entity.getArrival_date_end() != null){
				itemValue = "~" + DateUtil.toString(entity.getArrival_date_end(),DateUtil.DATE_PATTERN);
				setCellValue(index, "零件发放日", itemValue, sheet, titleStyle,cellStyle);
				index++;
			}
		}
		
		if(entity.getDec_px()!=null){//分解工程分线
			setCellValue(index, "分解工程分线", CodeListUtils.getValue("operator_px",String.valueOf(entity.getDec_px())), sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getNs_px()!=null){//NS工程分线
			setCellValue(index, "NS工程分线", CodeListUtils.getValue("operator_px",String.valueOf(entity.getNs_px())), sheet, titleStyle,cellStyle);
			index++;
		}
		if(entity.getCom_px()!=null){//总组工程分线
			setCellValue(index, "总组工程分线", CodeListUtils.getValue("operator_px",String.valueOf(entity.getCom_px())), sheet, titleStyle,cellStyle);
			index++;
		}
	}
	
	private void insertImage(WaittingTimeReportEntity entity,HSSFSheet sheet,HSSFWorkbook work) throws Exception{
		String chartCacheName1 ="作业时间 等待时间" + new Date().getTime() + ".png";
		String chartCacheName2 ="中断时间" + new Date().getTime() + ".png";
		String chartPic = "";
		
		HSSFClientAnchor anchor = null;
		BufferedImage bufferImg = null;
		ByteArrayOutputStream byteArrayOut = null;
		
		HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
		if (patriarch == null) {
			patriarch = sheet.createDrawingPatriarch();
		}
		
		int lastRowNum = sheet.getLastRowNum();
		
		if(!CommonStringUtil.isEmpty(entity.getSvg1())){
			chartPic = RvsUtils.convertSVGToPng(entity.getSvg1(),"S1" + chartCacheName1);
			bufferImg=ImageIO.read(new File(chartPic));
			
			byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 0,lastRowNum + 2, (short) 8, lastRowNum + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
		}
		
		if(!CommonStringUtil.isEmpty(entity.getSvg2())){
			chartPic = RvsUtils.convertSVGToPng(entity.getSvg2(),"S1" + chartCacheName2);
			bufferImg=ImageIO.read(new File(chartPic));
			
			byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 12,lastRowNum + 2, (short) 16, lastRowNum + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
		}
		
		if(anchor!=null) lastRowNum = anchor.getRow2();
		
		if(!CommonStringUtil.isEmpty(entity.getSvg3())){
			chartPic = RvsUtils.convertSVGToPng(entity.getSvg3(),"S2" + chartCacheName1);
			bufferImg=ImageIO.read(new File(chartPic));
			
			byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 0,lastRowNum + 2, (short) 8, lastRowNum + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
		}
		
		if(!CommonStringUtil.isEmpty(entity.getSvg4())){
			chartPic = RvsUtils.convertSVGToPng(entity.getSvg4(),"S2" + chartCacheName2);
			bufferImg=ImageIO.read(new File(chartPic));
			
			byteArrayOut=new ByteArrayOutputStream();
			ImageIO.write(bufferImg, "png",byteArrayOut);
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 12,lastRowNum + 2, (short) 16, lastRowNum + 10);
			patriarch.createPicture(anchor,work.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).resize(1);
		}
	}
	
	private void setDetailTitle(HSSFSheet sheet2,HSSFCellStyle detailTitleStyle,HSSFCellStyle detailBoldTitleStyle){
		HSSFRow row = sheet2.createRow(0);
		
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("序号");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(1);
		cell.setCellValue("日期");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("修理单号");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("内镜型号");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(4);
		cell.setCellValue("等级");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(5);
		cell.setCellValue("投线时间");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(6);
		cell.setCellValue("产出时间");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(7);
		cell.setCellValue("等待零件发放时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(8);
		cell.setCellValue("分解作业时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(9);
		cell.setCellValue("分解等待时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(10);
		cell.setCellValue("NS作业时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(11);
		cell.setCellValue("NS等待时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(12);
		cell.setCellValue("总组作业时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(13);
		cell.setCellValue("总组等待时间（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(14);
		cell.setCellValue("是否BO");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(15);
		cell.setCellValue("是否返工");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(16);
		cell.setCellValue("等待BO零件时间");
		cell.setCellStyle(detailBoldTitleStyle);
		
		cell = row.createCell(17);
		cell.setCellValue("异常中断时间");
		cell.setCellStyle(detailBoldTitleStyle);
		
		cell = row.createCell(18);
		cell.setCellValue("PA时间合计（单位：分钟）");
		cell.setCellStyle(detailBoldTitleStyle);
		
		cell = row.createCell(19);
		cell.setCellValue("等待时间合计（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(20);
		cell.setCellValue("总维修周期\n（单位：分钟）");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(21);
		cell.setCellValue("中断时间占比");
		cell.setCellStyle(detailTitleStyle);
		
		cell = row.createCell(22);
		cell.setCellValue("非作业时间占比");
		cell.setCellStyle(detailTitleStyle);
	}
	
	private void setAverage(HSSFSheet sheet2,int startIndex ,int endIndex,HSSFCellStyle cellStyle,HSSFCellStyle averageStyle,HSSFCellStyle percentageStyle){
		HSSFRow row = sheet2.createRow(endIndex);
		HSSFCell cell = null;
		for(int i = 0;i <= 22;i++){
			cell = row.createCell(i);
			
			String startPoint =  ReportUtils.getPosition(i, (startIndex + 1));
			String endPoint = ReportUtils.getPosition(i, endIndex);
			
			if((i >= 7 && i <= 13) || (i >= 16 && i <= 20)){
				cell.setCellFormula("IF(ISERROR(AVERAGE(" + startPoint + ":" + endPoint +  ")),\"-\",AVERAGE(" + startPoint + ":" + endPoint +  "))");
				cell.setCellStyle(averageStyle);
			}else if(i >= 21){
				cell.setCellFormula("IF(ISERROR(AVERAGE(" + startPoint + ":" + endPoint +  ")),\"-\",AVERAGE(" + startPoint + ":" + endPoint +  "))");
				cell.setCellStyle(percentageStyle);
			}else{
				cell.setCellStyle(cellStyle);
			}
		}
		row.getCell(0).setCellValue("平均");
		
		HSSFPatriarch patriarch = sheet2.getDrawingPatriarch();
		if (patriarch == null) {
			patriarch = sheet2.createDrawingPatriarch();
		}
		
		//斜线
		HSSFClientAnchor anchor = null;
		HSSFSimpleShape shape = null;
		for(int i = 1;i <= 6;i++){
			anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short)i, endIndex, (short)i, endIndex);
			shape = patriarch.createSimpleShape(anchor);
			shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);   
			shape.setLineStyle(HSSFSimpleShape.LINESTYLE_SOLID) ;  
		}
		
		anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short)14, endIndex, (short)14, endIndex);
		shape = patriarch.createSimpleShape(anchor);
		shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);   
		shape.setLineStyle(HSSFSimpleShape.LINESTYLE_SOLID) ; 
		
		anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short)15, endIndex, (short)15, endIndex);
		shape = patriarch.createSimpleShape(anchor);
		shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);   
		shape.setLineStyle(HSSFSimpleShape.LINESTYLE_SOLID) ; 
	}
	
	private void setCellValue(int rowIndex,String itemName,String itemValue,HSSFSheet sheet,HSSFCellStyle titleStyle,HSSFCellStyle cellStyle){
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(itemName);
		cell.setCellStyle(titleStyle);
		
		cell = row.createCell(1);
		cell.setCellValue(itemValue);
		cell.setCellStyle(cellStyle);
	}
}

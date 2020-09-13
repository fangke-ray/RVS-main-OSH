package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactReceptMaterialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialTagForm;
import com.osh.rvs.form.qf.FactMaterialForm;
import com.osh.rvs.form.qf.FactReceptMaterialForm;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.qf.AcceptanceMapper;
import com.osh.rvs.mapper.qf.FactReceptMaterialMapper;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.ProductionFeatureService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class FactReceptMaterialService {
	private FactMaterialService factMaterialService = new FactMaterialService();
	private MaterialTagService materialTagService = new MaterialTagService();
	private ProductionFeatureService featureService = new ProductionFeatureService();
	private AcceptanceService acceptanceService = new AcceptanceService();
	
	public List<FactReceptMaterialForm> searchReceptMaterial(SqlSession conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);

		List<FactReceptMaterialEntity> list = dao.searchReceptMaterial();

		List<FactReceptMaterialForm> respList = new ArrayList<FactReceptMaterialForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, FactReceptMaterialForm.class);

		return respList;
	}

	/**
	 * 新建临时维修品实物受理/测漏
	 * 
	 * @param form
	 * @param conn
	 */
	public void insertFactReceptMaterialTemp(ActionForm form, SqlSessionManager conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);

		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insertFactReceptMaterialTemp(entity);
	}

	public List<FactReceptMaterialForm> searchFactReceptMaterialTemp(ActionForm form, SqlSession conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);
		
		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactReceptMaterialEntity> list = dao.searchTemp(entity);
		
		List<FactReceptMaterialForm> respList = new ArrayList<FactReceptMaterialForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, FactReceptMaterialForm.class);
		
		return respList;
	}

	
	/**
	 * 更新维修品实物受理/测漏
	 * @param form
	 * @param req
	 * @param errors
	 * @param conn
	 * @throws Exception
	 */
	public void updateMaterial(FactReceptMaterialForm factReceptMaterialForm,HttpServletRequest req,List<MsgInfo> errors,SqlSessionManager conn) throws Exception{
		ProductionFeatureMapper productionFeatureMapper = conn.getMapper(ProductionFeatureMapper.class);
		
		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		//维修对象ID
		String materialID = factReceptMaterialForm.getMaterial_id();
		//Tab区分标记
		String flag = factReceptMaterialForm.getFlag();
		
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		List<MaterialTagForm> pageList = new AutofillArrayList<MaterialTagForm>(MaterialTagForm.class);
		Map<String, String[]> parameters = req.getParameterMap();
		String tagTypes = "";
		
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("material_tag".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);
					if ("tag_type".equals(column)) {
						pageList.get(icounts).setTag_type(value[0]);
						tagTypes += value[0] + ",";
					}
					pageList.get(icounts).setMaterial_id(materialID);
				}
			}
		}
		
		//非直送/其他(周边)
		if("0".equals(flag) || "3".equals(flag)){
			//判断受理工位（111）有没有做完
			boolean isDid = featureService.checkPositionDid(materialID, "00000000009", "2", null, conn);
			if(isDid){
				//维修对象属性标签
				int len = 0;
				//操作结果为‘0’记录数量
				int zeroNum = 0;
				//操作结果不为‘0’记录数量
				int notZeroNum = 0;
				
				if(tagTypes.contains("4")){//消毒
					ProductionFeatureEntity pf = new ProductionFeatureEntity();
					pf.setMaterial_id(materialID);
					pf.setPosition_id("00000000011");
					
					//查询灭菌记录
					List<ProductionFeatureEntity> pfList = productionFeatureMapper.searchProductionFeature(pf);
					len = pfList.size();
					
					//有记录
					if(len > 0){
						for(ProductionFeatureEntity pfEntity : pfList){
							Integer operateResult = pfEntity.getOperate_result();
							if(operateResult == 0){
								zeroNum ++;
							} else {
								notZeroNum ++;
							}
						}
					}
				} else if(tagTypes.contains("5")){//灭菌
					ProductionFeatureEntity pf = new ProductionFeatureEntity();
					pf.setMaterial_id(materialID);
					pf.setPosition_id("00000000010");
					
					//查询消毒记录
					List<ProductionFeatureEntity> pfList = productionFeatureMapper.searchProductionFeature(pf);
					len = pfList.size();
					//有记录
					if(len > 0){
						for(ProductionFeatureEntity pfEntity : pfList){
							Integer operateResult = pfEntity.getOperate_result();
							if(operateResult == 0){
								zeroNum ++;
							} else {
								notZeroNum ++;
							}
						}
					}
				}
				
				if(errors.size() == 0){
					if(tagTypes.contains("4")){//消毒
						//没有记录
						if(len == 0){
							insertWaittingDisinfect(materialID, user, conn);
						}else{
							if(notZeroNum > 0){
								MsgInfo info = new MsgInfo();
								info.setErrcode("info.linework.alreadyWorked");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.alreadyWorked","灭菌"));
								errors.add(info);
							} else if(zeroNum == 1){
								//删除等待灭菌
								productionFeatureMapper.removeWaiting(materialID, "00000000011");
								
								insertWaittingDisinfect(materialID, user, conn);
							}
						}
						
					} else if(tagTypes.contains("5")){//灭菌
						//没有记录
						if(len == 0){
							insertWaittingSterilize(materialID, user, conn);
						} else {
							if(notZeroNum > 0){
								MsgInfo info = new MsgInfo();
								info.setErrcode("info.linework.alreadyWorked");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.alreadyWorked","消毒"));
								errors.add(info);
							} else if(zeroNum == 1){
								//删除等待消毒
								productionFeatureMapper.removeWaiting(materialID, "00000000010");
								
								insertWaittingSterilize(materialID, user, conn);
							}
						}
					}
				}
			}
		}
		
		if(errors.size() == 0){
			//现品维修对象作业
			FactMaterialForm factMaterialForm = new FactMaterialForm();
			factMaterialForm.setMaterial_id(materialID);
			factMaterialForm.setProduction_type("102");
			//查询当前维修对象有没有做过（维修品实物受理/测漏）
			List<FactMaterialForm> fMaterialList =  factMaterialService.search(factMaterialForm, conn);
			if(fMaterialList.size() == 0){
				//新建现品维修对象作业
				factMaterialForm.setAf_pf_key(factReceptMaterialForm.getAf_pf_key());
				factMaterialService.insert(factMaterialForm, conn);
			}
			
			//维修对象属性标签
			materialTagService.deleteByMaterialId(materialID, conn);
			for(MaterialTagForm materialTagForm : pageList){
				materialTagService.insert(materialTagForm, conn);
			}
		}
	}
	
	/**
	 * 维修品实物受理/测漏 临时表更新
	 * @param form
	 * @param errors
	 * @param conn
	 */
	public void updateFactReceptTemp(ActionForm form,List<MsgInfo> errors,SqlSessionManager conn){
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);
		AcceptanceMapper acceptanceMapper = conn.getMapper(AcceptanceMapper.class);
		//拷贝
		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String factReceptId = entity.getFact_recept_id();
		
		FactReceptMaterialEntity dbEntity = dao.getFactReceptTemp(factReceptId);
		
		//临时记录不存在
		if(dbEntity == null){
			MsgInfo info = new MsgInfo();
			info.setErrcode("dbaccess.recordNotExist");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist","") + "请刷新页面确认。");
			errors.add(info);
		} else {
			if(CommonStringUtil.isEmpty(entity.getModel_id())){
				//找不到型号，型号ID设为00000000000
				entity.setModel_id("00000000000");
			}
			
			//material重复check
			MaterialEntity insertBean = new MaterialEntity();
			insertBean.setModel_id(entity.getModel_id());
			insertBean.setSerial_no(entity.getSerial_no());
			String existId1 = acceptanceMapper.checkModelSerialNo(insertBean);
			if (existId1 != null) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("dbaccess.columnNotUnique");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", insertBean.getModel_id() +","+ insertBean.getSerial_no(), "维修对象"));
				errors.add(info);
			}
			
			if(errors.size() == 0){
				if(!"00000000000".equals(entity.getModel_id()) && !entity.getSerial_no().startsWith("临")){
					//临时表重复check
					List<FactReceptMaterialEntity> list = dao.searchTemp(entity);
					
					//型号机身号不能重复
					if(list.size() == 1 && !factReceptId.equals(list.get(0).getFact_recept_id())){
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", entity.getModel_name() +","+ entity.getSerial_no(), "维修对象"));
						errors.add(info);
					}
				}
			}
		}
		
		if(errors.size() == 0){
			//更新临时表fact_recept_material_temp
			dao.updateFactReceptMaterialTemp(entity);
		}
	}
	
	/**
	 * 新建等待消毒
	 * @param materialID
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	private void insertWaittingDisinfect(String materialID,LoginData user,SqlSessionManager conn) throws Exception{
		// 发送至消毒
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(materialID);
		entity.setPosition_id("10");
		entity.setSection_id(user.getSection_id());
		entity.setPace(0);
		entity.setOperate_result(0);
		entity.setRework(0);
		entity.setOperator_id(user.getOperator_id());
		featureService.insert(entity, conn);
		// 发送完毕后，受理时间覆盖导入时间
		acceptanceService.updateFormalReception(new String[]{materialID}, conn);
	}
	
	/**
	 * 新建等待灭菌
	 * @param materialID
	 * @param user
	 * @param conn
	 * @throws Exception
	 */
	private void insertWaittingSterilize(String materialID,LoginData user,SqlSessionManager conn) throws Exception{
		ProductionFeatureEntity entity = new ProductionFeatureEntity();
		entity.setMaterial_id(materialID);
		entity.setPosition_id("11");
		entity.setPace(0);
		entity.setSection_id(user.getSection_id());
		entity.setOperate_result(0);
		entity.setRework(0);
		entity.setOperator_id(user.getOperator_id());
		featureService.insert(entity, conn);
		// 发送完毕后，受理时间覆盖导入时间
		acceptanceService.updateFormalReception(new String[]{materialID}, conn);
	}
}

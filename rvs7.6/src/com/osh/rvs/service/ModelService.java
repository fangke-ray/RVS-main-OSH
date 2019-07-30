package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.CategoryEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.CategoryMapper;
import com.osh.rvs.mapper.master.ModelMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ModelService {

	private static final Integer SERACH_AS_CELL = -1;
	private static final Integer SERACH_AS_ENDOSCOPE = -2;
	private static final Integer SERACH_AS_PERIPHERAL = -3;

	/**
	 * 检索记录列表
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<ModelForm> 查询结果表单
	 */
	public List<ModelForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		ModelEntity coditionBean = new ModelEntity();
		BeanUtil.copyToBean(form, coditionBean, null);

		// 从数据库中查询记录
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		List<ModelEntity> lResultBean = dao.searchModel(coditionBean);

		// 建立页面返回表单
		List<ModelForm> lResultForm = new ArrayList<ModelForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ModelForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return ModelForm 查询结果表单
	 */
	public ModelForm getDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		// 表单复制到数据对象
		ModelEntity coditionBean = new ModelEntity();
		BeanUtil.copyToBean(form, coditionBean, null);
		String model_id = coditionBean.getModel_id();

		// 从数据库中查询记录
		ModelMapper dao = conn.getMapper(ModelMapper.class);
		ModelEntity mb = dao.getModelByID(model_id);

		if (mb == null) {
			// 检索不到的情况下
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "维修对象型号"));
			errors.add(error);
			return null;
		} else {
			// 建立页面返回表单
			ModelForm mf = new ModelForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(mb, mf, null);

			List<String> imbalance_line_ids = dao.getModelImbalanceLine(model_id);
			mf.setImbalance_line_ids(imbalance_line_ids);

			return mf;
		}
	}

	/**
	 * 按照主键检索单条记录
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @return ModelForm 查询结果表单
	 */
	public ModelForm getDetail(String model_id, SqlSession conn) {
		// 从数据库中查询记录
		ModelEntity mb = getDetailEntity(model_id, conn);

		if (mb == null) {
			return null;
		} else {
			// 建立页面返回表单
			ModelForm mf = new ModelForm();

			// 数据对象复制到表单
			BeanUtil.copyToForm(mb, mf, null);
			return mf;
		}
	}
	public ModelEntity getDetailEntity(String model_id, SqlSession conn) {
		// 从数据库中查询记录
		ModelMapper dao = conn.getMapper(ModelMapper.class);
		ModelEntity mb = dao.getModelByID(model_id);
		return mb;
	}

	/**
	 * 标准检查以外的合法性检查
	 * @param modelForm 表单
	 * @param errors 错误内容列表
	 */
	public void customValidate(ActionForm modelForm, SqlSession conn, List<MsgInfo> errors) {
		// 工位ID不重复
		ModelMapper dao = conn.getMapper(ModelMapper.class);
		// 表单复制到数据对象
		ModelEntity conditionBean = new ModelEntity();
		BeanUtil.copyToBean(modelForm, conditionBean, (new CopyOptions()).include("id", "name"));
		List<String> resultBean = dao.checkModelByName(conditionBean);
		if (resultBean != null && resultBean.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("process_code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号名",
					conditionBean.getName(), "型号"));
			errors.add(error);
		}
	}

	/**
	 * 执行插入
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		ModelEntity insertBean = new ModelEntity();
		BeanUtil.copyToBean(form, insertBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		dao.insertModel(insertBean);

		// 取得刚才插入的主键
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		insertBean.setModel_id(commonMapper.getLastInsertID());

		String imbalance_line_id = insertBean.getImbalance_line_id();
		if (!CommonStringUtil.isEmpty(imbalance_line_id)) {
			String[] split = imbalance_line_id.split(",");
			for (int i = 0; i < split.length; i++) {
				insertBean.setImbalance_line_id(split[i]);
				dao.insertModelImbalanceLine(insertBean);
			}
		}

		imbalanceModelOfLine = null;
		kindOfModel = null;
	}

	/**
	 * 执行更新
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void update(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		ModelEntity updateBean = new ModelEntity();
		BeanUtil.copyToBean(form, updateBean, null);

		// 脏数据检查

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		dao.updateModel(updateBean);

		dao.deleteModelImbalanceLine(updateBean);

		String imbalance_line_id = updateBean.getImbalance_line_id();
		if (!CommonStringUtil.isEmpty(imbalance_line_id)) {
			String[] split = imbalance_line_id.split(",");
			for (int i = 0; i < split.length; i++) {
				updateBean.setImbalance_line_id(split[i]);
				dao.insertModelImbalanceLine(updateBean);
			}
		}

		// 清空反查缓存
		ReverseResolution.modelRever.clear();
		imbalanceModelOfLine = null;
		kindOfModel = null;

	}

	/**
	 * 执行逻辑删除
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @throws Exception
	 */
	public void delete(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		ModelEntity updateBean = new ModelEntity();
		BeanUtil.copyToBean(form, updateBean, null);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 在数据库中逻辑删除记录
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		dao.deleteModel(updateBean);

		dao.deleteModelImbalanceLine(updateBean);

		imbalanceModelOfLine = null;
		kindOfModel = null;
	}

	/***
	 * 取得自动补词数据
	 * @param conn
	 * @param errors
	 * @return
	 */
	public Map<String, String[]> getAutocomp(SqlSession conn, ActionErrors errors) {
		Map<String, String[]> mRet = new HashMap<String, String[]>();
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		// 取得自动补词记录
		mRet.put("feature1", dao.getFeature1AutoCompletes());
		mRet.put("feature2", dao.getFeature2AutoCompletes());
		mRet.put("series", dao.getFeature3AutoCompletes());
		mRet.put("el_base_type", dao.getElBaseTypeAutoCompletes());
		mRet.put("s_connector_base_type", dao.getSConnectorBaseTypeAutoCompletes());
		mRet.put("operate_part_type", dao.getOperatePartTypeAutoCompletes());
		mRet.put("ocular_type", dao.getOcularTypeAutoCompletes());
		
		return mRet;
	}
	/**
	 * 取得全部型号
	 * @param conn
	 * @return
	 */
	public List<ModelForm> getAllModel(SqlSession conn) {
		
		ModelMapper dao = conn.getMapper(ModelMapper.class);

		List<ModelEntity> lResultBean = dao.getAllModel();
		
		List<ModelForm> lResultForm = new ArrayList<ModelForm>();
		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, ModelForm.class);

		return lResultForm;
	}
	
	/**
	 * 取得全部型号(参照列表)
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {
		return getOptions(conn, true);
	}
	public String getOptions(SqlSession conn, boolean onlyForRepair) {
		List<String[]> mList = new ArrayList<String[]>();
		List<ModelForm> allModel = this.getAllModel(conn);
		
		for (ModelForm model: allModel) {
			if (onlyForRepair && 
					("8".equals(model.getKind()) || "9".equals(model.getKind()))) continue;
			String[] mline = new String[3];
			mline[0] = model.getId();
			mline[1] = model.getName();
			mline[2] = model.getCategory_name();
			mList.add(mline);
		}

		String mReferChooser = CodeListUtils.getReferChooser(mList);
		
		return mReferChooser;
	}
	
	
	/**
	 * 维修对象型号受理维修等级设定/废止一览
	 * @param form 表单
	 * @param conn 数据库会话
	 * @return 
	 */
	public List<ModelForm> searchAbolishOfModelLevel(ActionForm form,SqlSession conn){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ModelMapper dao=conn.getMapper(ModelMapper.class);

		String category_id = entity.getCategory_id();
		CategoryMapper cMapper = conn.getMapper(CategoryMapper.class);
		CategoryEntity cb = cMapper.getCategoryByID(category_id);

		List<ModelEntity> beanList = null;

		if(cb.getKind() == 0){//单元机种
			entity.setLevel(SERACH_AS_CELL);
			beanList=dao.searchAbolishOfModelLevel(entity);
			ModelEntity tempEntity=null;
			
			if(beanList.size()==1){
			}else{
				tempEntity=new ModelEntity();
				tempEntity.setLevel(0);
				tempEntity.setEchelon(0);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			}
		} else if(cb.getKind() == 7){ //周边
			entity.setLevel(SERACH_AS_PERIPHERAL);
			
			beanList=dao.searchAbolishOfModelLevel(entity);
			ModelEntity tempEntity=null;

			if(beanList.size()>0){
				Set<Integer> map=new HashSet<Integer>();
				for(int i=0;i<beanList.size();i++){
					tempEntity=beanList.get(i);
					Integer level=tempEntity.getLevel();
					map.add(level);
				}
				if(!map.contains(56)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(56);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				} 
				if(!map.contains(57)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(57);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				}
				if(!map.contains(58)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(58);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				}
				if(!map.contains(59)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(59);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				}
			}else{
				tempEntity=new ModelEntity();
				tempEntity.setLevel(56);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
				
				tempEntity=new ModelEntity();
				tempEntity.setLevel(57);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
				
				tempEntity=new ModelEntity();
				tempEntity.setLevel(58);
				tempEntity.setStatus(1);
				beanList.add(tempEntity);

				tempEntity=new ModelEntity();
				tempEntity.setLevel(59);
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			}
		}else{//其他机种
			entity.setLevel(SERACH_AS_ENDOSCOPE);
			beanList=dao.searchAbolishOfModelLevel(entity);
			ModelEntity tempEntity=null;
			if(beanList.size()>0){
				Set<Integer> map=new HashSet<Integer>();
				for(int i=0;i<beanList.size();i++){
					tempEntity=beanList.get(i);
					Integer level=tempEntity.getLevel();
					map.add(level);
				}
				if(!map.contains(1)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(1);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				} 
				if(!map.contains(2)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(2);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				}
				if(!map.contains(3)){
					tempEntity=new ModelEntity();
					tempEntity.setLevel(3);
					tempEntity.setModel_id(entity.getModel_id());
					tempEntity.setStatus(1);
					beanList.add(tempEntity);
				}
			}else{
				tempEntity=new ModelEntity();
				tempEntity.setLevel(1);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
				
				tempEntity=new ModelEntity();
				tempEntity.setLevel(2);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
				
				tempEntity=new ModelEntity();
				tempEntity.setLevel(3);
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			}
		}

		List<ModelForm> resList = new ArrayList<ModelForm>();

		// 复制数据到表单
		BeanUtil.copyToFormList(beanList, resList, CopyOptions.COPYOPTIONS_NOEMPTY, ModelForm.class);

		return resList;

		/*List<ModelEntity> beanList=dao.searchAbolishOfModelLevel(entity);
		ModelEntity tempEntity=null;
		
		if(beanList.size()>0){
			Map<Integer,Integer> map=new HashMap<Integer,Integer>();
			for(int i=0;i<beanList.size();i++){
				tempEntity=beanList.get(i);
				Integer level=tempEntity.getLevel();
				map.put(level, level);
			}
			if(!map.containsKey(1)){
				tempEntity=new ModelEntity();
				tempEntity.setLevel(1);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			} 
			if(!map.containsKey(2)){
				tempEntity=new ModelEntity();
				tempEntity.setLevel(2);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			}
			if(!map.containsKey(3)){
				tempEntity=new ModelEntity();
				tempEntity.setLevel(3);
				tempEntity.setModel_id(entity.getModel_id());
				tempEntity.setStatus(1);
				beanList.add(tempEntity);
			}
		}else{
			tempEntity=new ModelEntity();
			tempEntity.setLevel(1);
			tempEntity.setModel_id(entity.getModel_id());
			tempEntity.setStatus(1);
			beanList.add(tempEntity);
			
			tempEntity=new ModelEntity();
			tempEntity.setLevel(2);
			tempEntity.setModel_id(entity.getModel_id());
			tempEntity.setStatus(1);
			beanList.add(tempEntity);
			
			tempEntity=new ModelEntity();
			tempEntity.setLevel(3);
			tempEntity.setStatus(1);
			beanList.add(tempEntity);
		}
		*/
		/*List<ModelForm> resList=new ArrayList<ModelForm>();
		
		//复制数据到表单
		BeanUtil.copyToFormList(beanList, resList, CopyOptions.COPYOPTIONS_NOEMPTY, ModelForm.class);
		
		return resList;*/
	}
	
	
	
	public void updateAvaliableEndDate(ActionForm form,HttpServletRequest req,SqlSessionManager conn){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LoginData loginData=(LoginData)req.getSession().getAttribute(RvsConsts.SESSION_USER);
		
		String operator_id=loginData.getOperator_id();
		entity.setUpdated_by(operator_id);
		
		ModelMapper dao=conn.getMapper(ModelMapper.class);
		dao.updateAvaliablEndDate(entity);
		dao.updateOperator(entity);
	}
	
	
	public void insertMoldeLevel(ActionForm form,HttpServletRequest request,SqlSessionManager conn){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		
		String operator_id=loginData.getOperator_id();
		entity.setUpdated_by(operator_id);
		ModelMapper dao=conn.getMapper(ModelMapper.class);
		if("00000000013".equals(entity.getCategory_id())){//单元机种
			entity.setEchelon(-1);
		}
		dao.insertModelLevel(entity);
		dao.updateOperator(entity);
	}
	
	/**
	 * 插入型号等级历史
	 * @param form
	 * @param request
	 * @param conn
	 */
	public void insertModelLevelSetHistory(ActionForm form,HttpServletRequest request,SqlSessionManager conn){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		
		String operator_id=loginData.getOperator_id();
		entity.setUpdated_by(operator_id);
		
		ModelMapper dao=conn.getMapper(ModelMapper.class);
		dao.insertModelLevelSetHistory(entity);
		
	}
	
	/**
	 * 维修对象型号等级 终止check
	 * @param form 表单
	 * @param conn 数据库会话
	 * @param errors 保存错误信息
	 * @return
	 */
	public int searchExitsModelLevelSetHistory(ActionForm form,SqlSession conn,List<MsgInfo> errors){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(entity.getAvaliable_end_date()==null){
			MsgInfo error = new MsgInfo();
			error.setComponentid("edit_date");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","终止维修日"));
			errors.add(error);
			return 0;
		}else{
			ModelMapper dao=conn.getMapper(ModelMapper.class);
			ModelEntity tempEntity=dao.searchExitsModelLevelSetHistory(entity);
			if(tempEntity!=null){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	/**
	 * 改变梯队
	 * @param form
	 * @param conn
	 */
	public void changeEchelon(ActionForm form,SqlSessionManager conn){
		ModelEntity entity=new ModelEntity();
		//复制表单到数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ModelMapper dao=conn.getMapper(ModelMapper.class);
		
		ModelEntity connEntity = dao.getModeLevelSet(entity);
		if(connEntity==null){
			dao.insertModeLevelSet(entity);
		}else{
			dao.updateModeLevelSet(entity);
		}
	}


	/**
	 * 取得周边设备的型号
	 * 
	 * @param conn
	 * @return
	 */
	public String getModelReferChooser(SqlSession conn) {
		List<String[]> mList = new ArrayList<String[]>();

		ModelMapper dao = conn.getMapper(ModelMapper.class);
		List<ModelEntity> list = dao.getModelByKind7();

		// 建立页面返回表单
		List<ModelForm> lmf = new ArrayList<ModelForm>();
		if (list != null && list.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(list, lmf, null, ModelForm.class);
			for (ModelForm form : lmf) {
				String[] mline = new String[3];
				mline[0] = form.getId();
				mline[1] = form.getName();
				mline[2] = form.getCategory_name();
				mList.add(mline);
			}
			String pReferChooser = CodeListUtils.getReferChooser(mList);
			return pReferChooser;
		} else {
			return "";
		}
	}

	private static Map<String, Set<String>> imbalanceModelOfLine = null;
	private static Map<String, String> kindOfModel = null;

	/**
	 * 确认机型在工程是否不平衡
	 * @param modelId 机型 ID
	 * @param lineId 工程 ID
	 * @param conn
	 * @return
	 */
	public boolean checkImbalance(String modelId, String lineId, SqlSession conn) {
		if (imbalanceModelOfLine == null) {
			imbalanceModelOfLine = new HashMap<String, Set<String>>();

			ModelMapper mapper = conn.getMapper(ModelMapper.class);
			List<String> l = mapper.getLineImbalanceModel("00000000012");
			Set<String> ibSet12 = new HashSet<String>();
			for (String ibmodelId : l) {
				ibSet12.add(ibmodelId);
			}
			imbalanceModelOfLine.put("00000000012", ibSet12);

			l = mapper.getLineImbalanceModel("00000000013");
			Set<String> ibSet13 = new HashSet<String>();
			for (String ibmodelId : l) {
				ibSet13.add(ibmodelId);
			}
			imbalanceModelOfLine.put("00000000013", ibSet13);

			l = mapper.getLineImbalanceModel("00000000014");
			Set<String> ibSet14 = new HashSet<String>();
			for (String ibmodelId : l) {
				ibSet14.add(ibmodelId);
			}
			imbalanceModelOfLine.put("00000000014", ibSet14);
		}

		Set<String> imol = imbalanceModelOfLine.get(lineId);
		if (imol == null) return false;
		return imbalanceModelOfLine.get(lineId).contains(modelId);
	}

	public String getKind(String modelId, SqlSession conn) {

		if (kindOfModel == null) {
			kindOfModel = new HashMap<String, String>();

			ModelMapper mapper = conn.getMapper(ModelMapper.class);
			List<ModelEntity> lResultBean = mapper.getAllModel();
			for (ModelEntity resultBean : lResultBean) {
				kindOfModel.put(resultBean.getModel_id(), resultBean.getKind());
			}
		}
		return kindOfModel.get(modelId);
	}
}

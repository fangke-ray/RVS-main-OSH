package com.osh.rvs.service.partial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialUnpackEntity;
import com.osh.rvs.bean.partial.FactPartialWarehouseEntity;
import com.osh.rvs.bean.partial.PartialWarehouseDetailEntity;
import com.osh.rvs.bean.partial.PartialWarehouseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.FactPartialWarehouseForm;
import com.osh.rvs.form.partial.PartialWarehouseForm;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PartialUnpackMapper;
import com.osh.rvs.mapper.partial.FactPartialWarehouseMapper;
import com.osh.rvs.mapper.partial.PartialWarehouseDetailMapper;
import com.osh.rvs.mapper.partial.PartialWarehouseMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.UploadService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 零件入库单
 * 
 * @author liuxb
 * 
 */
public class PartialWarehouseService {
	/** yyyy/MM/dd **/
	private final String DATE_EXPRESSION = "\\d{4}/\\d{1,2}/\\d{1,2}";
	/** yyyy-MM-dd **/
	private final String ISO_DATE_EXPRESSION = "\\d{4}-\\d{1,2}-\\d{1,2}";

	public List<PartialWarehouseForm> search(ActionForm form, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseMapper dao = conn.getMapper(PartialWarehouseMapper.class);

		PartialWarehouseEntity entity = new PartialWarehouseEntity();
		// 复制表单数据到数据模型
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PartialWarehouseEntity> list = dao.search(entity);

		List<PartialWarehouseForm> respList = new ArrayList<PartialWarehouseForm>();
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseForm.class);
		}

		return respList;
	}

	/**
	 * 上传文件
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 * @param errors
	 */
	public void upload(ActionForm form, HttpServletRequest request, SqlSessionManager conn, List<MsgInfo> errors) {
		UploadService uService = new UploadService();

		String tempFileName = uService.getFile2Local(form, errors);

		if (errors.size() != 0)
			return;

		Map<String, Object> map = validateFile(tempFileName, conn, errors);

		if (errors.size() > 0) {
			return;
		}

		PartialWarehouseMapper partialWarehouseDao = conn.getMapper(PartialWarehouseMapper.class);
		PartialWarehouseDetailMapper partialWarehouseDetailDao = conn.getMapper(PartialWarehouseDetailMapper.class);
		CommonMapper commonDao = conn.getMapper(CommonMapper.class);

		PartialWarehouseEntity warehouseEntity = (PartialWarehouseEntity) map.get("warehouseEntity");
		@SuppressWarnings("unchecked")
		List<PartialWarehouseDetailEntity> list = (List<PartialWarehouseDetailEntity>) map.get("list");

		// 新建零件入库单
		partialWarehouseDao.insert(warehouseEntity);

		// 获取入库单KEY
		String key = commonDao.getLastInsertID();

		for (PartialWarehouseDetailEntity entity : list) {
			entity.setKey(key);
			// 新建零件入库明细
			partialWarehouseDetailDao.insert(entity);
		}

		// TODO 更新间接人员作业信息处理结束时间

	}

	private Map<String, Object> validateFile(String fileName, SqlSessionManager conn, List<MsgInfo> errors) {
		PartialMapper partialDao = conn.getMapper(PartialMapper.class);
		PartialUnpackMapper partialUnpackDao = conn.getMapper(PartialUnpackMapper.class);
		PartialWarehouseMapper partialWarehouseDao = conn.getMapper(PartialWarehouseMapper.class);

		File file = new File(fileName);

		// 取得文件编码
		String fileCode = RvsUtils.getFileCode(fileName);
		BufferedReader reader = null;

		// 需要封装标记
		boolean isNeedUnpack = false;

		Map<String, Object> respMap = new HashMap<String, Object>();
		List<PartialWarehouseDetailEntity> list = new ArrayList<PartialWarehouseDetailEntity>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), fileCode));
			String tempString = null;
			int rowNum = 0;
			MsgInfo error = null;
			PartialWarehouseEntity warehouseEntity = null;
			PartialWarehouseDetailEntity warehouseDetailEntity = null;

			while ((tempString = reader.readLine()) != null) {
				rowNum++;
				if (rowNum == 1 || rowNum == 2) {// 去除表头
					continue;
				}

				String[] arr = tempString.split("\t");// 制表格分割

				if (arr.length < 7) {
					error = new MsgInfo();
					error.setErrcode("file.invalidFormat");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
					errors.add(error);
					return null;
				}

				// 取得第三行（验证格式）
				if (rowNum == 3) {
					// DN单号
					String dnNo = arr[0];
					if (CommonStringUtil.isEmpty(dnNo)) {
						error = new MsgInfo();
						error.setErrcode("file.invalidFormat");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
						errors.add(error);
						return null;
					}

					// 日期
					String strDate = arr[6];
					if (CommonStringUtil.isEmpty(strDate)) {
						error = new MsgInfo();
						error.setErrcode("file.invalidFormat");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
						errors.add(error);
						return null;
					}

					if (dnNo.length() > 8) {
						error = new MsgInfo();
						error.setErrmsg("第" + rowNum + "行第1列交货长度大于8。");
						errors.add(error);
						return null;
					} else {
						// DN单号重复check
						warehouseEntity = partialWarehouseDao.getByDnNo(dnNo);
						if (warehouseEntity != null) {
							error = new MsgInfo();
							error.setErrcode("dbaccess.recordDuplicated");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "DN单号【" + dnNo + "】"));
							errors.add(error);
							return null;
						}
					}

					if (!strDate.matches(DATE_EXPRESSION) && !strDate.matches(ISO_DATE_EXPRESSION)) {// 日期形式不匹配
						error = new MsgInfo();
						error.setErrcode("validator.invalidParam.invalidDateValue");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidDateValue", "第" + rowNum + "行第7列Mat.Av.Dt.", DateUtil.DATE_PATTERN + "或者"
								+ DateUtil.ISO_DATE_PATTERN));
						errors.add(error);
						return null;
					}

					warehouseEntity = new PartialWarehouseEntity();
					// 设置DN单号
					warehouseEntity.setDn_no(dnNo);

					// 设置日期
					if (strDate.matches(DATE_EXPRESSION)) {
						warehouseEntity.setWarehouse_date(DateUtil.toDate(strDate, DateUtil.DATE_PATTERN));
					} else {
						warehouseEntity.setWarehouse_date(DateUtil.toDate(strDate, DateUtil.ISO_DATE_PATTERN));
					}

					respMap.put("warehouseEntity", warehouseEntity);
				}

				warehouseDetailEntity = new PartialWarehouseDetailEntity();

				// 物料（零件Code）
				String code = arr[3];
				if (CommonStringUtil.isEmpty(code)) {
					error = new MsgInfo();
					error.setErrmsg("第" + rowNum + "行第4列物料为空。");
					errors.add(error);
				} else {
					// 查询零件信息
					List<PartialEntity> partialList = partialDao.getPartialByCode(code);

					if (partialList == null || partialList.size() == 0) {
						// 零件不存在
						error = new MsgInfo();
						error.setErrcode("dbaccess.recordNotExist");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "第" + rowNum + "行第4列物料【" + code + "】"));
						errors.add(error);
					} else {
						PartialEntity partialEntity = partialList.get(0);
						String partialID = partialEntity.getPartial_id();

						warehouseDetailEntity.setPartial_id(partialID);

						if (!isNeedUnpack) {
							// 查询是否需要分装
							PartialUnpackEntity partialUnpackEntity = new PartialUnpackEntity();
							partialUnpackEntity.setPartial_id(partialID);
							partialUnpackEntity = partialUnpackDao.getPartialUnpack(partialUnpackEntity);

							if (partialUnpackEntity != null) {
								isNeedUnpack = true;
							}
						}
					}
				}

				// 数量
				String strQuantity = arr[4];
				if (CommonStringUtil.isEmpty(strQuantity)) {
					error = new MsgInfo();
					error.setErrcode("validator.required");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "第" + rowNum + "行第5列"));
					errors.add(error);
				} else if (strQuantity.length() > 5) {// 长度大于5
					error = new MsgInfo();
					error.setErrcode("validator.invalidParam.invalidMaxLengthValue");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMaxLengthValue", "第" + rowNum + "行第5列", "5"));
					errors.add(error);
				} else if (!UploadService.isNum(strQuantity)) {// 数量不是数字
					error = new MsgInfo();
					error.setErrcode("validator.invalidParam.invalidIntegerValue");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidIntegerValue", "第" + rowNum + "行第5列"));
					errors.add(error);
				} else if (Integer.valueOf(strQuantity) <= 0) {// 数字小于1
					error = new MsgInfo();
					error.setErrcode("validator.invalidParam.invalidMoreThanZero");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "第" + rowNum + "行第5列"));
					errors.add(error);
				} else {
					warehouseDetailEntity.setQuantity(Integer.valueOf(strQuantity));
				}

				list.add(warehouseDetailEntity);
			}

			if (rowNum < 3) {
				error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
			}

			if (errors.size() == 0) {
				Map<String, Integer> map = new HashMap<String, Integer>();

				// 遍历数据，相同零件数量合并
				for (PartialWarehouseDetailEntity entity : list) {
					// 零件ID
					String partialID = entity.getPartial_id();

					// 数量
					Integer quantity = entity.getQuantity();

					if (map.containsKey(partialID)) {
						Integer preQuantity = map.get(partialID);
						// 数量累计
						quantity += preQuantity;
						map.put(partialID, quantity);
					} else {
						map.put(partialID, quantity);
					}
				}

				list.clear();
				for (String partialID : map.keySet()) {
					PartialWarehouseDetailEntity entity = new PartialWarehouseDetailEntity();
					// 设置零件ID
					entity.setPartial_id(partialID);

					// 设置数量
					entity.setQuantity(map.get(partialID));

					list.add(entity);
				}

				respMap.put("list", list);

				warehouseEntity = (PartialWarehouseEntity) respMap.get("warehouseEntity");

				// 需要封裝
				if (isNeedUnpack) {
					// 收货完
					warehouseEntity.setStep(0);
				} else {
					// 核对/上架中
					warehouseEntity.setStep(1);
				}

				respMap.put("warehouseEntity", warehouseEntity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return respMap;
	}

	/**
	 * 根据入库进展查询入库单信息
	 * 
	 * @param steps
	 * @param conn
	 * @return
	 */
	public List<PartialWarehouseForm> searchByStep(String[] steps, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseMapper dao = conn.getMapper(PartialWarehouseMapper.class);

		List<PartialWarehouseEntity> list = dao.getByStep(steps);

		List<PartialWarehouseForm> respList = new ArrayList<PartialWarehouseForm>();
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseForm.class);
		}

		return respList;
	}

	/**
	 * 根据KEY获取入库单信息
	 * 
	 * @param key
	 * @param conn
	 * @return
	 */
	public PartialWarehouseForm getByKey(String key, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseMapper dao = conn.getMapper(PartialWarehouseMapper.class);

		PartialWarehouseEntity entity = dao.getByKey(key);

		PartialWarehouseForm respForm = null;

		if (entity != null) {
			respForm = new PartialWarehouseForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return respForm;
	}

	/**
	 * 更新零件入库作业数
	 * 
	 * @param request
	 * @param conn
	 * @param errors
	 */
	public void updateQuantity(HttpServletRequest request, SqlSession conn, List<MsgInfo> errors) {
		FactPartialWarehouseMapper factPartialWarehouseMapper = conn.getMapper(FactPartialWarehouseMapper.class);
		PartialWarehouseDetailMapper partialWarehouseDetailMapper = conn.getMapper(PartialWarehouseDetailMapper.class);

		AcceptFactService acceptFactService = new AcceptFactService();

		// 入库单KEY
		String key = request.getParameter("key");
		String productionType = request.getParameter("production_type");
		String label = "";
		if ("213".equals(productionType)) {
			label = "核对/上架数量";
		} else if ("214".equals(productionType)) {
			label = "分装数量";
		}

		// 当前登录者
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureForm productionForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
		// 作业KEY
		String afPfKey = productionForm.getAf_pf_key();

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		List<FactPartialWarehouseForm> pageList = new AutofillArrayList<FactPartialWarehouseForm>(FactPartialWarehouseForm.class);
		Map<String, String[]> parameters = request.getParameterMap();

		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("fact_partial_warehouse".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("spec_kind".equals(column)) {
						pageList.get(icounts).setSpec_kind(value[0]);
					} else if ("quantity".equals(column)) {
						pageList.get(icounts).setQuantity(value[0]);
					}

					pageList.get(icounts).setAf_pf_key(afPfKey);
					pageList.get(icounts).setPartial_warehouse_key(key);
				}
			}
		}

		for (FactPartialWarehouseForm factPartialWarehouseForm : pageList) {
			Validators v = BeanUtil.createBeanValidators(factPartialWarehouseForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("quantity", v.required("本次" + label));

			List<MsgInfo> errs = v.validate();
			for (int i = 0; i < errs.size(); i++) {
				errs.get(i).setLineno(CodeListUtils.getValue("partial_spec_kind", factPartialWarehouseForm.getSpec_kind()));
			}
			errors.addAll(errs);
		}

		FactPartialWarehouseEntity factPartialWarehouseEntity = null;

		Map<Integer, Integer> pageMap = new HashMap<Integer, Integer>();
		/** 检查负数 **/
		if (errors.size() == 0) {
			for (FactPartialWarehouseForm factPartialWarehouseForm : pageList) {
				factPartialWarehouseEntity = new FactPartialWarehouseEntity();
				BeanUtil.copyToBean(factPartialWarehouseForm, factPartialWarehouseEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

				if (factPartialWarehouseEntity.getQuantity() < 0) {
					MsgInfo error = new MsgInfo();
					error.setLineno(CodeListUtils.getValue("partial_spec_kind", factPartialWarehouseForm.getSpec_kind()));
					error.setErrcode("validator.invalidParam.invalidMoreThanOrEqualToZero");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanOrEqualToZero", "本次" + label));
					errors.add(error);
				}
				pageMap.put(factPartialWarehouseEntity.getSpec_kind(), factPartialWarehouseEntity.getQuantity());
			}
		}

		if (errors.size() == 0) {
			/** 【分装】/【核对/上架】总数 **/
			List<PartialWarehouseDetailEntity> kindList = new ArrayList<PartialWarehouseDetailEntity>();
			if ("213".equals(productionType)) {// 核对/上架
				kindList = partialWarehouseDetailMapper.countCollactionQuantityOfKind(key);
			} else {// 分装
				kindList = partialWarehouseDetailMapper.countUnpackQuantityOfKind(key);
			}

			/** 上次【分装】/【核对/上架】数量 **/
			factPartialWarehouseEntity = new FactPartialWarehouseEntity();
			// 设置入库单KEY
			factPartialWarehouseEntity.setPartial_warehouse_key(key);
			// 设置作业类型
			factPartialWarehouseEntity.setProduction_type(Integer.valueOf(productionType));
			// 设置作业KEY
			factPartialWarehouseEntity.setAf_pf_key(afPfKey);
			List<FactPartialWarehouseEntity> factList = factPartialWarehouseMapper.countQuantityOfSpecKind(factPartialWarehouseEntity);

			/** 上次数量,本次数量合并 **/
			for (FactPartialWarehouseEntity entity : factList) {
				// 规格种别
				Integer specKind = entity.getSpec_kind();
				// 上次数量
				Integer prevQuantity = entity.getQuantity();

				if (pageMap.containsKey(specKind)) {
					// 本次数量
					Integer curQuantity = pageMap.get(specKind);
					pageMap.put(specKind, curQuantity + prevQuantity);
				}
			}

			/** 总数检查 **/
			for (PartialWarehouseDetailEntity entity : kindList) {
				// 规格种别
				Integer specKind = entity.getSpec_kind();
				// 总数
				Integer totalQuantity = entity.getQuantity();

				if (pageMap.containsKey(specKind)) {
					// 数量
					Integer quantity = pageMap.get(specKind);

					// 大于总数
					if (totalQuantity < quantity) {
						MsgInfo error = new MsgInfo();
						error.setLineno(CodeListUtils.getValue("partial_spec_kind", specKind.toString()));
						error.setErrmsg(label + "之和大于总数！");
						errors.add(error);
					}
				}
			}
		}

		if (errors.size() == 0) {
			/** 判断当前作业是否存在零件入库作业数记录 **/
			factPartialWarehouseEntity = new FactPartialWarehouseEntity();
			// 设置入库单KEY
			factPartialWarehouseEntity.setPartial_warehouse_key(key);
			// 设置作业KEY
			factPartialWarehouseEntity.setAf_pf_key(afPfKey);
			int len = factPartialWarehouseMapper.search(factPartialWarehouseEntity).size();
			if (len == 0) {
				for (FactPartialWarehouseForm factPartialWarehouseForm : pageList) {
					FactPartialWarehouseEntity entity = new FactPartialWarehouseEntity();
					BeanUtil.copyToBean(factPartialWarehouseForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
					// 新建现品入库作业数
					factPartialWarehouseMapper.insert(entity);
				}
			} else {
				for (FactPartialWarehouseForm factPartialWarehouseForm : pageList) {
					FactPartialWarehouseEntity entity = new FactPartialWarehouseEntity();
					BeanUtil.copyToBean(factPartialWarehouseForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
					// 更新现品入库作业数
					factPartialWarehouseMapper.update(entity);
				}
			}
		}
	}
}

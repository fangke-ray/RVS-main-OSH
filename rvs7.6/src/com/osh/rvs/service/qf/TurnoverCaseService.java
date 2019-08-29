package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.form.qf.TurnoverCaseForm;
import com.osh.rvs.mapper.qf.TurnoverCaseMapper;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class TurnoverCaseService {

	public List<TurnoverCaseForm> searchTurnoverCase(ActionForm form,
			SqlSession conn) {
		TurnoverCaseEntity condition = new TurnoverCaseEntity();
		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> result = mapper.searchTurnoverCase(condition);

		List<TurnoverCaseForm> lcf = new ArrayList<TurnoverCaseForm>();
		BeanUtil.copyToFormList(result, lcf, 
				CopyOptions.COPYOPTIONS_NOEMPTY, TurnoverCaseForm.class);

		return lcf;
	}

	public List<String> getStorageHeaped(ActionForm form, SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		return mapper.getStorageHeaped();
	}

	public void changelocation(SqlSessionManager conn, String material_id,
			String location) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		boolean putinByPda = false;
		if (location.endsWith("+")) {
			location = location.substring(0, location.length() - 1);
			putinByPda = true;
		}
		// 寻找原来位置
		TurnoverCaseEntity condition = new TurnoverCaseEntity();
		condition.setMaterial_id(material_id);
		List<TurnoverCaseEntity> result = mapper.searchTurnoverCase(condition);

		TurnoverCaseEntity updEntity = new TurnoverCaseEntity();
		if (result.size() == 0) {
			updEntity.setMaterial_id(material_id);
			updEntity.setStorage_time(new Date());
			updEntity.setExecute(0);
			updEntity.setLocation(location);
			mapper.putin(updEntity);
		} else {
			updEntity = result.get(0);
			mapper.warehousing(updEntity.getLocation());
			updEntity.setLocation(location);
			if (putinByPda) {
				updEntity.setStorage_time(new Date());
				updEntity.setExecute(1);
			}
			mapper.putin(updEntity);
		}
	}

	public void warehousing(SqlSessionManager conn, String location) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		
		mapper.warehousing(location);
	}

	public TurnoverCaseEntity getStorageByMaterial(String material_id, SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		TurnoverCaseEntity condition = new TurnoverCaseEntity();
		condition.setMaterial_id(material_id);
		List<TurnoverCaseEntity> result = mapper.searchTurnoverCase(condition);
		if (result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public List<TurnoverCaseForm> getWarehousingPlanList(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> result = mapper.getWarehousingPlan();

		List<TurnoverCaseForm> lcf = new ArrayList<TurnoverCaseForm>();
		for (TurnoverCaseEntity turnoverCaseEntity :result) {
			TurnoverCaseForm turnoverCaseForm = new TurnoverCaseForm();

			BeanUtil.copyToForm(turnoverCaseEntity, turnoverCaseForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			turnoverCaseForm.setBound_out_ocm(CodeListUtils.getValue("material_direct_ocm", "" + turnoverCaseEntity.getBound_out_ocm()));

			lcf.add(turnoverCaseForm);
		}

		return lcf;
	}

	public List<String> warehousing(SqlSessionManager conn,
			Map<String, String[]> parameterMap) {
		List<String> locations = new AutofillArrayList<String> (String.class); 
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String table = m.group(1);
				if ("turnover_case".equals(table)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);
					if ("location".equals(column)) {
						locations.set(icounts, value[0]);
					}
				}
			}
		}

		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		for (String location : locations) {
			mapper.warehousing(location);
		}

		return locations;
	}

	/**
	 * 取得待入库镜箱一览
	 * @param conn
	 * @return
	 */
	public List<TurnoverCaseForm> getStoragePlanList(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> result = mapper.getStoragePlan();

		List<TurnoverCaseForm> lcf = new ArrayList<TurnoverCaseForm>();

		for (TurnoverCaseEntity turnoverCaseEntity :result) {
			TurnoverCaseForm turnoverCaseForm = new TurnoverCaseForm();

			BeanUtil.copyToForm(turnoverCaseEntity, turnoverCaseForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			turnoverCaseForm.setBound_out_ocm(CodeListUtils.getValue("material_direct_ocm", "" + turnoverCaseEntity.getBound_out_ocm()));

			lcf.add(turnoverCaseForm);
		}
		return lcf;
	}

	public void checkStorage(SqlSessionManager conn, String location) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		mapper.checkStorage(location);
	}

	public List<String> checkStorage(SqlSessionManager conn,
			Map<String, String[]> parameterMap) {
		List<String> locations = new AutofillArrayList<String> (String.class); 
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String table = m.group(1);
				if ("turnover_case".equals(table)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);
					if ("location".equals(column)) {
						locations.set(icounts, value[0]);
					}
				}
			}
		}

		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		for (String location : locations) {
			mapper.checkStorage(location);
		}

		return locations;
	}

	public TurnoverCaseEntity getEntityByLocation(String location,
			SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		return mapper.getEntityByLocation(location);
	}

	public TurnoverCaseEntity getEntityByLocationForStorage(String location,
			SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		return mapper.getEntityByLocationForStorage(location);
	}

	public TurnoverCaseEntity getEntityByLocationForShipping(String location,
			SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		return mapper.getEntityByLocationForShipping(location);
	}

	public String getShelfMap(String shelf, List<TurnoverCaseForm> planListOnShelf, SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> list = mapper.getListOnShelf(shelf);

		StringBuffer sb = new StringBuffer();
		Integer comLayer = null;
		sb.append("<tr>");
		for (TurnoverCaseEntity entity : list) {
			Integer iLayer = entity.getLayer();
			if (comLayer != iLayer) {
				if (comLayer != null) sb.append("</tr><tr>");
				comLayer = iLayer;
			}
//			if ("M".equals(shelf) && iLayer > 1) {
//				sb.append("<td colspan=\"3\" location=\""); // 不规则M TODO
//			} else {
				sb.append("<td location=\"");
//			}
			sb.append(entity.getLocation());
			sb.append("\" class=\"");
			Date storage_time = entity.getStorage_time();
			if (storage_time == null) {
				sb.append("status-empty");
			} else if (storage_time.before(entity.getStorage_time_start())) {
				sb.append("status-overtime");
			} else {
				sb.append("status-storaged");
			}

			// 对象
			for (TurnoverCaseForm planTcForm : planListOnShelf) {
				if (entity.getLocation().equals(planTcForm.getLocation())) {
					sb.append(" mapping");
					break;
				}
			}
			sb.append("\">■</td>");
		}
		sb.append("</tr>");
		return sb.toString();
	}

	public List<TurnoverCaseForm> filterOnShelf(
			List<TurnoverCaseForm> storagePlanList, String shelf) {
		List<TurnoverCaseForm> ret = 		new ArrayList<TurnoverCaseForm>();
		for (TurnoverCaseForm storagePlan : storagePlanList) {
			if (shelf.equals(storagePlan.getShelf())) {
				ret.add(storagePlan);
			}
		}
		return ret;
	}

	public List<TurnoverCaseForm> getIdleMaterialList(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> list = mapper.getIdleMaterialList();

		List<TurnoverCaseForm> lcf = new ArrayList<TurnoverCaseForm>();
		BeanUtil.copyToFormList(list, lcf, CopyOptions.COPYOPTIONS_NOEMPTY, TurnoverCaseForm.class);

		return lcf;
	}

	public void putinManual(String location, String material_id, SqlSessionManager conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		TurnoverCaseEntity entity = new TurnoverCaseEntity();
		entity.setLocation(location);
		entity.setMaterial_id(material_id);
		entity.setExecute(1);
		entity.setStorage_time(new Date());
		mapper.putin(entity);
	}

	public TurnoverCaseEntity checkEmptyLocation(String location,
			SqlSessionManager conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		return mapper.checkEmpty(location);
	}

	public void triggerUndoStorage(String material_id) throws IOReactorException, InterruptedException {
		HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
		httpclient.start();
		try {  
			HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/assign_tc_space/" 
				+ material_id + "/UNDO/");
			httpclient.execute(request, null);
		} catch (Exception e) {
		} finally {
			Thread.sleep(80);
			httpclient.shutdown();
		}
	}

}

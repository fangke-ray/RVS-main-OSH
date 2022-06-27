package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.inline.DisassembleStorageEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.DisassembleStorageForm;
import com.osh.rvs.mapper.inline.DisassembleStorageMapper;
import com.osh.rvs.service.PositionService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 分解库位实现
 * 
 * @author Ray.G
 *
 */
public class DisassembleStorageService {

	Logger _log = Logger.getLogger(DisassembleStorageService.class);

	/**
	 * 查询库位
	 * @param disassembleStorageForm
	 * @param conn
	 * @param msgInfos
	 * @return
	 */
	public List<DisassembleStorageForm> search(
			ActionForm disassembleStorageForm, SqlSession conn,
			List<MsgInfo> msgInfos) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity condition = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DisassembleStorageEntity> result = mapper.searchStorage(condition);

		List<DisassembleStorageForm> resultForm = new ArrayList<DisassembleStorageForm>();

		BeanUtil.copyToFormList(result, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY, DisassembleStorageForm.class);

		return resultForm;
	}

	/**
	 * 移动库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void changeLocation(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity condition = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity origin = mapper.getStorageByMaterialInPosition(condition);

		if (origin == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("position_id");
			error.setErrcode("info.linework.disassembleStorageMiss");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.disassembleStorageMiss"));
			msgInfos.add(error);
			return;
		}

		DisassembleStorageEntity target = mapper.getStorageByKey(condition);
		if (target == null || target.getMaterial_id() != null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("info.linework.disassembleStorageOccupied");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.disassembleStorageOccupied"));
			msgInfos.add(error);
			return;
		}

		mapper.warehouse(origin);
		condition.setRefresh_time(origin.getRefresh_time());
		mapper.putin(condition);
	}

	/**
	 * 放入库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void putin(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity condition = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity target = mapper.getStorageByKey(condition);
		if (target == null || target.getMaterial_id() != null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("info.linework.disassembleStorageOccupied");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.disassembleStorageOccupied"));
			msgInfos.add(error);
			return;
		}

		condition.setRefresh_time(new Date());
		mapper.putin(condition);
	}

	/**
	 * 手动移出库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void warehouse(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity condition = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity origin = mapper.getStorageByMaterialInPosition(condition);

		if (origin == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("position_id");
			error.setErrcode("info.linework.disassembleStorageMiss");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.disassembleStorageMiss"));
			msgInfos.add(error);
			return;
		}

		mapper.warehouse(condition);
	}

	/**
	 * 作业移出库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void warehouseInPosition(String material_id, String position_id, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity condition = new DisassembleStorageEntity();
		condition.setMaterial_id(material_id);
		condition.setPosition_id(position_id);

		DisassembleStorageEntity origin = mapper.getStorageByMaterialInPosition(condition);

		if (origin != null) {
			mapper.warehouse(origin);
		}
	}

	/**
	 * 建立库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void create(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity entity = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity target = mapper.getStorageByKey(entity);
		if (target != null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "分解库位"));
			msgInfos.add(error);
			return;
		}

		if (entity.getShelf() == null) entity.setShelf("");
		mapper.create(entity);
	}

	/**
	 * x修改库位设置
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void changeSetting(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity entity = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity target = mapper.getStorageByKey(entity);

		if (target == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "分解库位"));
			msgInfos.add(error);
			return;
		}

		if (entity.getShelf() == null) entity.setShelf("");
		mapper.changeSetting(entity);
	}

	/**
	 * 删除库位
	 * 
	 * @param disassembleStorageForm
	 * @param msgInfos
	 * @param conn
	 */
	public void remove(ActionForm disassembleStorageForm, List<MsgInfo> msgInfos, SqlSessionManager conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);

		DisassembleStorageEntity entity = new DisassembleStorageEntity();

		BeanUtil.copyToBean(disassembleStorageForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		DisassembleStorageEntity target = mapper.getStorageByKey(entity);
		if (target == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "分解库位"));
			msgInfos.add(error);
			return;
		}
		if (target.getMaterial_id() != null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("case_code");
			error.setErrcode("info.linework.disassembleStorageOccupied");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.disassembleStorageOccupied"));
			msgInfos.add(error);
			return;
		}

		mapper.remove(entity);
	}

	/**
	 * 取得库位表格
	 * 
	 * @param material_id
	 * @param position_id
	 * @param recomment
	 * @param lResponseResult
	 * @param conn
	 * @param msgInfos
	 */
	public void getLocationMap(String material_id, String position_id, boolean recomment, 
			Map<String, Object> lResponseResult, SqlSession conn,
			List<MsgInfo> msgInfos) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);
		List<DisassembleStorageEntity> storageOfPosition = mapper.getStorageOfPosition(position_id);

		StringBuffer retSb = new StringBuffer("");

		long maxRefreshTimestamp = 0l;
		DisassembleStorageEntity recommentPos = null;
		DisassembleStorageEntity firstEmptyPos = null;
		Map<String, List<DisassembleStorageEntity>> storageOfPositionBySelf = new TreeMap<String, List<DisassembleStorageEntity>>();

		for (DisassembleStorageEntity storage : storageOfPosition) {
			if (!storageOfPositionBySelf.containsKey(storage.getShelf())) {
				storageOfPositionBySelf.put(storage.getShelf(), new ArrayList<DisassembleStorageEntity>());
			}
			storageOfPositionBySelf.get(storage.getShelf()).add(storage);
			if (recomment) {
				if (storage.getAuto_arrange() == 1) {
					if (storage.getMaterial_id() == null) {
						if (recommentPos == null) {
							recommentPos = storage;
						}
						if (firstEmptyPos == null) {
							firstEmptyPos = storage;
						}
					} else {
						if (storage.getRefresh_time().getTime() > maxRefreshTimestamp) {
							maxRefreshTimestamp = storage.getRefresh_time().getTime();
							recommentPos = null;
						}
					}
				}
			}
		}
		if (recomment && recommentPos == null) {
			recommentPos = firstEmptyPos;
		}

		for (String shelf : storageOfPositionBySelf.keySet()) {
			int curLayer = -999;
			int maxItemCnt = 0;
			int lineCnt = 0;
			StringBuffer shelfSb = new StringBuffer("<tbody>");

			for (DisassembleStorageEntity storage : storageOfPositionBySelf.get(shelf)) {
				if (storage.getLayer() != curLayer) {
					if (shelfSb.length() > 0) shelfSb.append("</tr>");
					shelfSb.append("<tr>");
					curLayer = storage.getLayer();
					if (lineCnt > maxItemCnt) {
						maxItemCnt = lineCnt;
					}
					lineCnt = 0;
				}
				shelfSb.append("<td class=\"");
				if (storage.getMaterial_id() == null) {
					shelfSb.append("wip-empty");
					if (recommentPos != null) {
						if (recommentPos == storage) {
							shelfSb.append(" storage-recomment");
							recommentPos = null;
						}
					}
				} else {
					shelfSb.append("ui-storage-highlight");
				}
				shelfSb.append("\">");
				shelfSb.append(storage.getCase_code());
				shelfSb.append("</td>");
				lineCnt++;
			}
			if (!storageOfPositionBySelf.get(shelf).isEmpty()) {
				shelfSb.append("</tr>");
				if (lineCnt > maxItemCnt) {
					maxItemCnt = lineCnt;
				}
			}
			if (maxItemCnt == 0) {
				maxItemCnt = 3;
			}
			retSb.append("<div style=\"margin: 15px; float: left;\"><div class=\"ui-widget-header\" style=\"width: " + (maxItemCnt * 40) + "px; text-align: center;\">货架 ");
			retSb.append(shelf);
			retSb.append("</div><table class=\"condform storage-table\" style=\"width: " + (maxItemCnt * 40) + "px;\">");
			retSb.append(shelfSb);
			retSb.append("</table></div>");
		}
		retSb.append("<div class=\"clear\"></div>");

		lResponseResult.put("storageHtml", retSb.toString());
	}

	/**
	 * 取得库位所在工位选项
	 * 
	 * @param conn
	 * @return
	 */
	public String getStoragePosition(SqlSession conn) {
		Map<String, PositionEntity> mapEntity = PositionService.getInlineStoragePositions(conn);
		StringBuffer retSb = new StringBuffer("<option></option>");
		for (String position_id : mapEntity.keySet()) {
			PositionEntity pos = mapEntity.get(position_id);
			retSb.append("<option value='" + position_id + "'>" + pos.getProcess_code() + " " + pos.getName() + "</option>");
		}
		return retSb.toString();
	}

	/**
	 * 取得维修品在工位上的入库提示
	 * 
	 * @param isFact
	 * @param material_id
	 * @param storagePositions
	 * @param nextFingers
	 * @param conn
	 * @return
	 */
	public List<String> getStorageByMaterial(boolean isFact, String material_id, String section_id, Integer rework,
			List<String> storagePositions, List<String> nextFingers, SqlSession conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);
		List<String> newFingers = new ArrayList<String>();

		DisassembleStorageEntity condition = new DisassembleStorageEntity();
		condition.setMaterial_id(material_id);
		condition.setPositions(storagePositions);
		List<DisassembleStorageEntity> l = mapper.getStorageByMaterial(condition);

		for (DisassembleStorageEntity posi : l) {
			boolean hitNext = false;
			for (int i  = 0; i < nextFingers.size(); i++) {
				String nextFinger = nextFingers.get(i);
				if (CommonStringUtil.fillChar(nextFinger, '0', 11, true).equals(posi.getPosition_id())) {
					nextFingers.remove(i);
					hitNext = true;
					break;
				}

				if (nextFinger.indexOf(posi.getProcess_code()) > 0) {
					nextFingers.remove(i);
					hitNext = true;
					break;
				}
			}
			if (!hitNext && rework > 0) {
				continue;
			}

			if (posi.getCase_code() != null) {
				newFingers.add(posi.getProcess_code() + " 库位: " + posi.getCase_code());
			} else {
				if (isFact) {
					newFingers.add(posi.getProcess_code() + " 库位: (未选择)");
				} else {
					String recommendCaseCode = setRecommendCaseCode(material_id, section_id, posi.getPosition_id(), conn);
					_log.info("get recommendCaseCode = " + recommendCaseCode);
					if (recommendCaseCode == null) {
						newFingers.add(posi.getProcess_code() + " 库位: [lick\tposition_id='" + posi.getPosition_id() + "'\tprocess_code='" + posi.getProcess_code() + "'>(请选择)]");
					} else {
						newFingers.add(posi.getProcess_code() + " 库位: " + recommendCaseCode);
					}
				}
			}
		}
		if (nextFingers.size() > 0) {
			newFingers.addAll(0, nextFingers);
		}

		return newFingers;
	}

	/**
	 * 刷新入库提示
	 * 
	 * @param form
	 * @param finger
	 * @param session
	 * @param callbackResponse
	 */
	public void updateFinger(ActionForm form, String finger, HttpSession session, Map<String, Object> callbackResponse) {
		DisassembleStorageEntity entity = new DisassembleStorageEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		String position_id = entity.getPosition_id();
		String newFinger = finger.replaceAll("\\[lick\\tposition_id='" + position_id + "'\\t[^>]*'>\\(请选择\\)\\]", entity.getCase_code());

		session.setAttribute(RvsConsts.JUST_WORKING, newFinger);
		callbackResponse.put("fingers", newFinger);
		callbackResponse.put("past_fingers", session.getAttribute(RvsConsts.JUST_FINISHED));
	}

	/**
	 * 取得所在工位等待区中的库位提示 * 
	 * @param section_id
	 * @param position_id
	 * @param conn
	 * @return
	 */
	public Map<String, String> getFingersOfPosition(String section_id, String position_id, SqlSession conn) {
		DisassembleStorageMapper mapper = conn.getMapper(DisassembleStorageMapper.class);
		List<DisassembleStorageEntity> list = mapper.getStorageFingerOfPosition(section_id, position_id);
		if (list == null || list.size() == 0) {
			return null;
		}
		Map<String, String> ret = new HashMap<String, String>();
		for (DisassembleStorageEntity entity : list) {
			ret.put(entity.getMaterial_id(), entity.getCase_code());
		}
		return ret;
	}

	/**
	 * 自动分配库位
	 * （需额外connection）
	 * 
	 * @param section_id
	 * @param position_id
	 * @return
	 */
	private String setRecommendCaseCode(String material_id, String section_id, String position_id, SqlSession tempConn) {
		
		DisassembleStorageMapper mapper = tempConn.getMapper(DisassembleStorageMapper.class);
		List<DisassembleStorageEntity> storageOfPosition = mapper.getStorageOfPosition(position_id);

		long maxRefreshTimestamp = 0l;
		long maxRefreshTimestampLv2 = 0l;
		DisassembleStorageEntity recommentPos = null;
		DisassembleStorageEntity firstEmptyPos = null;
		DisassembleStorageEntity recommentPosLv2 = null;
		DisassembleStorageEntity firstEmptyPosLv2 = null;

		for (DisassembleStorageEntity storage : storageOfPosition) {
			if (storage.getAuto_arrange() == 1) {
				if (storage.getMaterial_id() == null) {
					if (recommentPos == null) {
						recommentPos = storage;
					}
					if (firstEmptyPos == null) {
						firstEmptyPos = storage;
					}
				} else {
					if (storage.getRefresh_time().getTime() > maxRefreshTimestamp) {
						maxRefreshTimestamp = storage.getRefresh_time().getTime();
						recommentPos = null;
					}
				}
			} else {
				if (storage.getMaterial_id() == null) {
					if (recommentPosLv2 == null) {
						recommentPosLv2 = storage;
					}
					if (firstEmptyPosLv2 == null) {
						firstEmptyPosLv2 = storage;
					}
				} else {
					if (storage.getRefresh_time().getTime() > maxRefreshTimestampLv2) {
						maxRefreshTimestampLv2 = storage.getRefresh_time().getTime();
						recommentPosLv2 = null;
					}
				}
			}
		}
		if (recommentPos == null) {
			recommentPos = firstEmptyPos;
		}
		if (recommentPos == null) {
			recommentPos = (recommentPosLv2 == null ? firstEmptyPosLv2 : recommentPosLv2);
		}
		if (recommentPos == null) {
			return null;
		}

		// 临时采用可写连接
		SqlSessionManager writableConn = RvsUtils.getTempWritableConn();

		try {
			writableConn.startManagedSession(false);

			DisassembleStorageMapper writableMapper = writableConn.getMapper(DisassembleStorageMapper.class);

			recommentPos.setRefresh_time(new Date());
			recommentPos.setMaterial_id(material_id);
			writableMapper.putin(recommentPos);

			writableConn.commit();
		} catch (Exception e) {
			if (writableConn != null && writableConn.isManagedSessionStarted()) {
				writableConn.rollback();
			}
		} finally {
			try {
				writableConn.close();
			} catch (Exception e) {
			} finally {
				writableConn = null;
			}
		}

		return recommentPos.getCase_code();
	}

}
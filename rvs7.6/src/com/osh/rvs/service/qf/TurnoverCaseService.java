package com.osh.rvs.service.qf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.qf.TurnoverCaseForm;
import com.osh.rvs.mapper.qf.TurnoverCaseMapper;
import com.osh.rvs.service.UserDefineCodesService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class TurnoverCaseService {
	Logger _log = Logger.getLogger(TurnoverCaseService.class);

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

		checkExcepted(location);
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
			String boundOutOcm = CodeListUtils.getValue("material_direct_ocm", "" + turnoverCaseEntity.getBound_out_ocm());
			if (boundOutOcm.length() > 3) {
				if (boundOutOcm.startsWith("OCSM-")) {
					boundOutOcm = boundOutOcm.substring(5);
				} else if (boundOutOcm.indexOf("C-TEC") >= 0) {
					boundOutOcm = boundOutOcm.substring(0, 4);
				} else if (boundOutOcm.length() > 5) {
					boundOutOcm = boundOutOcm.substring(0, 3);
				}
			}
			turnoverCaseForm.setBound_out_ocm(boundOutOcm);
			turnoverCaseForm.setLevel(CodeListUtils.getValue("material_level", "" + turnoverCaseEntity.getLevel()));

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

	/**
	 * 计划入库
	 * 
	 * @param conn
	 * @param location
	 */
	public void checkStorage(SqlSessionManager conn, String location) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		mapper.checkStorage(location);

		checkExcepted(location);
	}

	/**
	 * 计划入库
	 * 
	 * @param conn
	 * @param location
	 */
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

			checkExcepted(location);
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

	/**
	 * 手动放入
	 * 
	 * @param location
	 * @param material_id
	 * @param conn
	 */
	public void putinManual(String location, String material_id, SqlSessionManager conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		TurnoverCaseEntity entity = new TurnoverCaseEntity();
		entity.setLocation(location);
		entity.setMaterial_id(material_id);
		entity.setExecute(1);
		entity.setStorage_time(new Date());
		mapper.putin(entity);

		checkExcepted(location);
	}

	private static final String KIND_ENDOEYE = "06";
	private static final String KIND_UDI = "738";

	private static Set<String> locationSets = new HashSet<String>();

	// 当天已经排入的货架
	private static List<String> mappedShelfsForCurrent = new ArrayList<String>();
	// 当天已经排除的货架<货架号, 最后标记的库位号>
	private static Map<String, String> exceptedShelfsForCurrent = new HashMap<String, String>();


	/**
	 * 连续取得空置的通箱库位
	 * 
	 * @param kind 6 = Endoeye
	 * @param count 取得数量
	 * @param conn
	 * @return
	 * @throws Exception 
	 */
	public List<TurnoverCaseEntity> getEmptyLocations(String kind, int count,
			SqlSession conn) throws Exception {
		List<TurnoverCaseEntity> ret = new ArrayList<TurnoverCaseEntity>();

		List<String> exceptShelfs = new ArrayList<String> ();
		for (String mappedShelf : mappedShelfsForCurrent) {
			// 从已排的货架中取得库位
			ret.addAll(getEmptyLocationsInShelf(kind, mappedShelf, conn));
			if (ret.size() >= count) break;
		}

		while (ret.size() < count) {
			// 计算最空的并且排库位
			try {
				ret.addAll(getEmptyLocationEval(kind, conn, exceptShelfs));
			} catch (TurnOvercaseException e) {
				break;
			}
		}

		if (ret.size() > count) {
			ret = ret.subList(0, count);
		}

		return ret;
	}

	private List<TurnoverCaseEntity> getEmptyLocationEval(
			String kind, SqlSession conn, List<String> exceptShelfs) throws Exception {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, -10); // 10点以前按前一天预计的
		String todayString = DateUtil.toString(now.getTime(), DateUtil.DATE_PATTERN);

		boolean isEndoeye = kind.equals(KIND_ENDOEYE);
		boolean isUdi = kind.equals(KIND_UDI);

		if (!locationSets.contains(todayString)) {
			locationSets.add(todayString); // 当天重排
			mappedShelfsForCurrent.clear();
			exceptedShelfsForCurrent.clear();
		}

		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		List<TurnoverCaseEntity> result = new ArrayList<TurnoverCaseEntity>();

		if (!isEndoeye && !isUdi) {
			// 取得非Endoeye
			String mostSpacialShelf = mapper.getMostSpacialShelf(01, null, mappedShelfsForCurrent);
			if (mostSpacialShelf == null) throw new TurnOvercaseException("递归安排也无法找到库位！");
			mappedShelfsForCurrent.add(mostSpacialShelf);

			result = getEmptyLocationsInShelf(kind, mostSpacialShelf, conn);

			if (!result.isEmpty()) exceptedShelfsForCurrent.put(mostSpacialShelf, result.get(result.size() - 1).getLocation());
		}

		else if (isEndoeye) {
			// 取得Endoeye
			String mostSpacialShelf = mapper.getMostSpacialShelf(06, null, mappedShelfsForCurrent);
			if (mostSpacialShelf == null) throw new TurnOvercaseException("递归安排也无法找到库位！");
			mappedShelfsForCurrent.add(mostSpacialShelf);

			result = getEmptyLocationsInShelf(kind, mostSpacialShelf, conn);

			if (!result.isEmpty()) exceptedShelfsForCurrent.put(mostSpacialShelf, result.get(result.size() - 1).getLocation());
		}

		else if (isUdi) {
			// 取得UDI
			String mostSpacialShelf = mapper.getMostSpacialShelf(738, null, mappedShelfsForCurrent); // 738 = UDI
			if (mostSpacialShelf == null) throw new TurnOvercaseException("递归安排也无法找到库位！");
			mappedShelfsForCurrent.add(mostSpacialShelf);

			result = getEmptyLocationsInShelf(kind, mostSpacialShelf, conn);

			if (!result.isEmpty()) exceptedShelfsForCurrent.put(mostSpacialShelf, result.get(result.size() - 1).getLocation());
		}

		return result;
	}

	public List<TurnoverCaseEntity> getEmptyLocationsInShelf(String kind, String mappedShelf, SqlSession conn) {
		List<TurnoverCaseEntity> ret = new ArrayList<TurnoverCaseEntity>();
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);
		ret = mapper.getAllSpaceShelf(kind, mappedShelf);
		return ret;
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

	/**
	 * 打印生成
	 * 
	 * @param labels
	 * @return
	 */
	public String printLabels(String labels) {

		Rectangle rect = new Rectangle(110, 85); //120, 90
		Document document = new Document(rect, 2, 2, 0, 0);

		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");
		String filename = UUID.randomUUID().toString() + ".pdf";

		String[] labelArr = labels.split(";");

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document,
					new FileOutputStream(folder + "\\" + filename));
			document.open();
			BaseFont bfChinese = BaseFont.createFont(PathConsts.BASE_PATH + "\\msyh.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			Font detailFont = new Font(bfChinese, 28, Font.BOLD);

			for (int i = 0; i < labelArr.length - 1; i++) {
				String location = labelArr[i];

				drawBorder(pdfWriter, rect);

				addPage(document, location, detailFont);

				document.newPage();
			}
			String location = labelArr[labelArr.length - 1];

			drawBorder(pdfWriter, rect);
			addPage(document, location, detailFont);

		} catch (DocumentException de) {
			_log.error(de.getMessage(), de);
			return null;
		} catch (IOException ioe) {
			_log.error(ioe.getMessage(), ioe);
			return null;
		} finally {
			document.close();
			document = null;
		}

		return filename;
	}

	private void drawBorder(PdfWriter pdfWriter, Rectangle rect) {
		PdfContentByte cb = pdfWriter.getDirectContent();
		cb.setLineWidth(16f);
		cb.roundRectangle(0f, 0f, 110, 85, 16f);

//		cb.setLineWidth(8f);
//		cb.roundRectangle(4f, 4f, 102, 77, 12f);

		cb.stroke();
	}

	private void addPage(Document document, String location, Font detailFont) throws DocumentException {
		PdfPTable mainTable = new PdfPTable(1);

		mainTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainTable.setTotalWidth(110);
		mainTable.setLockedWidth(true);
		//mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		PdfPCell cell = new PdfPCell(new Paragraph(location, detailFont));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPaddingTop(25.0f);
		cell.setPaddingBottom(30.0f);
		mainTable.addCell(cell);

		document.add(mainTable);
	}

	/**
	 * 检查是否要清除当日计划列表的库位
	 * 
	 * @param location
	 */
	private void checkExcepted(String location) {
		if (exceptedShelfsForCurrent.containsValue(location)) {
			for (String shelf : exceptedShelfsForCurrent.keySet()) {
				if (exceptedShelfsForCurrent.get(shelf).equals(location)) {
					exceptedShelfsForCurrent.remove(shelf);
					mappedShelfsForCurrent.remove(shelf);
					break;
				}
			}
		}
	}

	/**
	 * 标记为预打印的库位
	 * 
	 * @param labels
	 * @param conn
	 * @return
	 */
	public String setToPrepare(String labels, SqlSessionManager conn) {
		String message = "";

		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		String[] labelArr = labels.split(";");

		for (String location : labelArr) {
			TurnoverCaseEntity entity = mapper.getEntityByLocation(location);
			Integer exe = entity.getExecute();
			if (entity.getMaterial_id() != null) {
				message += location + "已经分配<br>";
			} else {
				if (exe != null && exe == 9) {
					message += location + "已经打印过<br>";
				} else {
					mapper.setToPrepare(entity.getKey());
				}
			}
		}

		return message;
	}

	/**
	 * 取得预打印通箱库位
	 */
	public List<TurnoverCaseEntity> gerPreprintedLocations(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		List<TurnoverCaseEntity> preEntity = mapper.gerPreprintedLocations();

		return preEntity;
	}

	public List<TurnoverCaseEntity> getAnimalExpLocations(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		List<TurnoverCaseEntity> preEntity = mapper.getAnimalExpLocations();

		return preEntity;
	}

	public List<TurnoverCaseEntity> getAllEmptyLocations(SqlSession conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		List<TurnoverCaseEntity> allEntity = mapper.getAllEmptyLocations();

		return allEntity;
	}

	/**
	 * 库位设定给维修品
	 * 
	 * @param material_id
	 * @param location
	 * @param errors
	 * @param conn
	 */
	public void checkToLocation(String material_id, String location,
			List<MsgInfo> errors, SqlSessionManager conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		TurnoverCaseEntity condition = new TurnoverCaseEntity();
		if (material_id != null) {
			condition.setMaterial_id(material_id);
			List<TurnoverCaseEntity> ret = mapper.searchTurnoverCase(condition);
			if (ret.size() > 0) {
				MsgInfo error = new MsgInfo();
				error.setErrmsg("此维修品已经加入库位，请退出编辑框重新操作。");
				errors.add(error);
			}
		}

		condition.setMaterial_id(null);
		condition.setLocation(location);
		List<TurnoverCaseEntity> ret = mapper.searchTurnoverCase(condition);
		if (ret.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setErrmsg("库位不存在。");
			errors.add(error);
		} else {
//			TurnoverCaseEntity loc = ret.get(0);
//			if ((loc.getAnml_exp() == null || loc.getAnml_exp() != 1) &&
//					(loc.getExecute() == null || loc.getExecute() != 9)) {
//				MsgInfo error = new MsgInfo();
//				error.setErrmsg("此库位没有打印标签，或者已经被使用，请重新选择。");
//				errors.add(error);
//			}
		}
	}

	public Integer setToLocation(String material_id, String location,
			List<MsgInfo> errors, SqlSessionManager conn) {
		TurnoverCaseMapper mapper = conn.getMapper(TurnoverCaseMapper.class);

		TurnoverCaseEntity condition = new TurnoverCaseEntity();
		condition.setLocation(location);
		List<TurnoverCaseEntity> ret = mapper.searchTurnoverCase(condition);
		if (ret.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setErrmsg("库位不存在。");
			errors.add(error);
			return null;
		} else if (ret.get(0).getMaterial_id() != null) {
			MsgInfo error = new MsgInfo();
			error.setErrmsg("库位已占有。");
			errors.add(error);
			return null;
		}

		// 指定库位
		if (errors.size() == 0) {
			mapper.setToLocation(location, material_id);
		}
		return ret.get(0).getAnml_exp();
	}

	public String printRemote(String filePath, SqlSession conn) {
		String reqID = "" + new Date().getTime();
		Map<String, String> params = new HashMap<String, String>();
		params.put("reqId", reqID);

		String remoteUrl = "127.0.0.1";
		if (conn != null) {
			UserDefineCodesService udService = new UserDefineCodesService();
			String udHost = udService.searchUserDefineCodesValueByCode("TC_TICKET_PRINT_HOST", conn);
			if (!CommonStringUtil.isEmpty(udHost)) {
				remoteUrl = udHost;
			}
		}
		remoteUrl = "http://" + remoteUrl + ":8081/printReq";
		_log.info("remoteUrl=" + remoteUrl);

		filePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" + filePath;

		try {
			RvsUtils.sendMultipartFormData(remoteUrl, params, filePath);
			return reqID;
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
			return null;
		}
	}

}

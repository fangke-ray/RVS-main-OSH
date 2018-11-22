package com.osh.rvs.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.arnx.jsonic.JSON;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.jacob.com.Dispatch;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PcsRequestEntity;
import com.osh.rvs.mapper.infect.PeripheralInfectDeviceMapper;
import com.osh.rvs.mapper.inline.LeaderPcsInputMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SoloProductionFeatureMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.master.PcsRequestMapper;
import com.osh.rvs.service.MaterialProcessAssignService;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class PcsUtils {

	private static Logger logger = Logger.getLogger("ProcessCheckSheet");

	private static Map<String, Map<String, Map<String, String>>> fileBinder = new HashMap<String, Map<String, Map<String, String>>>();
	private static Map<String, String> folderTypes = new TreeMap<String, String>();

	private static String ext = ".html";
	private static String xls_ext = ".xls";

	private static final String CHECKED = "√";
	private static final String UNCHECKED = "　";
	private static final String FORBIDDEN = "×";
	private static final String NOCARE = "不操作";
	private static final String QUALIFIED = "合格";
	private static final String UNQUALIFIED = "不合格";

	private static final String COMMENT_451_461 = "总组CCD线长度";
	private static final String COMMENT_341 = "CCD有效长度"; 

	/** init */
	static {
		// try {
		// }
		// catch (Exception e) {
		//	logger.error(e.getMessage(), e);
		// }
		reset();
	}

	public static void reset() {
		reset(PathConsts.PCS_TEMPLATE);
	}
	public static void reset(String periodPath) {
		fileBinder.clear();
		File fXmls = new File(PathConsts.BASE_PATH + periodPath + "\\xml"); //xml
		if (fXmls.isDirectory()) {
			File[] fLines = fXmls.listFiles();
			for (File fLine : fLines) {
				if (fLine.isDirectory()) {
					String sLineName = fLine.getName();
					String sLineCode = toLineCode(sLineName);
					Map<String, Map<String, String>> lineBinder = new HashMap<String, Map<String, String>>();
					Map<String, String> basePaceBinder = new HashMap<String, String>();
					lineBinder.put("", basePaceBinder);
					fileBinder.put(sLineName, lineBinder);
					File[] fPaceOrPcsList = fLine.listFiles();
					for (File fPaceOrPcs : fPaceOrPcsList) {
						if (fPaceOrPcs.isDirectory()) {
							Map<String, String> paceBinder = new HashMap<String, String>();
							folderTypes.put(sLineCode + toPartCode(fPaceOrPcs.getName()), 
									sLineName + "\n" + fPaceOrPcs.getName());
							lineBinder.put(fPaceOrPcs.getName(), paceBinder);
							for (File fPcs : fPaceOrPcs.listFiles()) {
								if (fPcs.isFile()) {
									String filename = fPcs.getName().replaceAll(ext, ""); //ext
									String[] modelsOrCategories = filename.split("#");
									for (String modelOrCategory : modelsOrCategories) {
										paceBinder.put(normalize(modelOrCategory), fPcs.getAbsolutePath());
										// logger.info("======" + fPcs.getAbsolutePath());
									}
								}
							}
						} else {
							folderTypes.put(sLineCode, sLineName);

							String filename = fPaceOrPcs.getName().replaceAll(ext, ""); //ext
							String[] modelsOrCategories = filename.split("#");
							for (String modelOrCategory : modelsOrCategories) {
								basePaceBinder.put(normalize(modelOrCategory), fPaceOrPcs.getAbsolutePath());
								// logger.info("======" + fPaceOrPcs.getAbsolutePath());
							}
						}
					}
				}
			}
		}
	}
//	public static void reset(String periodPath) {
//		fileBinder.clear();
//		File fXmls = new File(PathConsts.BASE_PATH + periodPath + "\\excel"); //xml
//		if (fXmls.isDirectory()) {
//			File[] fLines = fXmls.listFiles();
//			for (File fLine : fLines) {
//				if (fLine.isDirectory()) {
//					String sLineName = fLine.getName();
//					String sLineCode = toLineCode(sLineName);
//					Map<String, Map<String, String>> lineBinder = new HashMap<String, Map<String, String>>();
//					Map<String, String> basePaceBinder = new HashMap<String, String>();
//					lineBinder.put("", basePaceBinder);
//					fileBinder.put(sLineName, lineBinder);
//					File[] fPaceOrPcsList = fLine.listFiles();
//					for (File fPaceOrPcs : fPaceOrPcsList) {
//						if (fPaceOrPcs.isDirectory()) {
//							Map<String, String> paceBinder = new HashMap<String, String>();
//							folderTypes.put(sLineCode + toPartCode(fPaceOrPcs.getName()), 
//									sLineName + "\n" + fPaceOrPcs.getName());
//							lineBinder.put(fPaceOrPcs.getName(), paceBinder);
//							for (File fPcs : fPaceOrPcs.listFiles()) {
//								if (fPcs.isFile()) {
//									String filename = fPcs.getName().replaceAll(xls_ext, ""); //ext
//									String[] modelsOrCategories = filename.split("#");
//									for (String modelOrCategory : modelsOrCategories) {
//										paceBinder.put(normalize(modelOrCategory), fPcs.getAbsolutePath());
//										// logger.info("======" + fPcs.getAbsolutePath());
//									}
//								}
//							}
//						} else {
//							folderTypes.put(sLineCode, sLineName);
//
//							String filename = fPaceOrPcs.getName().replaceAll(xls_ext, ""); //ext
//							String[] modelsOrCategories = filename.split("#");
//							for (String modelOrCategory : modelsOrCategories) {
//								basePaceBinder.put(normalize(modelOrCategory), fPaceOrPcs.getAbsolutePath());
//								// logger.info("======" + fPaceOrPcs.getAbsolutePath());
//							}
//						}
//					}
//				}
//			}
//		}
//	}

	private static String toLineCode(String sLineName) {
		// TODO 分别工程名
		switch (sLineName) {
		case "分解工程" : return "12";
		case "NS 工程" : return "13";
		case "总组工程" : return "14";
		case "最终检验" : return "15";
		case "检查卡" : return "19";
		}
		return "99";
	}
	private static String toPartCode(String name) {
		String ret = "";
		for (byte b : name.getBytes()) {
			ret += (b % 10);
		}
		return ang(ret);
	}

	private static String ang(String inc) {
		if (inc.length() < 6) {
			return inc;
		}
		int iRet = 0;
		for (byte b : inc.getBytes()) {
			iRet += b;
		}
		return ang("" + iRet);
	}

	public static String getHtml(String processCode) {
		return ("");
	}

	/**
	 * 取得工程检查票格式
	 * @param lineName 工程名
	 * @param modelName 型号名
	 * @param categoryName 机种名
	 * @param conn 
	 * @return
	 */
	public static Map<String, String> getXmlContents(String lineName, String modelName, ModelEntity modelEntity, SqlSession conn) {
		return getXmlContents(lineName, modelName, modelEntity, false, null, false, conn);
	}
	public static Map<String, String> getXmlContents(String lineName, String modelName, ModelEntity modelEntity, String material_id, boolean lightFix, SqlSession conn) {
		return getXmlContents(lineName, modelName, modelEntity, false, material_id, lightFix, conn);
	}
	public static Map<String, String> getXmlContents(String lineName, String modelName, ModelEntity modelEntity, 
			boolean checkHistory,
			String material_id, boolean lightFix, SqlSession conn) {
		logger.info("getXmlContents for line=" + lineName + " modelName=" + modelName);

		Map<String, String> ret = new LinkedHashMap<String, String>();
		if (modelName == null || lineName == null || !fileBinder.containsKey(lineName)) {
			return null;
		}

		String modelNameBFile = normalize(modelName);

		// 取得工程名对应的文件夹
		Map<String, Map<String, String>> lineBinder = fileBinder.get(lineName);

		List<PcsRequestEntity> lM = null;
		List<PcsRequestEntity> lH = null;
		if (material_id != null) {
			PcsRequestMapper prMapper = conn.getMapper(PcsRequestMapper.class);
			lM = prMapper.checkMaterialAssignAsOld(material_id);

			// 如果参照历史,取得指定机型的修改履历
			if (checkHistory) {
				lH= prMapper.getFixHistoryOfMaterial(material_id);
			}
		}

		for (String pace : lineBinder.keySet()) {
			String filename = null;
			// 如果因改废订而指定了
			if (material_id != null) {
				String folderTypesKey = getFolderTypesKey(lineName, pace, folderTypes);

				for (PcsRequestEntity pre : lM) {
					Integer lineType = pre.getLine_type();
					if ((""+lineType).equals(folderTypesKey)) {
						filename = PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\_request\\" + trimZero(pre.getPcs_request_key())
								+ "\\old.html";
						break;
					}
				}
				if (checkHistory && filename == null) {
					for (PcsRequestEntity pre : lH) {
						Integer lineType = pre.getLine_type();
						if ((""+lineType).equals(folderTypesKey)) {
							filename = PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\_request\\" + trimZero(pre.getPcs_request_key())
									+ "\\old.html";
							if (!new File(filename).exists()) {
								filename = null;
							}
							break;
						}
					}
				}
			}

			if (filename == null) {
				Map<String, String> files = lineBinder.get(pace);
				if (files.containsKey(modelNameBFile)) {
					// 按型号名取得文件
					filename = files.get(modelNameBFile);
				} else {
					if (modelEntity == null && conn !=null) {
						modelEntity = ReverseResolution.getModelEntityByName(modelName, conn);
					}
	
					// 按目录对应分类取得 TODO
					String packkind = getPackFromLineName(pace);
					String getPackName = getPackName(packkind, modelEntity);;
	
					if (!CommonStringUtil.isEmpty(getPackName)) {
						filename = files.get(getPackName);
					}
					if (filename == null) {
						String norCategoryName = normalize(modelEntity.getCategory_name());
						if (modelEntity.getCategory_name() != null && files.containsKey(norCategoryName)) {
							// 按机种名取得文件
							filename = files.get(norCategoryName);
						}
					}
				}
			}
			if (filename != null) {
				File xmlfile = new File(filename);
				if (xmlfile.exists()) {
					if (xmlfile.isFile()) {

						if (lightFix) {
							String lightFilename = filename.replaceAll("\\\\xml", "\\\\_lightmedium\\\\xml");
							File lxmlfile = new File(lightFilename);
							if (lxmlfile.exists()) {
								if (lxmlfile.isFile()) {
									xmlfile = lxmlfile;
								}
							}
						}

						BufferedReader input = null;
						try {
							input = new BufferedReader(new InputStreamReader(new FileInputStream(xmlfile),"UTF-8"));
							StringBuffer buffer = new StringBuffer();
							String text;

							while ((text = input.readLine()) != null)
								buffer.append(text);

							System.out.println(buffer.length());
							String content = buffer.toString();
							if (!CommonStringUtil.isEmpty(content)) {
								ret.put(lineName + ("".equals(pace) ? "" : ("-"  + pace)), content);
							}
						} catch (IOException ioException) {
						} finally {
							try {
								input.close();
							} catch (IOException e) {
							}
							input = null;
						}
					}
				}
			}
		}

		return ret;
	}

	private static String getPackName(String packkind, ModelEntity modelEntity) {
		if (packkind == null) {
			return null;
		}

		Method getter;
		String value = null;
		try {
			getter = ModelEntity.class.getMethod("get" + packkind.substring(0, 1).toUpperCase() + packkind.substring(1));
			Object ovalue = getter.invoke(modelEntity, new Object[] { });
			if (ovalue != null) value = ovalue.toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return value;
	}
	private static String getPackFromLineName(String pace) {
		// TODO properties
		switch (pace) {
		case "EL 座" : return "el_base_type";
		case "S 连接座" : return "s_connector_base_type";
		case "操作部" : return "operate_part_type";
		case "接眼" : return "ocular_type";
		}
		return null;
	}
	/**
	 * 取得页面表示工程检查票
	 * @param srcPcs 工程检查票范本
	 * @param material_id 维修对象
	 * @param sorc_no 显示用SORC号
	 * @param currentProcessCode 当期工位号
	 * @param leaderLineId 线长查看时所在工程
	 * @param conn 
	 * @return
	 */
	public static Map<String, String> toHtml(Map<String, String> srcPcses, String materialId, String sorcNo,
			String modelName, String serialNo, String level, String currentProcessCode, String leaderLineId, SqlSession conn) {
		logger.info("getXmlContents for material=" + materialId + " currentProcessCode=" + currentProcessCode);
		String currentProcessCodeOrg = currentProcessCode;

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		LeaderPcsInputMapper llDao = conn.getMapper(LeaderPcsInputMapper.class);

		// 取得维修对象返工次数
		int reworkCount;
		// DUMMY 对应
		if ((currentProcessCode != null && currentProcessCode.startsWith("3")) || "00000000013".equals(leaderLineId)) {
			reworkCount = dao.getReworkCountWithLine(materialId, "00000000013"); // TODO FIXIT 关系到线长编辑的工程检查票
		} else {
			reworkCount = dao.getReworkCount(materialId);
		}
		logger.info("reworkCount:"+ reworkCount);

		Map<String, String> htmlPcses = new LinkedHashMap<String, String>();
		Map<String, Integer> reworkOfPcses = new LinkedHashMap<String, Integer>();

		if (srcPcses == null) return htmlPcses;

		boolean isLightFix = !CommonStringUtil.isEmpty(level) && "9".equals(level.substring(0, 1)); 
		boolean isPrepheral = !CommonStringUtil.isEmpty(level) && "5".equals(level.substring(0, 1)); 

		currentProcessCode = checkOverAll(currentProcessCode);

		Pattern pCurrentProcessCode = Pattern.compile("<pcinput.*?scope=\"E\".*?position=\""+currentProcessCode+"\".*?/>");

//		Map<String, Integer> checkedOnId = new HashMap<String, Integer>(); TODO

		int factMaxRework = 1;
		// 对于每次返工
		for (int iRework = 0, factRework = 0; iRework <= reworkCount; iRework++) {
			logger.info("iRework:"+ iRework);

			ProductionFeatureEntity condEntity = new ProductionFeatureEntity();
			condEntity.setMaterial_id(materialId);
			condEntity.setRework(iRework);
			// 取得每次返工中作业记录
			List<ProductionFeatureEntity> pfEntities = dao.getProductionPcsOnRework(condEntity);

			// 取得每次返工中线长作业人记录
			List<ProductionFeatureEntity> lpcs = llDao.searchLeaderPcsInput(condEntity);

			// 本次替换掉的工位件数
			boolean bReplacedAtPosition = false;

			// 是最新一次返工
			boolean bNewest = iRework == reworkCount;

			Map<String, String> tempMap = new HashMap<String, String>();

			String currentComments = "";

			// 对于每张工检票
			for (String pcsName : srcPcses.keySet()) {
				logger.info("pcsName:"+ pcsName);

				Map<String, TreeMap<String, String>> allComments = new HashMap<String, TreeMap<String, String>>();
				// 总组工程检查票 TODO //
//				if (pcsName.startsWith("总组工程")) {
//					TreeMap<String, String> z = new TreeMap<String, String>();
//					z.put("00000000", "<准备中断处理信息>");
//					allComments.put("EC00001", z);
//				}

				String specify = srcPcses.get(pcsName);
				//String orgSpecify = specify;
				// 全体对象
				// GI
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<img src=\"/images/pcs/$1\"/>");
				// GS
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"S\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + sorcNo + "</label>");
				// GM
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + modelName + "</label>");
				// GC
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + serialNo + "</label>");
				// GR
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + CodeListUtils.getValue("material_level", level) + "</label>");

				// 本作业对象中有替换成功的项目
				boolean hasCurrent = false;

				// 线长对象
				if (lpcs !=null && lpcs.size() > 0) {
					for (ProductionFeatureEntity pf : lpcs) {
						// 工位代码
						String line_id = pf.getLine_id();
						// logger.info("line_id"+ line_id);

						String processCode = ""; // TODO 强制判断
						if("00000000012".equals(line_id)) {
							processCode = "2\\d{2}";
						} else if("00000000013".equals(line_id)) {
							processCode = "3\\d{2}";
						} else if("00000000014".equals(line_id)) {
							processCode = "(4|5|8)\\d{2}";
						}

						Pattern pProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"E\" type=\"\\w\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
						Matcher mProcessCode = pProcessCode.matcher(specify);

						Pattern plProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"L\" type=\"\\w\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
						Matcher mlProcessCode = plProcessCode.matcher(specify);

						if (mProcessCode.find() || mlProcessCode.find()) {

							String sPcs_inputs = pf.getPcs_inputs();
							if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
								// 解析输入值
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);


								// 当前工位最新返工
								logger.info("Leader sPcs_inputs edit:"+ sPcs_inputs);

								// 可输入
								if(leaderLineId != null) hasCurrent = true;
								// 读入既有值的输入
								for (String pcid : jsonPcs_inputs.keySet()) {
									// 输入值
									String sInput = jsonPcs_inputs.get(pcid);
									// 类别
									char sIype = pcid.charAt(1);

									if (!"".equals(sInput)) {
										switch (sIype) {

										case 'I': {
											// 输入：I
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + sInput + "</label>");
											break;
										}
										case 'R': {
											// 单选：R
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
													"<label>" + CHECKED + "</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + UNCHECKED + "</label>");
											break;
										}
										case 'M': {
											// 合格确认：M
											if ("1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\""+currentProcessCode+"\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + CHECKED + "</label><input name=\"$1\" class=\"i_sff\" type=\"hidden\" checked value=\"1\">");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + CHECKED + "</label>");
											} else if ("-1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\""+currentProcessCode+"\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + FORBIDDEN + "</label><input name=\"$1\" class=\"i_sff\" type=\"hidden\" checked value=\"-1\">");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + FORBIDDEN + "</label>");
											}
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + NOCARE + "</label>");
											break;
										}
										case 'N': {
											// 签章：N
											if ("1".equals(sInput)) {
												// 确认
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<img src=\"/images/sign/" + pf.getJob_no() + "\"/>");
												//		"<img src=\"images/operator/" + pf.getJob_no() + "\"/>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED").replaceAll("LN", "LD") + ")\\d\\d\" scope=\"\\w\" type=\"D\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											} else if ("-1".equals(sInput)) {
												// 不做
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>"+NOCARE+"</label>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED").replaceAll("LN", "LD") + ")\\d\\d\" scope=\"\\w\" type=\"D\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											}
											break;
										}
										}
									}
								}

								// 备注信息
								currentComments = pf.getPcs_comments();
								if (!CommonStringUtil.isEmpty(currentComments)) {
									@SuppressWarnings("unchecked")
									Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
									for (String commentskey : jsonPcs_comments.keySet()) {
										if (!allComments.containsKey(commentskey)) {
											allComments.put(commentskey, new TreeMap<String, String>());
										}
										if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
											TreeMap<String, String> tm = allComments.get(commentskey);
											tm.put(DateUtil.toString(pf.getFinish_time(), "yyyyMMdd9999"+tm.size()), pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey));
											allComments.put(commentskey, tm);
										}
									}
								}
							}
						}
					}
				}

				Set<String> hasProcess = new HashSet<String>();
				for (ProductionFeatureEntity pf : pfEntities) {
					// 工位代码
					String processCode = pf.getProcess_code();
					if ("612".equals(processCode)) processCode = "611";
					// logger.info("process_code"+ process_code);
					String orgProcessCode = processCode;

					boolean isCurrent = processCode.equals(currentProcessCode);
					if (currentProcessCode != null && currentProcessCode.indexOf("\\d") >= 0) {
						isCurrent = processCode.matches(currentProcessCode);
					}
					processCode = checkOverAll(processCode);

					Pattern pProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"E\" type=\"\\w\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
					Matcher mProcessCode = pProcessCode.matcher(specify);

					if (hasProcess.contains(orgProcessCode) || mProcessCode.find()) {

						hasProcess.add(orgProcessCode);
						// 如果有本工位的标签，进行替换
						bReplacedAtPosition = true;

						String sPcs_inputs = pf.getPcs_inputs();
						if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
							// 解析输入值
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

							if (bNewest && isCurrent) {
								// 当前工位最新返工
								logger.info("sPcs_inputs edit:"+ sPcs_inputs);

								// 可输入
								hasCurrent = true;
								// 读入既有值的输入
								for (String pcid : jsonPcs_inputs.keySet()) {
									// 输入值
									String sInput = jsonPcs_inputs.get(pcid);
									// 类别
									char sIype = pcid.charAt(1);

									if (!"".equals(sInput)) {
										switch (sIype) {

										case 'I': {
											// 输入：I
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"I\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + sInput + "</label>");
											break;
										}
										case 'R': {
											// 单选：R
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
													"<label>" + CHECKED + "</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + UNCHECKED + "</label>");
											break;
										}
										case 'M': {
											// 合格确认：M
											if ("1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + CHECKED + "</label><input name=\"$1\" class=\"i_sff\" type=\"hidden\" checked value=\"1\">");
											} else if ("-1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + FORBIDDEN + "</label><input name=\"$1\" class=\"i_sff\" type=\"hidden\" checked value=\"-1\">");
											}
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + NOCARE + "</label>");
											break;
										}
										case 'N': {
											// 签章：N
											if ("1".equals(sInput)) {
												// 确认
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<img src=\"/images/sign/" + pf.getJob_no() + "\"/>");
														// "<img src=\"images/operator/" + pf.getJob_no() + "\"/>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											} else if ("-1".equals(sInput)) {
												// 不做
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>"+NOCARE+"</label>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											}
											break;
										}
										case 'X': {
											// 返工：X
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.substring(0, 5).replaceAll("EX", "EN") + ")\\d\\d\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label style=\"color:red;\">发生返工</label>");
											break;
										}
										case 'T': {
											// 综合合格判定：T
											if ("1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"T\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<section locate=\"$1\" class=\"i_total\">合格</section>" + 
																"<input type=\"hidden\" name=\"$1\" value=\"1\" class=\"i_total_hidden\"/>"
														);
											} else if ("-1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"T\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<section locate=\"$1\" class=\"i_total\">不合格</section>" + 
														"<input type=\"hidden\" name=\"$1\" value=\"-1\" class=\"i_total_hidden\"/>"
														);
											}
											break;
										}
										}

//										// 记录标签在首次输入的返工次数
//										if (!checkedOnId.containsKey(pcid)) {
//											checkedOnId.put(pcid, iRework);
//										}
									} else {
//										switch (sIype) {
//
//										case 'I': {
//											// 输入：I
//											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")00\" scope=\"E\" type=\"I\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
//													"<input type=\"text\" name=\"$1\" value=\"\"/>");
//											break;
//										}
//										case 'R': {
//											// 单选：R
////											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + "\\d\\d)\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
////													"<input type=\"radio\" name=\"$1\" value=\"$2\" checked/>");
//											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
//													"<input type=\"radio\" name=\"$1\" value=\"$2\"/>");
//											break;
//										}
//										case 'M': {
//											// 复选：M
////											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
////													"<input type=\"checkbox\" name=\"$1\" value=\"$2\" checked/>");
//											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
//													"<input type=\"checkbox\" name=\"$1\" value=\"$2\"/>");
//											break;
//										}
//										case 'N': {
//											// 签章：N
////											if ("1".equals(sInput)) {
////												// 按钮
////												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
////														"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" checked></input><label for=\"$1_y\">确认</label>" +
////														"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
////											} else if ("-1".equals(sInput)) {
////												// 按钮
////												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
////														"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
////														"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" checked></input><label for=\"$1_n\">不做</label>");
////											} else {
//												// 按钮
//												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
//														"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
//														"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
//														"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
////											}
//											break;
//										}
//										}
									}
								}
							} else {
								// 非当前工位 or 非最新返工
								logger.info("sPcs_inputs view:"+ sPcs_inputs);

								for (String pcid : jsonPcs_inputs.keySet()) {
									// 输入值
									String sInput = jsonPcs_inputs.get(pcid);
									// 类别
									char sIype = pcid.charAt(1);

									if (!"".equals(sInput)) {
										switch (sIype) {
	
										case 'I': {
											// 输入：I
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"I\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + sInput + "</label>");
											break;
										}
										case 'R': {
											// 单选：R
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
													"<label>" + CHECKED + "</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + UNCHECKED + "</label>");
											break;
										}
										case 'M': {
											// 合格确认：M
											if ("1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + CHECKED + "</label>");
											} else if ("-1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + FORBIDDEN + "</label>");
											}
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + NOCARE + "</label>");
											break;
										}
										case 'N': {
											// 签章：N
											if ("1".equals(sInput)) {
												// 确认
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<img src=\"/images/sign/" + pf.getJob_no().toUpperCase() + "\"/>");
														// "<img src=\"images/operator/" + pf.getJob_no().toUpperCase() + "\"/>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											} else if ("-1".equals(sInput)) {
												// 不做
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>"+NOCARE+"</label>");
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
											}
											break;
										}
										case 'X': {
											// 返工：X
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.substring(0, 5).replaceAll("EX", "EN") + ")\\d\\d\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label style=\"color:red;\">发生返工</label>");
											break;
										}
										case 'T': {
											// 综合合格判定：T
											if ("1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"T\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
														"<section locate=\"$1\">合格</section>"
														);
											} else if ("-1".equals(sInput)) {
												specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"T\" position=\"" + processCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<section locate=\"$1\">不合格</section>"
														);
											}
											break;
										}
										}
	
//										// 记录标签在首次输入的返工次数
//										if (!checkedOnId.containsKey(pcid)) {
//											checkedOnId.put(pcid, iRework);
//										}
									}
								}
							}

							// 备注信息
							currentComments = pf.getPcs_comments();
							if (!CommonStringUtil.isEmpty(currentComments)) {
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
								for (String commentskey : jsonPcs_comments.keySet()) {
									if (!allComments.containsKey(commentskey)) {
										allComments.put(commentskey, new TreeMap<String, String>());
									}
									if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
										TreeMap<String, String> tm = allComments.get(commentskey);
										tm.put(pf.getFinish_time() == null ? "Final" : DateUtil.toString(pf.getFinish_time(), "yyyyMMddHHmmss" + tm.size()),
											pf.getOperator_name() + "@" + pf.getProcess_code() + "工位:" + jsonPcs_comments.get(commentskey));
										allComments.put(commentskey, tm);
									}
								}
							}
						}
					}
					// 所在工位有备注 TODO
				} // for 工位

				// 如果是最高的返工，文件中还有本工位没有替换掉的
				if (bNewest && currentProcessCode != null) {

					Matcher mCurrentProcessCode = pCurrentProcessCode.matcher(specify);

					logger.info("sPcs_inputs new:");

					if (mCurrentProcessCode.find()) {

						bReplacedAtPosition = true;

						logger.info("sPcs_inputs new make:");

						hasCurrent = true;

//						String reworkedOnId = ""; TODO
//						if (!checkedOnId.containsKey(pcid)) {
//							checkedOnId.put(pcid, iRework);
//						}

						// 输入：I
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"I\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<input type=\"text\" name=\"$1\" value=\"\"/>");
						if (isLightFix) {
							specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}311\\d{2})\\d{2}\" scope=\"E\" type=\"R\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
									UNCHECKED);
							specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}331\\d{2})\\d{2}\" scope=\"E\" type=\"R\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
									UNCHECKED);
						}
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"R\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								"<input type=\"radio\" name=\"$1\" value=\"$2\"/>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"M\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" checked/><label for=\"$1_0\">不需确认</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"/><label for=\"$1_y\">合格</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"/><label for=\"$1_n\">不合格</label><input type=button class=\"i_switchM\" for=\"$1\" value=\"---\">");
						// 按钮
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"N\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"D\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<input type=\"hidden\" value=\"#date#\"></input>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"T\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<section class=\"i_total\">-</section><input type=\"hidden\" name=\"$1\" value=\"\" class=\"i_total_hidden\">");
					}

					// 线长空格
					if (leaderLineId != null) {

						// 输入：I
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"I\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", // TODO 000 -》line code
								"<input type=\"text\" name=\"$1\" value=\"\"/>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"R\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								"<input type=\"radio\" name=\"$1\" value=\"$3\"/>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"M\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								"<input type=\"checkbox\" name=\"$1\" value=\"$3\"/>");
						// 按钮
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"N\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
								"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"D\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								"<input type=\"hidden\" value=\"#date#\"></input>");
					} else {
						// 如果是最高的返工
					}
				}

				// 本工位有填值，则备注也能填。
				if (hasCurrent || leaderLineId != null) {
					boolean instead = false;

					for (String commentPcid : allComments.keySet()) {
						TreeMap<String, String> commentsOfPcid = allComments.get(commentPcid);
						if (commentsOfPcid != null && commentsOfPcid.size() > 0) {
							String commentsJoined = "<BR>";
							for (String rcdTime : commentsOfPcid.keySet()) {
								commentsJoined += CommonStringUtil.decodeHtmlText(commentsOfPcid.get(rcdTime)) + "<BR>";
							}

							if (isPrepheral && "EC00000".equals(commentPcid)) { // 周边维修显示点检用设备
								PeripheralInfectDeviceMapper pidMapper = conn.getMapper(PeripheralInfectDeviceMapper.class);
								PeripheralInfectDeviceEntity pidCondition = new PeripheralInfectDeviceEntity();
								pidCondition.setMaterial_id(materialId);
								pidCondition.setPosition_id(currentProcessCodeOrg); // process_code => position_id
								String groupedInfectMessage = pidMapper.getGroupedInfectMessage(pidCondition);
								if (!CommonStringUtil.isEmpty(groupedInfectMessage)) {
									commentsJoined = CommonStringUtil.decodeHtmlText(groupedInfectMessage) + commentsJoined;
								}
								instead = true;
							}

							// 备注文本+输入框表示
							specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
									commentsJoined + staticContent(currentProcessCode) + "<textarea name=\"$1\"></textarea>");
							// 备注文本
							specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
									commentsJoined);
						}
					}

					if (isPrepheral && !instead) {
						PeripheralInfectDeviceMapper pidMapper = conn.getMapper(PeripheralInfectDeviceMapper.class);
						PeripheralInfectDeviceEntity pidCondition = new PeripheralInfectDeviceEntity();
						pidCondition.setMaterial_id(materialId);
						// 备注文本+输入框表示
						String groupedInfectMessage = pidMapper.getGroupedInfectMessage(pidCondition);
						if (!CommonStringUtil.isEmpty(groupedInfectMessage)) {
							specify = specify.replaceAll("<pcinput pcid=\"@#EC0000000\" scope=\"E\" type=\"C\" position=\"000\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
									CommonStringUtil.decodeHtmlText(groupedInfectMessage) 
									+ "<textarea name=\"EC00000\"></textarea>");
						}
					}
					// 没有输入过的文本框
					if (leaderLineId != null) {
						specify = specify.replaceAll("<pcinput pcid=\"@#([EL]C\\d{5})\\d{2}\" scope=\"[EL]\" type=\"C\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								 staticContent(currentProcessCode) + "<textarea name=\"$1\"></textarea>");
					} else {
						specify = specify.replaceAll("<pcinput pcid=\"@#(EC\\d{5})\\d{2}\" scope=\"E\" type=\"C\" position=\"(000|" + currentProcessCode + ")\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								 staticContent(currentProcessCode) + "<textarea name=\"$1\"></textarea>");
					}
				} else {
					boolean instead = false;

					// 备注文本表示
					for (String commentPcid : allComments.keySet()) {
						TreeMap<String, String> commentsOfPcid = allComments.get(commentPcid);
						if (commentsOfPcid != null && commentsOfPcid.size() > 0) {
							String commentsJoined = "<BR>";
							for (String rcdTime : commentsOfPcid.keySet()) {
								commentsJoined += CommonStringUtil.decodeHtmlText(commentsOfPcid.get(rcdTime)) + "<BR>";
							}

							if (isPrepheral && "EC00000".equals(commentPcid)) { // 周边维修显示点检用设备
								PeripheralInfectDeviceMapper pidMapper = conn.getMapper(PeripheralInfectDeviceMapper.class);
								PeripheralInfectDeviceEntity pidCondition = new PeripheralInfectDeviceEntity();
								pidCondition.setMaterial_id(materialId);
								pidCondition.setPosition_id(currentProcessCodeOrg);
								String groupedInfectMessage = pidMapper.getGroupedInfectMessage(pidCondition);
								if (!CommonStringUtil.isEmpty(groupedInfectMessage)) {
									commentsJoined = CommonStringUtil.decodeHtmlText(groupedInfectMessage) + commentsJoined;
								}
								instead = true;
							}

							// 备注文本
							specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
									commentsJoined);
						}
					}
					if (isPrepheral && !instead) {
						PeripheralInfectDeviceMapper pidMapper = conn.getMapper(PeripheralInfectDeviceMapper.class);
						PeripheralInfectDeviceEntity pidCondition = new PeripheralInfectDeviceEntity();
						pidCondition.setMaterial_id(materialId);
						// 备注文本+输入框表示
						String groupedInfectMessage = pidMapper.getGroupedInfectMessage(pidCondition);
						if (!CommonStringUtil.isEmpty(groupedInfectMessage)) {
							specify = specify.replaceAll("<pcinput pcid=\"@#EC0000000\" scope=\"E\" type=\"C\" position=\"000\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
									CommonStringUtil.decodeHtmlText(groupedInfectMessage));
						}
					}
				}

				// 线长补填
				if (leaderLineId != null && bNewest) {
					// 输入：I
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"text\" name=\"$1\" value=\"\"/>");
					// process_code = 311 331 小修不单选
					if (isLightFix) {
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}311\\d{2})\\d{2}\" scope=\"E\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								UNCHECKED);
						specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}331\\d{2})\\d{2}\" scope=\"E\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
								UNCHECKED);
					}
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"radio\" name=\"$1\" value=\"$2\"/>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" checked/><label for=\"$1_0\">不需确认</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"/><label for=\"$1_y\">合格</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"/><label for=\"$1_n\">不合格</label><input type=button class=\"i_switchM\" for=\"$1\" value=\"---\">");
					// 按钮
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"D\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"hidden\" value=\"#date#\"></input>");
				}

				// 清除没赋值的标签
				specify = specify.replaceAll("<pcinput.*?/>", "");

				// 不管有没有替换过，都放进临时的Map
				tempMap.put(pcsName, specify);
			} // for 工程检查票文件

			// 如果没有替换掉作业记录，就不算实际返工，但是最高这次算存在，因为最高这次必替换当前工位。
			if (bReplacedAtPosition) {
				factRework++;
				// 放入第factRework次的工程检查票
				String sheetname = "";
				if (factRework != 1) { //iRework != reworkCount
					sheetname = " 第" + (factRework-1) + "次返工";
				}

				factMaxRework = factRework;

				for (String pcsName : tempMap.keySet()) {
					htmlPcses.put(pcsName + sheetname, tempMap.get(pcsName));
					reworkOfPcses.put(pcsName + sheetname, factRework);
				}
			}
		} // for 返工


		for (String reworkOfPcsKey : reworkOfPcses.keySet()) {
			if (factMaxRework == reworkOfPcses.get(reworkOfPcsKey)) {
				htmlPcses.put(reworkOfPcsKey, htmlPcses.get(reworkOfPcsKey) + "<newstatus />");
			}
		}
		// 工位对象
		return htmlPcses;
	}
	/**
	 * 工程单元维修匹配
	 * @param currentProcessCode
	 * @return
	 */
	private static String checkOverAll(String currentProcessCode) {
		if ("500".equals(currentProcessCode)) {
			return "[45]\\d\\d";
		} else if ("400".equals(currentProcessCode)) {
			// return "4\\d\\d";
		} else if ("300".equals(currentProcessCode)) {
			return "3\\d\\d";
		} else if ("200".equals(currentProcessCode)) {
			return "2\\d\\d";
		}
		return currentProcessCode;
	}
	private static String checkOverAllExcel(String currentProcessCode) {
		if ("500".equals(currentProcessCode)) {
			return "5??";
		} else if ("400".equals(currentProcessCode)) {
			// return "4??";
		} else if ("300".equals(currentProcessCode)) {
			return "3??";
		} else if ("200".equals(currentProcessCode)) {
			return "2??";
		}
		return currentProcessCode;
	}

	/** 测试用工程检查票HTML 单页 */
	public static String toHtmlTest(String content, String modelName) {
		if (content == null) {
			return "文件转换失败!";
		}
		String specify = content;
		// 全体对象
		// GI
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<img src=\"/images/pcs/$1\"/>");
		// GS
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"S\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>(修理单号)</label>");
		// GM
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + modelName + "</label>");
		// GC
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>(机身号)</label>");
		// GC
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>(维修等级)</label>");

		// 输入：I
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"I\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
				"<input type=\"text\" name=\"$1\" value=\"\"/ code=\"$2\" class=\"i_act\"><label locate=\"$1\" code=\"$2\" class=\"i_com\"></label>"); // 

		// 单选：R
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"R\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
				" <input type=\"radio\" name=\"$1\" code=\"$2\" value=\"$3\" class=\"i_act\"/><label locate=\"$1\" code=\"$2\" value=\"$3\" class=\"i_com\"></label>");
		// 合格确认：M
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"M\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
				"<section locate=\"$1\" code=\"$2\" value=\"\" class=\"i_com\"></section>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" code=\"$2\" class=\"i_act tag_m\"/><label for=\"$1_0\">不需确认</label>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" code=\"$2\" class=\"i_act tag_m\"/><label for=\"$1_y\">合格</label>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" code=\"$2\" class=\"i_act tag_m\"/><label for=\"$1_n\">不合格</label>");
		// 按钮
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"N\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
				"<section locate=\"$1\" code=\"$2\" value=\"\" class=\"i_com\"></section>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" code=\"$2\" class=\"i_act tag_n\"/><label for=\"$1_0\">未选</label>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" code=\"$2\" class=\"i_act tag_n\"/><label for=\"$1_y\">确认</label>" +
				"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" code=\"$2\" class=\"i_act tag_n\"/><label for=\"$1_n\">不做</label>");

		// 合格总计
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"T\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
				"<section locate=\"$1\" code=\"$2\" class=\"i_total\">合格</section>" + 
				"<input type=\"hidden\" name=\"$1\" value=\"1\" code=\"$2\" class=\"i_total_hidden\"/>");

		// 备注信息
		specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"\\w\" type=\"C\" position=\"(\\d{3})\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
				"<textarea name=\"$1\" code=\"$2\"></textarea><br><textarea name=\"$1_container\" readonly disabled style='resize:none;height:80%;'></textarea>");

		return specify;
	}
	public static Map<String, String> toHtmlBlank(Map<String, String> srcPcses, String modelName) {

		Map<String, String> htmlPcses = new LinkedHashMap<String, String>();

		if (srcPcses == null) return htmlPcses;

		// 对于每张工检票
		for (String pcsName : srcPcses.keySet()) {
			logger.info("pcsName:"+ pcsName);

			String specify = srcPcses.get(pcsName);
			//String orgSpecify = specify;
			// 全体对象
			// GI
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<img src=\"/images/pcs/$1\"/>");
			// GS
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"S\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>(修理单号)</label>");
			// GM
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + modelName + "</label>");
			// GC
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>(机身号)</label>");

			// 输入：I
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
					"<input type=\"text\" name=\"$1\" value=\"\"/>"); // 

			// 单选：R
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
					" <input type=\"radio\" name=\"$1\" value=\"$2\"/>");
			// 合格确认：M
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
					"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\"/><label for=\"$1_0\">不需确认</label>" +
					"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"/><label for=\"$1_y\">合格</label>" +
					"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"/><label for=\"$1_n\">不合格</label>");
			// 按钮
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
					"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"/><label for=\"$1_y\">确认</label>" +
					"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"/><label for=\"$1_n\">不做</label>");

			// 备注信息
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"\\w\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
					"<textarea name=\"$1\"></textarea>");


			// 清除没赋值的标签
			// specify = specify.replaceAll("<pcinput.*?/>", "");

			// 都放进Map
			htmlPcses.put(pcsName, specify);
		} // for 工程检查票文件

	
		// 工位对象
		return htmlPcses;
	}

	public static Map<String, String> toHtml4Fix(Map<String, String> srcPcses, String materialId, String sorcNo,
			String modelName, String serialNo, SqlSession conn) {

		logger.info("getXmlContents for material=" + materialId + " forFix");

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		LeaderPcsInputMapper llDao = conn.getMapper(LeaderPcsInputMapper.class);

		// 取得维修对象返工次数
		int reworkCount;
		// DUMMY 对应
		reworkCount = dao.getReworkCount(materialId);

		logger.info("reworkCount:"+ reworkCount);

		Map<String, String> htmlPcses = new LinkedHashMap<String, String>();
		Map<String, Integer> reworkOfPcses = new LinkedHashMap<String, Integer>();

		if (srcPcses == null) return htmlPcses;

		int factMaxRework = 1;
		// 对于每次返工
		for (int iRework = 0, factRework = 0; iRework <= reworkCount; iRework++) {
			logger.info("iRework:"+ iRework);

			ProductionFeatureEntity condEntity = new ProductionFeatureEntity();
			condEntity.setMaterial_id(materialId);
			condEntity.setRework(iRework);
			// 取得每次返工中作业记录
			List<ProductionFeatureEntity> pfEntities = dao.getProductionPcsOnRework(condEntity);

			// 取得每次返工中线长作业人记录
			List<ProductionFeatureEntity> lpcs = llDao.searchLeaderPcsInput(condEntity);

			// 本次替换掉的工位件数
			boolean bReplacedAtPosition = false;

			Map<String, String> tempMap = new HashMap<String, String>();

			String currentComments = "";

			// 对于每张工检票
			for (String pcsName : srcPcses.keySet()) {
				logger.info("pcsName:"+ pcsName);

				HashMap<String, TreeMap<String, HashMap<String, String>>> allComments = new HashMap<String, TreeMap<String, HashMap<String, String>>>();

				String specify = srcPcses.get(pcsName);
				//String orgSpecify = specify;
				// 全体对象
				// GI
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<img src=\"/images/pcs/$1\"/>");
				// GS
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"S\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + sorcNo + "</label>");
				// GM
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + modelName + "</label>");
				// GC
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + serialNo + "</label>");
				// GR
				specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + serialNo + "</label>");

				// 线长对象
				if (lpcs !=null && lpcs.size() > 0) {
					for (ProductionFeatureEntity pf : lpcs) {
						// 工位代码
						String line_id = pf.getLine_id();
						String jam_code = pf.getJam_code();

						String sPcs_inputs = pf.getPcs_inputs();
						if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
							// 解析输入值
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

							// 当前工位最新返工
							logger.info("Leader sPcs_inputs edit:"+ sPcs_inputs);

							// 读入既有值的输入
							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);

								if (!"".equals(sInput)) {
									switch (sIype) {

									case 'I': {
										// 输入：I
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<input type=\"text\" name=\"$1\" value=\"" + sInput + "\" jam_code=\"" + jam_code + "\"/>");
										break;
									}
									case 'R': {
										// 单选：R
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
												"<input type=\"radio\" name=\"$1\" value=\"$2\" checked jam_code=\"" + jam_code + "\"/>");
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
												"<input type=\"radio\" name=\"$1\" value=\"$2\" jam_code=\"" + jam_code + "\"/>");
										break;
									}
									case 'M': {
										// 合格确认：M
										if ("1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										} else if ("-1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										} else {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										}
										break;
									}
									case 'N': {
										if ("1".equals(sInput)) {
											// 按钮
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不做</label>");
										} else if ("-1".equals(sInput)) {
											// 按钮
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不做</label>");
										}
									break;
									}
									}
								}
							}

							// 备注信息
							currentComments = pf.getPcs_comments();
							if (!CommonStringUtil.isEmpty(currentComments)) {
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
								for (String commentskey : jsonPcs_comments.keySet()) {
									if (!allComments.containsKey(commentskey)) {
										allComments.put(commentskey, new TreeMap<String, HashMap<String, String>>());
									}
									if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
										TreeMap<String, HashMap<String, String>> tm = allComments.get(commentskey);
										HashMap<String, String> commentBean = new HashMap<String, String>();
										commentBean.put("operator_name", pf.getOperator_name());
										commentBean.put("line_id", line_id);
										commentBean.put("finish_time", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
										commentBean.put("content", jsonPcs_comments.get(commentskey));
										commentBean.put("jam_code", jam_code);
										tm.put(DateUtil.toString(pf.getFinish_time(), "yyyyMMdd9999"+tm.size()), commentBean);
										allComments.put(commentskey, tm);
									}
								}
							}
						}
					}
				}

				Set<String> hasProcess = new HashSet<String>();
				for (ProductionFeatureEntity pf : pfEntities) {
					// 工位代码
					String process_code = pf.getProcess_code();
					if ("612".equals(process_code)) process_code = "611";
					logger.info("process_code"+ process_code);

					String jam_code = getJam_codeByPf(pf);
					Pattern pProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"E\" type=\"\\w\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
					Matcher mProcessCode = pProcessCode.matcher(specify);

					if (hasProcess.contains(process_code) || mProcessCode.find()) {

						hasProcess.add(process_code);
						// 如果有本工位的标签，进行替换
						bReplacedAtPosition = true;

						String sPcs_inputs = pf.getPcs_inputs();
						if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
							// 解析输入值
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

							// 当前工位最新返工
							logger.info("sPcs_inputs edit:"+ sPcs_inputs);

							// 读入既有值的输入
							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);

								if (!"".equals(sInput)) {
									switch (sIype) {

									case 'I': {
										// 输入：I
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"I\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<input type=\"text\" name=\"$1\" value=\"" + sInput + "\" jam_code=\"" + jam_code + "\"/>");
										break;
									}
									case 'R': {
										// 单选：R
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
												"<input type=\"radio\" name=\"$1\" value=\"$2\" checked jam_code=\"" + jam_code + "\"/>");
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
												"<input type=\"radio\" name=\"$1\" value=\"$2\" jam_code=\"" + jam_code + "\"/>");
										break;
									}
									case 'M': {
										// 合格确认：M
										if ("1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										} else if ("-1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										} else {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"0\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_0\">不需确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">合格</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不合格</label>");
										}
										break;
									}
									case 'N': {
										if ("1".equals(sInput)) {
											// 按钮
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不做</label>");
										} else if ("-1".equals(sInput)) {
											// 按钮
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\" jam_code=\"" + jam_code + "\"/><label for=\"$1_y\">确认</label>" +
													"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\" checked jam_code=\"" + jam_code + "\"/><label for=\"$1_n\">不做</label>");
										}
										break;
									}
									case 'X': {
										// 返工：X
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.substring(0, 5).replaceAll("EX", "EN") + ")\\d\\d\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label style=\"color:red;\">发生返工</label>");
										break;
									}
									}

//										// 记录标签在首次输入的返工次数
//										if (!checkedOnId.containsKey(pcid)) {
//											checkedOnId.put(pcid, iRework);
//										}
								}
							}


							// 备注信息
							currentComments = pf.getPcs_comments();
							if (!CommonStringUtil.isEmpty(currentComments)) {
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
								for (String commentskey : jsonPcs_comments.keySet()) {
									if (!allComments.containsKey(commentskey)) {
										allComments.put(commentskey, new TreeMap<String, HashMap<String, String>>());
									}
									if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
										TreeMap<String, HashMap<String, String>> tm = allComments.get(commentskey);
										HashMap<String, String> commentBean = new HashMap<String, String>();
										commentBean.put("operator_name", pf.getOperator_name());
										commentBean.put("line_id", pf.getLine_id());
										commentBean.put("finish_time", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
										commentBean.put("content", jsonPcs_comments.get(commentskey));
										commentBean.put("jam_code", jam_code);
										tm.put(pf.getFinish_time() == null ? "Final" : DateUtil.toString(pf.getFinish_time(), "yyyyMMddHHmmss" + tm.size()),
												commentBean);
									}
								}
							}
						}
					}
					// 所在工位有备注 TODO
				} // for 工位

				// 备注文本表示
				for (String commentPcid : allComments.keySet()) {
					TreeMap<String, HashMap<String, String>> commentsOfPcid = allComments.get(commentPcid);
					if (commentsOfPcid != null && commentsOfPcid.size() > 0) {
						String commentsJoined = "<BR>";
						for (String rcdTime : commentsOfPcid.keySet()) {
							HashMap<String, String> commentBean = commentsOfPcid.get(rcdTime);
							commentsJoined += "<label>" + commentBean.get("operator_name") + "在" + commentBean.get("finish_time") +"：</label>" +
									"<textarea name=\"$1\" class='s' jam_code=\""+ commentBean.get("jam_code") + "\">" + 
									commentBean.get("content") + "</textarea>" + "<BR>";
						}

						// 备注文本
						specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								commentsJoined);
					}
				}

				// 清除没赋值的标签
				specify = specify.replaceAll("<pcinput.*?/>", "");

				// 不管有没有替换过，都放进临时的Map
				tempMap.put(pcsName, specify);
			} // for 工程检查票文件

			// 如果没有替换掉作业记录，就不算实际返工，但是最高这次算存在，因为最高这次必替换当前工位。
			if (bReplacedAtPosition) {
				factRework++;
				// 放入第factRework次的工程检查票
				String sheetname = "";
				if (factRework != 1) { //iRework != reworkCount
					sheetname = " 第" + factRework + "次返工";
				}

				factMaxRework = factRework;

				for (String pcsName : tempMap.keySet()) {
					htmlPcses.put(pcsName + sheetname, tempMap.get(pcsName));
					reworkOfPcses.put(pcsName + sheetname, factRework);
				}
			}
		} // for 返工


		for (String reworkOfPcsKey : reworkOfPcses.keySet()) {
			if (factMaxRework == reworkOfPcses.get(reworkOfPcsKey)) {
				htmlPcses.put(reworkOfPcsKey, htmlPcses.get(reworkOfPcsKey) + "<newstatus />");
			}
		}
		// 工位对象
		return htmlPcses;
	}

	/**
	 * @param pf 00 000 0 00
	 * @return
	 */
	private static String getJam_codeByPf(ProductionFeatureEntity pf) throws NumberFormatException {
		int iPosition_id = Integer.parseInt(pf.getPosition_id());

		int hJamCode = (iPosition_id << 3) + (pf.getRework() << 2) + pf.getPace();
		String ret = Integer.toString(hJamCode, 20);
		return pf.getSection_id().substring(pf.getSection_id().length() - 1) + "_" + ret;
	}

	public static ProductionFeatureEntity putJam_codeToPf(String jam_code) {
		String[] jam_codes = jam_code.split("\\_");

		ProductionFeatureEntity ret = new ProductionFeatureEntity();
		ret.setSection_id(jam_codes[0]);

		int iKem = Integer.parseInt(jam_codes[1], 20);
		int iPosition_id = iKem >> 3;
		iKem -= iPosition_id << 3;
		int iRework = iKem >> 2;
		int iPace = iKem - (iRework << 2);

		ret.setPosition_id(""+iPosition_id);
		ret.setRework(iRework);
		ret.setPace(iPace);

		return ret;
	}

	private static String staticContent(String currentProcessCode) {
		String comment = null;
		if ("441".equals(currentProcessCode) || "440".equals(currentProcessCode)
				|| "442".equals(currentProcessCode)
				|| "443".equals(currentProcessCode)
				|| "481".equals(currentProcessCode)) comment = COMMENT_451_461;
		if ("450".equals(currentProcessCode)) comment = COMMENT_451_461;
		if ("341".equals(currentProcessCode)) comment = COMMENT_341;
		if (comment == null) {
			return "";
		}
		return "<textarea class=\"i_frequent\">" + comment + "</textarea>";
	}

	/** 保存用型号Key */
	private static String normalize(String modelName) {
		return modelName.replaceAll(" ", "").replace("（", "(").replace("）", ")");
	}

	/** 取得归档模板 **/
	public static Map<String, String> getXlsContents(String lineName, String modelName, String categoryName,
			String material_id, boolean lightFix, boolean checkHistory, SqlSession conn) {
		logger.info("getXlsContents for line=" + lineName + " modelName=" + modelName);
		checkHistory = true;

		Map<String, String> ret = new HashMap<String, String>();
		if (modelName == null || lineName == null || !fileBinder.containsKey(lineName)) {
			return null;
		}

		if (categoryName == null && conn !=null) {
			ModelMapper dao = conn.getMapper(ModelMapper.class);
			categoryName = normalize(dao.getCategoryNameByModelName(RvsUtils.regfy(modelName)));
		}

		Map<String, Map<String, String>> lineBinder = fileBinder.get(lineName);
		List<PcsRequestEntity> lM = null;
		List<PcsRequestEntity> lH = null;
		if (material_id != null) {
			PcsRequestMapper prMapper = conn.getMapper(PcsRequestMapper.class);
			lM = prMapper.checkMaterialAssignAsOld(material_id);

			// 如果参照历史,取得指定机型的修改履历
			if (checkHistory) {
				lH= prMapper.getFixHistoryOfMaterial(material_id);
			}
		}

		for (String pace : lineBinder.keySet()) {
			// 如果因改废订而指定了
			String filename = null;
			if (material_id != null) {
				String folderTypesKey = getFolderTypesKey(lineName, pace, folderTypes);
				for (PcsRequestEntity pre : lM) {
					Integer lineType = pre.getLine_type();
					if ((""+lineType).equals(folderTypesKey)) {
						filename = PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\_request\\" + trimZero(pre.getPcs_request_key())
								+ "\\old.xls";
						break;
					}
				}
				if (checkHistory && filename == null) {
					for (PcsRequestEntity pre : lH) {
						Integer lineType = pre.getLine_type();
						if ((""+lineType).equals(folderTypesKey)) {
							filename = PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\_request\\" + trimZero(pre.getPcs_request_key())
									+ "\\old.xls";
							if (!new File(filename).exists()) {
								filename = null;
							}
							break;
						}
					}
				}
			}

			if (filename == null) {
				Map<String, String> files = lineBinder.get(pace);

				String modelNameBFile = normalize(modelName);

				if (files.containsKey(modelNameBFile)) {
					// 按型号名取得文件
					filename = files.get(modelNameBFile);
				} else {
					ModelEntity modelEntity = ReverseResolution.getModelEntityByName(modelName, conn);

					// 按目录对应分类取得 TODO
					String packkind = getPackFromLineName(pace);
					String getPackName = getPackName(packkind, modelEntity);;

					if (!CommonStringUtil.isEmpty(getPackName)) {
						filename = files.get(getPackName);
					}
					if (filename == null) {
						String norCategoryName = normalize(modelEntity.getCategory_name());
						if (modelEntity.getCategory_name() != null && files.containsKey(norCategoryName)) {
							// 按机种名取得文件
							filename = files.get(norCategoryName);
						}
					}
				}

				if (filename != null) {
					if (lightFix) {
						String lightFilename = filename.replaceAll("\\\\xml", "\\\\_lightmedium\\\\xml");
						File lxmlfile = new File(lightFilename);
						if (lxmlfile.exists()) {
							if (lxmlfile.isFile()) {
								filename = lightFilename;
							}
						}
					}

					ret.put(lineName + pace, filename.replaceAll("\\\\xml\\\\", "\\\\excel\\\\").replaceAll(ext, xls_ext));
				}
			} else {
				ret.put(lineName + pace, filename);
			}
		}

		return ret;
	}

	/**
	 * @param srcPcses 相关工程检查票文件 key 显示名 value 文件本地路径
	 * @param materialId 维修对象ID
	 * @param sorcNo
	 * @param modelName
	 * @param serialNo
	 * @param currentProcessCode 当前页面工位代码
	 * @param folderPath 
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static String toPdf(Map<String, String> srcPcses, String materialId, String sorcNo, String modelName,
			String serialNo, String level, String currentProcessCode, String folderPath, SqlSession conn) throws IOException {
		logger.info("getXmlContents for material=" + materialId + " currentProcessCode=" + currentProcessCode);

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		LeaderPcsInputMapper llDao = conn.getMapper(LeaderPcsInputMapper.class);

		// 取得维修对象返工次数
		int reworkCount = dao.getReworkCount(materialId);
		logger.info("reworkCount:"+ reworkCount);

		if (srcPcses == null) return null;

		File material = new File(folderPath);
		if (!material.exists()) {
			material.mkdirs();
		}

		List<String> hasBlank = new ArrayList<String>();

		boolean isLightFix = "9".equals(level.substring(0, 1)); 
		boolean isPrepheral = "5".equals(level.substring(0, 1)); 

		// 对于每次返工
		for (int iRework = 0, factRework = 0; iRework <= reworkCount; iRework++) {
			logger.info("iRework:"+ iRework);

			ProductionFeatureEntity condEntity = new ProductionFeatureEntity();
			condEntity.setMaterial_id(materialId);
			condEntity.setRework(iRework);
			// 取得每次返工中作业记录
			List<ProductionFeatureEntity> pfEntities = dao.getProductionPcsOnRework(condEntity);
			List<ProductionFeatureEntity> lpcs = llDao.searchLeaderPcsInput(condEntity);

			if (pfEntities.size() == 0 && lpcs.size() == 00) {
				continue;
			}

			// 本次替换掉的工位件数
			boolean bReplacedAtPosition = false;

			Map<String, String> tempMap = new LinkedHashMap<String, String>();

			// Map<String, String> allComments = new HashMap<String, String>();
			String currentComments = "";

			// 对于每张工检票
			for (String pcsName : srcPcses.keySet()) {
				logger.info("pcsName:"+ pcsName);
				// if (!pcsName.startsWith("分解工程S 连接座")) continue;

				String specify = srcPcses.get(pcsName);
				// 复制一份模板
				String cacheFilename =  pcsName + new Date().getTime() + ".xls";
				Date today = new Date();
				String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM") + "\\" + cacheFilename;
				FileUtils.copyFile(new File(specify), new File(cachePath));
//				XlsUtil xlsspecify = new XlsUtil(specify);
//				xlsspecify.SaveAs(cachePath);
//				xlsspecify.Release();

				XlsUtil xls = new XlsUtil(cachePath);
				xls.SelectActiveSheet();

				// 查找多工位标签
				Map<String, List<String>> multiPosMap = new HashMap<String, List<String>>();
				checkMultiPosMap(xls, multiPosMap);

				// 全体对象
				// GI
				//xls.Replace("@#GI???????", target);
				// GS
				xls.Replace("@#GS???????", sorcNo);
				// GM
				xls.Replace("@#GM???????", modelName);
				// GC
				xls.Replace("@#GC???????", serialNo);
				// GR
				xls.Replace("@#GR???????", CodeListUtils.getValue("material_level", level));

				Map<String, String> lineComments = new HashMap<String, String>();

				String additionalStr = null;
				if (isLightFix 
						&& "总组工程".equals(pcsName)) {
					// 小修理流程
					MaterialProcessAssignService mpas = new MaterialProcessAssignService();
					String lightFixStr = mpas.getLightFixesByMaterial(materialId, conn);

					additionalStr = (lightFixStr == null ? "" : "小修理内容为：" + lightFixStr + "\n");

					Dispatch cell = xls.Locate("@#EC???????");
					String FoundValue = null;
					String firstFoundValue = null;
					if (cell != null) FoundValue = Dispatch.get(cell, "Value").toString();
					if (FoundValue != null && !FoundValue.equals(firstFoundValue)) { // TODO while exchange
						if (firstFoundValue == null) firstFoundValue = FoundValue;

						xls.SetValue(cell, 
								FoundValue.replace("@#EC", additionalStr + "\n" + "@#EC"));

						cell = xls.Locate("@#EC???????");
						if (cell == null) {
							FoundValue = null;
						} else {
							FoundValue = Dispatch.get(cell, "Value").toString();
						}
					}
				}

				if (isPrepheral 
						&& "检查卡".equals(pcsName)) { // 周边维修显示点检用设备
					PeripheralInfectDeviceMapper pidMapper = conn.getMapper(PeripheralInfectDeviceMapper.class);
					PeripheralInfectDeviceEntity pidCondition = new PeripheralInfectDeviceEntity();
					pidCondition.setMaterial_id(materialId);

					if (xls.Hit("@#EC0000000")) {
						String groupedInfectMessage = pidMapper.getGroupedInfectMessage(pidCondition);
						if (!CommonStringUtil.isEmpty(groupedInfectMessage)) {
							xls.Replace("@#EC0000000", groupedInfectMessage + "\n@#EC0000000");
						}
					}
				}

				// 线长对象
				if (lpcs !=null && lpcs.size() > 0) {
					for (ProductionFeatureEntity pf : lpcs) {
						// 工位代码
						String line_id = pf.getLine_id();
						logger.info("line_id"+ line_id);

						String process_code = ""; // TODO 强制判断
						String process_code2 = null; // TODO 强制判断
						String process_code3 = null; // TODO 强制判断
						if("00000000012".equals(line_id)) {
							process_code = "2??";
						} else if("00000000013".equals(line_id)) {
							process_code = "3??";
						} else if("00000000014".equals(line_id)) {
							process_code = "4??";  // TODO
							process_code2 = "5??";
							process_code3 = "8??";
						}

						// 判断有本工号的标签
						if (xls.Hit("@#E?" + process_code + "????") 
								|| (process_code2 != null && (xls.Hit("@#E?" + process_code2 + "????"))) 
								|| (process_code3 != null && (xls.Hit("@#E?" + process_code3 + "????"))) 
								|| xls.Hit("@#L????????")) {

							String sPcs_inputs = pf.getPcs_inputs();
							if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
								// 解析输入值
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

								// 当前工位最新返工
								logger.info("sPcs_inputs edit:"+ sPcs_inputs);

								// 读入既有值的输入
								for (String pcid : jsonPcs_inputs.keySet()) {
									// 输入值
									String sInput = jsonPcs_inputs.get(pcid);
									// 类别
									char sIype = pcid.charAt(1);

									if (!"".equals(sInput)) {
										switch (sIype) {
										
										case 'I': {
											// 输入：I
											if (!CommonStringUtil.isEmpty(sInput)) {
												xls.Replace("@#"+pcid+"??", sInput);
											}
											break;
										}
										case 'R': {
											// 单选：R
											if (!CommonStringUtil.isEmpty(sInput)) {
												xls.Replace("@#"+pcid+sInput, CHECKED);
												xls.Replace("@#"+pcid+"??", UNCHECKED);
											}
											break;
										}
										case 'M': {
											// 合格确认：M
											if ("1".equals(sInput)) {
												xls.Replace("@#"+pcid+"??", CHECKED);
											} else if ("-1".equals(sInput)) {
												Dispatch cell = xls.Locate("@#"+pcid+"??");
												if (cell != null) {
													xls.SetCellBackGroundColor(cell, "255");
													Dispatch font = xls.GetCellFont(cell);
													Dispatch.put(font, "Color", "16777215"); // FFFFFF
												}
												xls.Replace("@#"+pcid+"??", FORBIDDEN);
											}
											xls.Replace("@#"+pcid+"??", NOCARE);
											break;
										}
										case 'N': {
											// 签章：N
											if ("1".equals(sInput)) {
												// 按钮
												Dispatch cell = xls.Locate("@#"+pcid+"??");
												if (cell != null) {
													xls.sign(PathConsts.BASE_PATH + PathConsts.IMAGES + "\\sign\\" + pf.getJob_no().toUpperCase(),
															cell);
												}
												xls.Replace("@#"+pcid+"??", ""); // sign
												xls.Replace("@#"+pcid.replaceAll("EN", "ED").replaceAll("LN", "LD")+"??", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
											} else if ("-1".equals(sInput)) { 
												// 不做
												// if 611
												if (pcid.indexOf("N611") >= 0) {
													Dispatch cell = xls.Locate("@#"+pcid+"??");
													if (cell != null)  xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
													cell = xls.Locate("@#"+pcid.replaceAll("EN", "ED").replaceAll("LN", "LD")+"??");
													if (cell != null) xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
												} else {
													xls.Replace("@#"+pcid+"??", NOCARE);
												}
												xls.Replace("@#"+pcid.replaceAll("EN", "ED").replaceAll("LN", "LD")+"??", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
											}
											break;
										}
										}

										// 删除复工位标签
										if (multiPosMap.containsKey(pcid)) {
											for (String multiId : multiPosMap.get(pcid)) {
												xls.Replace("@#"+multiId+"??", "");
												if (sIype == 'N') {
													xls.Replace("@#"+multiId.substring(0,1)+"D" + multiId.substring(2) + "??", "");
												}
											}
										}
									}
								}

								currentComments = pf.getPcs_comments();
								if (!CommonStringUtil.isEmpty(currentComments)) {
									@SuppressWarnings("unchecked")
									Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
									for (String commentskey : jsonPcs_comments.keySet()) {
										if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
//											xls.Replace("@#"+commentskey+"??", pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey) +
//													"\n@#"+commentskey+"00");
											if (lineComments.containsKey(commentskey)){
												lineComments.put(commentskey, lineComments.get(commentskey) + "\n" + pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey));
											} else {
												lineComments.put(commentskey, pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey));
											}
										}
//										if (!allComments.containsKey(commentskey)) {
//											allComments.put(commentskey, "");
//											// GC
//											xls.Replace("@#"+commentskey+"??", allComments.get(commentskey) + "\n" + pf.getOperator_name() +
//													"： @#"+commentskey+"??");
//										}
									}
								}
							}
						}
					}
				}

				for (ProductionFeatureEntity pf : pfEntities) {
					// 工位号
					String process_code = pf.getProcess_code();
					if ("612".equals(process_code)) process_code = "611";
					logger.info("process_code"+ process_code);

					String checkedOverAllProcessCode = checkOverAllExcel(process_code);

					// 判断有本工号的标签
//					if (xls.Hit("@#E?" + process_code + "????") ||
//							("400".equals(process_code) && pcsName.startsWith("总")) ) {
					
					if (xls.Hit("@#E?" + process_code + "????") || (!checkedOverAllProcessCode.equals(process_code))) {
	
						// 如果有本工位的标签，进行替换
						bReplacedAtPosition = true;
	
						String sPcs_inputs = pf.getPcs_inputs();
						if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
							// 解析输入值
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);
	
							logger.info("sPcs_inputs edit:"+ sPcs_inputs);
	
							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);
	
								switch (sIype) {
	
								case 'I': {
									if (!CommonStringUtil.isEmpty(sInput)) {
										// 输入：I
										xls.Replace("@#"+pcid+"??", sInput);
									}
									break;
								}
								case 'R': {
									if (!CommonStringUtil.isEmpty(sInput)) {
										// 单选：R
										xls.Replace("@#"+pcid+sInput, CHECKED);
										xls.Replace("@#"+pcid+"??", UNCHECKED);
									}
									break;
								}
								case 'M': {
									// 合格确认：M
									if ("1".equals(sInput)) {
										xls.Replace("@#"+pcid+"??", CHECKED);
									} else if ("-1".equals(sInput)) {
										xls.Replace("@#"+pcid+"??", FORBIDDEN);
									}
									xls.Replace("@#"+pcid+"??", NOCARE);
									break;
								}
								case 'N': {
									// 签章：N
									if ("1".equals(sInput)) {
										// 按钮
										Dispatch cell = xls.Locate("@#"+pcid+"??");
										if (cell != null) {
											xls.sign(PathConsts.BASE_PATH + PathConsts.IMAGES + "\\sign\\" + pf.getJob_no().toUpperCase(),
													cell);
										}
										xls.Replace("@#"+pcid+"??", ""); // sign
										xls.Replace("@#"+pcid.replaceAll("EN", "ED")+"??", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
									} else if ("-1".equals(sInput)) {
										// 不做
										// if 611
										if (pcid.indexOf("N611") >= 0) {
											Dispatch cell = xls.Locate("@#"+pcid+"??");
											if (cell != null) xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
											cell = xls.Locate("@#"+pcid.replaceAll("EN", "ED")+"??");
											if (cell != null) xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
										}
										xls.Replace("@#"+pcid+"??", NOCARE);
										xls.Replace("@#"+pcid.replaceAll("EN", "ED")+"??", DateUtil.toString(pf.getFinish_time(), "MM-dd"));
									}
									break;
								}
								case 'X': {
									// 返工：X
									String Xkey = "@#"+pcid.substring(0, 5).replaceAll("EX", "EN")+"????";
									Dispatch cell = xls.Locate(Xkey);
									String FoundValue = null;
									if (cell != null) FoundValue = Dispatch.get(cell, "Value").toString();
									while (FoundValue != null) {
										xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
										xls.SetValue(cell, "发生返工");
										cell = xls.Locate(Xkey);
										if (cell == null) {
											FoundValue = null;
										} else {
											FoundValue = Dispatch.get(cell, "Value").toString();
										}
									}

									break;
								}
								case 'T': {
									// 合格确认：M
									if ("1".equals(sInput)) {
										xls.Replace("@#"+pcid+"??", QUALIFIED); // sign
									} else if ("0".equals(sInput)) {
										xls.Replace("@#"+pcid+"??", NOCARE); // sign
									} else if ("-1".equals(sInput)) {
										Dispatch cell = xls.Locate("@#"+pcid+"??");
										if (cell != null) {
											xls.SetValue(cell, UNQUALIFIED);
											xls.SetCellBackGroundColor(cell, "255"); 
											Dispatch font = xls.GetCellFont(cell);
											Dispatch.put(font, "Color", "16777215"); // FFFFFF
										}
									}
									break;
								}
								}

								// 删除复工位标签
								if (multiPosMap.containsKey(pcid)) {
									for (String multiId : multiPosMap.get(pcid)) {
										xls.Replace("@#"+multiId+"??", "");
										if (sIype == 'N') {
											xls.Replace("@#"+multiId.substring(0,1)+"D" + multiId.substring(2) + "??", "");
										}
									}
								}
							}
	
							// 备注信息
							currentComments = pf.getPcs_comments();
							if (!CommonStringUtil.isEmpty(currentComments)) {
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
								for (String commentskey : jsonPcs_comments.keySet()) {
//									if (!allComments.containsKey(commentskey)) {
//										allComments.put(commentskey, "");
										// GC
									if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
										xls.Replace("@#"+commentskey+"??", jsonPcs_comments.get(commentskey) + "\n@#"+commentskey+"00");
									}
//									}
								}
							}
						}
					}
				}
				for (String commentskey : lineComments.keySet()) {
					String comment = lineComments.get(commentskey);
					xls.Replace("@#"+commentskey+"??", comment +
					"\n@#"+commentskey+"00");
				}

				// 清除没赋值的标签 并且 变灰 14.7.8 edit
				Dispatch cell = xls.Locate("@#?????????");
				String FoundValue = null;
				if (cell != null) FoundValue = Dispatch.get(cell, "Value").toString();
				// String compareValue = "ZZZ$$$Z$";
				while (FoundValue != null) {
					if (FoundValue.indexOf("@#EC") < 0 && FoundValue.indexOf("@#LC") < 0 && FoundValue.indexOf("@#GI") < 0) {
						xls.SetCellBackGroundColor(cell, "12566463"); // BFBFBF;
						if (FoundValue.indexOf("@#EN611") < 0 && FoundValue.indexOf("@#EI611") < 0 && FoundValue.indexOf("@#ER611") < 0) {
							hasBlank.add(FoundValue);
						}
					}
					if (FoundValue.replaceAll("@#\\w{2}\\d{7}", "").equals(FoundValue)) {
						xls.SetValue(cell, "<<<格式错误，无法处理！>>>");
					} else {
						xls.SetValue(cell, FoundValue.replaceAll("@#\\w{2}\\d{7}", ""));
					}
					// xls.ReplaceInCell(cell, "@#?????????", "");
					cell = xls.Locate("@#?????????");
					if (cell == null) {
						FoundValue = null;
					} else {
						FoundValue = Dispatch.get(cell, "Value").toString();
					}
				}
				// xls.Replace("@#?????????", "");

				xls.SaveCloseExcel(false);
				tempMap.put(pcsName, cachePath);
			}

			// 如果没有替换掉作业记录，就不算实际返工。
			if (bReplacedAtPosition) {
				// 放入第factRework次的工程检查票
				String sheetname = "";
				if (factRework != 0) {
					sheetname = " 第" + factRework + "次返工";
				}
				factRework++;

				for (String pcsName : tempMap.keySet()) {
					String cachePath = tempMap.get(pcsName);
					XlsUtil xls = new XlsUtil(cachePath);
					xls.SaveAsPdf(folderPath + "\\" + pcsName + sheetname);
				}
			} else {
				// 不是的就不另存为PDF了
//				for (String pcsName : tempMap.keySet()) {
//					XlsUtil xls = tempMap.get(pcsName);
//					xls.Release();
//				}
			}
		}
		if (hasBlank.size() > 0) {
			logger.info(sorcNo + "有未填项目。返工" + reworkCount);
			if (reworkCount == 0)
				return CommonStringUtil.joinBy(";", hasBlank.toArray(new String[hasBlank.size()]));
		}

		// 工位对象
		return "";
	}

	public static void checkMultiPosMap(XlsUtil xls,
			Map<String, List<String>> multiPosMap) {
		Dispatch cell = xls.Locate("@#?????????@#?????????");
		String FoundValue = null;
		String firstFoundValue = null;
		if (cell != null) {
			FoundValue = Dispatch.get(cell, "Value").toString();
			firstFoundValue = FoundValue;
		}

		Pattern pTags = Pattern.compile("(@#[EL][NIR]\\d{7}){2,}");

		while (FoundValue != null) {

			Matcher mTags = pTags.matcher(FoundValue);
			while(mTags.find()) {
				String tags = mTags.group();
				int idx = 0;
				List<String> tagList = new ArrayList<String>();
				while (idx < tags.length()) {
					tagList.add(tags.substring(idx+=2, idx+=7));
					idx+=2;
				}
				for (int i = 0; i< tagList.size(); i++) {
					List<String> tagListOther = new ArrayList<String>();
					tagListOther.addAll(tagList);
					tagListOther.remove(i);
					multiPosMap.put(tagList.get(i), tagListOther);
				}
			}

			cell = xls.LocateNext(cell);
			if (cell == null) {
				FoundValue = null;
			} else {
				FoundValue = Dispatch.get(cell, "Value").toString();
				if (firstFoundValue.equals(FoundValue)) break;
			}
		}
	}
	/**
	 * @param srcPcses 相关工程检查票文件 key 显示名 value 文件本地路径
	 * @param materialId 维修对象ID
	 * @param sorcNo
	 * @param modelName
	 * @param serialNo
	 * @param currentProcessCode 当前页面工位代码
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static String toTemplatesXls(Map<String, String> srcPcses, String modelName, String cachePath, SqlSession conn) throws IOException {

		if (srcPcses == null) return null;

		Map<String, String> tempMap = new LinkedHashMap<String, String>();

		// 对于每张工检票
		for (String pcsName : srcPcses.keySet()) {
			logger.info("pcsName:"+ pcsName);

			String specify = srcPcses.get(pcsName);
			// 复制一份模板
			String cacheFile = cachePath + pcsName + ".xls";
			FileUtils.copyFile(new File(specify), new File(cacheFile));

			XlsUtil xls = new XlsUtil(cacheFile);
			xls.SelectActiveSheet();

			// GM
			xls.Replace("@#GM???????", modelName);
			xls.Replace("@#EI???????", "　　　");
			xls.Replace("@#ER???????", "　");
			xls.Replace("@#LI???????", "　　　");
			xls.Replace("@#LR???????", "　");
			xls.Replace("@#EM???????", "　不需确认　合格　不合格");


			// 清除没赋值的标签
			xls.Replace("@#?????????", "");

			xls.SaveCloseExcel(false);
			tempMap.put(pcsName, cacheFile);
		}


		// 工位对象
		return "temp";
	}

	/**
	 * 独立工位（先端预制）用工程检查票显示
	 * @param fileTempl
	 * @param model_name
	 * @param serial_no
	 * @param process_code
	 * @param line_id
	 * @param conn
	 * @return
	 */
	public static Map<String, String> toHtmlSnout(Map<String, String> srcPcses, String modelName,
			String serial_no, String currentProcessCode, String leaderLineId, SqlSession conn) {


		SoloProductionFeatureMapper dao = conn.getMapper(SoloProductionFeatureMapper.class);
		LeaderPcsInputMapper llDao = conn.getMapper(LeaderPcsInputMapper.class);

		// 取得维修对象返工次数
		logger.info("reworkCount:1");

		Map<String, String> htmlPcses = new LinkedHashMap<String, String>();

		if (srcPcses == null) return htmlPcses;

		Pattern pCurrentProcessCode = Pattern.compile("<pcinput.*?scope=\"E\".*?position=\""+currentProcessCode+"\".*?/>");

//		Map<String, Integer> checkedOnId = new HashMap<String, Integer>(); TODO

		// 对于每次返工
		logger.info("iRework:0");

		SoloProductionFeatureEntity condition = new SoloProductionFeatureEntity();
		condition.setSerial_no(serial_no);

		// 取得每次返工中作业记录
		List<SoloProductionFeatureEntity> pfEntities = dao.searchSoloProductionFeature(condition);
		ProductionFeatureEntity condEntity = new ProductionFeatureEntity();
		condEntity.setSerial_no(serial_no);

		List<ProductionFeatureEntity> lpcs = llDao.searchLeaderPcsInput(condEntity);

		// 取得线长作业人记录

		// 本次替换掉的工位件数
		boolean bReplacedAtPosition = false;

		// 是最新一次返工
		boolean bNewest = true;

		Map<String, String> tempMap = new HashMap<String, String>();

		Map<String, TreeMap<String, String>> allComments = new HashMap<String, TreeMap<String, String>>();
		String currentComments = "";

		// 对于每张工检票
		for (String pcsName : srcPcses.keySet()) {
			logger.info("pcsName:"+ pcsName);

			String specify = srcPcses.get(pcsName);
			// 全体对象
			// GI
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<img src=\"/images/pcs/$1\"/>");
			// GM
			specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"G\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", "<label>" + modelName + "</label>");

			// 本作业对象中有替换成功的项目
			boolean hasCurrent = false;

			// 线长对象
			if (lpcs !=null && lpcs.size() > 0) {
				for (ProductionFeatureEntity pf : lpcs) {
					// 工位代码
					String line_id = pf.getLine_id();
					logger.info("line_id"+ line_id);

					String process_code = ""; // TODO 强制判断
					if("00000000012".equals(line_id)) {
						process_code = "2\\d{2}";
					} else if("00000000013".equals(line_id)) {
						process_code = "3\\d{2}";
					} else if("00000000014".equals(line_id)) {
						process_code = "(4|5)\\d{2}";
					}

					Pattern pProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"E\" type=\"\\w\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
					Matcher mProcessCode = pProcessCode.matcher(specify);

					Pattern plProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"L\" type=\"\\w\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
					Matcher mlProcessCode = plProcessCode.matcher(specify);

					if (mProcessCode.find() || mlProcessCode.find()) {

						String sPcs_inputs = pf.getPcs_inputs();
						if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
							// 解析输入值
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);


							// 当前工位最新返工
							logger.info("Leader sPcs_inputs edit:"+ sPcs_inputs);

							// 可输入
							if(leaderLineId != null) hasCurrent = true;
							// 读入既有值的输入
							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);

								if (!"".equals(sInput)) {
									switch (sIype) {

									case 'I': {
										// 输入：I
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"I\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + sInput + "</label>");
										break;
									}
									case 'R': {
										// 单选：R
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
												"<label>" + CHECKED + "</label>");
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"R\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + UNCHECKED + "</label>");
										break;
									}
									case 'M': {
										// 合格确认：M
										if ("1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + CHECKED + "</label>");
										} else if ("-1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + FORBIDDEN + "</label>");
										}
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"M\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + NOCARE + "</label>");
										break;
									}
									case 'N': {
										// 签章：N
										if ("1".equals(sInput)) {
											// 确认
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<img src=\"/images/sign/" + pf.getJob_no() + "\"/>");
													// "<img src=\"images/operator/" + pf.getJob_no() + "\"/>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED").replaceAll("LN", "LD") + ")\\d\\d\" scope=\"\\w\" type=\"D\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										} else if ("-1".equals(sInput)) {
											// 不做
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"\\w\" type=\"N\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>"+NOCARE+"</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED").replaceAll("LN", "LD") + ")\\d\\d\" scope=\"\\w\" type=\"D\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										}
										break;
									}
									}
								}
							}

							// 备注信息
							currentComments = pf.getPcs_comments();
							if (!CommonStringUtil.isEmpty(currentComments)) {
								@SuppressWarnings("unchecked")
								Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
								for (String commentskey : jsonPcs_comments.keySet()) {
									if (!allComments.containsKey(commentskey)) {
										allComments.put(commentskey, new TreeMap<String, String>());
									}
									if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
										TreeMap<String, String> tm = allComments.get(commentskey);
										tm.put(DateUtil.toString(pf.getFinish_time(), "yyyyMMdd9999"+tm.size()), pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey));
										allComments.put(commentskey, tm);
									}
								}
							}
						}
					}
				}
			}

			for (SoloProductionFeatureEntity pf : pfEntities) {
				// 工位代码
				String process_code = "301";
				logger.info("process_code"+ process_code);

				boolean isCurrent = process_code.equals(currentProcessCode);

				Pattern pProcessCode = Pattern.compile("<pcinput pcid=\"@#(\\w{2}\\d{7})\" scope=\"E\" type=\"\\w\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>");
				Matcher mProcessCode = pProcessCode.matcher(specify);

				if (mProcessCode.find()) {
					// 如果有本工位的标签，进行替换
					bReplacedAtPosition = true;

					String sPcs_inputs = pf.getPcs_inputs();
					if (!CommonStringUtil.isEmpty(sPcs_inputs)) {
						// 解析输入值
						@SuppressWarnings("unchecked")
						Map<String, String> jsonPcs_inputs = JSON.decode(sPcs_inputs, Map.class);

						if (bNewest && isCurrent) {
							// 当前工位最新返工
							logger.info("sPcs_inputs edit:"+ sPcs_inputs);

							// 可输入
							hasCurrent = true;
							// 读入既有值的输入
							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);

								if (!"".equals(sInput)) {
									switch (sIype) {

									case 'I': {
										// 输入：I
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"I\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + sInput + "</label>");
										break;
									}
									case 'R': {
										// 单选：R
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
												"<label>" + CHECKED + "</label>");
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + UNCHECKED + "</label>");
										break;
									}
									case 'M': {
										// 合格确认：M
										if ("1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + CHECKED + "</label>");
										} else if ("-1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + FORBIDDEN + "</label>");
										}
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + NOCARE + "</label>");
										break;
									}
									case 'N': {
										// 签章：N
										if ("1".equals(sInput)) {
											// 确认
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<img src=\"/images/sign/" + pf.getJob_no() + "\"/>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										} else if ("-1".equals(sInput)) {
											// 不做
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>"+NOCARE+"</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										}
										break;
									}
									}
								} else {
								}
							}
						} else {
							// 非当前工位 or 非最新返工
							logger.info("sPcs_inputs view:"+ sPcs_inputs);

							for (String pcid : jsonPcs_inputs.keySet()) {
								// 输入值
								String sInput = jsonPcs_inputs.get(pcid);
								// 类别
								char sIype = pcid.charAt(1);

								if (!"".equals(sInput)) {
									switch (sIype) {

									case 'I': {
										// 输入：I
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"I\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + sInput + "</label>");
										break;
									}
									case 'R': {
										// 单选：R
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"("+sInput+")\"/>",
												"<label>" + CHECKED + "</label>");
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"R\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + UNCHECKED + "</label>");
										break;
									}
									case 'M': {
										// 合格确认：M
										if ("1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + CHECKED + "</label>");
										} else if ("-1".equals(sInput)) {
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + FORBIDDEN + "</label>");
										}
										specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"M\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
												"<label>" + NOCARE + "</label>");
										break;
									}
									case 'N': {
										// 签章：N
										if ("1".equals(sInput)) {
											// 确认
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<img src=\"/images/sign/" + pf.getJob_no().toUpperCase() + "\"/>");
													// "<img src=\"images/operator/" + pf.getJob_no() + "\"/>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										} else if ("-1".equals(sInput)) {
											// 不做
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid + ")\\d\\d\" scope=\"E\" type=\"N\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>"+NOCARE+"</label>");
											specify = specify.replaceAll("<pcinput pcid=\"@#(" + pcid.replaceAll("EN", "ED") + ")\\d\\d\" scope=\"E\" type=\"D\" position=\"" + process_code + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
													"<label>" + DateUtil.toString(pf.getFinish_time(), "MM-dd") + "</label>");
										}
										break;
									}
									}

//										// 记录标签在首次输入的返工次数
//										if (!checkedOnId.containsKey(pcid)) {
//											checkedOnId.put(pcid, iRework);
//										}
								}
							}
						}

						// 备注信息
						currentComments = pf.getPcs_comments();
						if (!CommonStringUtil.isEmpty(currentComments)) {
							@SuppressWarnings("unchecked")
							Map<String, String> jsonPcs_comments = JSON.decode(currentComments, Map.class);
							for (String commentskey : jsonPcs_comments.keySet()) {
								if (!allComments.containsKey(commentskey)) {
									allComments.put(commentskey, new TreeMap<String, String>());
								}
								if (!CommonStringUtil.isEmpty(jsonPcs_comments.get(commentskey))) {
									TreeMap<String, String> tm = allComments.get(commentskey);
									tm.put(DateUtil.toString(pf.getFinish_time(), "yyyyMMdd9999"+tm.size()), pf.getOperator_name() + ":" + jsonPcs_comments.get(commentskey));
									allComments.put(commentskey, tm);
								}
							}
						}
					}
				}
			} // for 工位

			// 如果是最高的返工，文件中还有本工位没有替换掉的
			if (bNewest) {

				Matcher mCurrentProcessCode = pCurrentProcessCode.matcher(specify);

				logger.info("sPcs_inputs new:");

				if (mCurrentProcessCode.find()) {

					bReplacedAtPosition = true;

					logger.info("sPcs_inputs new make:");

					hasCurrent = true;

					// 输入：I
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"I\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"text\" name=\"$1\" value=\"\"/>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"R\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"radio\" name=\"$1\" value=\"$2\"/>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"M\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"checkbox\" name=\"$1\" value=\"$2\"/>");
					// 按钮
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"N\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"E\" type=\"D\" position=\"" + currentProcessCode + "\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"hidden\" value=\"#date#\"></input>");
				}

				// 线长空格
				if (leaderLineId != null) {

					// 输入：I
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"I\" position=\"000\" name=\"\\d{2}\" sub=\"\\d{2}\"/>", // TODO 000 -》line code
							"<input type=\"text\" name=\"$1\" value=\"\"/>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"R\" position=\"000\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"radio\" name=\"$1\" value=\"$2\"/>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"M\" position=\"000\" name=\"\\d{2}\" sub=\"(\\d{2})\"/>",
							"<input type=\"checkbox\" name=\"$1\" value=\"$2\"/>");
					// 按钮
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"N\" position=\"000\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"radio\" name=\"$1\" id=\"$1_0\" value=\"\" checked></input><label for=\"$1_0\">撤消</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_y\" value=\"1\"></input><label for=\"$1_y\">确认</label>" +
							"<input type=\"radio\" name=\"$1\" id=\"$1_n\" value=\"-1\"></input><label for=\"$1_n\">不做</label>");
					specify = specify.replaceAll("<pcinput pcid=\"@#(\\w{2}\\d{5})\\d{2}\" scope=\"L\" type=\"D\" position=\"000\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
							"<input type=\"hidden\" value=\"#date#\"></input>");
				}
			}

			// 本工位有填值，则备注也能填。
			if (hasCurrent || leaderLineId != null) {
				for (String commentPcid : allComments.keySet()) {
					TreeMap<String, String> commentsOfPcid = allComments.get(commentPcid);
					if (commentsOfPcid != null && commentsOfPcid.size() > 0) {
						String commentsJoined = "<BR>";
						for (String rcdTime : commentsOfPcid.keySet()) {
							commentsJoined += CommonStringUtil.decodeHtmlText(commentsOfPcid.get(rcdTime)) + "<BR>";
						}

						// 备注文本+输入框表示
						specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								commentsJoined + staticContent(currentProcessCode) + "<textarea name=\"$1\"></textarea>");
					}
				}
				// 没有输入过的文本框
				specify = specify.replaceAll("<pcinput pcid=\"@#(EC\\d{5})\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
						 staticContent(currentProcessCode) +"<textarea name=\"$1\"></textarea>");
			} else {
				// 备注文本表示
				for (String commentPcid : allComments.keySet()) {
					TreeMap<String, String> commentsOfPcid = allComments.get(commentPcid);
					if (commentsOfPcid != null && commentsOfPcid.size() > 0) {
						String commentsJoined = "<BR>";
						for (String rcdTime : commentsOfPcid.keySet()) {
							commentsJoined += CommonStringUtil.decodeHtmlText(commentsOfPcid.get(rcdTime)) + "<BR>";
						}

						// 备注文本
						specify = specify.replaceAll("<pcinput pcid=\"@#("+commentPcid+")\\d{2}\" scope=\"E\" type=\"C\" position=\"\\d{3}\" name=\"\\d{2}\" sub=\"\\d{2}\"/>",
								commentsJoined);
					}
				}
			}

			// 清除没赋值的标签
			specify = specify.replaceAll("<pcinput.*?/>", "");

			// 不管有没有替换过，都放进临时的Map
			tempMap.put(pcsName, specify);
		} // for 工程检查票文件

		// 如果没有替换掉作业记录，就不算实际返工，但是最高这次算存在，因为最高这次必替换当前工位。
		if (bReplacedAtPosition) {
			// 放入第factRework次的工程检查票
			String sheetname = "";

			String newstatus = "";
			newstatus = "<newstatus />";

			for (String pcsName : tempMap.keySet()) {
				htmlPcses.put(pcsName + sheetname, tempMap.get(pcsName) + newstatus);
			}
		}

		// 线长对象 TODO
		// 工位对象
		return htmlPcses;
	}
	public static Map<String, String> getFolderTypes() {
		return folderTypes;
	}

//	public static String getFolderTypesReverse(String name) {
//		return folderTypes;
//	}
	// 得到指定目录指定型号对应的文件
	public static String getFileName(String path, ModelEntity model,
			SqlSessionManager conn) {
		String modelName = model.getName();
//		String cModel_id = ReverseResolution.getModelByName(modelName, conn);
//		if (model.getModel_id().equals(cModel_id)) {
//			return getFileName(path, modelName);
//		} else {
			ModelMapper mMapper = conn.getMapper(ModelMapper.class);
			model = mMapper.getModelByID(model.getModel_id());
			return getFileName(path, model);
//		}
	}
	public static String getFileName(String path, ModelEntity modelEntity) {
		String[] tpath = path.split("\n");
		String firstStep = "";
		String secondStep = "";

		// TODO ??
//		char[] what = {(char)63};
//		modelName = modelName.replaceAll(new String(what), " ");
		if (tpath.length == 2) {
			firstStep = tpath[0];
			secondStep = tpath[1];
		} else {
			firstStep = path;
		}

		Map<String, String> filePareMap = fileBinder.get(firstStep).get(secondStep);

		String fileModelsOnCode = getFileModelsOnCode(path, filePareMap, modelEntity);
		return fileModelsOnCode;
//		if (tpath.length == 2) {
//			return firstStep + "\\" + secondStep + "\\" + getTemplateFileName(filePareMap.get(modelName));
//		} else {
//			return firstStep + "\\" + getTemplateFileName(filePareMap.get(modelName));
//		}
	}
	public static String getFilePath(String path) {
		String[] tpath = path.split("\n");
		String firstStep = "";
		String secondStep = "";

		if (tpath.length == 2) {
			firstStep = tpath[0];
			secondStep = tpath[1];
		} else {
			firstStep = path;
		}

		if (tpath.length == 2) {
			return firstStep + "\\" + secondStep + "\\";
		} else {
			return firstStep + "\\";
		}
	}
	public static Map<String, String> getFileComparedGroup(String path) {
		String[] tpath = path.split("\n");
		String firstStep = "";
		String secondStep = "";

		if (tpath.length == 2) {
			firstStep = tpath[0];
			secondStep = tpath[1];
		} else {
			firstStep = path;
		}

		Map<String, String> mpa = fileBinder.get(firstStep).get(secondStep);
		return mpa;
	}
	private static String getTemplateFileName(String fullpath) {
		if (fullpath == null) return null;
		return fullpath.replaceAll("^.*\\\\([^\\\\]*).html", "$1");
	}
	public static void convert2Page(String savePath, String xlsFilename) {
		File tFile = new File(xlsFilename);
		if (!tFile.exists()) {
			return;
		}
		String xmlFilename = xlsFilename.replaceAll("\\.xls", ".xml");
		String htmlFilename = xlsFilename.replaceAll("\\.xls", ext);
		XlsUtil template = null;
		try {
			template = new XlsUtil(xlsFilename);
			template.SaveAsXml(xmlFilename);
			ReadPcs.coverent(xmlFilename, htmlFilename);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// template.CloseExcel(false);
		} finally {
			if (template != null) {
				template.Release();
			}
			template = null;
		}
	}
	public static String getFileModelsOnCode(String path,
			Map<String, String> files, ModelEntity modelEntity) {
		String[] tpath = path.split("\n");
		String secondStep = "";

		if (tpath.length == 2) {
			secondStep = tpath[1];
		} else {
			secondStep = path;
		}

		String filename = null;
		String modelNameBFile = normalize(modelEntity.getName());

		if (files.containsKey(modelNameBFile)) {
			// 按型号名取得文件
			filename = files.get(modelNameBFile);
		} else {

			// 按目录对应分类取得 TODO
			String packkind = getPackFromLineName(secondStep);
			String getPackName = getPackName(packkind, modelEntity);;

			if (!CommonStringUtil.isEmpty(getPackName)) {
				filename = files.get(getPackName);
			}
			if (filename == null) {
				String norCategoryName = normalize(modelEntity.getCategory_name());
				if (modelEntity.getCategory_name() != null && files.containsKey(norCategoryName)) {
					// 按机种名取得文件
					filename = files.get(norCategoryName);
				}
			}
		}

		return filename;
	}

	private static String trimZero(String pcs_request_key) {
		if (pcs_request_key == null)
			return null;
		else return pcs_request_key.replaceAll("^0*([^0].*)$", "$1");
	}

	public static String getFolderTypesKey(String lineName, String pace,
			Map<String, String> folderTypes) {
		for (String key : folderTypes.keySet()) {
			if (CommonStringUtil.joinBy("\n", lineName, pace).equals(folderTypes.get(key))) {
				return key;
			}
		}
		return null;
	}

}

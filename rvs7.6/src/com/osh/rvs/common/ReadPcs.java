package com.osh.rvs.common;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadPcs {

	private static Map<String, String> styleclasses = new HashMap<String, String>();

	private static Map<String, String> styleidmap = new HashMap<String, String>();

	// ss:WrapText="1"
	private static final String SS_WRAPTEXT_1 = "white-space:normal; word-break:break-all; overflow:hidden; ";

	public static void main(String[] args) {
//		@SuppressWarnings("unused")
//		String filename = args[0];
//		String fromfile = ".\\" +
//				filename +
//				".xml";
//		String tofile = ".\\" +
//				filename.toUpperCase() +
//				".html";
		String na = "new";
		String fromfile = "D:\\rvs\\PcsTemplates\\_request\\15045\\"+na+".xml";

		String tofile = "D:\\rvs\\PcsTemplates\\_request\\15045\\"+na+".html";
		
//		fromfile = "D:\\rvs\\PcsTemplates\\excelxml\\总组工程\\"+na+".xml";
		coverent(fromfile, tofile);
	}

	public static void coverent(String fromfile, String tofile) {
		// Defines a factory API
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		String tablecontents = "";
		try {
			// DocumentBuilderFactoryからDocumentBuilderインスタンスを取得
			DocumentBuilder db = dbf.newDocumentBuilder();

			//File file = new File(basedir + "EL座-粗镜及电子细镜（150、180、VISERA除外）.xml");
			File file = new File(fromfile);
			// XML DOMを作成
			Document doc = db.parse(file);

			// 外形
			NodeList nl = doc.getElementsByTagName("Styles");
			if (nl==null || nl.getLength() == 0) return;
			Element styles = (Element) nl.item(0);
			NodeList stylelist = styles.getElementsByTagName("Style");
			boolean findDefault = false;
			for (int i =0;i<stylelist.getLength();i++) {
				Node style = stylelist.item(i);
				String styleid = style.getAttributes().getNamedItem("ss:ID").getNodeValue();
				if (!findDefault) {
					if ("Default".equalsIgnoreCase(styleid)) {
						// 生成Default css
//						String defaultTd = getcss(style);
//						NodeList alignment = ((Element)style).getElementsByTagName("Alignment");
//						System.out.println(						alignment.item(0).getAttributes().getNamedItem("ss:Horizontal"));
//						System.out.println(						alignment.item(0).getAttributes().getNamedItem("ss:Vertical"));
//						System.out.println(						alignment.item(0).getAttributes().getNamedItem("ss:WrapText"));

						NodeList borders = ((Element)style).getElementsByTagName("Borders");

						findDefault = true;
					}
				} else {
					NodeList alignment = ((Element)style).getElementsByTagName("Alignment");
					NodeList borders = ((Element)style).getElementsByTagName("Borders");
					NodeList numberFormat = ((Element)style).getElementsByTagName("NumberFormat");
					styleidmap.put(styleid, getAlignmentCss(alignment) + getBorderCss(borders) + getNumberFormatCss(numberFormat));
				}
			}

			// 位置 + 内容
			NodeList tnl = doc.getElementsByTagName("Worksheet");
			if (tnl==null || tnl.getLength() == 0) return;
			int wsLength = tnl.getLength();
			if (wsLength > 1) { // 确定都只有1页有效
				wsLength = 1;
			}
			for (int i =0;i<wsLength;i++) { // sheet的循环
				Node worksheet = tnl.item(i);
				String sheetname = worksheet.getAttributes().getNamedItem("ss:Name").getNodeValue();
				NodeList tables = ((Element) worksheet).getElementsByTagName("Table");
				if (tables==null || tables.getLength() == 0) continue;
				Element table = (Element) tables.item(0);
				int sheetrows = getAttributeIntValue(table.getAttributes().getNamedItem("ss:ExpandedRowCount"));
				int sheetcols = getAttributeIntValue(table.getAttributes().getNamedItem("ss:ExpandedColumnCount"));

				NodeList columns = table.getElementsByTagName("Column");
				//列的宽度
				float[] columnswidth = new float[sheetcols];
				//列宽度没有付给td 默认是付给了
				boolean[] columnswidthnotsetted = new boolean[sheetcols];

				int columnswidthcount = 0;
				// Default Width
				for (int j =0;j<columns.getLength();j++) { // 列的循环
					Element columsg = (Element)columns.item(j);
					int ispan = 1;
					ispan += getAttributeIntValue(columsg.getAttributes().getNamedItem("ss:Span"));
					int ssindex = getAttributeIntValue(columsg.getAttributes().getNamedItem("ss:Index"));
					if (ssindex != 0) columnswidthcount = ssindex - 1;
					float width = getAttributeFloatValue(columsg.getAttributes().getNamedItem("ss:Width"));
					for (int k=0;k<ispan;k++,columnswidthcount++) {
						columnswidth[columnswidthcount] = width;
						if (width > 0) {
							columnswidthnotsetted[columnswidthcount] = true;
						}
					}
				} // 列的循环

				NodeList rows = table.getElementsByTagName("Row");
				// 每一列的行剩余合并数
				int[] columnspannedStatic = new int[sheetcols];
				// 需要维持Excel xml和Html5一致的空行数
				int fixTrs = 0;
				for (int irow =0;irow<rows.getLength();irow++) { // 行的循环
					String trcontents = "";
					boolean linespan = true;
					Integer irowspan = null;

					Element row = (Element)rows.item(irow);
					int trheight = getAttributeIntValue(row.getAttributes().getNamedItem("ss:Height"));

					// ss:Span --> 纯空行
					Node ssSpan = row.getAttributes().getNamedItem("ss:Span");
					if (ssSpan != null) {
						int iSpanCount = getAttributeIntValue(ssSpan);
						for (int iSpan = 0; iSpan <= iSpanCount; iSpan++) {
							tablecontents += "<tr/>";
							
							for (int irowcellExact = 0; irowcellExact < columnspannedStatic.length; irowcellExact++) {
								if (columnspannedStatic[irowcellExact] < 0) {
									// 之后被横向合并+纵向合并的单元格
									columnspannedStatic[irowcellExact]++;
								} else {
									// 如果是开始合并行的下方行
									columnspannedStatic[irowcellExact]--;
								}
							}
							if (fixTrs > 0) {
								fixTrs--;
							}
						}
						continue;
					}

					int irowcellExact = 0;
					NodeList rowcells = row.getElementsByTagName("Cell");
					int[] columnspanned = columnspannedStatic.clone();
					try {
					// tr border?
					trcontents+="<tr>";
					//System.out.println("row:>>"+irow+">>cells"+rowcells.getLength());

					for (int irowcell = 0; irowcell<rowcells.getLength(); irowcell++, irowcellExact++) {  // 单元格的循环
//						getRowCell(columnspannedTest);
						Element rowcell = (Element)rowcells.item(irowcell);

						if (irowcellExact >= sheetcols) break;
						if (columnspanned[irowcellExact] < 0) { // 这是不正常的
							// 如果是被横向合并+纵向合并的单元格，就简单过去
							columnspanned[irowcellExact]++;
							//columnspanned[irowcellExact] = - columnspanned[irowcellExact] - 1;
						} else if (columnspanned[irowcellExact] > 0) {
							// 有被合并就不算是全行跨列了 -> TODO 有被合并也可能是全行跨列
							linespan = false;
//							try{
							while (columnspanned[irowcellExact] != 0) {
								if (columnspanned[irowcellExact] < 0) {
									// 之后被横向合并+纵向合并的单元格
									columnspanned[irowcellExact]++;
								} else {
									// 如果是开始合并行的下方行
									columnspanned[irowcellExact]--;
								}
								irowcellExact++;
							}
//							}
//							catch(ArrayIndexOutOfBoundsException e) {
//								System.out.println("catch" + irowcellExact);
//							} finally {
//								
//							}
						} else {
						}

						int mergeAcross = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:MergeAcross"));
						int mergeDown = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:MergeDown"));
						// 如果全格子同样跨列值，需要调整
						if (linespan) {
							if (irowspan != null && irowspan < mergeDown) {
								linespan = false;
							}
							if (irowspan == null || irowspan > mergeDown) 
								irowspan = mergeDown;
						}

						int index = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:Index")) - 1;

						// 如果指定了列数，则补上到指定列的td。
						if (index > 0) {
							while (irowcellExact < index) {
								if (columnspanned[irowcellExact] < 0) {
									linespan = false;
									// 之后被横向合并+纵向合并的单元格
									columnspanned[irowcellExact]++;
								} else if (columnspanned[irowcellExact] > 0) {
									linespan = false;
									// 如果是开始合并行的下方行
									columnspanned[irowcellExact]--;
								} else {
									trcontents+="<td></td>";
								}
								irowcellExact++;
							}
						}

						// 取得文本内容
						String cellText = "";
						NodeList data = rowcell.getElementsByTagName("Data");
						for (int idatum = 0;idatum < data.getLength();idatum++) {
							Element textdata = (Element) data.item(idatum);
							// Data ss:Type="DateTime" TODO??
							cellText += decodeHtmlText(textdata.getTextContent());
						}
						data = rowcell.getElementsByTagName("ss:Data");
						for (int idatum = 0;idatum < data.getLength();idatum++) {
							Element textdata = (Element) data.item(idatum);
							cellText += decodeHtmlText(textdata.getTextContent());
						}

						// 解析标识符
						cellText = transTag(cellText);

						// 生成td
						if (mergeAcross>0 || mergeDown >0) { // 如果存在跨行列
							columnspanned[irowcellExact] = mergeDown;
							for (int iAcross = 1; iAcross <= mergeAcross; iAcross++) {
//								if (irowcellExact < columnspanned.length - 1)
									columnspanned[++irowcellExact] = - mergeDown;
//								else {
//									System.out.println("catch" + irowcellExact);
//								}
								//columnspanned[irowcellExact + iAcross] = - mergeDown;
							}
							String html = "";
							String css = getcss(rowcell);
							if (css.contains(" NBk")) {
								cellText = cellText.replaceAll("^\\-(\\d*)$", "($1)");
								css.replaceAll(" NBk", "");
							}
							html+="<td " + css;
							if (mergeAcross > 0) {
								html+="colspan='"+ (mergeAcross+1) +"' ";
							}
							if (mergeDown > 0) {
								html+="rowspan='"+ (mergeDown+1) +"' ";
							}
							if (css.contains(" IT")) {
								html+=		"><span>" + cellText + "</span></td>";
							} else {
								html+=		">" + cellText + "</td>";
							}

							trcontents+=html;
						} else {
							String sWidth = "";
							if (columnswidthnotsetted[irowcellExact] && mergeAcross == 0) {
								sWidth = " style=\"width:" + columnswidth[irowcellExact] + "px;\"";
								columnswidthnotsetted[irowcellExact] = false;
							}
							String css = getcss(rowcell);
							if (css.contains(" NBk")) {
								cellText = cellText.replaceAll("^\\-(\\d*)$", "($1)");
								//css.replaceAll(" NBk", "");
							}
							if (css.contains(" IT")) {
								trcontents+="<td " + css + sWidth + "><span>" + cellText + "</span></td>";
							} else {
								trcontents+="<td " + css + sWidth + ">" + cellText + "</td>";
							}
						}
					}  // 单元格的循环

					trcontents+="</tr>";
					} catch (Exception e) {
						
					}
					
					if (fixTrs > 0) {
						// 如果没有顶替的行,补上
						if (!"<tr></tr>".equals(trcontents)) {
							for (int ifilltr = 0; ifilltr < fixTrs; ifilltr++) {
								for (int icols=0; icols < columnspannedStatic.length;icols++){
									if (columnspannedStatic[icols] < 0) {
										columnspannedStatic[icols]++;
									} else if (columnspannedStatic[icols] > 0) {
										columnspannedStatic[icols]--;
									} else {
									}
								}
							}
							trcontents = "<tr p_t="+fixTrs+" /><tr>";
							// 重新画
							irowcellExact = 0;
							columnspanned = columnspannedStatic.clone();

							for (int irowcell = 0; irowcell<rowcells.getLength(); irowcell++, irowcellExact++) {  // 单元格的循环
//									getRowCell(columnspannedTest);
								Element rowcell = (Element)rowcells.item(irowcell);

								if (irowcellExact >= sheetcols) break;
								if (columnspanned[irowcellExact] < 0) { // 这是不正常的
									// 如果是被横向合并+纵向合并的单元格，就简单过去
									columnspanned[irowcellExact]++;
									//columnspanned[irowcellExact] = - columnspanned[irowcellExact] - 1;
								} else if (columnspanned[irowcellExact] > 0) {
									// 有被合并就不算是全行跨列了 -> TODO 有被合并也可能是全行跨列
									linespan = false;
//										try{
									while (columnspanned[irowcellExact] != 0) {
										if (columnspanned[irowcellExact] < 0) {
											// 之后被横向合并+纵向合并的单元格
											columnspanned[irowcellExact]++;
										} else {
											// 如果是开始合并行的下方行
											columnspanned[irowcellExact]--;
										}
										irowcellExact++;
									}
//										}
//										catch(ArrayIndexOutOfBoundsException e) {
//											System.out.println("catch" + irowcellExact);
//										} finally {
//											
//										}
								} else {
								}

								int mergeAcross = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:MergeAcross"));
								int mergeDown = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:MergeDown"));
								// 如果全格子同样跨列值，需要调整
								if (linespan) {
									if (irowspan != null && irowspan < mergeDown) {
										linespan = false;
									}
									if (irowspan == null || irowspan > mergeDown) 
										irowspan = mergeDown;
								}

								int index = getAttributeIntValue(rowcell.getAttributes().getNamedItem("ss:Index")) - 1;

								// 如果指定了列数，则补上到指定列的td。
								if (index > 0) {
									while (irowcellExact < index) {
										if (columnspanned[irowcellExact] < 0) {
											linespan = false;
											// 之后被横向合并+纵向合并的单元格
											columnspanned[irowcellExact]++;
										} else if (columnspanned[irowcellExact] > 0) {
											linespan = false;
											// 如果是开始合并行的下方行
											columnspanned[irowcellExact]--;
										} else {
											trcontents+="<td></td>";
										}
										irowcellExact++;
									}
								}

								// 取得文本内容
								String cellText = "";
								NodeList data = rowcell.getElementsByTagName("Data");
								for (int idatum = 0;idatum < data.getLength();idatum++) {
									Element textdata = (Element) data.item(idatum);
									// Data ss:Type="DateTime" TODO??
									cellText += decodeHtmlText(textdata.getTextContent());
								}
								data = rowcell.getElementsByTagName("ss:Data");
								for (int idatum = 0;idatum < data.getLength();idatum++) {
									Element textdata = (Element) data.item(idatum);
									cellText += decodeHtmlText(textdata.getTextContent());
								}

								// 解析标识符
								cellText = transTag(cellText);

								// 生成td
								if (mergeAcross>0 || mergeDown >0) { // 如果存在跨行列
									columnspanned[irowcellExact] = mergeDown;
									for (int iAcross = 1; iAcross <= mergeAcross; iAcross++) {
										columnspanned[++irowcellExact] = - mergeDown;
										//columnspanned[irowcellExact + iAcross] = - mergeDown;
									}
									String html = "";
									String css = getcss(rowcell);
									if (css.contains(" NBk")) {
										cellText = cellText.replaceAll("^\\-(\\d*)$", "($1)");
										css.replaceAll(" NBk", "");
									}
									html+="<td " + css;
									if (mergeAcross > 0) {
										html+="colspan='"+ (mergeAcross+1) +"' ";
									}
									if (mergeDown > 0) {
										html+="rowspan='"+ (mergeDown+1) +"' ";
									}
									if (css.contains(" IT")) {
										html+=		"><span>" + cellText + "</span></td>";
									} else {
										html+=		">" + cellText + "</td>";
									}

									trcontents+=html;
								} else {
									String sWidth = "";
									if (columnswidthnotsetted[irowcellExact] && mergeAcross == 0) {
										sWidth = " style=\"width:" + columnswidth[irowcellExact] + "px;\"";
										columnswidthnotsetted[irowcellExact] = false;
									}
									String css = getcss(rowcell);
									if (css.contains(" NBk")) {
										cellText = cellText.replaceAll("^\\-(\\d*)$", "($1)");
										//css.replaceAll(" NBk", "");
									}
									if (css.contains(" IT")) {
										trcontents+="<td " + css + sWidth + "><span>" + cellText + "</span></td>";
									} else {
										trcontents+="<td " + css + sWidth + ">" + cellText + "</td>";
									}
								}
							}  // 单元格的循环
							
							trcontents += "</tr>";

							fixTrs = 0;
						} else {
							// 否则顶替
							fixTrs--;
						}
					}

					if (linespan && irowspan != null && irowspan != 0) {
						// 全行有跨列时,为了对应HTML,加<tr/>
						//trcontents = trcontents.replaceAll("linespan='(\\d+)'", "rowspan='$1'");
						fixTrs = irowspan;
					}
					tablecontents += trcontents;
					//System.out.println(irow + "Line:"); //arraystring(columnspanned )
					for (int iremain = irowcellExact; iremain < sheetcols; iremain++) {
						if (columnspanned[iremain] > 0)
							columnspanned[iremain]--;
					}
					columnspannedStatic = columnspanned.clone();
				}  // 行的循环

				tablecontents = tablecontents.replaceAll("  ", " ").replaceAll(" >", ">").replaceAll(" ' ", "' ");
			}
			//System.out.println(style.getAttributes().getNamedItem("ss:ID").getNodeValue());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 生成模板文件
		File f = new File(tofile);

		BufferedWriter output = null;
		try {
			if(!f.exists()) {
				try {
					f.createNewFile();
				} catch (Exception e) {
					System.out.println(tofile);
					throw e;
				}
			}

			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			output.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			output.write("<style>td{font-size:12px;}.HC{text-align:center}.HL{text-align:left}.HR{text-align:right}");
			output.write(".VT{valign:top}.VC{valign:middle}.VB{valign:bottom}");
			output.write(".WT{white-space:normal; word-break:break-all; overflow:hidden;}.IT{width:1em;}.IT span{width:1em;letter-spacing: 1em;}");

			String styleHtml = "";
			for (String stylekey : styleclasses.keySet()) {
				styleHtml += "." + stylekey + "{" + styleclasses.get(stylekey) + "}";
			}
			output.write(styleHtml);
			output.write("</style></head><table style=\"border-collapse:collapse;\">");
			output.write(tablecontents);
			output.write("</table></html>");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			output = null;
			f = null;
		}
	}
//	private static String toS(int[] columnspannedStatic) {
//		String ret = "[";
//		// TODO Auto-generated method stub
//		for (int s : columnspannedStatic) {
//			ret += (s +",");
//		}
//		ret +="]";
//		return ret;
//	}

	// 解析标识符
	private static String transTag(String cellText) {
		cellText = cellText.replaceAll("@#([ELG])([IRMNDCST])(\\d{3})(\\d{2})(\\d{2})"
				, "<pcinput pcid=\"$0\" scope=\"$1\" type=\"$2\" position=\"$3\" name=\"$4\" sub=\"$5\"/>");
		return cellText;
	}

	private static int getAttributeIntValue(Node attribute) {
		if (attribute == null) return 0;
		try {
			return Integer.parseInt(attribute.getNodeValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static float getAttributeFloatValue(Node attribute) {
		if (attribute == null) return 0;
		try {
			return Float.parseFloat(attribute.getNodeValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static String getAttributeStringValue(Node attribute) {
		if (attribute == null) return null;
		return attribute.getNodeValue();
	}

	private static String getAlignmentCss(NodeList alignment) {
		String ret="";
		if (alignment != null && alignment.getLength() > 0) {
			NamedNodeMap alignmentattributes = alignment.item(0).getAttributes();
			if (alignmentattributes.getNamedItem("ss:Horizontal") != null) {
				// Left, Center, Right, Fill
				String ss_Horizontal = alignmentattributes.getNamedItem("ss:Horizontal").getNodeValue();
				if ("Center".equals(ss_Horizontal) || "Right".equals(ss_Horizontal)) // "Left".equals(ss_Horizontal) ||默认
					ret += "H" + ss_Horizontal.substring(0, 1) + " ";
				if ("Fill".equals(ss_Horizontal))
					ret += "HC ";
			}
			if (alignmentattributes.getNamedItem("ss:Vertical") != null) {
				String ss_Vertical = alignmentattributes.getNamedItem("ss:Vertical").getNodeValue();
				if (!"Center".equals(ss_Vertical))// "Center".equals(ss_Vertical) ||默认
					ret += "V" + ss_Vertical.substring(0, 1) + " ";
			}
			if (alignmentattributes.getNamedItem("ss:WrapText") != null && !"0".equals(alignmentattributes.getNamedItem("ss:WrapText").getNodeValue())) {
				ret += "WT ";
			}
			if (alignmentattributes.getNamedItem("ss:VerticalText") != null
					&& "1".equals(alignmentattributes.getNamedItem("ss:VerticalText").getNodeValue()))  {// VerticalText
				ret += "IT ";
			}
		}
		return ret;
	}

	private static String getNumberFormatCss(NodeList numberFormat) {
		if (numberFormat != null && numberFormat.getLength() > 0) {
			NamedNodeMap attributes = numberFormat.item(0).getAttributes();
			Node ssFormat = attributes.getNamedItem("ss:Format");
			if (ssFormat != null && "0_);\\(0\\)".equals(ssFormat.getNodeValue())) {
				return " NBk";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private static String getBorderCss(NodeList borders) {
		Map<String, String> borderTypes = new HashMap<String, String>();
		borderTypes.put("Top", "T0");
		borderTypes.put("Bottom", "B0");
		borderTypes.put("Left", "L0");
		borderTypes.put("Right", "R0");

		if (borders != null && borders.getLength() > 0) {
			NodeList borderlist = ((Element) borders.item(0)).getElementsByTagName("Border");
			for (int j=0;j<borderlist.getLength();j++) {
				NamedNodeMap attributes = borderlist.item(j).getAttributes();
				String position = attributes.getNamedItem("ss:Position").getNodeValue();
				if (position != null) {
					Node LineWeight = attributes.getNamedItem("ss:Weight");
					String sLineWeight = "1";
					if (LineWeight != null) sLineWeight = LineWeight.getNodeValue();
					String styletext = position.substring(0, 1) 
								+ simplfy(attributes.getNamedItem("ss:LineStyle").getNodeValue()) 
								+ sLineWeight;
//					} catch(Exception e) {
//						System.out.println("NullPointerException");
//						throw e;
//					}
					if (!styleclasses.containsKey(styletext)) {
						styleclasses.put(styletext, generateBorderCss(position, attributes.getNamedItem("ss:LineStyle").getNodeValue(),  sLineWeight));
					}
					borderTypes.put(position, styletext);
				}
			}
		}
		return borderTypes.get("Top") + " " + borderTypes.get("Bottom") + " " + borderTypes.get("Left") + " " + borderTypes.get("Right") + " ";
	}

	private static String simplfy(String lineStyle) {
		if ("Continuous".equalsIgnoreCase(lineStyle)) {
			return "Sd";
		} else if ("Double".equalsIgnoreCase(lineStyle)) {
			return "Db";
		} else if ("Dash".equalsIgnoreCase(lineStyle)) {
			return "Ds";
		} else if ("Dot".equalsIgnoreCase(lineStyle) || "DashDot".equalsIgnoreCase(lineStyle) || "DashDotDot".equalsIgnoreCase(lineStyle) || "SlantDashDot".equalsIgnoreCase(lineStyle)) {
			return "Dt";
		} else {
			return "No";
		}
	}

	private static String generateBorderCss(String position, String lineStyle, String weight) {
		String retCss = "border-";
		retCss += position.toLowerCase() + ": " + weight + "px ";
		// None, Continuous, Dash, Dot, DashDot, DashDotDot, SlantDashDot, Double
		if ("Continuous".equalsIgnoreCase(lineStyle)) {
			retCss += "solid ";
		} else if ("Double".equalsIgnoreCase(lineStyle)) {
			retCss += "double ";
		} else if ("Dash".equalsIgnoreCase(lineStyle)) {
			retCss += "dashed ";
		} else if ("Dot".equalsIgnoreCase(lineStyle) || "DashDot".equalsIgnoreCase(lineStyle) || "DashDotDot".equalsIgnoreCase(lineStyle) || "SlantDashDot".equalsIgnoreCase(lineStyle)) {
			retCss += "dotted ";
		}
		retCss += ";";
		return retCss;
	}

	private static String getcss(Node style) {
		String styleID = getAttributeStringValue(style.getAttributes().getNamedItem("ss:StyleID"));

		if (styleID == null || !styleidmap.containsKey(styleID)) {
			return "";
		} else {
			return " class='" + styleidmap.get(styleID) + "' ";
		}
	}

    public static String decodeHtmlText(String text) {
    	if (text == null) return text;

    	return text.replaceAll("<(.+?)>", "&lt;$1&gt;").replaceAll(" ", "&nbsp;").replaceAll("\n", "<br>").replaceAll("T00:00:00\\.000", "");
    }
}

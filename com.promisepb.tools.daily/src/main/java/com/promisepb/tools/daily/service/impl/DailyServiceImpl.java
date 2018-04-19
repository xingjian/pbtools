/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.daily.service.DailyService;
import com.promisepb.tools.daily.vo.RoadEvent;
import com.promisepb.tools.daily.vo.RoadEventJHYS;
import com.promisepb.tools.daily.vo.WeatherMessage;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:DailyService接口实现类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月20日 下午5:51:08  
 */
public class DailyServiceImpl implements DailyService {
	private String srcDailyPath;
	private String toDailyPath;
	private static Logger logger = LoggerFactory.getLogger(DailyServiceImpl.class);
	private String eventKeys;
	private Map<String,String> keysMap = new HashMap<String,String>();
	private String currentKeyString = "";
	// 高速公路事件信息列表
	private List<RoadEvent> listGSEvent;
	// 高速公路计划施工信息列表,高速公路施工延期信息列表
	private List<RoadEventJHYS> listGSJHYS;
	private List<RoadEvent> listPTEvent;
	private List<RoadEventJHYS> listPTJHYS;
	private List<WeatherMessage> listWM;
	private String currentDateStr="";
	private String afterDataStr = "";
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日"); 
	private int dayGlobal = 0;
	private String yearCurrent;
	private SimpleDateFormat formatYM =  new SimpleDateFormat("yyyy年MM月");
	public final Pattern pattern = Pattern.compile("\\d+日");
	/**
	 * 处理日报模版
	 */
	@Override
	public void handleDaily() {
		listGSEvent = new ArrayList<RoadEvent>();
		listGSJHYS = new ArrayList<RoadEventJHYS>();
		listPTEvent = new ArrayList<RoadEvent>(); 
		listPTJHYS = new ArrayList<RoadEventJHYS>();
		listWM = new ArrayList<WeatherMessage>();
		logger.debug("start handleDaily,srcDailyPath:"+srcDailyPath+",toDailyPath"+toDailyPath);
		initKeysMap();
		List<File> listFile = new ArrayList<File>();
		PBFileUtil.GetFilesByPath(srcDailyPath, listFile);
		for(File fileTemp : listFile) {
			String fileName = fileTemp.getName();
			if((fileName.lastIndexOf(".docx")!=-1 || fileName.lastIndexOf(".doc")!=-1)&&fileName.indexOf("~$")==-1) {
				logger.debug(fileTemp.getAbsolutePath());
				initDateStr(fileName);
				readDailyFile(fileTemp.getAbsolutePath());
			}
		}
		Collections.sort(listGSEvent);
		Collections.sort(listGSJHYS);
		Collections.sort(listPTEvent);
		Collections.sort(listPTJHYS);
		Collections.sort(listWM);
		PBPOIExcelUtil.ExportDataByList(listGSEvent, toDailyPath+File.separator+"gsevent(高速公路事件信息).xls");
		PBPOIExcelUtil.ExportDataByList(listGSJHYS, toDailyPath+File.separator+"gsevent(高速公路施工和延期).xls");
		PBPOIExcelUtil.ExportDataByList(listPTEvent, toDailyPath+File.separator+"ptglevent(普通公路事件信息).xls");
		PBPOIExcelUtil.ExportDataByList(listPTJHYS, toDailyPath+File.separator+"ptglevent(普通公路施工和延期).xls");
		PBPOIExcelUtil.ExportDataByList(listWM, toDailyPath+File.separator+"weather-message.xls");
	}
	
	public void initDateStr(String fileName) {
		String[] dateStrArr = PBStringUtil.GetNumberArrByString(fileName);
		String year = dateStrArr[0];
		yearCurrent = year;
		String month =  dateStrArr[2];
		String day =  dateStrArr[3];
		dayGlobal = Integer.parseInt(day);
		try {
			Date date = PBStringUtil.sdf_yMd.parse(year+"-"+month+"-"+day);
			Date date2= PBStringUtil.DateCalc(date, -1);
			currentDateStr = sdf.format(date2);
			afterDataStr = sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 初始化keys
	 */
	public void initKeysMap() {
		String[] keyArr = eventKeys.split(",");
		for(String strTemp : keyArr) {
			keysMap.put(strTemp.split(":")[1], strTemp.split(":")[0]);
		}
	}
	
	public void readDailyFile(String filePath) {
		logger.debug("start handle daily file : " + filePath);
		try {
			FileInputStream fis = new FileInputStream(new File(filePath));
			XWPFDocument xdoc = new XWPFDocument(fis);
			List<IBodyElement> listBodyElement = xdoc.getBodyElements();
			processlistBodyElements(listBodyElement);
			xdoc.close();
			fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public  void processlistBodyElements(List<IBodyElement> listBodyElement) {
		if (listBodyElement != null && listBodyElement.size() > 0) {
            // 遍历所有结构
			boolean iskeyTable = false;
			boolean firstTableFlag = true;
            for (IBodyElement bodyElement : listBodyElement) {
            	if(bodyElement instanceof XWPFTable) {
            		if(iskeyTable) {
            			handleXWPFTable(currentKeyString, (XWPFTable)bodyElement);
            			iskeyTable=false;
            		}else if(firstTableFlag) {
            			XWPFTable table = (XWPFTable)bodyElement;
            			handleWeatherMessage(table);
            			firstTableFlag = false;
            		}
            	}else if(bodyElement instanceof XWPFParagraph){
            		XWPFParagraph paragraph = (XWPFParagraph)bodyElement;
            		String text = paragraph.getText();
            		 if(null!=text&&checkTextAndKey(text)) {
                     	iskeyTable = true;
                     }
            	}
               
            }
        }
	}
	
	public void handleWeatherMessage(XWPFTable  table) {
		List<XWPFTableRow> xwpfTableRows = table.getRows();
		for(int i = 1 ; i < xwpfTableRows.size() ; i++) {
			XWPFTableRow rowTemp = xwpfTableRows.get(i);
	    	XWPFTableCell  cell = rowTemp.getTableCells().get(0);
	    	String messageAll = cell.getParagraphArray(2).getText();
	    	int indexStop = messageAll.indexOf("。");
	    	String firstStop = messageAll.substring(0, indexStop);
	    	String otherMessage = messageAll.substring(indexStop+1,messageAll.length());
	    	String docTime = currentDateStr;
	    	WeatherMessage wmTemp = new WeatherMessage();
	    	wmTemp.setDocTime(docTime);
	    	wmTemp.setFirstStop(firstStop);
	    	wmTemp.setOtherMessage(otherMessage);
	    	listWM.add(wmTemp);
		}
	}
	
	public boolean checkTextAndKey(String text) {
		boolean result = false;
		for (Map.Entry<String, String> entry : keysMap.entrySet()) {
		    if(text.indexOf(entry.getKey())!=-1) {
		    	result = true;
		    	currentKeyString=entry.getValue();
		    	break;
		    }
		}
		return result;
	}
	
	public String handleJHYSTime(String timeStr) {
		timeStr = timeStr.replaceAll(" +","").replaceAll("预计", "");
		if(timeStr.indexOf("年")!=-1) {
			return timeStr;
		}else {
			return yearCurrent+"年"+timeStr;
		}
	}
	
	/**
	 * 根据开始时间，进行填补完整字段
	 * @return
	 */
	public String handleStartTimeStr(String str,String currentTime,String afterTime) {
		str = str.replaceAll(" +","");
		String reg1 = "\\s*(次日|[0-9]{1,2}日)?[0-9]{1,2}(:|：)[0-9]{1,2}\\s*";//高速公路事件信息和普通公路事件信息正则表达
		Pattern pattern = Pattern.compile(reg1);
		Matcher matcher = pattern.matcher(str);
		String result = str;
		if(matcher.matches()) {
				result = currentTime+" "+str;
		}
		return result;
	}
	
	/**
	 * 根据开始时间，进行填补完整字段
	 * @return
	 */
	public String handleEndTimeStr(String str,String currentTime,String afterTime,String startTime) {
		try {
			str = str.replaceAll(" +","");
			String reg1 = "\\s*(次日|[0-9]{1,2}日)?[0-9]{1,2}(:|：)[0-9]{1,2}\\s*";//高速公路事件信息和普通公路事件信息正则表达
			Pattern pattern = Pattern.compile(reg1);
			Matcher matcher = pattern.matcher(str);
			String result = str;
			if(matcher.matches()) {
				if(str.indexOf("次日")!=-1) {
					result = afterTime +" "+str.replaceAll("次日", "");
				}else if(str.indexOf("日")!=-1){
					String strTemp1 = getDayStrByString(str);
		        	int dayNum1 = Integer.parseInt(strTemp1.replaceAll("日", ""));
		        	String strTemp2 = getDayStrByString(startTime);
		        	int dayNum2 = Integer.parseInt(strTemp2.replaceAll("日", ""));
		        	Calendar c = Calendar.getInstance();
		        	c.setTime(formatYM.parse(startTime.replaceAll(" +","").replaceAll("|[0-9]{1,2}日?[0-9]{1,2}(:|：)[0-9]{1,2}", "")));
		        	if(dayNum1<dayNum2) {
		        		c.add(Calendar.MONTH, 1);
		        		int yearTemp = c.get(Calendar.YEAR);
		        		int monthTemp = c.get(Calendar.MONTH)+1;
		        		Date date = PBStringUtil.sdf_yMd.parse(yearTemp+"-"+monthTemp+"-"+strTemp1);
		        		result = sdf.format(date)+" "+str.replaceAll("\\d+日", "");
		        	}else {
		        		int yearTemp = c.get(Calendar.YEAR);
		        		int monthTemp = c.get(Calendar.MONTH)+1;
		        		Date date = PBStringUtil.sdf_yMd.parse(yearTemp+"-"+monthTemp+"-"+strTemp1);
		        		result = sdf.format(date)+" "+str.replaceAll("\\d+日", "");
		        	}
				}else {
					result = currentTime+" "+str;
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
		
	}
	
	/**
	 * 1:高速公路事件信息列表,
	 * 2:高速公路计划施工信息列表,
	 * 3:高速公路施工延期信息列表,
	 * 4:普通公路事件信息列表,
	 * 5:普通公路计划施工信息列表,
	 * 6:普通公路施工延期信息列表
	 * @param keyString
	 * @param table
	 */
	public void handleXWPFTable(String keyString,XWPFTable table) {
		if(keyString.trim().equals("1")) {//高速公路事件信息列表
			String currentDateStrGSEvent = currentDateStr;
			String afterDataStrGSEvent = afterDataStr;
			List<XWPFTableRow> xwpfTableRows = table.getRows();
		    for(int i = 1 ; i < xwpfTableRows.size() ; i++){
		    	XWPFTableRow rowTemp = xwpfTableRows.get(i);
		    	List<XWPFTableCell> cellList = rowTemp.getTableCells();
		    	if(null!=cellList&&cellList.size()>1) {
		    		String roadName = cellList.get(0).getText();
			    	String findTime = cellList.get(1).getText();
			    	String reTime = cellList.get(2).getText();
			    	String detail = cellList.get(3).getText();
			    	String cause = cellList.get(4).getText();
			    	
			    	RoadEvent reTemp = new RoadEvent();
			    	reTemp.setRoadName(roadName);
			    	reTemp.setDetails(detail);
			    	reTemp.setStartTime(handleStartTimeStr(findTime,currentDateStrGSEvent,afterDataStrGSEvent));
			    	reTemp.setEndTime(handleEndTimeStr(reTime,currentDateStrGSEvent,afterDataStrGSEvent,reTemp.getStartTime()));
			    	reTemp.setReason(cause);
			    	reTemp.setTime(afterDataStr);
			    	if(null==reTime||reTime.trim().equals("暂未恢复")) {
			    		reTemp.setRemark("未恢复");
			    		reTemp.setStatus("0");
			    	}else {
			    		reTemp.setRemark(" ");
			    		reTemp.setStatus("1");
			    	}
			    	listGSEvent.add(reTemp);
		    	}else if(null!=cellList&&cellList.size()==1) {
		    		//补报rows
		    		String pbStr = cellList.get(0).getText();
		    		String[] resultTime = handleBGTime(currentDateStrGSEvent,afterDataStrGSEvent,pbStr);
		    		currentDateStrGSEvent = resultTime[0];
		    		afterDataStrGSEvent = resultTime[1];		
		    	}
		    }
		}else if(keyString.trim().equals("2")||keyString.trim().equals("3")) {//高速公路计划施工信息列表和高速公路施工延期信息列表
			List<XWPFTableRow> xwpfTableRows = table.getRows();
		    for(int i = 1 ; i < xwpfTableRows.size() ; i++){
		    	XWPFTableRow rowTemp = xwpfTableRows.get(i);
		    	List<XWPFTableCell> cellList = rowTemp.getTableCells();
		    	if(null!=cellList&&cellList.size()>1) {
		    		String roadName = cellList.get(0).getText();
			    	String findTime = cellList.get(1).getText();
			    	String reTime = cellList.get(2).getText();
			    	String detail = cellList.get(3).getText();
			    	String eventType = cellList.get(4).getText();
			    	RoadEventJHYS reJHYS = new RoadEventJHYS();
			    	reJHYS.setRoadName(roadName);
			    	reJHYS.setStartTime(handleJHYSTime(findTime));
			    	reJHYS.setEndTime(handleJHYSTime(reTime));
			    	reJHYS.setDetails(detail);
			    	reJHYS.setEventType(eventType);
			    	reJHYS.setTime(afterDataStr);
			    	listGSJHYS.add(reJHYS);
		    	}
		    }
		}else if(keyString.trim().equals("4")) {//普通公路事件信息列表
			String currentDateStrPTGLEvent = currentDateStr;
			String afterDataStrPTGLEvent = afterDataStr;
			List<XWPFTableRow> xwpfTableRows = table.getRows();
		    for(int i = 1 ; i < xwpfTableRows.size() ; i++){
		    	XWPFTableRow rowTemp = xwpfTableRows.get(i);
		    	List<XWPFTableCell> cellList = rowTemp.getTableCells();
		    	if(null!=cellList&&cellList.size()>1) {
		    		String roadName = cellList.get(0).getText();
			    	String findTime = cellList.get(1).getText();
			    	String reTime = cellList.get(2).getText();
			    	String detail = cellList.get(3).getText();
			    	String cause = cellList.get(4).getText();
			    	RoadEvent reTemp = new RoadEvent();
			    	reTemp.setRoadName(roadName);
			    	reTemp.setDetails(detail);
			    	reTemp.setStartTime(handleJHYSTime(handleStartTimeStr(findTime,currentDateStrPTGLEvent,afterDataStrPTGLEvent)));
			    	String endTimeTemp = handleEndTimeStr(reTime,currentDateStrPTGLEvent,afterDataStrPTGLEvent,reTemp.getStartTime());
			    	reTemp.setEndTime(endTimeTemp);
			    	if(endTimeTemp.indexOf("暂未恢复")==-1) {
			    		reTemp.setEndTime(handleJHYSTime(endTimeTemp));
			    	}
			    	reTemp.setReason(cause);
			    	reTemp.setTime(afterDataStr);
			    	if(null==reTime||reTime.trim().equals("暂未恢复")) {
			    		reTemp.setRemark("未恢复");
			    		reTemp.setStatus("0");
			    	}else {
			    		reTemp.setRemark(" ");
			    		reTemp.setStatus("1");
			    	}
			    	listPTEvent.add(reTemp);
		    	}else {
		    		String pbStr = cellList.get(0).getText();
		    		String[] resultTime = handleBGTime(currentDateStrPTGLEvent,afterDataStrPTGLEvent,pbStr);
		    		currentDateStrPTGLEvent = resultTime[0];
		    		afterDataStrPTGLEvent = resultTime[1];		
		    	}
		    }
		}else if(keyString.trim().equals("5")||keyString.trim().equals("6")) {//普通公路计划施工信息列表和普通公路施工延期信息列表
			String currentDateStrPTGLEvent = currentDateStr;
			String afterDataStrPTGLEvent = afterDataStr;
			List<XWPFTableRow> xwpfTableRows = table.getRows();
		    for(int i = 1 ; i < xwpfTableRows.size() ; i++){
		    	XWPFTableRow rowTemp = xwpfTableRows.get(i);
		    	List<XWPFTableCell> cellList = rowTemp.getTableCells();
		    	if(null!=cellList&&cellList.size()>1) {
		    		String roadName = cellList.get(0).getText();
			    	String findTime = cellList.get(1).getText();
			    	String reTime = cellList.get(2).getText();
			    	String detail = cellList.get(3).getText();
			    	String cause = cellList.get(4).getText();
			    	RoadEventJHYS reJHYS = new RoadEventJHYS();
			    	reJHYS.setRoadName(roadName);
			    	reJHYS.setStartTime(handleJHYSTime(findTime));
			    	reJHYS.setEndTime(handleJHYSTime(reTime));
			    	reJHYS.setDetails(detail);
			    	reJHYS.setEventType(cause);
			    	reJHYS.setTime(afterDataStr);
			    	listPTJHYS.add(reJHYS);
		    	}
		    }
		}
	}
	
	public String getDayStrByString(String strTemp) {
		strTemp = strTemp.replaceAll(" +","");
        Matcher matcher = pattern.matcher(strTemp);
        while( matcher.find()) {
        	strTemp = matcher.group();
        }
        return strTemp;
	}
	
	private String[] handleBGTime(String currentDateStrGSEvent, String afterDataStrGSEvent, String pbStr) {
		String[] result = new String[2];
		try {
        	String strTemp = getDayStrByString(pbStr);
        	int pbday = Integer.parseInt(strTemp.replaceAll("日", ""));
        	if(pbday>dayGlobal) {
        		Calendar c = Calendar.getInstance();
        		c.setTime(formatYM.parse(currentDateStrGSEvent.replaceAll("\\d+日", "")));
        		c.add(Calendar.MONTH, -1);
        		int yearTemp = c.get(Calendar.YEAR);
        		int monthTemp = c.get(Calendar.MONTH)+1;
        		Date date = PBStringUtil.sdf_yMd.parse(yearTemp+"-"+monthTemp+"-"+pbday);
    			Date date2= PBStringUtil.DateCalc(date, 1);
    			currentDateStrGSEvent = sdf.format(date);
    			afterDataStrGSEvent = sdf.format(date2);
        	}else {
        		currentDateStrGSEvent = currentDateStr.replaceAll("\\d+日", strTemp);
        		Date date = sdf.parse(currentDateStrGSEvent);
    			Date date2= PBStringUtil.DateCalc(date, 1);
    			afterDataStrGSEvent = sdf.format(date2);
        	}
		}catch(Exception e) {
			e.printStackTrace();
		}
		result[0] = currentDateStrGSEvent;
		result[1] = afterDataStrGSEvent;
		return result;
	}

	public String getSrcDailyPath() {
		return srcDailyPath;
	}
	public void setSrcDailyPath(String srcDailyPath) {
		this.srcDailyPath = srcDailyPath;
	}
	public String getToDailyPath() {
		return toDailyPath;
	}
	public void setToDailyPath(String toDailyPath) {
		this.toDailyPath = toDailyPath;
	}

	public String getEventKeys() {
		return eventKeys;
	}

	public void setEventKeys(String event_keys) {
		this.eventKeys = event_keys;
	}
	
}

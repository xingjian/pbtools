/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.daily.service.ZTYSQKReportService;
import com.promisepb.tools.daily.vo.WeatherMessage;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述: 总体运行情况报告抽取处理
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月9日 上午11:11:27  
 */
public class ZTYSQKReportServiceImpl implements ZTYSQKReportService {

	private String srcDailyPath;
	private String toDailyPath;
	private static Logger logger = LoggerFactory.getLogger(ZTYSQKReportServiceImpl.class);
	private List<WeatherMessage> listWM;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日"); 
	@Override
	public void handleZTYXQKReport() {
		try {
			listWM = new ArrayList<WeatherMessage>();
			List<File> listFile = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(srcDailyPath, listFile);
			String para4Str = "";
			String para10Str="";
			for(File fileTemp : listFile) {
				String fileName = fileTemp.getName();
				FileInputStream fis = new FileInputStream(fileTemp);
				if( fileName.endsWith(".doc")&&fileName.indexOf("~$")==-1) {
					HWPFDocument doc = new HWPFDocument(fis);
					Range range = doc.getRange();
					if(null!=range&&range.numParagraphs()>10) {
						Paragraph para4 = range.getParagraph(4);
						Paragraph para10 = range.getParagraph(10);
						para4Str = para4.text();
						para10Str = para10.text();
					}
					doc.close();
				}else if(fileName.endsWith(".docx")&&fileName.indexOf("~$")==-1) {
					XWPFDocument xdoc = new XWPFDocument(fis);
					List<IBodyElement> listBodyElement = xdoc.getBodyElements();
					if(null!=listBodyElement&&listBodyElement.size()>10) {
						XWPFParagraph wspfp4 = (XWPFParagraph)listBodyElement.get(4);
						XWPFParagraph wspfp10 = (XWPFParagraph)listBodyElement.get(10);
						para4Str = wspfp4.getText();
						para10Str = wspfp10.getText();
					}
					xdoc.close();
				}else {
					continue;
				}
				fis.close();
				String[] arrayTemp = para4Str.split("\\s+");
				WeatherMessage wmTemp = new WeatherMessage();
				wmTemp.setFileName(fileTemp.getAbsolutePath());
				boolean booDate = true;
				if(arrayTemp.length!=2) {
					booDate = false;
				}else {
					 booDate = PBStringUtil.CheckValidDate(arrayTemp[1], sdf);
				}
				int indexStop = para10Str.indexOf("。");
				if(indexStop!=-1) {
					String firstStop = para10Str.substring(0, indexStop);
			    	String otherMessage = para10Str.substring(indexStop+1,para10Str.length());
			    	wmTemp.setFirstStop(firstStop);
					wmTemp.setOtherMessage(otherMessage);
				}else {
					wmTemp.setFirstStop("天气情况格式异常。");
					wmTemp.setOtherMessage(para10Str);
				}
		    	
				if(booDate) {
					wmTemp.setDocTime(sdf.format(PBStringUtil.DateCalc(sdf.parse(arrayTemp[1]), -1)));
				}else {
					wmTemp.setDocTime("9999年12月31日");
					wmTemp.setFirstStop(fileTemp.getAbsolutePath());
				}
				listWM.add(wmTemp);
			}
			Collections.sort(listWM);
			PBPOIExcelUtil.ExportDataByList(listWM, toDailyPath+File.separator+"weather-ztyxqk.xls");
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	
}

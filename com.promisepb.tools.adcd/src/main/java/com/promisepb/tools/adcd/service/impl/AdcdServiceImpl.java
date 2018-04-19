/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.adcd.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.adcd.service.AdcdService;
import com.promisepb.tools.adcd.vo.ADCDVO;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

import ch.qos.logback.core.db.dialect.DBUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年2月28日 下午3:07:36  
 */
public class AdcdServiceImpl implements AdcdService {
	private Logger log = LoggerFactory.getLogger(AdcdServiceImpl.class);
	private String adcdUrlS;
	private String tempWorkspace;
	private static int timeoutMillis = 5000;
	/* 
	 * 启动服务任务
	 */
	@Override
	public void startTask() {
		log.info("start adcdservice task......");
		for(String str : adcdUrlS.split(",")) {
			String[] strArray = str.split("@");
			String strURL = strArray[0];
			String cityName = strArray[1];
			log.info("start analysis weburl "+strURL);
			String regStr = "";
			boolean isLoop = false;
			int depth = 0;
			String method = "GET";
			String result = getADCDPage(strURL, regStr, isLoop, depth, method);
			//将下载好的数据存储到文本文件中
			if(result.equals("success")){
				PBPOIExcelUtil.ExportDataByList(adcdvoList, tempWorkspace+File.separator+cityName+"-adcd-"+PBStringUtil.GetDateString("yyyyMMddkkmmss", new Date())+".xls");
			}
		}
	} 
	
	@Override
	public String getADCDPage(String htmlURL, String regStr,boolean isLoop, int depth,String method) {
		log.debug("htmlURL:"+htmlURL);
		String retResult = "success";
		try{
			Document doc = Jsoup.parse(new URL(htmlURL), timeoutMillis);
	        String line = doc.html();  
	        String regex = "<tr\\s+class=\'(citytr|countytr|towntr)\'><td><a\\s+href=\'([^\']+)'>([^>]*)</a></td><td><a\\s+href=\'[^\']+\'>([^>]+)</a></td></tr>";
	        Pattern p = Pattern.compile(regex); 
	        String mainURL = htmlURL.substring(0, htmlURL.lastIndexOf("/")+1);
	            Matcher m = p.matcher(line);
	            while(m.find()) {  
	                String adcdurl = mainURL+m.group(2);
	                String adcdCode = m.group(3);
	                String adcdName = m.group(4);
	                ADCDVO adcdvo = new ADCDVO();
	                adcdvo.setName(adcdName);
	                adcdvo.setCode(adcdCode);
	                adcdvoList.add(adcdvo);
	                if(adcdurl.substring(adcdurl.lastIndexOf("/")+1, adcdurl.lastIndexOf(".html")).length()==9){
	                	getVillageListByURL(adcdurl,regex,isLoop,depth,method);
	                }else{
	                	getADCDPage(mainURL+m.group(2),regex,isLoop,depth,method);
	                }
	            }  
		}catch(Exception e){
			retResult = "failure";
			e.printStackTrace();
		}
		return retResult;
	}

	public String getVillageListByURL(String htmlURL, String regStr,boolean isLoop, int depth,String method){
		log.debug("htmlURL:"+htmlURL);
		String retResult = "success";
		try{
			Document doc = Jsoup.parse(new URL(htmlURL), timeoutMillis);
	        String line = doc.html();  
	        String regex = "<tr\\s+class=\'villagetr\'><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td></tr>";
	        Pattern p = Pattern.compile(regex); 
	            Matcher m = p.matcher(line);
	            while(m.find()) {  
	                ADCDVO adcdvo = new ADCDVO();
	                adcdvo.setName(m.group(3));
	                adcdvo.setCode(m.group(1));
	                adcdvoList.add(adcdvo);
	            }  
		}catch(Exception e){
			retResult = "failure";
			e.printStackTrace();
		}
		return retResult;
	}

	public String getAdcdUrlS() {
		return adcdUrlS;
	}

	public void setAdcdUrlS(String adcdUrlS) {
		this.adcdUrlS = adcdUrlS;
	}

	public String getTempWorkspace() {
		return tempWorkspace;
	}

	public void setTempWorkspace(String tempWorkspace) {
		this.tempWorkspace = tempWorkspace;
	} 
	
	
}

/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.busline.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.busline.BusLineMessage;
import com.promisepb.tools.busline.service.BusLineService;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:BusLineService接口实现
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年2月27日 下午1:13:04  
 */
public class BusLineServiceImpl implements BusLineService {

	private static  Logger log = LoggerFactory.getLogger(BusLineServiceImpl.class);
	private static int timeoutMillis = 5000;
	private String tempworkspace;
	// 网络地址信息，用逗号分隔
	private String busLineUrlS;
	/* 
	 * 启动服务任务
	 */
	@Override
	public void startTask() {
		log.info("start buslineservice task......");
		handleBusLineUrlS();
	}
	
	/**
	 * 解析buslineurls，并获取相应的地址信息
	 * @return
	 */
	public List<String> handleBusLineUrlS(){
		List<String> list = new ArrayList<String>();
		for(String str : busLineUrlS.split(",")) {
			String[] strArray = str.split("@");
			String strURL = strArray[0];
			String cityName = strArray[1];
			log.info("start analysis weburl "+strURL);
			List<String> rootLevel1List = analysisRootURL(strURL);
			List<String> listResultLevel1 = analysisRootLevel1URL(rootLevel1List,strURL,cityName);
			String exportResult = PBFileUtil.WriteListToTxt(listResultLevel1, tempworkspace+File.separator+cityName+"busline-url-name-"+PBStringUtil.GetDateString("yyyyMMddkkmmss", new Date())+".txt",true);
			log.info("export "+cityName+" success.");
			fetchBusLineInformation(listResultLevel1,cityName);
		}
		return list;
	}
	
	public void fetchBusLineInformation(List<String> list,String city) {
		List<BusLineMessage> busLineMessageList = new ArrayList<BusLineMessage>();
		for(String strTemp : list) {
			String[] arryTemp = strTemp.split(",");
			String cityName = arryTemp[0];
			String buslineName = arryTemp[1];
			String buslineURL = arryTemp[2];
			try {
				Document doc = Jsoup.parse(new URL(buslineURL), timeoutMillis);
				Element contentElement = doc.select("div.bus_i_content").first();
				Element contentElement_bus_i_t1 = contentElement.select("div.bus_i_t1").first();
				String lineNameAll = contentElement_bus_i_t1.select("h1").text();
				String lineType = contentElement_bus_i_t1.select("a").first().text();
				Elements contentElement_bus_i_t4 = contentElement.select("p.bus_i_t4");
				String yxsj = contentElement_bus_i_t4.get(0).text();
				String priceMess = contentElement_bus_i_t4.get(1).text();
				String company = contentElement_bus_i_t4.get(2).text();
				String updateTime = contentElement_bus_i_t4.get(3).text();
				Elements busLineTxtElements = doc.select("div.bus_line_txt");
				for(int i=0;i<busLineTxtElements.size();i++) {
					BusLineMessage blm = new BusLineMessage();
					blm.setName(lineNameAll);
					blm.setCompany(company);
					blm.setLineType(lineType);
					blm.setOperateMessage(yxsj);
					blm.setPrice(priceMess);
					blm.setUpdateTime(updateTime);
					blm.setCityName(cityName);
					blm.setCode(buslineName);
					blm.setWebURL(buslineURL);
					String busLineTxtStr = busLineTxtElements.get(i).select("strong").text();
					blm.setFullName(busLineTxtStr);
					Element busLineSiteElement  = doc.select("div.bus_line_site").get(i);
					Elements busSiteLayers = busLineSiteElement.select("div.bus_site_layer");
					String stationMessage = "";
					for(Element eleBusSiteLayer : busSiteLayers) {
						Elements eleBusSiteLayer_divTemp = eleBusSiteLayer.children().select("div");
						for(Element subEle_divTemp : eleBusSiteLayer_divTemp) {
							String stationIndex = subEle_divTemp.select("i").text();
							String stationName = subEle_divTemp.select("a").text();
							stationMessage += (stationIndex+":"+stationName)+";";
						}
					}
					blm.setStationMessage(stationMessage);
					busLineMessageList.add(blm);
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PBPOIExcelUtil.ExportDataByList(busLineMessageList, tempworkspace+File.separator+city+"bus-line-message-"+PBStringUtil.GetDateString("yyyyMMddkkmmss", new Date())+".xls");
		log.info("bus line data fetch success!");
	}
	
	public List<String>  analysisRootLevel1URL(List<String> level1List , String baseURL,String cityName){
		List<String> list = new ArrayList<String>();
		try {
			Map<String,String> map = new HashMap<String,String>();
			for(String str : level1List) {
				log.info("start abtain "+baseURL+str);
				Document doc = Jsoup.parse(new URL(baseURL+str), timeoutMillis);
				Elements elements = doc.select("div.stie_list").select("a");
				for(Element elementTemp : elements) {
					String hrefStr = elementTemp.attr("href");
					String lineCodeName = elementTemp.text();
					if(null==map.get(lineCodeName)) {
						map.put(lineCodeName, baseURL+hrefStr);
					}
				}
			}
			for(Map.Entry<String,String> entry : map.entrySet()) {
				list.add(cityName+","+entry.getKey()+","+entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 开始分析根目录的url
	 * @param rootURL
	 * @return
	 */
	public List<String> analysisRootURL(String rootURL){
		try {
			List<String> subURLList1 = new ArrayList<>();
			Document doc = Jsoup.parse(new URL(rootURL), timeoutMillis);
			subURLList1 = doc.select("div.bus_kt_r1").first().select("a").eachAttr("href");
			List<String> subURLList2 = doc.select("div.bus_kt_r2").first().select("a").eachAttr("href");
			subURLList1.addAll(subURLList2);
			return subURLList1;
		} catch (Exception e) {
			log.info(e.toString());
		}
		return null;
	}
	
	public String getBusLineUrlS() {
		return busLineUrlS;
	}
	public void setBusLineUrlS(String busLineUrlS) {
		this.busLineUrlS = busLineUrlS;
	}

	public static int getTimeoutMillis() {
		return timeoutMillis;
	}

	public static void setTimeoutMillis(int timeoutMillis) {
		BusLineServiceImpl.timeoutMillis = timeoutMillis;
	}

	public String getTempworkspace() {
		return tempworkspace;
	}

	public void setTempworkspace(String tempworkspace) {
		this.tempworkspace = tempworkspace;
	}
	
}

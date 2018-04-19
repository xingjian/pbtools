/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.vo;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.promisepb.utils.poiutils.POIOptionMeta;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述: 日报总体运行情况天气信息抽取
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月7日 下午5:51:27  
 */
public class WeatherMessage implements Comparable<WeatherMessage>{
	private String fileName;
	private String docTime;
	private String firstStop;
	private String otherMessage;
	
	@POIOptionMeta(isExport=false)
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	@Override
	public int compareTo(WeatherMessage o) {
		return PBStringUtil.CompareDate(this.getDocTime(), o.getDocTime(),sdf);
    }
	
	public String getDocTime() {
		return docTime;
	}

	public void setDocTime(String docTime) {
		this.docTime = docTime;
	}
	
	public String getFirstStop() {
		return firstStop;
	}
	public void setFirstStop(String firstStop) {
		this.firstStop = firstStop;
	}
	public String getOtherMessage() {
		return otherMessage;
	}
	public void setOtherMessage(String otherMessage) {
		this.otherMessage = otherMessage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}

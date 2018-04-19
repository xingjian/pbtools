/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.busline;

/**  
 * 功能描述: 公交线路信息
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年2月28日 上午10:32:47  
 */
public class BusLineMessage {
	private String code;
	private String name;
	private String fullName;
	private String lineType;
	private String company;
	private String price;
	private String cityName;
	private String stationMessage;
	private String updateTime;
	private String webURL;
	private String operateMessage;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLineType() {
		return lineType;
	}
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getStationMessage() {
		return stationMessage;
	}
	public void setStationMessage(String stationMessage) {
		this.stationMessage = stationMessage;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getWebURL() {
		return webURL;
	}
	public void setWebURL(String webURL) {
		this.webURL = webURL;
	}
	public String getOperateMessage() {
		return operateMessage;
	}
	public void setOperateMessage(String operateMessage) {
		this.operateMessage = operateMessage;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	
}

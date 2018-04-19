/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.iccard.vo;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年3月13日 下午6:35:55  
 */
public class BusLineVO {
	private String id;
	private String label;
	private String name;
	private String runTime;
	private double length;
	private String company;
	private String minIndex="";
	private String maxIndex="";
	private String minWKT="";
	private String maxWKT="";
	private double distance;
	private double zxxs;
	private String lineCode="";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRunTime() {
		return runTime;
	}
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getMinIndex() {
		return minIndex;
	}
	public void setMinIndex(String minIndex) {
		this.minIndex = minIndex;
	}
	public String getMaxIndex() {
		return maxIndex;
	}
	public void setMaxIndex(String maxIndex) {
		this.maxIndex = maxIndex;
	}
	public String getMinWKT() {
		return minWKT;
	}
	public void setMinWKT(String minWKT) {
		this.minWKT = minWKT;
	}
	public String getMaxWKT() {
		return maxWKT;
	}
	public void setMaxWKT(String maxWKT) {
		this.maxWKT = maxWKT;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getZxxs() {
		return zxxs;
	}
	public void setZxxs(double zxxs) {
		this.zxxs = zxxs;
	}
	public String getLineCode() {
		if(null==lineCode) {lineCode="";}
		return lineCode;
	}
	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	
}

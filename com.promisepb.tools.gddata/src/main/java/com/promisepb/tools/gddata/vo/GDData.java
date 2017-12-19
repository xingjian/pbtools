/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.gddata.vo;

import java.text.SimpleDateFormat;

import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月3日 上午10:36:03  
 */
public class GDData implements Comparable<GDData> {
	private String roadID;
	private String roadName;
	private String speed;
	private String level;
	private String bjTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	
	public String getRoadID() {
		return roadID;
	}
	public void setRoadID(String roadID) {
		this.roadID = roadID;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getBjTime() {
		return bjTime;
	}
	public void setBjTime(String bjTime) {
		this.bjTime = bjTime;
	}
	@Override
	public int compareTo(GDData o) {
		return PBStringUtil.CompareDate(this.bjTime, o.getBjTime(),sdf);
	}
	@Override
	public String toString() {
		return bjTime+","+roadID+","+ roadName+","+level +","+speed;
	}
}

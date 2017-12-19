/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.taxigps.vo;

import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月3日 上午10:36:03  
 */
public class TaxiGPS implements Comparable<TaxiGPS> {
	private String carCode;
	private String company;
	private double o_x;
	private double o_y;
	private String bjTime;
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public double getO_x() {
		return o_x;
	}
	public void setO_x(double o_x) {
		this.o_x = o_x;
	}
	public double getO_y() {
		return o_y;
	}
	public void setO_y(double o_y) {
		this.o_y = o_y;
	}
	public String getBjTime() {
		return bjTime;
	}
	public void setBjTime(String bjTime) {
		this.bjTime = bjTime;
	}
	@Override
	public int compareTo(TaxiGPS o) {
		return PBStringUtil.CompareDate(this.bjTime, o.getBjTime());
	}
	@Override
	public String toString() {
		return carCode+","+ company+","+o_x +","+o_y+","+bjTime;
	}
	
	
}

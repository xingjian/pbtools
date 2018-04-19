/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.vo;

import java.text.SimpleDateFormat;

import com.promisepb.utils.poiutils.POIOptionMeta;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:施工延期和计划施工
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月22日 下午11:36:41  
 */
public class RoadEventJHYS implements Comparable<RoadEventJHYS>{
		private String roadName;
		private String startTime;
		private String endTime;
		private String details;
		private String eventType	;
		@POIOptionMeta(isExport=false)
		private String isRecovery;
		@POIOptionMeta(isExport=false)
		private String status;
		private String time;
		@POIOptionMeta(isExport=false)
		private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		public String getEventType() {
			return eventType;
		}
		public void setEventType(String eventType) {
			this.eventType = eventType;
		}
		public String getRoadName() {
			return roadName;
		}
		public void setRoadName(String roadName) {
			this.roadName = roadName;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getDetails() {
			return details;
		}
		public void setDetails(String details) {
			this.details = details;
		}
		public String getIsRecovery() {
			return isRecovery;
		}
		public void setIsRecovery(String isRecovery) {
			this.isRecovery = isRecovery;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		@Override
		public int compareTo(RoadEventJHYS o) {
			return PBStringUtil.CompareDate(this.getTime(), o.getTime(),sdf);
		}
}

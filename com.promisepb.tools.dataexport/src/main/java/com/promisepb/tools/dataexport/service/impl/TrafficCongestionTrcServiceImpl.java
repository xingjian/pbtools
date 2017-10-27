/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport.service.impl;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.dataexport.service.TrafficCongestionTrcService;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月15日 下午8:57:55  
 */
public class TrafficCongestionTrcServiceImpl  implements TrafficCongestionTrcService{

	private static final Logger logger = LoggerFactory.getLogger(TrafficCongestionTrcServiceImpl.class);
	private Connection connection;
	private SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );
	private String dataFilePath;

	/**
	 * 导出全路网交通指数
	 */
	@Override
	public File exportDataFile() {
		String sqlDateStr = calcData()+"0000";
		String sql = "select * from mdh03_traffic_congestion_trc where zone_name='全路网' and calculate_time > to_date('"+sqlDateStr+"','yyyymmddhh24mi') order by calculate_time";
		String fileName = "全路网交通指数("+calcData()+"-"+PBStringUtil.GetDateString("yyyyMMddHHmm", null)+").xlsx";
		String result = PBPOIExcelUtil.ExportDataBySQL(sql, connection, dataFilePath+fileName, 1000000);
		logger.info("导出全路网交通指数成功，文件路径："+dataFilePath+fileName);
		return new File(dataFilePath+fileName);
	}

	/**
	 * 计算日期字符串，按照周五返回周一的时间字符串，周日返回周六的字符串
	 * @return
	 */
	public String calcData() {
		//获取当天星期几
		int dayOfWeek = PBStringUtil.DayForWeek("");
		Date today = new Date();     
        Calendar c = Calendar.getInstance();  
        c.setTime(today);  
		if(dayOfWeek==7) {//表示今天星期日
			c.add(Calendar.DAY_OF_MONTH, -6);
		}else if(dayOfWeek==6) {//表示今天星期六
			c.add(Calendar.DAY_OF_MONTH, -5);
		}else if(dayOfWeek==5) {//表示今天星期五
			c.add(Calendar.DAY_OF_MONTH, -4);
		}else if(dayOfWeek==4) {//表示今天星期四
			c.add(Calendar.DAY_OF_MONTH, -3);
		}else if(dayOfWeek==3) {//表示今天星期三
			c.add(Calendar.DAY_OF_MONTH, -2);
		}else if(dayOfWeek==2) {//表示今天星期二
			c.add(Calendar.DAY_OF_MONTH, -1);
		}else if(dayOfWeek==1) {//表示今天星期一
			c.add(Calendar.DAY_OF_MONTH, -7);
		}
		return format.format(c.getTime());
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}
	
}

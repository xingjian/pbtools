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

import com.promisepb.tools.dataexport.service.JcyjSubareaNetSpeedService;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述: 导出重点区域交通指数
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月27日 下午1:37:14  
 */
public class JcyjSubareaNetSpeedServiceImpl implements JcyjSubareaNetSpeedService {
	
	private static final Logger logger = LoggerFactory.getLogger(JcyjSubareaNetSpeedServiceImpl.class);
	//导出文件存放路径
	private String dataFilePath;
	private SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );
	private Connection connection;
	
	/**
	 * 导出重点区域交通指数
	 */
	@Override
	public File exportDataFile() {
		logger.info("开始执行交通指数导出......!");
		String sqlDateStr = calcData()+"0000";
		String sql = "select * from Jcyj_Subarea_Net_Speed where to_date(dat,'yyyymmddhh24mi') > to_date('"+sqlDateStr+"','yyyymmddhh24mi') order by dat";
		String fileName = "重点区域交通指数("+calcData()+"-"+PBStringUtil.GetDateString("yyyyMMddHHmm", null)+").xlsx";
		String result = PBPOIExcelUtil.ExportDataBySQL(sql, connection, dataFilePath+fileName, 1000000);
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

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	
}

/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexportone.service.impl;

import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.dataexportone.service.DataExportOneService;
import com.promisepb.tools.dataexportone.vo.DataSourceConfig;
import com.promisepb.tools.dataexportone.vo.EmailConfig;
import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.emailutils.PBMailUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月1日 下午3:43:58  
 */
public class DataExportOneServiceImpl implements DataExportOneService {

	private final static Logger logger = LoggerFactory.getLogger(DataExportOneServiceImpl.class);
	private DataSourceConfig dsc;
	private EmailConfig ec;
	private String sql;
	private SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );
	private String exportPath;
	private String emailContent;
	private String emailTitle;
	private String suffixContent;
	private String startTime;
	private String endTime;
	private int pageSize;
	private int dayStep;
	@Override
	public void exportDataFile() {
		try {
			Connection connection  = PBDBConnection.GetOracleConnection(dsc.getJdbcURL(), dsc.getUserName(), dsc.getPasswd());
			if(null!=connection) {
				logger.info("init dbConnection success !");
				if(null!=startTime&&null!=endTime) {
					List<String> dateList = initTimeArgs(dayStep);
					for(int i=0;i<dateList.size()-1;i++) {
						String d1 = dateList.get(i);
						String d2 = dateList.get(i+1);
						String exeSQL = sql.replace("#D1",d1).replace("#D2", d2);
						logger.info(exeSQL);
						PBPOIExcelUtil.ExportCSVBySQL(exeSQL, connection, exportPath+File.separator+d1+suffixContent+".csv", pageSize,"UTF-8");
						logger.info("export data success!");
					}
				}else {
					PBPOIExcelUtil.ExportCSVBySQL(sql, connection, exportPath+File.separator+format.format(new Date())+suffixContent+".csv", pageSize,"UTF-8");
					logger.info("export data success!");
				}
				PBDBConnection.CloseConnection(connection);
				PBMailUtil.SendEmailText(ec.getSendEmailProps(), ec.getSendMail(), ec.getErrorMails(), null, null, emailTitle+"-"+format.format(new Date()),emailContent);
			}else {
				logger.info("init dbConnection error !");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorEmail();
		}
		
	}

	public void sendErrorEmail() {
		try {
			PBMailUtil.SendEmailText(ec.getSendEmailProps(), ec.getSendMail(), ec.getErrorMails(), null, null, emailTitle+"(导出程序发生异常)-"+format.format(new Date()),"导出程序发生异常,请知晓！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取日期时间范围
	 * @return
	 */
	public List<String> initTimeArgs(int day){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		List<String> result = new ArrayList<String>();
		int index = 0;
		try {
			Date dateS = formatter.parse(startTime);
			Date dateE = formatter.parse(endTime);
			Calendar dd = Calendar.getInstance();//定义日期实例
			dd.setTime(dateS);//设置日期起始时间
			while(dd.getTime().before(dateE)){//判断是否到结束日期
				result.add(index,formatter.format(dd.getTime()));
				index++;
				dd.add(Calendar.DATE, day);//进行当前日期月份加1
			}
			result.add(index,formatter.format(dd.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public DataSourceConfig getDsc() {
		return dsc;
	}

	public void setDsc(DataSourceConfig dsc) {
		this.dsc = dsc;
	}

	public EmailConfig getEc() {
		return ec;
	}

	public void setEc(EmailConfig ec) {
		this.ec = ec;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getExportPath() {
		return exportPath;
	}

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	public String getSuffixContent() {
		return suffixContent;
	}

	public void setSuffixContent(String suffixContent) {
		this.suffixContent = suffixContent;
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

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getDayStep() {
		return dayStep;
	}

	public void setDayStep(int dayStep) {
		this.dayStep = dayStep;
	}
	
	
	
}

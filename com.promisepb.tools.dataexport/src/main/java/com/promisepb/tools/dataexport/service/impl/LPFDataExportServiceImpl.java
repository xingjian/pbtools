/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport.service.impl;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.promisepb.tools.dataexport.service.LPFDataExportService;
import com.promisepb.tools.dataexport.vo.DataSourceConfig;
import com.promisepb.tools.dataexport.vo.EmailConfig;
import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.emailutils.PBMailUtil;

/**  
 * 功能描述:LPFDataExportService
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月16日 下午11:26:45  
 */
public class LPFDataExportServiceImpl implements LPFDataExportService {

	private DataSourceConfig dsc;
	private EmailConfig ec;
	private TrafficCongestionTrcServiceImpl tctsi;
	private JcyjSubareaNetSpeedServiceImpl tsnssi;
	private SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );
	
	@Override
	public void exportData() {
		Connection connection  = PBDBConnection.GetOracleConnection(dsc.getJdbcURL(), dsc.getUserName(), dsc.getPasswd());
		tctsi.setConnection(connection);
		tsnssi.setConnection(connection);
		File file1 = tctsi.exportDataFile();
		File file2 = tsnssi.exportDataFile();
		PBDBConnection.CloseConnection(connection);
		long fileS = file1.length()+file2.length();
        double fileSize = fileS / 1048576;
        List<File> listFile = new ArrayList<File>();
        listFile.add(file1);
        listFile.add(file2);
        try {
        	 if(fileSize<25) {//发送带附件的邮件
             	PBMailUtil.SendEmailAttachment(ec.getSendEmailProps(),ec.getSendMail(), ec.getReceiveMails(), ec.getCcMails(), null, "交通指数"+"--"+format.format(new Date()), "你好！附件为本周所需要交通指数，请查收！",listFile);
             }else {//发送文件存放路径消息
             	PBMailUtil.SendEmailText(ec.getSendEmailProps(), ec.getSendMail(), ec.getErrorMails(), null, null, "交通指数导出文件大于25M-"+format.format(new Date()), "交通指数导出文件过大,请查看系统路径！");
             }
        }catch(Exception e) {
        	try {
				PBMailUtil.SendEmailText(ec.getSendEmailProps(), ec.getSendMail(), ec.getErrorMails(), null, null, "交通指数导出异常"+format.format(new Date()), "交通指数导出异常,请注意！");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	e.printStackTrace();
        	
        }
       
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

	public TrafficCongestionTrcServiceImpl getTctsi() {
		return tctsi;
	}

	public void setTctsi(TrafficCongestionTrcServiceImpl tctsi) {
		this.tctsi = tctsi;
	}

	public JcyjSubareaNetSpeedServiceImpl getTsnssi() {
		return tsnssi;
	}

	public void setTsnssi(JcyjSubareaNetSpeedServiceImpl tsnssi) {
		this.tsnssi = tsnssi;
	}

	
	
}

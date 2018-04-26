/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.daily.service.TOCCZTYXQKExtractService;
import com.promisepb.utils.ftputils.FTPUtil;
import com.promisepb.utils.ftputils.vo.FTPFileInfo;

/**  
 * 功能描述: tocc总体运行情况报告处理接口实现类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年4月20日 上午10:37:31  
 */
public class TOCCZTYXQKExtractServiceImpl implements TOCCZTYXQKExtractService {

	public static Logger logger = LoggerFactory.getLogger(TOCCZTYXQKExtractServiceImpl.class);
	public String ftpIP;//ftp ip地址
	public int ftpPort;// ftp 端口
	public String ftpUserName;//ftp 用户名
	public String ftpPasswd;// ftp 密码
	public String ftpControlEncoding;
	public String ftpZTYXQKDir;//ftp 总体运行情况报告目录
	public String downLoadZTYXQKDir;//下载存放总体运行情况报告目录
	public DataSource derbyDS;
	
	/**
	 * 总体运行情况抽取天气信息
	 */
	@Override
	public void startWeatherMessageExtract() {
		initDerbyDB();
		try {
			FTPUtil.InitFTPClient(this.ftpIP, this.ftpPort, this.ftpUserName, this.ftpPasswd, this.ftpControlEncoding);
			List<FTPFileInfo> listFTPFileInfo = FTPUtil.ListFiles(ftpZTYXQKDir);
			for(FTPFileInfo ftpFileTemp : listFTPFileInfo) {
				System.out.println(ftpFileTemp.getName());
			}
			FTPUtil.CloseFTPClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void initDerbyDB() {
		try {
			Connection connection = derbyDS.getConnection();
			boolean boo = isTableExist(connection,"TOCC_ZYYXQK_RECORD");
			if(!boo) {
				createTables(connection);
			}else {
				logger.info("tocc_zyyxqk_record is exist! ");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public boolean createTables(Connection conn) {
		String createZTYXQLRecord = "create table tocc_zyyxqk_record( id varchar(32) primary key, downLoadFileName varchar(200),downLoadTime varchar(50),isHandle varchar(1))";
		boolean result = false;
		try {
			Statement state = conn.createStatement();
			result =state.execute(createZTYXQLRecord);
			state.close();
			logger.info("create table tocc_zyyxqk_record success! ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isTableExist( Connection conn,String tableName){
		boolean result = false;
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);  
            result = rs.next();
            rs.close();
        } catch (SQLException e) {  
        	logger.info("检查表是否存在出现sql异常",e);  
        }  
        return result;  
    } 
	
	public String getFtpIP() {
		return ftpIP;
	}

	public void setFtpIP(String ftpIP) {
		this.ftpIP = ftpIP;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getFtpPasswd() {
		return ftpPasswd;
	}

	public void setFtpPasswd(String ftpPasswd) {
		this.ftpPasswd = ftpPasswd;
	}

	public String getFtpZTYXQKDir() {
		return ftpZTYXQKDir;
	}

	public void setFtpZTYXQKDir(String ftpZTYXQKDir) {
		this.ftpZTYXQKDir = ftpZTYXQKDir;
	}

	public String getDownLoadZTYXQKDir() {
		return downLoadZTYXQKDir;
	}

	public void setDownLoadZTYXQKDir(String downLoadZTYXQKDir) {
		this.downLoadZTYXQKDir = downLoadZTYXQKDir;
	}

	public String getFtpControlEncoding() {
		return ftpControlEncoding;
	}

	public void setFtpControlEncoding(String ftpControlEncoding) {
		this.ftpControlEncoding = ftpControlEncoding;
	}

	public DataSource getDerbyDS() {
		return derbyDS;
	}

	public void setDerbyDS(DataSource derbyDS) {
		this.derbyDS = derbyDS;
	}

	
	
}

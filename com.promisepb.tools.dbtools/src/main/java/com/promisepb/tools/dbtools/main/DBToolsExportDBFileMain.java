/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dbtools.main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.dbtools.DBTools;
import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.dbutils.PBDBType;
import com.promisepb.utils.dbutils.PBDBUtil;
import com.promisepb.utils.dbutils.vo.ColumnDesc;

/**  
 * 功能描述: 调用dbtools 输出数据库设计文档
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月24日 下午2:20:03  
 */
public class DBToolsExportDBFileMain {
	
	private static final Logger logger = LoggerFactory.getLogger(DBToolsExportDBFileMain.class);
	
	/**
	 * 初始化配置文件
	 * @return
	 */
	public Properties  initConfig() {
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream (DBToolsExportDBFileMain.class.getResourceAsStream("/config.properties"));
			prop.load(in);     ///加载属性列表
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public void executeExportFile() {
		Properties prop = initConfig();
		logger.info("加载config.properties成功！");
		Map<String,Object> map = new HashMap<String,Object>();
		Iterator<String> it=prop.stringPropertyNames().iterator();
		 while(it.hasNext()){
			 	String key=it.next();
		        map.put(key, prop.getProperty(key));
		}
		Connection connection = null;
		if(PBDBType.Oracle ==PBDBUtil.GetDataBaseTypeByJDBCURL(prop.getProperty("jdbc.url"))) {
			connection = PBDBConnection.GetOracleConnection(prop.getProperty("jdbc.url"),prop);
		}else if(PBDBType.PostgreSQL ==PBDBUtil.GetDataBaseTypeByJDBCURL(prop.getProperty("jdbc.url"))) {
			connection = PBDBConnection.GetPostGresConnection(prop.getProperty("jdbc.url"), prop.getProperty("user"), prop.getProperty("password"));
		}else if(PBDBType.DM==PBDBUtil.GetDataBaseTypeByJDBCURL(prop.getProperty("jdbc.url"))) {
			connection = PBDBConnection.GetDMConnection(prop.getProperty("jdbc.url"),prop);
		}
		if(null!=connection) {
			logger.info("打开数据源连接成功！");
		}else {
			logger.info("打开数据源连接失败！");
		}
        List<String> views = PBDBUtil.GetTables(connection);
		List<String> tables = new ArrayList<String>();
        for(String viewname : views){
            try {
            	 List<ColumnDesc> result = PBDBUtil.GetTableStructure(connection, viewname.replace(":", ""));
            	 tables.add(viewname);
            }catch(Exception e) {
            	logger.info(e.getMessage());
            	logger.info("获取表："+viewname+"信息有误！");
            }
        }
        logger.info("初始化table成功！共计："+tables.size()+" 张表！");
		DBTools.ExportDBTableStructureToWord(prop.getProperty("path"), connection, tables,map);
		PBDBConnection.CloseConnection(connection);
		logger.info("关闭数据源连接！");
		
	}
	
	public static void main(String[] args) {
		DBToolsExportDBFileMain main = new DBToolsExportDBFileMain();
		main.executeExportFile();
	}

}

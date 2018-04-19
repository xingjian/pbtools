/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.iccard.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 功能描述: 公交都市系统数据工具类
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年3月13日 下午2:16:07  
 */
public class BusCityUtil {
	
	private static Logger logger = LoggerFactory.getLogger(BusCityUtil.class);
	/**
	 * 更新busline adcd为空的数据
	 * @param connection
	 * @throws Exception
	 */
	 public static void UpdateBuslineADCDNAME(Connection connection) throws Exception{
	        String sqlQuery = "select id,admincodel,admincoder from navigationline_beijing";
	        ResultSet rsNavigation = connection.createStatement().executeQuery(sqlQuery);
	        Map<String,String> navMap = new HashMap<String,String>();
	        while(rsNavigation.next()){
	            if(null!=rsNavigation.getString(2)){
	                navMap.put(rsNavigation.getString(1), rsNavigation.getString(2)+","+rsNavigation.getString(3));
	            }
	        }
	        String sqlQueryBusLine = "select id,label from bj_buscity_busline";
	        String updateSQL = "update bj_buscity_busline set adcds=? where id=?";
	        PreparedStatement psUpdate = connection.prepareStatement(updateSQL);
	        ResultSet rsBusLine = connection.createStatement().executeQuery(sqlQueryBusLine);
	        int index = 0;
	        while(rsBusLine.next()){
	            index++;
	            String id = rsBusLine.getString(1);
	            String label = rsBusLine.getString(2);
	            String queryBusline = "select navigationid from bj_buscity_buslinelink where buslineid='"+id+"'";
	            ResultSet rsSub = connection.createStatement().executeQuery(queryBusline);
	            Map<String,String> mapNames = new HashMap<String,String>();
	            while(rsSub.next()){
	                String navigationid = rsSub.getString(1);
	                String name = navMap.get(navigationid);
	                if(null!=name&&!name.equals("")&&!name.equals("null")){
	                	String[] nameArray = name.split(",");
	                    mapNames.put(nameArray[0], "1");
	                    mapNames.put(nameArray[1], "1");
	                }
	                
	            }
	            String namesstr = "";
	            for (Map.Entry<String, String> entry : mapNames.entrySet()) {
	                namesstr = namesstr+","+entry.getKey();
	            }
	            if(namesstr.length()>1){
	                namesstr = namesstr.substring(1, namesstr.length());
	            }
	            psUpdate.setString(1, namesstr);
	            psUpdate.setString(2, id);
	            psUpdate.addBatch();
	            if(index%5000==0){
	                psUpdate.executeBatch();
	            }
	            logger.info(index+"->"+label+"("+namesstr+")");
	        }
	        psUpdate.executeBatch();
	    }
}

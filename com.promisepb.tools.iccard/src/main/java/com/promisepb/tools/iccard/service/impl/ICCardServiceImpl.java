/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.iccard.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.iccard.service.ICCardService;
import com.promisepb.tools.iccard.utils.BusCityUtil;
import com.promisepb.tools.iccard.vo.BusLineVO;
import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.vividsolutions.jts.geom.Point;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年3月13日 上午10:22:52  
 */
public class ICCardServiceImpl implements ICCardService {

	private static Logger logger = LoggerFactory.getLogger(ICCardServiceImpl.class);
	private String jdbcURL;
	private String userName;
	private String passwd;
	private Connection connect;
	private Connection connectMySQL;
	private String tempworkspace;
	private int sheetNumMax = 100000;
	private SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );
	private String mysqlJdbcURL;
	private String mysqlUserName;
	private String mysqlPasswd;
	
	
	public void updateBusLineADCDNAME() {
		try {
			BusCityUtil.UpdateBuslineADCDNAME(connect);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		logger.info("ICCardServiceImpl call init method.");
		connect = PBDBConnection.GetPostGresConnection(jdbcURL, userName, passwd);
	}
	
	public void getMysqlConnect() {
		if(null==connectMySQL) {
			connectMySQL = PBDBConnection.GetPostGresConnection(mysqlJdbcURL, mysqlUserName, mysqlPasswd);
		}
	}
	
	public void close() {
		logger.info("ICCardServiceImpl call close method.");
		PBDBConnection.CloseConnection(connect);
	}
	
	@Override
	public List<BusLineVO> getBusLineByADCD(String code) {
		String querySQL = "";
		if(null==code||code.trim().equals("")) {
			querySQL =  "select id,label,name,runtime,length,sygs,linecode from bj_buscity_busline where  isvalid='1' and linenum !='夜' order by name";
		}else {
			querySQL = "select id,label,name,runtime,length,sygs,linecode from bj_buscity_busline where adcds like '%"+code+"%' and isvalid='1' and linenum !='夜' order by name";
		}
		
		List<BusLineVO> busLineVOList = new ArrayList<BusLineVO>();
		try {
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery(querySQL);
			while(resultSet.next()) {
				String id = resultSet.getString(1);
				String label = resultSet.getString(2);
				String name = resultSet.getString(3);
				String runtime = resultSet.getString(4);
				int length = resultSet.getInt(5);
				String sygs = resultSet.getString(6);
				String lineCode = resultSet.getString(7);
				BusLineVO blvoTemp = new BusLineVO();
				blvoTemp.setId(id);
				blvoTemp.setCompany(sygs);
				blvoTemp.setLabel(label);
				blvoTemp.setLength(length);
				blvoTemp.setRunTime(runtime);
				blvoTemp.setName(name);
				blvoTemp.setLineCode(lineCode);
				busLineVOList.add(blvoTemp);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return busLineVOList;
	}

	@Override
	public BusLineVO getBusLineZXXS(BusLineVO blvo) {
		String busLineID = blvo.getId();
		String querySQL = "select index,st_astext(the_geom) from bj_buscity_busstation t1,(select min(index) m1,max(index) m2 from bj_buscity_busstation where buslineid='"+busLineID+"') t2  where t1.index in (t2.m1,t2.m2)  and t1.buslineid='"+busLineID+"' order by index";
		try {
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery(querySQL);
			resultSet.next();
			String minIndex = resultSet.getString(1);
			String minWKT = resultSet.getString(2);
			resultSet.next();
			String maxIndex = resultSet.getString(1);
			String maxWKT = resultSet.getString(2);
			blvo.setMinIndex(minIndex);
			blvo.setMinWKT(minWKT);
			blvo.setMaxIndex(maxIndex);
			blvo.setMaxWKT(maxWKT);
			Point p1 = PBGTGeometryUtil.createPointByWKT(minWKT);
			Point p2 = PBGTGeometryUtil.createPointByWKT(maxWKT);
			PBGTGeometryUtil.createPointByWKT(maxWKT);
			double distance = PBGTGeometryUtil.GetDistance84(p1.getX(), p1.getY(), p2.getX(), p2.getY());
			blvo.setDistance(distance);
			blvo.setZxxs(blvo.getLength()/blvo.getDistance());
			resultSet.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blvo;
	}

	@Override
	public void exportFileBusLineByADCD(String code) {
		List<BusLineVO> list = getBusLineByADCD(code);
		for(int i=0;i<list.size();i++) {
			BusLineVO blvoTemp = list.get(i);
			getBusLineZXXS(blvoTemp);
		}
		PBPOIExcelUtil.ExportDataByList(list, tempworkspace+File.separator+format.format(new Date())+"-"+code+".xls");
	}
	
	@Override
	public void copyBusLineToMySQL() {
		String querySQL = "SELECT id, label, name, arrow , version, state, isvalid, linecode, starttime, endtime, ykt_sxx, adcds FROM bj_buscity_busline";
		String insertSQL = "INSERT INTO bj_buscity_busline ( id, label, name, arrow , version, state, isvalid, linecode, starttime, endtime, ykt_sxx, adcds)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			getMysqlConnect();
			Statement statement = connect.createStatement();
			PreparedStatement ps = connectMySQL.prepareStatement(insertSQL);
			ResultSet resultSet = statement.executeQuery(querySQL);
			while(resultSet.next()) {
				String id = resultSet.getString(1);
				String label = resultSet.getString(2);
				String name = resultSet.getString(3);
				String arrow = resultSet.getString(4);
				String version = resultSet.getString(5);
				String state =resultSet.getString(6);
			    String isvalid = resultSet.getString(7);
			    int linecode = resultSet.getInt(8);
			    String starttime = resultSet.getString(9);
			    String endtime = resultSet.getString(10);
			    int ykt_sxx = resultSet.getInt(11);
			    String  adcds = resultSet.getString(12);
			    ps.setString(1, id);
			    ps.setString(2, label);
			    ps.setString(3, name);
			    ps.setString(4, arrow);
			    ps.setString(5, version);
			    ps.setString(6, state);
			    ps.setString(7, isvalid);
			    ps.setInt(8, linecode);
			    ps.setString(9, starttime);
			    ps.setString(10, endtime);
			    ps.setInt(11, ykt_sxx);
			    ps.setString(12, adcds);
			    ps.addBatch();
			}
			resultSet.close();
			statement.close();
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void copyBusStationToMySQL() {
		String querySQL = "SELECT id, name, index , stationid, buslineid, adcdcode, adcdname FROM bj_buscity_busstation";
		String insertSQL = "INSERT INTO bj_buscity_busstation ( id, nname, iindex , stationid, buslineid, adcdcode, adcdname)  VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			getMysqlConnect();
			Statement statement = connect.createStatement();
			PreparedStatement ps = connectMySQL.prepareStatement(insertSQL);
			ResultSet resultSet = statement.executeQuery(querySQL);
			while(resultSet.next()) {
				String id = resultSet.getString(1);
				String name = resultSet.getString(2);
				int index = resultSet.getInt(3);
				int stationid = resultSet.getInt(4);
				String buslineid = resultSet.getString(5);
				String adcdcode =resultSet.getString(6);
			    String adcdname = resultSet.getString(7);
			    
			    ps.setString(1, id);
			    ps.setString(2, name);
			    ps.setInt(3, index);
			    ps.setInt(4, stationid);
			    ps.setString(5, buslineid);
			    ps.setString(6, adcdcode);
			    ps.setString(7, adcdname);
			    ps.addBatch();
			}
			resultSet.close();
			statement.close();
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public Connection getConnect() {
		return connect;
	}

	public void setConnect(Connection connect) {
		this.connect = connect;
	}

	public String getTempworkspace() {
		return tempworkspace;
	}

	public void setTempworkspace(String tempworkspace) {
		this.tempworkspace = tempworkspace;
	}

	public Map<String,String> getMapBusNum(String busNumCSVFilePath) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			FileInputStream fis = new FileInputStream(busNumCSVFilePath);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            boolean isFirst = true;
            while((str = br.readLine())!=null){
            	if(isFirst) {
            		isFirst = false;
            		continue;
            	}
                if(str.trim()!=""){
                	str = str.replaceAll("\"","");
                	String[] strArrTemp = str.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1); 
    				if(null!=strArrTemp&&strArrTemp.length==14) {
    					int carnumber = getIntValue(strArrTemp[2]);
    					int  vehiclecode  = getIntValue(strArrTemp[4]);
    					map.put(carnumber+"-"+vehiclecode, carnumber+""+vehiclecode);
    				}
                }
            }
            br.close();
            isr.close();
            fis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public void exportICCardByBusNum(String busNumCSVFilePath, String icCardCSVFilePath) {
		try {
			Map<String,String> map = getMapBusNum(busNumCSVFilePath);
            getMysqlConnect();
    		String insertSQL = "insert into iccard_busnum_result (id,cardid,onstation,ontime,offstation,offtime,onlinecode, buscode, carnumber, tmode, length, trace, offlinecode, transform, filltype, onbusorder, offbusorder, onstationname, offstationname, onbusorderold , offbusorderold, arraw, wrongnumber) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    		PreparedStatement ps = connectMySQL.prepareStatement(insertSQL);
			int lineNum = 0;
			FileInputStream fis = new FileInputStream(icCardCSVFilePath);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            boolean isFirst = true;
            while((str = br.readLine())!=null){
            	if(isFirst) {
            		isFirst = false;
            		continue;
            	}
                if(str.trim()!=""){
                	String[] strArrTemp = str.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1); 
    				if(null!=strArrTemp&&strArrTemp.length==24) {
    					String id = strArrTemp[0];
    					String cardid = strArrTemp[2];
    					int onstation = getIntValue(strArrTemp[3]);
    					String  ontime  = strArrTemp[4];
    					int  offstation  = getIntValue(strArrTemp[5]);
    					String  offtime   = strArrTemp[6];
    					String  onlinecode  = strArrTemp[7];
    					String  buscode   = strArrTemp[8];
    					int  carnumber  = getIntValue(strArrTemp[9]);
    					String  tmode    = strArrTemp[10];
    					int  length    = getIntValue(strArrTemp[11]);
    					String  trace   = strArrTemp[12];
    					String  offlinecode  = strArrTemp[13];
    					int  transform  = getIntValue(strArrTemp[14]);
    					int  filltype   = getIntValue(strArrTemp[15]);
    					int  onbusorder  = getIntValue(strArrTemp[16]);
    					int  offbusorder  = getIntValue(strArrTemp[17]);
    					String  onstationname = strArrTemp[18];
    					String  offstationname = strArrTemp[19];
    					int  onbusorderold = getIntValue(strArrTemp[20]);
    					int  offbusorderold = getIntValue(strArrTemp[21]);
    					int  arraw = getIntValue(strArrTemp[22]);
    					int  wrongnumber  = getIntValue(strArrTemp[23]);
    					if(null!=buscode&&!(buscode.trim().equals(""))&&map.containsKey(carnumber+"-"+Integer.parseInt(buscode))) {
    						ps.setString(1, id);
        					ps.setString(2, cardid);
        					ps.setInt(3, onstation);
        					ps.setString(4, ontime);
        					ps.setInt(5, offstation);
        					ps.setString(6, offtime);
        					ps.setString(7, onlinecode);
        					ps.setString(8, buscode);
        					ps.setInt(9, carnumber);
        					ps.setString(10, tmode);
        					ps.setInt(11, length);
        					ps.setString(12, trace);
        					ps.setString(13, offlinecode);
        					ps.setInt(14, transform);
        					ps.setInt(15, filltype);
        					ps.setInt(16, onbusorder);
        					ps.setInt(17, offbusorder);
        					ps.setString(18, onstationname);
        					ps.setString(19, offstationname);
        					ps.setInt(20, onbusorderold);
        					ps.setInt(21, offbusorderold);
        					ps.setInt(22, arraw);
        					ps.setInt(23, wrongnumber);
        					ps.addBatch();
        					lineNum++;
        					if(lineNum%50000==0) {
        						ps.executeBatch();
        						logger.info("提交了"+lineNum+"条数据。");
        					}
    					}
    				}else {
    					logger.info("lineNum : "+lineNum+" 数据缺少项。");
    				}
                }
            }
            ps.executeBatch();
            logger.info("提交了"+lineNum+"条数据。");
            ps.close();
            br.close();
            isr.close();
            fis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void importBusNumCSV(String csvFilePath) {
		getMysqlConnect();
		String insertSQL = "insert into bus_num (id,datatime,carnumber,linecode,vehiclecode,station,ontime,onpeople,offpeople,arrow,orderfield,people,dm,cardcount) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = connectMySQL.prepareStatement(insertSQL);
			int lineNum = 0;
			FileInputStream fis = new FileInputStream(csvFilePath);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            boolean isFirst = true;
            while((str = br.readLine())!=null){
            	if(isFirst) {
            		isFirst = false;
            		continue;
            	}
                if(str.trim()!=""){
                	str = str.replaceAll("\"","");  
                	String[] strArrTemp = str.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1); 
    				if(null!=strArrTemp&&strArrTemp.length==14) {
    					String id = strArrTemp[0];
    					String datatime = strArrTemp[1];
    					int carnumber = getIntValue(strArrTemp[2]);
    					String  linecode  = strArrTemp[3];
    					int  vehiclecode  = getIntValue(strArrTemp[4]);
    					String  station   = strArrTemp[5];
    					String  ontime  = strArrTemp[6];
    					int  onpeople   = getIntValue(strArrTemp[7]);
    					int  offpeople  = getIntValue(strArrTemp[8]);
    					int  arrow    = getIntValue(strArrTemp[9]);
    					int  orderfield    = getIntValue(strArrTemp[10]);
    					int  people   = getIntValue(strArrTemp[11]);
    					String  dm  = strArrTemp[12];
    					int  cardcount  = getIntValue(strArrTemp[13]);
    					
    					ps.setString(1, id);
    					ps.setString(2, datatime);
    					ps.setInt(3, carnumber);
    					ps.setString(4, linecode);
    					ps.setInt(5, vehiclecode);
    					ps.setString(6, station);
    					ps.setString(7, ontime);
    					ps.setInt(8, onpeople);
    					ps.setInt(9, offpeople);
    					ps.setInt(10, arrow);
    					ps.setInt(11, orderfield);
    					ps.setInt(12, people);
    					ps.setString(13, dm);
    					ps.setInt(14, cardcount);
    					ps.addBatch();
    					lineNum++;
    					if(lineNum%50000==0) {
    						ps.executeBatch();
    						logger.info("提交了"+lineNum+"条数据。");
    					}
    				}else {
    					logger.info("lineNum : "+lineNum+" 数据缺少项。");
    				}
                }
            }
            ps.executeBatch();
            logger.info("提交了"+lineNum+"条数据。");
            ps.close();
            br.close();
            isr.close();
            fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void importICCarCSV(String csvFilePath) {
		getMysqlConnect();
		String insertSQL = "insert into iccard_bnk (id,cardid,onstation,ontime,offstation,offtime,onlinecode, buscode, carnumber, tmode, length, trace, offlinecode, transform, filltype, onbusorder, offbusorder, onstationname, offstationname, onbusorderold , offbusorderold, arraw, wrongnumber) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = connectMySQL.prepareStatement(insertSQL);
			int lineNum = 0;
			FileInputStream fis = new FileInputStream(csvFilePath);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            boolean isFirst = true;
            while((str = br.readLine())!=null){
            	if(isFirst) {
            		isFirst = false;
            		continue;
            	}
                if(str.trim()!=""){
                	String[] strArrTemp = str.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1); 
    				if(null!=strArrTemp&&strArrTemp.length==24) {
    					String id = strArrTemp[0];
    					String cardid = strArrTemp[2];
    					int onstation = getIntValue(strArrTemp[3]);
    					String  ontime  = strArrTemp[4];
    					int  offstation  = getIntValue(strArrTemp[5]);
    					String  offtime   = strArrTemp[6];
    					String  onlinecode  = strArrTemp[7];
    					String  buscode   = strArrTemp[8];
    					int  carnumber  = getIntValue(strArrTemp[9]);
    					String  tmode    = strArrTemp[10];
    					int  length    = getIntValue(strArrTemp[11]);
    					String  trace   = strArrTemp[12];
    					String  offlinecode  = strArrTemp[13];
    					int  transform  = getIntValue(strArrTemp[14]);
    					int  filltype   = getIntValue(strArrTemp[15]);
    					int  onbusorder  = getIntValue(strArrTemp[16]);
    					int  offbusorder  = getIntValue(strArrTemp[17]);
    					String  onstationname = strArrTemp[18];
    					String  offstationname = strArrTemp[19];
    					int  onbusorderold = getIntValue(strArrTemp[20]);
    					int  offbusorderold = getIntValue(strArrTemp[21]);
    					int  arraw = getIntValue(strArrTemp[22]);
    					int  wrongnumber  = getIntValue(strArrTemp[23]);
    					ps.setString(1, id);
    					ps.setString(2, cardid);
    					ps.setInt(3, onstation);
    					ps.setString(4, ontime);
    					ps.setInt(5, offstation);
    					ps.setString(6, offtime);
    					ps.setString(7, onlinecode);
    					ps.setString(8, buscode);
    					ps.setInt(9, carnumber);
    					ps.setString(10, tmode);
    					ps.setInt(11, length);
    					ps.setString(12, trace);
    					ps.setString(13, offlinecode);
    					ps.setInt(14, transform);
    					ps.setInt(15, filltype);
    					ps.setInt(16, onbusorder);
    					ps.setInt(17, offbusorder);
    					ps.setString(18, onstationname);
    					ps.setString(19, offstationname);
    					ps.setInt(20, onbusorderold);
    					ps.setInt(21, offbusorderold);
    					ps.setInt(22, arraw);
    					ps.setInt(23, wrongnumber);
    					ps.addBatch();
    					lineNum++;
    					if(lineNum%50000==0) {
    						ps.executeBatch();
    						logger.info("提交了"+lineNum+"条数据。");
    					}
    				}else {
    					logger.info("lineNum : "+lineNum+" 数据缺少项。");
    				}
                }
            }
            ps.executeBatch();
            logger.info("提交了"+lineNum+"条数据。");
            ps.close();
            br.close();
            isr.close();
            fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void filterICCardBusNumResultByLineCodes(Map<String,String> lineCodeMap) {
		getMysqlConnect();
		try {
			connectMySQL.setAutoCommit(false);
			PreparedStatement ps = connectMySQL.prepareStatement("delete from iccard_busnum_result where id=?");
			ps.setFetchSize(500);
			ResultSet rs = connectMySQL.createStatement().executeQuery("select id,onlinecode from iccard_busnum_result");
			int index = 0;
			while(rs.next()) {
				String id = rs.getString(1);
				String onLineCode = rs.getString(2);
				if(!lineCodeMap.containsKey(onLineCode)) {
					ps.setString(1, id);
					ps.addBatch();
					index++;
					if(index%50000==0) {
						ps.executeBatch();
						ps.clearBatch();
					}
				}
			}
			ps.executeBatch();
			connectMySQL.commit();
			ps.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getIntValue(String str) {
		if(null==str||str.trim().equals("")) {
			return -999999;
		}else {
			return Integer.parseInt(str);
		}
	}
	
	public String getMysqlJdbcURL() {
		return mysqlJdbcURL;
	}

	public void setMysqlJdbcURL(String mysqlJdbcURL) {
		this.mysqlJdbcURL = mysqlJdbcURL;
	}

	public String getMysqlUserName() {
		return mysqlUserName;
	}

	public void setMysqlUserName(String mysqlUserName) {
		this.mysqlUserName = mysqlUserName;
	}

	public String getMysqlPasswd() {
		return mysqlPasswd;
	}

	public void setMysqlPasswd(String mysqlPasswd) {
		this.mysqlPasswd = mysqlPasswd;
	}
	
}

/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.deegree.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.deegree.service.CreateZZGMLService;
import com.promisepb.tools.deegree.vo.DataSourceConfig;
import com.promisepb.tools.deegree.vo.ZZBusGPS;
import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.dbutils.PBDBType;
import com.promisepb.utils.mathutils.PBMathUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月21日 下午5:27:29  
 */
public class CreateZZGMLServiceImpl implements CreateZZGMLService {
	private static Logger logger = LoggerFactory.getLogger(CreateZZGMLServiceImpl.class);
	private String toFilePath;
	private String headerXML;
	private String envelopeXML;
	private String contentXML;
	private String sql;
	private DataSourceConfig dsc;
	
	@Override
	public void createGMLFile() {
		try {
			Connection connection  = PBDBConnection.GetOracleConnection(dsc.getJdbcURL(), dsc.getUserName(), dsc.getPasswd());
			if(null!=connection) {
				logger.info("init dbConnection success !");
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
				List<ZZBusGPS> listVO = new ArrayList<ZZBusGPS>();
				Map<String,String> mapID = new HashMap<String,String>();
				while(rs.next()) {
					String id = rs.getString(2);
					String x = rs.getString(3);
					String y = rs.getString(4);
					if(!mapID.containsKey(id)) {
						ZZBusGPS vo = new ZZBusGPS();
						vo.setId(id);
						vo.setTime(PBStringUtil.GetCurrentDateString());
						vo.setX(x);
						vo.setY(y);
						vo.setSpeed(PBMathUtil.GetRandomInt(10, 70)+"");
						vo.setMzl(PBMathUtil.GetRandomInt(10, 120)+"");
						mapID.put(id, "");
						listVO.add(vo);
					}
					
				}
				rs.close();
				statement.close();
				connection.close();
				createDataFile(listVO);
				PBPOIExcelUtil.ExportDataByList(listVO, toFilePath+File.separator+"zz-bus.xlsx");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	 public <T> String createDataFile(List<T> listObject){
	        String ret = "success";
	        try {
	            File output = new File(toFilePath+File.separator+"zz-bus.gml");
	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
	            String envelopeContent = getFileString(this.envelopeXML);
	            String header = getFileString(this.headerXML);
	            String content = getFileString(this.contentXML);
	            writer.write(header,0,header.length());
	            writer.write(envelopeContent,0,envelopeContent.length());
	            
	            Field[] fds = null;
	            Class clazz = null;
	            Map<String,String> map = new HashMap<String, String>();
	            for (Object object : listObject){
	                //获取集合中的对象类型
	                if (null == clazz) {
	                    clazz = object.getClass();
	                    //获取他的字段数组
	                    fds = clazz.getDeclaredFields();
	                    for(int i=0;i<fds.length;i++){  
	                        String columnLabel = fds[i].getName().toString();
	                        map.put(columnLabel, "get"+PBStringUtil.ChangeFirstUpper(columnLabel));
	                    }
	                }
	                String c = content;
	                for(Map.Entry<String, String> entry : map.entrySet()){
	                    Method metd = clazz.getMethod(entry.getValue(), null);
	                    String attributeName = entry.getKey();
	                    String attributeValue = metd.invoke(object, null).toString();
	                    c = c.replace("${"+attributeName+"}",attributeValue);
	                }
	                writer.write(c,0,c.length());
	            }
	            writer.write("</gml:FeatureCollection>",0,24);
	            writer.flush();
	            writer.close();
	        } catch (Exception e) {
	            ret = "failture";
	            e.printStackTrace();
	        }
	        return ret;
	    }
	    
	    public String getFileString(String fileName) throws Exception{
	        InputStream m = new FileInputStream(new File(fileName));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(m, "UTF-8"));
	        StringBuffer sb = new StringBuffer();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        reader.close();
	        return sb.toString();        
	    }

	
	public String getToFilePath() {
		return toFilePath;
	}
	public void setToFilePath(String toFilePath) {
		this.toFilePath = toFilePath;
	}
	public String getHeaderXML() {
		return headerXML;
	}
	public void setHeaderXML(String headerXML) {
		this.headerXML = headerXML;
	}
	public String getEnvelopeXML() {
		return envelopeXML;
	}
	public void setEnvelopeXML(String envelopeXML) {
		this.envelopeXML = envelopeXML;
	}
	public String getContentXML() {
		return contentXML;
	}
	public void setContentXML(String contentXML) {
		this.contentXML = contentXML;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public DataSourceConfig getDsc() {
		return dsc;
	}
	public void setDsc(DataSourceConfig dsc) {
		this.dsc = dsc;
	}
	
}

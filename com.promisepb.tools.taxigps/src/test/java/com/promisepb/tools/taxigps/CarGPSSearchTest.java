/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.taxigps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.promisepb.utils.dbutils.PBDBConnection;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisutils.PBGISCoorTransformUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月2日 下午9:28:53  
 */
public class CarGPSSearchTest {

	@Test
	public void insertGPSToPostGIS() throws Exception {
		//京BV0852,京BP5760,京BN8074,京BU6802,京BR0098
		List<String> listTemp = PBFileUtil.ReadCSVFile("F:\\toccworkspace\\test\\yzf\\tttt.csv", "UTF-8");
		String urlPG = "jdbc:postgresql://localhost:5433/opengis";
        String usernamePG = "postgres";
        String passwdPG = "000000";
        Connection connectionPG = PBDBConnection.GetPostGresConnection(urlPG, usernamePG, passwdPG);
        
		String insertSQL = "INSERT INTO yzf_gps(carcode, beijingtime, wgs84lng, wgs84lat,  v_x, v_y, company,remark) VALUES (?, to_timestamp(?,'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?,?)";
		PreparedStatement psInsert = connectionPG.prepareStatement(insertSQL);
		//JYJ,13311495254,京BR6069,2017-11-01 22:10:18,116.6062927,39.9222298,0,1,1,114,CZ
		for(String strTemp : listTemp) {
			String[] arryTemp = strTemp.split(",");
			if(arryTemp.length!=11) {
				continue;
			}
			String carcode = arryTemp[2];
			if("京BV0852,京BP5760,京BN8074,京BU6802,京BR0098".indexOf(carcode)!=-1) {
				String  beijingtime = arryTemp[3];
				double  wgs84lng = Double.valueOf(arryTemp[4]);
				double  wgs84lat = Double.valueOf(arryTemp[5]);
				double[] dTemp = PBGISCoorTransformUtil.From84To02(wgs84lng, wgs84lat);
				psInsert.setString(1, carcode);
				psInsert.setString(2, beijingtime);
				psInsert.setDouble(3, wgs84lng);
				psInsert.setDouble(4, wgs84lat);
				psInsert.setDouble(5, dTemp[0]);
				psInsert.setDouble(6, dTemp[1]);
				psInsert.setString(7, arryTemp[0]);
				psInsert.setString(8, arryTemp[1]);
				psInsert.addBatch();
			}
		}
		psInsert.executeBatch();
	}
	
	@Test
	public void filterGPS() throws Exception{
		SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfShort = new SimpleDateFormat("yyyyMMddHHmm");
		Date sDate_s  = null;
		Date eDate_e =  null;
		sDate_s =  sdfShort.parse("201711012140");
		eDate_e =  sdfShort.parse("201711012220");
		
		BufferedWriter csvWtriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream("F:\\toccworkspace\\test\\yzf\\tttt.csv"), "UTF-8"), 10240);;
		List<File> fileList = new ArrayList<File>();
		PBFileUtil.GetFilesByPath("F:\\toccworkspace\\taxi_gps\\20171101GPS", fileList);
		for(File fileTemp : fileList) {
			List<String> listTemp = PBFileUtil.ReadCSVFile(fileTemp.getAbsolutePath(), "UTF-8");
			List<String> filterListResult = new ArrayList<String>();
			for(String stemp : listTemp) {
				if(null!=stemp&&!(stemp.trim().equals(""))) {
					String[] arrayTemp = stemp.split(",");
					if(arrayTemp.length==11) {
						if(belongCalendar(sdfAll.parse(arrayTemp[3]),sDate_s,eDate_e)) {
							filterListResult.add(stemp);
						}
					}
				}
			}
			PBPOIExcelUtil.WriteRow(filterListResult, csvWtriter);
			csvWtriter.flush();
		}
		csvWtriter.close();
	}
	
	
	public  boolean belongCalendar(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);
        Calendar after = Calendar.getInstance();
        after.setTime(from);

        Calendar before = Calendar.getInstance();
        before.setTime(to);

        if (date.after(after) && date.before(before)) {
            return true;
        } else {
            return false;
        }
    }
}

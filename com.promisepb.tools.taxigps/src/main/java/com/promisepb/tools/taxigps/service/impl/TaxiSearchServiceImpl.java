/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.taxigps.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.taxigps.service.TaxiSearchService;
import com.promisepb.tools.taxigps.vo.TaxiGPS;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.gisutils.PBGISCoorTransformUtil;
import com.promisepb.utils.gisutils.PBGTGeometryUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;
import com.vividsolutions.jts.geom.Coordinate;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月2日 下午5:18:23  
 */
public class TaxiSearchServiceImpl implements TaxiSearchService {

		private static final Logger logger  = LoggerFactory.getLogger(TaxiSearchServiceImpl.class);
		private String gpsFilePath;
		private String tempPath;
		private String stime_s;
		private String stime_e;
		private String etime_s;
		private String etime_e;
		private int  buffer;
		private String startPoint;
		private String endPoint;
		public SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public SimpleDateFormat sdfShort = new SimpleDateFormat("yyyyMMddHHmm");
		public BufferedWriter csvWtriter;
		public String filterGPSPath;
		public Coordinate sPoint;
		public Coordinate ePoint;
		public BufferedWriter csvResultWtriter;
		public String resultFilePath;
		public Date sDate_s  = null;
		public Date sDate_e =  null;
		public Date eDate_s =  null;
		public Date eDate_e =  null;
		public Map<String,List<TaxiGPS>> map = new HashMap<String,List<TaxiGPS>>();
		/**
		 * 初始化变量
		 */
		public void initVar() {
			logger.info("初始化参数......");
			String[] strArr1 = startPoint.split(",");
			String[] strArr2 = endPoint.split(",");
			sPoint = new Coordinate(Double.valueOf(strArr1[0]),Double.valueOf(strArr1[1]));
			ePoint = new Coordinate(Double.valueOf(strArr2[0]),Double.valueOf(strArr2[1]));
			try {
				sDate_s =  sdfShort.parse(this.stime_s);
				sDate_e =  sdfShort.parse(this.stime_e);
				eDate_s =  sdfShort.parse(this.etime_s);
				eDate_e =  sdfShort.parse(this.etime_e);
				filterGPSPath = tempPath+File.separator+"filtergps"+sdfShort.format(new Date())+".csv";
				resultFilePath = tempPath+File.separator+"result"+sdfShort.format(new Date())+".csv";
				csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filterGPSPath), "UTF-8"), 10240);
				csvResultWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFilePath), "UTF-8"), 10240);
				logger.info("初始化参数完成！.");
			}catch(Exception e) {
				e.printStackTrace();
				logger.info("初始化参数错误！");
			}
		}
		
		/**
		 * 开始执行查询
		 */
		public void execuSearch() {
			logger.info("start execuSearch......");
			try {
				initVar();
				filterGPS();
				handleFilterGPSFile();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("filter gps error!");
			}
			logger.info("end execuSearch success!");
		}
		
		/**
		 * 处理过滤之后的GPS文件
		 * @param file
		 */
		public void handleFilterGPSFile() {
			try {
				logger.info("开始比对GPS数据......");
				int s_index=0;
				int e_index=0;
				List<String> listTemp = PBFileUtil.ReadCSVFile(filterGPSPath, "UTF-8");
				Map<String,String> mapIntegerS = new HashMap<String,String>();
				Map<String,String> mapIntegerE = new HashMap<String,String>();
				for(String strTemp : listTemp) {
					String[] strTempArr = strTemp.split(",");
					TaxiGPS t1 = new TaxiGPS();
					t1.setCarCode(strTempArr[2]);
					t1.setO_x(Double.valueOf(strTempArr[4]));
					t1.setO_y(Double.valueOf(strTempArr[5]));
					t1.setBjTime(strTempArr[3]);
					t1.setCompany(strTempArr[0]);
					if(null==map.get(strTempArr[2])) {
						List<TaxiGPS> listTemp1 = new ArrayList<TaxiGPS>();
						listTemp1.add(t1);
						map.put(strTempArr[2], listTemp1);
					}else {
						map.get(strTempArr[2]).add(t1);
					}
					Date date = sdfAll.parse(strTempArr[3]);
					double[] dTemp = PBGISCoorTransformUtil.From84To02(Double.valueOf(strTempArr[4]), Double.valueOf(strTempArr[5]));
					boolean boo1 = PBStringUtil.BelongCalendar(date,sDate_s,sDate_e);
					if(boo1) {
						double distanceTemp = PBGTGeometryUtil.GetDistance84(sPoint.x, sPoint.y, dTemp[0],dTemp[1]);
						if(distanceTemp<=buffer) {
							if(null==mapIntegerS.get(strTempArr[2])) {
								mapIntegerS.put(strTempArr[2], "1");
							}
							s_index++;
						}
					}else {
						boolean boo2 = PBStringUtil.BelongCalendar(date,eDate_s,eDate_e);
						if(boo2) {
							double distanceTemp = PBGTGeometryUtil.GetDistance84(ePoint.x, ePoint.y, dTemp[0],dTemp[1]);
							if(distanceTemp<=buffer) {
								if(null==mapIntegerE.get(strTempArr[2])) {
									mapIntegerE.put(strTempArr[2],"1");
								}
								e_index++;
							}
						}
					}
				}
				logger.info("比对GPS数据完成！");
				logger.info("起点共比对出GPS数据"+s_index+"条。");
				logger.info("终点共比对出GPS数据"+e_index+"条。");
				String carCodes="";
				for (Map.Entry<String, String> entry : mapIntegerS.entrySet()) {
					String keyTemp =  entry.getKey();
					if(null!=mapIntegerE.get(keyTemp)) {
						carCodes+=keyTemp+",";
						if(null!=map.get(keyTemp)) {
							List<TaxiGPS> listTaxiGPS = map.get(keyTemp);
							Collections.sort(listTaxiGPS);
							PBPOIExcelUtil.WriteRow(listTaxiGPS, csvResultWtriter);
						}
					}
					
				}
				csvResultWtriter.write("***************************************************");
				csvResultWtriter.newLine();
				csvResultWtriter.write(carCodes.substring(0, carCodes.length()-1));
				csvResultWtriter.newLine();
				csvResultWtriter.write("**************************************************************");
				csvResultWtriter.flush();
				csvResultWtriter.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 	提取gps 把整个时间范围的数据都进行提取
		 */
		public void filterGPS() throws Exception{
			logger.info("提前时间范围"+stime_s+"到"+stime_e+"开始......");
			List<File> fileList = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(gpsFilePath, fileList);
			int index = 0;
			for(File fileTemp : fileList) {
				List<String> listTemp = PBFileUtil.ReadCSVFile(fileTemp.getAbsolutePath(), "UTF-8");
				List<String> filterListResult = new ArrayList<String>();
				for(String stemp : listTemp) {
					if(null!=stemp&&!(stemp.trim().equals(""))) {
						String[] arrayTemp = stemp.split(",");
						if(arrayTemp.length==11) {
							if(PBStringUtil.BelongCalendar(sdfAll.parse(arrayTemp[3]),sDate_s,eDate_e)) {
								if(null!=arrayTemp[2]&&!(arrayTemp[2].trim().equals(""))) {
									filterListResult.add(stemp);
									index++;
								}
							}
						}
					}
				}
				PBPOIExcelUtil.WriteRow(filterListResult, csvWtriter);
				csvWtriter.flush();
				logger.info(fileTemp.getAbsolutePath()+" handle success!");
			}
			csvWtriter.close();
			logger.info("提前时间范围"+stime_s+"到"+stime_e+"结束！共计查找GPS数量"+index+"条！");
		}
		
		public String getGpsFilePath() {
			return gpsFilePath;
		}
		public void setGpsFilePath(String gpsFilePath) {
			this.gpsFilePath = gpsFilePath;
		}
		public String getTempPath() {
			return tempPath;
		}
		public void setTempPath(String tempPath) {
			this.tempPath = tempPath;
		}
		public String getStime_s() {
			return stime_s;
		}
		public void setStime_s(String stime_s) {
			this.stime_s = stime_s;
		}
		public String getStime_e() {
			return stime_e;
		}
		public void setStime_e(String stime_e) {
			this.stime_e = stime_e;
		}
		public String getEtime_s() {
			return etime_s;
		}
		public void setEtime_s(String etime_s) {
			this.etime_s = etime_s;
		}
		public String getEtime_e() {
			return etime_e;
		}
		public void setEtime_e(String etime_e) {
			this.etime_e = etime_e;
		}
		public int getBuffer() {
			return buffer;
		}
		public void setBuffer(int buffer) {
			this.buffer = buffer;
		}
		public String getStartPoint() {
			return startPoint;
		}
		public void setStartPoint(String startPoint) {
			this.startPoint = startPoint;
		}
		public String getEndPoint() {
			return endPoint;
		}
		public void setEndPoint(String endPoint) {
			this.endPoint = endPoint;
		}
		
}

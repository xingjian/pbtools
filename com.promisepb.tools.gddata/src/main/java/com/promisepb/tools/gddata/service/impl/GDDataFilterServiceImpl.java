/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.gddata.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.gddata.service.GDDataFilterService;
import com.promisepb.tools.gddata.vo.GDData;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:高德路况数据过滤
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月30日 上午11:22:59  
 */
public class GDDataFilterServiceImpl implements GDDataFilterService {

	public static Logger logger = LoggerFactory.getLogger(GDDataFilterServiceImpl.class);
	public String exportDataPath;
	public String gdDataPath;
	public String roadids;
	public String charSet;
	
	@Override
	public void filterGDDataByRoadIDs() {
		try {
			List<File> result = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(gdDataPath, result);
			String[] roadidArr = roadids.split(",");
			Map<String, String> mapGD = new HashMap<String, String>();
			for (String roadidTemp : roadidArr) {
				mapGD.put(roadidTemp, roadidTemp);
			}
			for (File fileTemp : result) {
				String zipFileName = PBFileUtil.GetFileName(fileTemp.getPath());
				List<GDData> resultList = new ArrayList<GDData>();
				BufferedWriter csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportDataPath+File.separator+"gd_filter_data_"+zipFileName+"_"+PBStringUtil.GetDateString("yyyyMMddHHmmss", new Date())+".csv"), charSet), 10240);
				ZipFile zf = new ZipFile(fileTemp);
				InputStream in = new BufferedInputStream(new FileInputStream(fileTemp));
				ZipInputStream zin = new ZipInputStream(in);
				ZipEntry ze;
				while ((ze = zin.getNextEntry()) != null) {
					if (!ze.isDirectory()) {
						long size = ze.getSize();
						if (size > 0) {
							BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze),charSet));
							String line;
							String fileNameTemp = ze.getName();
							String subFileNameTemp = fileNameTemp.substring(fileNameTemp.lastIndexOf("/")+1);
							while ((line = br.readLine()) != null) {
								String[] arr = line.split(",");
								if (null == arr || arr.length != 4) {
									continue;
								} else {
									String roadid = arr[1];
									if (null != mapGD.get(roadid)) {
										GDData gdDataTemp = new GDData();
										gdDataTemp.setBjTime(subFileNameTemp);
										gdDataTemp.setLevel(arr[2]);
										gdDataTemp.setRoadID(roadid);
										gdDataTemp.setRoadName(arr[0]);
										gdDataTemp.setSpeed(arr[3]);
										resultList.add(gdDataTemp);
									}
								}
							}
							br.close();
						}
					}
				}
				zin.closeEntry();
				zin.close();
				in.close();
				zf.close();
				Collections.sort(resultList);
				PBPOIExcelUtil.WriteRow(resultList, csvWtriter);
				csvWtriter.flush();
				csvWtriter.close();
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getExportDataPath() {
		return exportDataPath;
	}

	public void setExportDataPath(String exportDataPath) {
		this.exportDataPath = exportDataPath;
	}

	public String getGdDataPath() {
		return gdDataPath;
	}

	public void setGdDataPath(String gdDataPath) {
		this.gdDataPath = gdDataPath;
	}

	public String getRoadids() {
		return roadids;
	}

	public void setRoadids(String roadids) {
		this.roadids = roadids;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	
	
}

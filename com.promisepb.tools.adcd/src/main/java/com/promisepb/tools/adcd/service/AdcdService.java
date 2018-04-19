/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.adcd.service;

import java.util.ArrayList;
import java.util.List;

import com.promisepb.tools.adcd.vo.ADCDVO;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年2月28日 下午3:07:17  
 */
public interface AdcdService {
	
	/**
	 * 开始启动任务
	 */
	public void startTask();
	
	List<ADCDVO> adcdvoList = new ArrayList<ADCDVO>();
	/**
	 * 获取行政区划代码的页面并保存到指定的路径
	 * @param htmlURL 初始页面网址
	 * @param regStr 抽取规则
	 * @param isLoop 是否循环抽取
	 * @param depth 指定页面循环深度
	 * @return 运行结果
	 */
	public String getADCDPage(String htmlURL,String regStr,boolean isLoop,int depth,String method);
	
	/**
	 * 
	 * @param htmlURL
	 * @param regStr
	 * @param isLoop
	 * @param depth
	 * @param method
	 * @return
	 */
	public String getVillageListByURL(String htmlURL, String regStr,boolean isLoop, int depth,String method);
}

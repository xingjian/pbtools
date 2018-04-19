/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.busline.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**  
 * 功能描述: busline 程序的入口函数
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年2月27日 下午1:03:54  
 */
public class BusLineMain {

	/**
	 * 启动函数
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
	}

}

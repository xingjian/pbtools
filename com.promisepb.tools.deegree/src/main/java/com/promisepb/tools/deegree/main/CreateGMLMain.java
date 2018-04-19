/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.deegree.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月21日 下午5:20:03  
 */
public class CreateGMLMain {

	public static void main(String[] args) {
		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
	}

}

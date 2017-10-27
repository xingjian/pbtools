/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport;

import org.junit.Test;

import com.promisepb.tools.dataexport.service.impl.JcyjSubareaNetSpeedServiceImpl;

/**  
 * 功能描述:JcyjSubareaNetSpeedServiceImpl 测试用例
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月27日 下午5:29:20  
 */
public class JcyjSubareaNetSpeedServiceImplTest {

	/**
	 * 测试JcyjSubareaNetSpeedServiceImpl calcDate方法，保证星期日和星期五返回的正确性
	 */
	@Test
	public void testCalcDate() {
		JcyjSubareaNetSpeedServiceImpl jsnssi = new JcyjSubareaNetSpeedServiceImpl();
		System.out.println(jsnssi.calcData());
	}
}

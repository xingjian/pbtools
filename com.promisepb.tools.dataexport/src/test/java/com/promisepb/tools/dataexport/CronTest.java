/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.quartz.CronExpression;

/**  
 * 功能描述: 测试Cron表达式
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月27日 下午12:57:47  
 */
public class CronTest {
	
	/**
	 * 测试给林鹏飞导出数据Cron表达式 0 0 1 ? * SAT,MON,    0 0 19 ? * SAT
	 * 要求：每个星期五20点 和每个周日20点
	 */
	@Test
	public void testJTZSLPF() {
		try {
			//Seconds Minutes Hours Day-of-Month Month Day-of-Week Year
            CronExpression exp = new CronExpression("0 0 7,8 ? * *");  
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
            Date d = new Date();  
            int i = 0;  
            //循环得到接下来n此的触发时间点，供验证  
            while (i < 30) {  
                d = exp.getNextValidTimeAfter(d);  
                System.out.println(df.format(d));  
                ++i;  
            }  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }
	}
		
}

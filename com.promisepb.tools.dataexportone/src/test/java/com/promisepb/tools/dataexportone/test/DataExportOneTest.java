/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexportone.test;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.tools.dataexportone.service.impl.DataExportOneServiceImpl;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年11月1日 下午4:17:37  
 */
public class DataExportOneTest {

	private static final Logger logger = LoggerFactory.getLogger(DataExportOneTest.class);
	
	@Test
	public void testInitTimeArgs() {
		DataExportOneServiceImpl deosi = new DataExportOneServiceImpl();
		deosi.setStartTime("20170501");
		deosi.setEndTime("20170531");
		List<String> list = deosi.initTimeArgs(7);
		for(int i=0;i<list.size()-1;i++) {
			logger.info(list.get(i)+"-"+list.get(i+1));
		}
	}
}

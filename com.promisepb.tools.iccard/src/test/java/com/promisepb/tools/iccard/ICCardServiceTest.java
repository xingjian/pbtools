/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.iccard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.promisepb.tools.iccard.service.ICCardService;
import com.promisepb.tools.iccard.vo.BusLineVO;
import com.promisepb.utils.poiutils.PBPOIExcelUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年3月13日 下午12:42:24  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ICCardServiceTest {

	@Resource
	private ICCardService icCardService;
	
	@Test
	public void testGetBusLineByADCD() {
		List<BusLineVO> list = icCardService.getBusLineByADCD("110105");
		System.out.println(list.size());
	}
	
	@Test
	public void testExportFileBusLineByADCD() {
		 icCardService.exportFileBusLineByADCD("");
	}
	
	@Test
	public void testImportICCarCSV() {
		String csvPath = "E:\\tempworkspace\\iccard\\20170410-cardbody.csv";
		icCardService.importICCarCSV(csvPath);
	}
	
	@Test
	public void testImportBusNumCSV() {
		//select * from DE_DETAIL_BUS_NUMBER partition(DE_DETAIL_BUS_NUMBER20170411) where ontime>='20170410 07:30:00' and ontime<='20170410 08:30:00' and ORDERFIELD=1
		String csvPath = "E:\\tempworkspace\\iccard\\bus_num.csv";
		icCardService.importBusNumCSV(csvPath);
	}
	
	@Test
	public void testExportICCardByBusNum() {
		String csv_busnum = "E:\\tempworkspace\\iccard\\bus_num.csv";
		String csv_iccard = "E:\\tempworkspace\\iccard\\20170410-cardbody.csv";
		icCardService.exportICCardByBusNum(csv_busnum,csv_iccard);
	}
	
	@Test
	public void testFilterICCardBusNumResultByLineCodes() {
		String filePath = "E:\\tempworkspace\\iccard\\20180321-110105.xls";
		Map<String,String> map = new HashMap<String,String>();
		List<String> list = PBPOIExcelUtil.ReadXLS(filePath,",",2,649,13,13);
		for(String strTemp : list) {
			map.put(strTemp, strTemp);
		}
		icCardService.filterICCardBusNumResultByLineCodes(map);
	}
	
	@Test
	public void testStringSub() {
		String busCode = "00018603";
		System.out.println(Integer.parseInt(busCode));
	}
	
	@Test
	public void testCopyBusLineToMySQL() {
		icCardService.copyBusLineToMySQL();
	}
	
	@Test
	public void testCopyBusStationToMySQL() {
		icCardService.copyBusStationToMySQL();
	}
}

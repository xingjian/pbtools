/**
* @Copyright@2018 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.iccard.service;

import java.util.List;
import java.util.Map;

import com.promisepb.tools.iccard.vo.BusLineVO;

/**  
 * 功能描述: 处理北京工业大学王老师的数据需求服务接口定义
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2018年3月13日 上午9:35:00  
 */
public interface ICCardService {

	/**
	 * 返回行政区划内的公交线路
	 * @param code
	 */
	public List<BusLineVO> getBusLineByADCD(String code);
	public BusLineVO getBusLineZXXS(BusLineVO blvo);
	public void exportFileBusLineByADCD(String code);
	public void importICCarCSV(String csvFilePath) ;
	public void importBusNumCSV(String csvFilePath);
	public void exportICCardByBusNum(String busNumCSVFilePath,String icCardCSVFilePath);
	public void filterICCardBusNumResultByLineCodes(Map<String,String> lineCodeMap) ;
	public void copyBusLineToMySQL();
	public void copyBusStationToMySQL();
}

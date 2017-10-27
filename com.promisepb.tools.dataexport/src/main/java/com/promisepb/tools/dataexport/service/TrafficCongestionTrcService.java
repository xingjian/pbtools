/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport.service;

import java.io.File;

/**  
 * 功能描述:5分钟交通指数服务接口
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月14日 下午9:04:38  
 */
public interface TrafficCongestionTrcService {

	//导出全路网交通指数
	public File exportDataFile();
}

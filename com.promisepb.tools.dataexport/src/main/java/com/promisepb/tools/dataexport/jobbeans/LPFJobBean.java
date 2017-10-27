/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexport.jobbeans;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.promisepb.tools.dataexport.service.LPFDataExportService;

/**  
 * 功能描述:LPFJobBean
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年8月27日 下午3:27:51  
 */
public class LPFJobBean extends QuartzJobBean {

	private LPFDataExportService lpfDataExportService;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		lpfDataExportService.exportData();
	}

	public LPFDataExportService getLpfDataExportService() {
		return lpfDataExportService;
	}

	public void setLpfDataExportService(LPFDataExportService lpfDataExportService) {
		this.lpfDataExportService = lpfDataExportService;
	}
	
	
}

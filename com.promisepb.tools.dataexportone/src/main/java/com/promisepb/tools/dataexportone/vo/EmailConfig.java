/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dataexportone.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.emailutils.PBMailUtil;

/**  
 * 功能描述:Email参数封装
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年10月16日 下午10:50:14  
 */
public class EmailConfig {

	private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);
	//格式smtp#smtp.tongtusoft.com.cn#true#xingjian@tongtusoft.com.cn#passwd
	private String fromUserConfig;
	//格式pengfei_lin@qq.com,xingjian@tongtusoft.com.cn
	private String toUserConfig;
	//格式wangjingjing@bjjtw.gov.cn,youthweng@bjut.edu.cn,337827558@qq.com
	private String ccUserConfig;
	//格式xingjian@tongtusoft.com.cn
	private String errorUserConfig;
	//接收邮件地址
	private List<InternetAddress> receiveMails = new ArrayList<InternetAddress>();
	//抄送邮件地址
	private List<InternetAddress> ccMails = new ArrayList<InternetAddress>();
	//错误信息发送邮件地址
	private List<InternetAddress> errorMails = new ArrayList<InternetAddress>();
	//发送邮箱信息
	private Properties sendEmailProps = null;
	//发送邮件地址
	private InternetAddress sendMail = null;
	/**
	 * 初始化邮件config
	 */
	public void initEmailConfig() {
		logger.info("初始化邮箱信息......!");
		try {
			String[] fromUserStringArray = fromUserConfig.split("#");
			if(fromUserStringArray[4].trim().equals("null")) {
				fromUserStringArray[4] = "fld789&*(";
			}
			sendMail = new InternetAddress(fromUserStringArray[3],fromUserStringArray[3],"UTF-8");
			sendEmailProps = PBMailUtil.CreateProperties(fromUserStringArray[0], fromUserStringArray[1], fromUserStringArray[2], fromUserStringArray[3], fromUserStringArray[4]);
			if(null!=toUserConfig&&!toUserConfig.trim().equals("")) {
				String[] toUserArray = toUserConfig.split(",");
				for(int i=0;i<toUserArray.length;i++) {
					InternetAddress toMail = new InternetAddress(toUserArray[i],toUserArray[i],"UTF-8");
					receiveMails.add(toMail);
				}
			}
			if(null!=ccUserConfig&&!ccUserConfig.trim().equals("")) {
				String[] ccUserArray = ccUserConfig.split(",");
				for(int i=0;i<ccUserArray.length;i++) {
					InternetAddress ccMail = new InternetAddress(ccUserArray[i],ccUserArray[i],"UTF-8");
					ccMails.add(ccMail);
				}
			}
			if(null!=errorUserConfig&&!errorUserConfig.trim().equals("")) {
				String[] errorUserArray = errorUserConfig.split(",");
				for(int i=0;i<errorUserArray.length;i++) {
					InternetAddress errorMail = new InternetAddress(errorUserArray[i],errorUserArray[i],"UTF-8");
					errorMails.add(errorMail);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("初始化邮箱信息完成!");
	}
	public String getFromUserConfig() {
		return fromUserConfig;
	}
	public void setFromUserConfig(String fromUserConfig) {
		this.fromUserConfig = fromUserConfig;
	}
	public String getToUserConfig() {
		return toUserConfig;
	}
	public void setToUserConfig(String toUserConfig) {
		this.toUserConfig = toUserConfig;
	}
	public String getCcUserConfig() {
		return ccUserConfig;
	}
	public void setCcUserConfig(String ccUserConfig) {
		this.ccUserConfig = ccUserConfig;
	}
	public String getErrorUserConfig() {
		return errorUserConfig;
	}
	public void setErrorUserConfig(String errorUserConfig) {
		this.errorUserConfig = errorUserConfig;
	}
	public List<InternetAddress> getReceiveMails() {
		return receiveMails;
	}
	public void setReceiveMails(List<InternetAddress> receiveMails) {
		this.receiveMails = receiveMails;
	}
	public List<InternetAddress> getCcMails() {
		return ccMails;
	}
	public void setCcMails(List<InternetAddress> ccMails) {
		this.ccMails = ccMails;
	}
	public List<InternetAddress> getErrorMails() {
		return errorMails;
	}
	public void setErrorMails(List<InternetAddress> errorMails) {
		this.errorMails = errorMails;
	}
	public Properties getSendEmailProps() {
		return sendEmailProps;
	}
	public void setSendEmailProps(Properties sendEmailProps) {
		this.sendEmailProps = sendEmailProps;
	}
	public InternetAddress getSendMail() {
		return sendMail;
	}
	public void setSendMail(InternetAddress sendMail) {
		this.sendMail = sendMail;
	}	
}

/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.daily.test;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

/**  
 * 功能描述:
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年12月23日 下午7:56:16  
 */
public class DailyTest {

	/**
	 * 通过文件名字获取日期
	 */
	@Test
	public void testFileNameReg() {
		String fileName= "北京市交通运行监测日报 2017年第233期（11月1日）.docx";
		Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(fileName);
        System.out.println(matcher.groupCount());
        while( matcher.find()) {
        	String str = matcher.group();
            System.out.println(str);
        }
	}
	
	@Test
	public void testDateFormatStr() throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日"); 
		Date date = PBStringUtil.sdf_yMd.parse(2017+"-"+8+"-"+1);
		System.out.println(sdf.format(date));
		Date date2 = PBStringUtil.DateCalc(date, 1);
		System.out.println(sdf.format(date2));
	}
	
	@Test
	public void testDateStrValid() {
		String str = "   次日11:26    		";
		String reg1 = "\\s*(次日|[0-9]{1,2}日)?[0-9]{1,2}(:|：)[0-9]{1,2}\\s*";
		Pattern pattern = Pattern.compile(reg1);
		Matcher matcher = pattern.matcher(str);
		System.out.println(matcher.matches());
	}
	
	@Test
	public void testDateFormatStr2() {
		String str = "补    报：   4    日（周六）";
		str = str.replaceAll(" +","");
		Pattern pattern = Pattern.compile("\\d+日");
        Matcher matcher = pattern.matcher(str);
        while( matcher.find()) {
        	String strTemp = matcher.group();
            System.out.println(strTemp);
        }
	}
	
	@Test
	public void testDateFormatStr3() {
		String str = "补    报：7月   4    日（周六）";
		str = str.replaceAll(" +","");
		Pattern pattern = Pattern.compile("\\d+日");
        Matcher matcher = pattern.matcher(str);
        while( matcher.find()) {
        	String strTemp = matcher.group();
            System.out.println(strTemp);
        }
	}
	@Test
	public void testReadDocxFile() {
		String filePath = "F:\\project\\综合交通运行监测与服务（北京Tocc）\\报告\\日报\\2017年日报";
		try {
			List<File> listFile = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(filePath, listFile);
			for(File fileTemp : listFile) {
				String fileName = fileTemp.getName();
				if((fileName.lastIndexOf(".docx")!=-1 || fileName.lastIndexOf(".doc")!=-1)&&fileName.indexOf("~$")==-1) {
					FileInputStream fis = new FileInputStream(fileTemp);
					XWPFDocument xdoc = new XWPFDocument(fis);
					List<IBodyElement> listBodyElement = xdoc.getBodyElements();
					boolean firstTableFlag = true;
					if (listBodyElement != null && listBodyElement.size() > 0) {
			            for (IBodyElement bodyElement : listBodyElement) {
			            	if(bodyElement instanceof XWPFTable) {
			            		if(firstTableFlag) {
			            			XWPFTable table = (XWPFTable)bodyElement;
			            			List<XWPFTableRow> xwpfTableRows = table.getRows();
			            			for(int i = 1 ; i < xwpfTableRows.size() ; i++) {
			            				XWPFTableRow rowTemp = xwpfTableRows.get(i);
			            		    	XWPFTableCell  cell = rowTemp.getTableCells().get(0);
			            		    	System.out.println(fileName+"----"+cell.getParagraphArray(2).getText());
			            			}
			            			firstTableFlag = false;
			            		}
			            	}
			            }
			        }
					xdoc.close();
					fis.close();
				}
			}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	@Test
	public void testReadZTYXQKDocxFile() {
		String filePath = "F:\\project\\综合交通运行监测与服务（北京Tocc）\\报告\\日报\\2017总体运行情况\\2017总体运行情况";
		try {
			List<File> listFile = new ArrayList<File>();
			PBFileUtil.GetFilesByPath(filePath, listFile);
			for(File fileTemp : listFile) {
				String fileName = fileTemp.getName();
				FileInputStream fis = new FileInputStream(fileTemp);
				//System.out.println(fileName);
				if( fileName.endsWith(".doc")&&fileName.indexOf("~$")==-1) {
					HWPFDocument doc = new HWPFDocument(fis);
					Range range = doc.getRange();
					Paragraph para4 = range.getParagraph(4);
					Paragraph para10 = range.getParagraph(10);
					System.out.println(para4.text());
					//System.out.println(para10.text());
					doc.close();
				}else if(fileName.endsWith(".docx")&&fileName.indexOf("~$")==-1) {
					XWPFDocument xdoc = new XWPFDocument(fis);
					List<IBodyElement> listBodyElement = xdoc.getBodyElements();
					XWPFParagraph wspfp4 = (XWPFParagraph)listBodyElement.get(4);
					XWPFParagraph wspfp10 = (XWPFParagraph)listBodyElement.get(10);
					System.out.println(wspfp4.getText());
					//System.out.println(wspfp10.getText());
				}
				fis.close();
			}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}

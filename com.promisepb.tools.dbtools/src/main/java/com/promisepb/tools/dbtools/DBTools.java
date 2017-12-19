/**
* @Copyright@2017 Beijing Tongtu Software Technology Co. Ltd.
*/
package com.promisepb.tools.dbtools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.promisepb.utils.dbutils.PBDBUtil;
import com.promisepb.utils.dbutils.vo.ColumnDesc;
import com.promisepb.utils.dbutils.vo.TableDesc;
import com.promisepb.utils.fileutils.PBFileUtil;
import com.promisepb.utils.stringutils.PBStringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**  
 * 功能描述:数据库帮助类（文件相关）
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2017年3月19日 下午9:42:03  
 */
public class DBTools {

	private static final Logger logger = LoggerFactory.getLogger(DBTools.class);
    private static Configuration cfg = null;
    
    /**
     * 导出数据库表结构到word 目前测试支持支持oracle,mysql,postgresql
     * @param wordPath 输出word路径
     * @param connection 数据库连接信息
     * @param tables 要导出的表 默认为数据链接能读取的表
     * @param args 要替换的数值 如:{{username}}
     * "projectname" "fileType" "level" "authuser" "checkuser" "version" "date" "ip" "username" "passwd"
     * @return
     */
    public static String ExportDBTableStructureToWord(String wordPath,Connection connection,List<String> tables,Map<String,Object> args){
        //处理普通变量赋值为空的情况
        if(null==args){
            args = new HashMap<String,Object>();
        }
        args.put("projectname", null==args.get("projectname")?"无":args.get("projectname"));
        args.put("fileType", null==args.get("fileType")?"无":args.get("fileType"));
        args.put("level", null==args.get("level")?"无":args.get("level"));
        args.put("authuser", null==args.get("authuser")?"无":args.get("authuser"));
        args.put("checkuser", null==args.get("checkuser")?"无":args.get("checkuser"));
        args.put("version", null==args.get("version")?"无":args.get("version"));
        args.put("date", null==args.get("date")?PBStringUtil.GetDateString("yyyy-MM-dd", new Date()):args.get("date"));
        args.put("databasename", null==args.get("databasename")?"无":args.get("databasename"));
        args.put("ip", null==args.get("ip")?"无":args.get("ip"));
        args.put("user", null==args.get("user")?"无":args.get("user"));
        args.put("password", null==args.get("password")?"无":args.get("password"));
        
        //确定要输出的表名称
        if(null==tables||tables.size()<1){
            tables = PBDBUtil.GetTables(connection);
        }
        
        List<TableDesc> listTables = new ArrayList<TableDesc>();
        try {
            for(String tableDescStr : tables){
                String[] tableDescArr = tableDescStr.split(":");
                String tableNameTemp = tableDescArr[0];
                String tableRemarkTemp = "无";
                if(tableDescArr.length>1){
                    tableRemarkTemp = tableDescArr[1];
                }
                List<ColumnDesc> listColumnTemp = PBDBUtil.GetTableStructure(connection,tableNameTemp);
                listTables.add(new TableDesc(tableNameTemp,tableRemarkTemp,listColumnTemp));
            }
            args.put("tables", listTables);
            Writer writer= new FileWriter(new File(wordPath+File.separator+"documentTemplateTemp.xml"));
            Process("/","document.flt",args,writer);
            //合并并输出
            String toWordFilePath = wordPath+File.separator+"数据库结构设计文档"+PBStringUtil.GetDateString("yyyyMMddHHmmss",null)+".docx";
            ExportDocx(wordPath+File.separator+"documentTemplateTemp.xml",args.get("template").toString(),toWordFilePath);
            PBFileUtil.deleteFile(wordPath+File.separator+"documentTemplateTemp.xml");
            logger.info("成功导出数据库设计文件！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
    
    /**
     * 注册tmlplate的load路径 "/"表示classpath
     * @param loadPath
     */
    public static void InitCfg(String loadPath){
        cfg = new Configuration();
        //注册tmlplate的load路径
        cfg.setClassForTemplateLoading(DBTools.class, loadPath); 
    }
    
    /**
     * 获取模版
     * @param name
     * @return
     */
    public static Template GetTemplate(String loadPath,String name){
        try {
            if(null==cfg){
                InitCfg(loadPath);
            }
            return cfg.getTemplate(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    } 
    
    /** 
     *  
     * @param templatefile 模板文件 
     * @param param  需要填充的内容 
     * @param out    填充完成输出的文件 
     * @throws IOException 
     * @throws TemplateException 
     */  
    public static void Process(String loadPath,String templatefile, Map param ,Writer out) throws IOException, TemplateException{  
     //获取模板  
     Template template=GetTemplate(loadPath,templatefile);  
     template.setOutputEncoding("UTF-8");  
     //合并数据  
     template.process(param, out);  
     if(out!=null){  
           out.close();  
       }  
    } 
    
    /** 
     *  
     * @param documentFile  动态生成数据的docunment.xml文件 
     * @param docxTemplate  docx的模板 
     * @param toFileName    需要导出的文件路径 
     * @throws ZipException 
     * @throws IOException 
     */  
    public static String ExportDocx(String documentFile,String docxTemplate,String toFilePath) throws ZipException, IOException {  
        try {
            File docxFile = new File(docxTemplate);  
            ZipFile zipFile = new ZipFile(docxFile);              
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();  
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(toFilePath));  
            int len=-1;  
            byte[] buffer=new byte[1024];  
            while(zipEntrys.hasMoreElements()) {  
                ZipEntry next = zipEntrys.nextElement();  
                InputStream is = zipFile.getInputStream(next);  
                //把输入流的文件传到输出流中 如果是word/document.xml由我们输入  
                zipout.putNextEntry(new ZipEntry(next.toString()));  
                if("word/document.xml".equals(next.toString())){  
                    InputStream in = new FileInputStream(documentFile);  
                    while((len = in.read(buffer))!=-1){  
                        zipout.write(buffer,0,len);  
                    }  
                    in.close();  
                }else {  
                    while((len = is.read(buffer))!=-1){  
                        zipout.write(buffer,0,len);  
                    }  
                    is.close();  
                }         
            }             
            zipout.close();           
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return "success";
    }  
}


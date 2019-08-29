package com.dinapin.orderdish.sqliteComJdbc.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

public class SystemUtils {
	
	private static final String SYSTEM_USER_PATH = "C:\\Users\\";
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	/**
	 * 判断当前操作系统是否是64位
	 * @return
	 */
	public static boolean is64Bit() {
		boolean is64Bit = false;
		if (System.getProperty("os.name").contains("Windows")) {
			is64Bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64Bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		
		return is64Bit;
	}
	
	/**
	 * 获取当前用户名
	 * 
	 * @return
	 */
	public static String getCurrentUserName() {
		
		Map<String, String> map = System.getenv();
		if(map != null) {
			return map.get("USERNAME");
		} else {
			return null;
		}
	}
	
	/**
	 * 窗口用户确认
	 * 
	 * @return
	 */
	public static boolean confirmDialog() {
		
		//用户确认是否安装.net framework
		Object[] options = {"安装", "放弃"}; 
		int index = JOptionPane.showOptionDialog(null, "系统未安装.Net Framework, 是否现在安装? ", "确认", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, 
					null, options, options[0]);
		
		if(index != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String getSystemUserPath() {
		
		return SYSTEM_USER_PATH + getCurrentUserName();
	}
	
	/**
	 * 网络文件下载
	 * 
	 * @param fileUrl
	 * @param fileSavePath
	 * @param fileSaveName
	 * @return
	 * @throws Exception
	 */
	public static boolean fileDownload(String fileUrl, String fileSavePath, String fileSaveName) throws Exception {
		
		BufferedOutputStream bos = null;
        InputStream is = null;
        try {
        	
        	byte[] buff = new byte[8192];
        	is = new URL(fileUrl).openStream();
        	File file = new File(fileSavePath, fileSaveName);
        	file.getParentFile().mkdirs();
        	bos = new BufferedOutputStream(new FileOutputStream(file));
        	int count = 0;
        	while ( (count = is.read(buff)) != -1) {
        		bos.write(buff, 0, count);
        	}
        } catch (IOException e) {
        	throw new Exception(e);
        } finally {
        	 if (is != null) {
                 try {
                     is.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             
             if (bos != null) {
                 try {
                     bos.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
        
        return true;
	}
	
	/**
	 * 文件是否存在
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static boolean isFileExist(String fileName) throws Exception {
		
		if(StringUtils.isBlank(fileName)) {
			
			return false;
		}
		File file = new File(fileName);
		return file.exists();
	}
	
	/**
     * 解析时间
     * 
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static Date dateParse(String dateStr) throws Exception {
    	
    	if(StringUtils.isEmpty(dateStr)) {
    		return null;
    	}
    	
    	return formatter.parse(dateStr);
    }
    
    /**
     * 时间格式化
     * 
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
    	
    	if(date == null) {
    		return null;
    	}
    	
    	return formatter.format(date);
    }
}

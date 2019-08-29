/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author dingwenbin
 *
 */
public class PropertiesUtil {
	
	private static Logger log = Logger.getLogger(PropertiesUtil.class);
	
	/**
	 * 
	 */
	public static String DOTNET_DOWNLOAD = "http://download.microsoft.com/download/E/2/1/E21644B5-2DF2-47C2-91BD-63C560427900/NDP452-KB2901907-x86-x64-AllOS-ENU.exe";
	
	/**
	 * .net framework默认安装路径
	 */
	public static String DOTNET_DEFAULT_PATH = "C:\\Windows\\Microsoft.NET";
	
	/**
	 * .net framework MSCorEE.dll的路径
	 */
	public static String DOTNET_CORE_DLL_PATH = "C:\\Windows\\System32\\MSCorEE.dll";
	
	/**
	 * system32路径
	 */
	public static String SYSTEM_32_PATH = "C:\\Windows\\System32";
	
	/**
	 * 程序lib目录
	 */
	public static String LIB_PATH = "lib";
	
	/**
	 * 动态库名称， 请勿修改
	 */
	public static String DLL_COM_NAME = "SQLiteCom2Dll";
	
	public static String SQLITE_DLL_NAME = "System.Data.SQLite.dll";
	
	public static String NEWTONSOFT_JSON_DLL = "Newtonsoft.Json.dll";
	/**
	 * 数据库连接成功的验证消息
	 */
	public static String CONNECT_VERIFY_MESSAGE = "dbVerifyTest";
	
	public static String BAT_FILE_NAME = "sqlite_env_ini.bat";
	
	static {
		
		try {
			
			InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlite_com_jdbc.properties");
			
			Properties properties = new Properties();
			properties.load(inStream);
			
			if(properties.containsKey("dotnet.download")) {
				DOTNET_DOWNLOAD = properties.getProperty("dotnet.download");
			}
			
			if(properties.containsKey("dotnet.default.path")) {
				DOTNET_DEFAULT_PATH = properties.getProperty("dotnet.default.path");
			}
			
			if(properties.containsKey("dotnet.core.dll.path")) {
				DOTNET_CORE_DLL_PATH = properties.getProperty("dotnet.core.dll.path");
			}
			
			if(properties.containsKey("system32.default.path")) {
				SYSTEM_32_PATH = properties.getProperty("system32.default.path");
			} 
			
		} catch(Exception e) {
			log.info("Failed to load properties", e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(PropertiesUtil.SYSTEM_32_PATH);
	}
}

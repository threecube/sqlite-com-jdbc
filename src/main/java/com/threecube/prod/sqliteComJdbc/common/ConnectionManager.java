/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dinapin.orderdish.sqliteComJdbc.enums.DllMethodEnum;
import com.dinapin.orderdish.sqliteComJdbc.service.SqliteConncetion;
import com.dinapin.orderdish.sqliteComJdbc.service.impl.SqliteConnectionImpl;
import com.dinapin.orderdish.sqliteComJdbc.utils.PropertiesUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 *	sqlite连接管理器
 * 
 * @author dingwenbin
 *
 */
public class ConnectionManager {
	
	public static ActiveXComponent dotnetCom;
	
	private static final Logger log = Logger.getLogger(ConnectionManager.class);
	
	static {
		
		try {
			
			dotnetCom = new ActiveXComponent("SqliteCom2Dll.DBOperation2");
			
			Variant var = Dispatch.call(dotnetCom, DllMethodEnum.SAY_HELLO.getMethodName(), PropertiesUtil.CONNECT_VERIFY_MESSAGE);
			if(var != null) {
				
				if(!StringUtils.equals(PropertiesUtil.CONNECT_VERIFY_MESSAGE, var.toString())) {
					
					log.error("Invalid result from calling COM test api, result: " + var.toString());
					throw new Exception("Failed to call COM test api");
				} else {
					
					log.info("Success to call COM test api");
				}
			} else {
				
				log.error("Return null from calling COM test api");
				throw new Exception("null result from COM test api");
			}
			
		} catch (Exception e) {
			log.error("Failed to load C# dll SqliteCom2Dll.DBOperation2", e);
		}
	}
	
	private ConnectionManager() {
		
	}
	
	/**
	 * 创建数据库连接
	 * 
	 * @param connectionUrl
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static SqliteConncetion createConnection(String connectionUrl, String password) throws Exception {
		
		SqliteConncetion connection = null;
		
		log.info("Start to create connection with sqlite");
		
		try {
			
			Dispatch.call(dotnetCom, DllMethodEnum.DB_INIT.getMethodName(), connectionUrl, StringUtils.isNotBlank(password), password);
			
			connection = new SqliteConnectionImpl();
			
		} catch (Exception e) {
			
			log.error("Exception when creating connection with sqlite");
			throw new Exception(e);
		}
		
		log.info("Success to create connection with sqlite");
		
		return connection;
	}
}

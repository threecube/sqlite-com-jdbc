/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dinapin.orderdish.sqliteComJdbc.common.ConnectionManager;
import com.dinapin.orderdish.sqliteComJdbc.common.EnvInitialization;
import com.dinapin.orderdish.sqliteComJdbc.enums.RuntimeBitEnum;
import com.dinapin.orderdish.sqliteComJdbc.service.SqliteConncetion;
import com.dinapin.orderdish.sqliteComJdbc.utils.SqliteMapperUtil;

/**
 * @author dingwenbin
 *
 */
public class SqliteDriver {

	private static final Logger log = Logger.getLogger(SqliteDriver.class);
	
	private String dbFilePath;
	
	private String dbPassword;
	
	private volatile SqliteConncetion sqliteConncetion;
		
	static {
		try {
			
        	log.info("Start to init dll runtime environment.");
        	prepareSqliteDllRuntime();        	
        } catch (Exception e) {
        	
        	log.error("Failed to get SqliteConncetion", e);
        }
	}
	
	/**
	 * not allowed to create object
	 */
	private SqliteDriver() {
	}
	
	/**
	 * 获取指定
	 * 
	 * @param dbFilePath
	 * @param dbPassword
	 * @return
	 */
	public static SqliteDriver createInstance(String dbFilePath, String dbPassword) throws Exception {
		
		if(StringUtils.isBlank(dbFilePath)) {
			log.error("数据库文件路径不能为空");
			throw new Exception("非法数据库文件");
		}
		
		SqliteDriver sqliteDriver = new SqliteDriver();
		sqliteDriver.dbFilePath = dbFilePath;
		sqliteDriver.dbPassword = dbPassword;
		
		sqliteDriver.getConnection();
		
		return sqliteDriver;
	}
	
	/**
     * 开启一个事务
     * 
     * @throws Exception
     */
    public void beginTransaction() throws Exception {

        getConnection().beginTransaction();
    }

	/**
     * 提交事务
     * 
     * @throws Exception
     */
    public void commit() throws Exception {

        getConnection().commit();
    }

    /**
     * 回滚事务
     * 
     * @throws Exception
     */
    public void rollback() throws Exception {
    	
    	getConnection().rollback();
    }
    
    /**
     * 执行非查询的sql操作
     * 
     * @param nonQuerySql
     * @return
     * @throws Exception
     */
    public int execute(String nonQuerySql, Object...params) throws Exception {
    	
    	if(params != null && params.length > 0) {
    		
    		nonQuerySql = String.format(nonQuerySql, params);
    	}
    	
		if(StringUtils.isBlank(nonQuerySql)) {
			
			log.warn("入参为null或者空");
			throw new Exception("入参sql为null或者空");
		}
		
        return getConnection().executeNonQuery(nonQuerySql);
    }

    /**
     * 执行查询操作
     * 
     * @param querySql
     * @return
     * @throws Exception
     */
    public List<JSONObject> executeQuery(String querySql, Object... params) throws Exception {
    	
    	if(params != null && params.length > 0) {
    		
    		querySql = String.format(querySql, params);
    	}
    	
    	if(StringUtils.isBlank(querySql)) {
			
			log.warn("入参为null或者空");
			throw new Exception("入参sql为null或者空");
		}

        return getConnection().executeQuery(querySql);
    }
    
    public<T> List<T> executeQuery(String querySql, Class<T> clazz, Object... params) throws Exception {
    	
    	if(params != null && params.length > 0) {
    		
    		querySql = String.format(querySql, params);
    	}
    	
    	if(StringUtils.isBlank(querySql) || clazz == null) {
			
			log.warn("入参为null或者空");
			throw new Exception("入参sql为null或者空");
		}

        List<JSONObject> jsonList = getConnection().executeQuery(querySql);
       
        return SqliteMapperUtil.json2DOList(jsonList, clazz);
    }
    
	/**
     * 获取连接
     * 
     * @return
     * @throws Exception
     */
    private SqliteConncetion getConnection() throws Exception {

        if(sqliteConncetion == null) {

            synchronized (SqliteDriver.class) {

                if(sqliteConncetion == null) {
                	
                    sqliteConncetion = ConnectionManager.createConnection(this.dbFilePath, this.dbPassword);
                }
            }
        }

        return sqliteConncetion;
    }
    
    /**
     * 准备SqliteCom2Dll的dll库运行环境
     * 
     * @throws Exception
     */
    private static void prepareSqliteDllRuntime() throws Exception {
    	
    	String jrePath =  System.getProperty("java.home");
		if(StringUtils.isEmpty(jrePath)) {
			throw new Exception("Failed to find jdk path");
		}
		
		String jdkBinPath = String.format("%s\\bin", jrePath);
		
		String jdkBits = System.getProperty("sun.arch.data.model");
		RuntimeBitEnum bits = StringUtils.equals(jdkBits, "64") ? RuntimeBitEnum.BITS_64 : RuntimeBitEnum.BITS_32;

		log.info(String.format("Found jdk path of dcManager is %s, %s", jdkBinPath, bits));
		
		EnvInitialization.init(jdkBinPath, bits);
	}
}

/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dinapin.orderdish.sqliteComJdbc.common.ConnectionManager;
import com.dinapin.orderdish.sqliteComJdbc.enums.DllMethodEnum;
import com.dinapin.orderdish.sqliteComJdbc.service.SqliteConncetion;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingwenbin
 *
 */
public class SqliteConnectionImpl implements SqliteConncetion {
	
	/**
	 * 存放开启的事务标识
	 */
	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	
	/**
	 * Sqlite不允许并发连接存在，为了防止大量报错， 进行并发控制
	 */
	private static final Lock lock = new ReentrantLock();
	
	private static final Logger logger = Logger.getLogger(SqliteConnectionImpl.class);
	
	public void beginTransaction() throws Exception {
		
		logger.info("Start a new transaction");
		try {
			
			lock.tryLock(1, TimeUnit.MINUTES);
			
			Variant result = Dispatch.call(ConnectionManager.dotnetCom, DllMethodEnum.BEGIN_TRANS.getMethodName());
			if(result == null) {
				
				logger.error("Failed to begin a transaction, result is null");
				throw new Exception("Failed to begin a transaction, result is null");
			}
			
			threadLocal.set(result.toString());
			logger.info("Success to start a new transaction " + threadLocal.get());
		} catch (Exception e) {
			
			logger.error("Failed to begin a transaction", e);
			lock.unlock();
			throw new Exception(e);
		}
	}

	public void commit() throws Exception {
		
		logger.info("Start to commit " + threadLocal.get());
		
		try {
			Variant result = Dispatch.call(ConnectionManager.dotnetCom, DllMethodEnum.COMMIT.getMethodName(), threadLocal.get());
			if(result == null) {
				
				logger.error("Failed to commit transaction, result is null");
				throw new Exception("Failed to commit transaction, result is null");
			}
			
		} catch (Exception e) {
			
			logger.error("Failed to commit transaction", e);
			throw new Exception(e);
		} finally {
			
			lock.unlock();
			// not allow to commit again even if commit failure
			threadLocal.remove();
		}
	}

	public void rollback() throws Exception {
		
		logger.info("Start to rollback " + threadLocal.get());
		
		try {
			Variant result = Dispatch.call(ConnectionManager.dotnetCom, DllMethodEnum.ROLLBACK.getMethodName(), threadLocal.get());
			if(result == null) {
				
				logger.error("Failed to rollback transaction, result is null");
				throw new Exception("Failed to rollback transaction, result is null");
			}
			
		} catch (Exception e) {
			
			logger.error("Failed to rollback transaction", e);
			throw new Exception(e);
		} finally {
			
			lock.unlock();
			threadLocal.remove();
		}
	}
	
	public List<JSONObject> executeQuery(String querySql) throws Exception {
		
		logger.info(String.format("Execute query sql [%s]", querySql));
		boolean isLockSuccess = false;
		try {
			if(StringUtils.isBlank(threadLocal.get())) {
				
				// 尝试加锁， 如果lock已经加锁，则继续
				isLockSuccess = lock.tryLock();
			}
			
			Variant result = Dispatch.call(ConnectionManager.dotnetCom, DllMethodEnum.EXECUTE_QUERY.getMethodName(), querySql, threadLocal.get());
			if(result == null) {
				
				logger.error("Failed to execute query sql, result is null");
				throw new Exception("Failed to execute query sql, result is null");
			}
			
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			JSONObject originJson = JSONObject.parseObject(result.toString());
			
			if(originJson == null || originJson.isEmpty()) {
				return jsonList;
			}
			
			for(Object originObject : originJson.values().toArray()) {
				jsonList.add(JSONObject.parseObject((String)originObject));
			}
			
			logger.info(String.format("Result of query: %s", jsonList));
			
			return jsonList;
		} catch (Exception e) {
			
			logger.error("Failed to execute sql: " + querySql, e);
			throw new Exception(e);
		} finally {
			if(StringUtils.isBlank(threadLocal.get()) && isLockSuccess) {
				lock.unlock();
			}
		}
	}

	public int executeNonQuery(String nonQuerySql) throws Exception {
		
		logger.info(String.format("Execute sql [%s]", nonQuerySql));
		boolean isLockSuccess = false;
		
		try {
			
			if(StringUtils.isBlank(threadLocal.get())) {
				isLockSuccess = lock.tryLock();
			}
			
			Variant result = Dispatch.call(ConnectionManager.dotnetCom, DllMethodEnum.EXECUTE_NON_QUERY.getMethodName(), nonQuerySql, threadLocal.get());
			if(result == null) {
				logger.error("Failed to execute sql " + nonQuerySql);
				throw new Exception("Failed to execute non-query sql");
			}
			
			return Integer.valueOf(result.toString());
		} catch (Exception e) {
			
			logger.error("Failed to execute sql " + nonQuerySql, e);
			throw new Exception(e);
		} finally {
			if(StringUtils.isBlank(threadLocal.get()) && isLockSuccess) {
				lock.unlock();
			}
		}
	}

}

/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @author dingwenbin
 *
 */
public interface SqliteConncetion {
	
	/**
	 * 开启一个事务
	 * 
	 * @throws Exception
	 */
	public void beginTransaction() throws Exception;
	
	/**
	 * 事务提交
	 * 
	 * @throws Exception
	 */
	public void commit() throws Exception;
	
	/**
	 * 回滚事务
	 * 
	 * @throws Exception
	 */
	public void rollback() throws Exception;
	
	/**
	 * 执行查询操作
	 * 
	 * @param querySql
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> executeQuery(String querySql) throws Exception;
	
	/**
	 * 执行增删改操作
	 * 
	 * @param nonQuerySql
	 * @return
	 * @throws Exception
	 */
	public int executeNonQuery(String nonQuerySql) throws Exception;
}

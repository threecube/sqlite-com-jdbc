/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.dinapin.orderdish.sqliteComJdbc.annotation.AnnotationUtil;
import com.dinapin.orderdish.sqliteComJdbc.utils.SystemUtils;

/**
 * sqlite的dml支持类
 * 
 * @author dingwenbin
 *
 */
public class SqliteDmlHelper {
	
	private static final Logger log = Logger.getLogger(SqliteDmlHelper.class);
	
	/**
	 * insert sql模板
	 */
	private static final String INSERT_FORMAT = "insert into %s(%s) values(%s)";
	
	/**
	 * 条件查询sql模板
	 */
	private static final String SELECT_COLUMNS_WHERE_FORMAT = "select %s from %s where %s";
	
	/**
	 * 条件更新
	 */
	private static final String UPDATE_COLUMNS_WHERE_FORMAT = "update %s set %s where %s";
	
	/**
	 * 全表查询sql模板
	 */
	private static final String SELECT_COLUMNS_FORMAT = "select %s from %s";
	
	/**
	 * 根据转入的DO对象，生成insert sql
	 * 
	 * @param object DO对象
	 * @param tableName 表名
	 * @return
	 * @throws Exception
	 */
	public static<T> String genInsert(T object) throws Exception {
		
		Assert.assertNotNull("入参为null", object);
		
		Class<?> clazz = object.getClass();
		String cloumnStr = null;
		String values = null;
		Map<String, Pair<String, Integer>> field2Cloumn = AnnotationUtil.field2Column(clazz);
		for(Field field : clazz.getDeclaredFields()) {
			
			field.setAccessible(true);
			String fieldName = field.getName();
			Object value = field.get(object);
			String fieldType = field.getType().getSimpleName();
			if(value == null && !field2Cloumn.containsKey(fieldName)) {
				continue;
			}
			
			if(cloumnStr == null) {
				cloumnStr = field2Cloumn.get(fieldName).getLeft();
			} else {
				cloumnStr = cloumnStr + "," + field2Cloumn.get(fieldName).getLeft();
			}
			
			String newValueStr = String.valueOf(value);
			if(StringUtils.equalsIgnoreCase(fieldType, "String")) {
				
				newValueStr = String.format("\"%s\"", value);
				
			} else if(StringUtils.equalsIgnoreCase(fieldType, "date")) {
				String timeValue = SystemUtils.dateFormat((Date)value);
				newValueStr = String.format("datetime(\"%s\")", timeValue);
			}
			
			if(values == null) {
				values = newValueStr;
			} else {
				values = String.format("%s,%s", values, newValueStr);
			}
		}
		
		return String.format(INSERT_FORMAT, getTableName(clazz), cloumnStr, values);
	}
	
	/**
	 * 生成全表查询的sql， 返回所有字段的值
	 * 
	 * @param clazz DO的类
	 * @param tableName 表名
	 * @return
	 * @throws Exception
	 */
	public static String genSelectAll(Class<?> clazz) throws Exception {
		
		Assert.assertNotNull("入参为null", clazz);
		String tableName = getTableName(clazz);
		return String.format(SELECT_COLUMNS_FORMAT, genSqlColumns(clazz, tableName), tableName);
	}
	
	/**
	 * 生成条件查询的sql，返回所有字段的值
	 * 
	 * @param conditions
	 * @param tableName
	 * @return
	 */
	public static String genSelectAllWithWhere(Map<String, Object> conditions, Class<?> clazz) throws Exception {
		
		Assert.assertNotNull("入参为null", conditions);
		Assert.assertNotNull("入参为null", clazz);
		String tableName = getTableName(clazz);
		return String.format(SELECT_COLUMNS_WHERE_FORMAT, genSqlColumns(clazz, tableName), tableName, genSqlWhere(conditions));
	}
	
	/**
	 * 生成update的sql语句
	 * 
	 * @param updateColumns
	 * @param conditions
	 * @param tableName
	 * @return
	 */
	public static String genUpdateWithWhere(Map<String, Object> updateColumns, Map<String, Object> conditions, Class<?> clazz) throws Exception {
		
		Assert.assertNotNull("入参为null", updateColumns);
		Assert.assertNotNull("入参为null", conditions);
		Assert.assertNotNull("入参为null", clazz);
		Map<String, Object> setColumns = new HashMap<>();
		
		Iterator<Map.Entry<String, Object>> iterator = updateColumns.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			setColumns.put(StringUtils.lowerCase(entry.getKey()), entry.getValue());
		}
		
		String tableName = getTableName(clazz);
		String whereCaluse = genSqlWhere(conditions);
		String setColumnStr = genSqlSet(setColumns, clazz, tableName);
		return String.format(UPDATE_COLUMNS_WHERE_FORMAT, tableName, setColumnStr, whereCaluse);
	}

	/**
	 * 生成条件查询的sql，返回指定的字段
	 * 
	 * @param columns 指定查询的字段名
	 * @param conditions 查询条件
	 * @param tableName 表名
	 * @return
	 */
	public static String genSelectColumnsWithWhere(List<String> columns, Map<String, Object> conditions, Class<?> clazz) throws Exception {
		
		Assert.assertNotNull("入参为null", columns);
		Assert.assertNotNull("入参为null", conditions);
		Assert.assertNotNull("入参为null", clazz);
		String tableName = getTableName(clazz);
		if(conditions == null || conditions.isEmpty()) {
			return genSelectColumns(columns, clazz);
		}
		
		String columnsStr = null;
		for(String column : columns) {
			if(columnsStr == null) {
				columnsStr = column;
			} else {
				columnsStr = String.format("%s,%s", columnsStr, column);
			}
		}
		
		return String.format(SELECT_COLUMNS_WHERE_FORMAT, columnsStr, tableName, genSqlWhere(conditions));
	}
	
	/**
	 * 全表查询， 值返回指定的列
	 * 
	 * @param columns 指定查询的列名
	 * @param tableName 表名
	 * @return
	 */
	public static String genSelectColumns(List<String> columns, Class<?> clazz) throws Exception {
		
		Assert.assertNotNull("入参为null", columns);
		Assert.assertNotNull("入参为null", clazz);
		String columnsStr = null;
		for(String column : columns) {
			
			if(columnsStr == null) {
				
				columnsStr = column;
			} else {
				
				columnsStr = String.format("%s,%s", columnsStr, column);
			}
		}
		
		return String.format(SELECT_COLUMNS_FORMAT, columnsStr, getTableName(clazz));
	}

	/**
	 * 组装where条件查询的字符串
	 * 
	 * @param conditions
	 * @return
	 */
	private static String genSqlWhere(Map<String, Object> conditions) {
		
		String whereStr = null;
		Iterator<String> iterator = conditions.keySet().iterator();
		while(iterator.hasNext()) {
			
			String propertyName = iterator.next();
			Object value = conditions.get(propertyName);
			if(value instanceof String) {
				
				if(whereStr == null) {
					whereStr = String.format("%s=\"%s\"", propertyName, value);
				} else {
					whereStr = String.format("%s and %s=\"%s\"", whereStr, propertyName, value);
				}
			} else {
				
				if(whereStr == null) {
					whereStr = String.format("%s=%s", propertyName, value);
				} else {
					whereStr = String.format("%s and %s=%s", whereStr, propertyName, value);
				}
			}
		}
		
		return whereStr;
	}
	
	/**
	 * 组装查询的列
	 * 
	 * @param tableName
	 * @return
	 */
	private static String genSqlColumns (Class<?> clazz, String tableName) {
		
		String propertiesStr = null;
		for(Field field : clazz.getDeclaredFields()) {
			
			field.setAccessible(true);
			String fieldName = field.getName();
			String fieldType = field.getType().getSimpleName();
			
			if(StringUtils.equalsIgnoreCase(fieldType, "date")) {
				
				// sqlite中datetime字段映射到java的date变量中会出错， 需要使用datetime做转换
				fieldName = String.format("datetime(%s) as %s", fieldName, fieldName);
			}
			
			if(propertiesStr == null) {
				
				propertiesStr = fieldName;
			} else {
				
				propertiesStr = propertiesStr + "," + fieldName;
			}
		}
		
		return propertiesStr;
	}
	
	/**
	 * 组装update中的set参数
	 * 
	 * @param updateColumns
	 * @param tableName
	 * @return
	 */
	private static String genSqlSet(Map<String, Object> updateColumns, Class<?> clazz, String tableName) {
		
		String setStr = null;
		for(Field field : clazz.getDeclaredFields()) {
			
			field.setAccessible(true);
			String fieldName = StringUtils.lowerCase(field.getName());
			if(!updateColumns.containsKey(fieldName)) {
				continue;
			}
			Object value = updateColumns.get(fieldName);
			String fieldType = field.getType().getSimpleName();
			
			String newValueStr =String.format("%s=%s", fieldName, value);
			if(StringUtils.equalsIgnoreCase(fieldType, "String")) {
				
				newValueStr = String.format("%s=\"%s\"", fieldName, value);
				
			} else if(StringUtils.equalsIgnoreCase(fieldType, "date")) {
				String timeValue = SystemUtils.dateFormat((Date)value);
				newValueStr = String.format("%s=datetime(\"%s\")", fieldName, timeValue);
			}
			
			if(setStr == null) {
				setStr = newValueStr;
			} else {
				setStr = String.format("%s,%s", setStr, newValueStr);
			}
		}
		
		return setStr;
	}
	
	private static String getTableName(Class<?> clazz) throws Exception {
		
		String tableName = AnnotationUtil.getTableName(clazz);
		if(StringUtils.isBlank(tableName)) {
			
			log.error(String.format("类%s上未找到注解@TableName", clazz.getName()));
		}
		
		return tableName;
	}
}

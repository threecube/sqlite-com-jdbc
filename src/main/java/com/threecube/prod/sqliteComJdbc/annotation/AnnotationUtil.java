/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

/**
 * DO属性与表属性的映射关系解析
 * 
 * @author dingwenbin
 *
 */
public class AnnotationUtil {
	
	private static final Logger log = Logger.getLogger(AnnotationUtil.class);
	
	/**
	 * 获取对象属性名与表字段名的映射关系
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Pair<String, Integer>> field2Column(Class<?> clazz) throws Exception {
		
		if(clazz == null) {
			throw new Exception("clazz is null");
		}
		Map<String, Pair<String, Integer>> propertyMap = new HashMap<>();
		
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			
			Pair<String, Integer> name2Scale;
			if(field.isAnnotationPresent(TableColumn.class)) {
				
				TableColumn itemMap = field.getAnnotation(TableColumn.class);
				Integer scale = itemMap.scale();
				String columnName = itemMap.name();
				if(StringUtils.isBlank(columnName)) {
					
					name2Scale = new ImmutablePair<>(field.getName(), scale == null ? -1 : scale);
				} else {
					
					name2Scale = new ImmutablePair<>(columnName, scale == null ? -1 : scale);
				}
				
				propertyMap.put(field.getName(), name2Scale);
			} else {
				
				// 如果没有注解，则默认认为属性名和表字段名相同
				name2Scale = new ImmutablePair<>(field.getName(), -1);
				propertyMap.put(field.getName(), name2Scale);
			}
		}
		
		return propertyMap;
	}
	
	/**
	 * 获取表字段名到对象属性名的映射关系
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Pair<String, Integer>> column2Field(Class<?> clazz) throws Exception {
		
		if(clazz == null) {
			throw new Exception("clazz is null");
		}
		Map<String, Pair<String, Integer>> propertyMap = new HashMap<>();
		
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			
			Pair<String, Integer> name2Scale;
			if(field.isAnnotationPresent(TableColumn.class)) {
				
				TableColumn itemMap = field.getAnnotation(TableColumn.class);
				Integer scale = itemMap.scale();
				String columnName = itemMap.name();
				
				name2Scale = new ImmutablePair<>(field.getName(), scale == null ? -1 : scale);
				
				propertyMap.put(StringUtils.isBlank(columnName) ? field.getName() : columnName, name2Scale);
			} else {
				
				// 如果没有注解，则默认认为属性名和表字段名相同
				name2Scale = new ImmutablePair<>(field.getName(), -1);
				propertyMap.put(field.getName(), name2Scale);
			}
		}
		
		return propertyMap;
	}
	
	/**
	 * 获取class的TableName注解的value值
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static String getTableName(Class<?> clazz) throws Exception {
		
		if(clazz == null) {
			throw new Exception("clazz is null");
		}
		
		if(clazz.isAnnotationPresent(TableName.class)) {
			
			TableName tableName = clazz.getAnnotation(TableName.class);
			
			return tableName.value();
		}
		
		return null;
	}
}

/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.fastjson.JSONObject;
import com.dinapin.orderdish.sqliteComJdbc.annotation.AnnotationUtil;

/**
 * sqlite的表数据到DO对象的映射器
 * 
 * @author dingwenbin
 *
 */
public class SqliteMapperUtil {

	/**
	 * 
	 * @param jsonList
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static<T> List<T> json2DOList(List<JSONObject> jsonList, Class<T> clazz) throws Exception {
		
		List<T> doList = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(jsonList)) {
			
			for(JSONObject jsonData : jsonList) {
				
				doList.add(json2DO(jsonData, clazz));
			}
		}
		
		return doList;
	}
	
	/**
	 * 
	 * @param jsonData
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static<T> T json2DO(JSONObject jsonData, Class<T> clazz) throws Exception {
		
		if(jsonData == null) {
			
			throw new Exception("Invalid parameter: null parameter");
		}
		
		Map<String, Pair<String, Integer>> field2Column = AnnotationUtil.field2Column(clazz);
		T object = clazz.newInstance();
		
		for(Field field : clazz.getDeclaredFields()) {
			
			String fieldName = field.getName();
			String upperFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Pair<String, Integer> column2Scale = field2Column.get(fieldName);
			String columnName = column2Scale == null ? fieldName : column2Scale.getLeft();
			int scale = column2Scale == null ? -1 : column2Scale.getRight();
			String upperColumnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
			
			if(!jsonData.containsKey(columnName) && !jsonData.containsKey(upperColumnName)) {
				
				continue;
			}
			Object value = jsonData.get(columnName) != null ? jsonData.get(columnName) : jsonData.get(upperColumnName);
			
			if(value == null) {
				continue;
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("set");
            sb.append(upperFieldName);
            Method method = clazz.getMethod(sb.toString(), field.getType());
            
            String fieldType = field.getType().getSimpleName();
            if(StringUtils.equalsIgnoreCase(fieldType, "date")) {
            	
            	Date date = SystemUtils.dateParse((String) value);
            	method.invoke(object, date);
			} else if (scale > -1) {
				
				// 设置了精度， 进行精度处理
				method.invoke(object, AccuracyDealUtil.deal(value, scale, fieldType));
			} else {
				
				method.invoke(object, value);
			}
		}
		
		return object;
	}
	
}

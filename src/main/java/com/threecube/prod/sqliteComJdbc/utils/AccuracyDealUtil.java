/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.utils;

import java.math.BigDecimal;

/**
 * 针对表的某个字段进行精度配置
 * 
 * @author dingwenbin
 *
 */
public class AccuracyDealUtil {
	
	public static Object deal(Object value, int scale, String fieldType) {
		
		try {
			Object result;
			switch(fieldType.toLowerCase()) {
			case "double":
				double doubleValue = (double) value;
				result = new BigDecimal(doubleValue).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
				break;
			case "float":
				float floatValue = (float) value;
				result = new BigDecimal(floatValue).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
				break;
			case "bigdecimal":
				BigDecimal newValue = (BigDecimal) value;
				result = newValue.setScale(scale, BigDecimal.ROUND_HALF_UP);
				break;
			default:
				result = value;	
			} 
			
			return result;
		} catch (Exception e) {
			// 捕获精度处理的异常, 存在误设置导致的异常，该异常不能影响系统正常运行
			
		}
		return value;
	}
	
}

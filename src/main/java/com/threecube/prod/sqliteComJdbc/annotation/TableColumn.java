/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段与表属性的映射关系
 * 
 * @author dingwenbin
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableColumn {
	
	/**
	 * 字段名
	 * 
	 * @return
	 */
	String name() default "";
	
	/**
	 * 精度设置， 主要针对double、float和BigDecimal类型的字段
	 * 
	 * @return
	 */
	int scale() default -1;
}

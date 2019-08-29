/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.dinapin.orderdish.sqliteComJdbc.annotation.TableColumn;
import com.dinapin.orderdish.sqliteComJdbc.annotation.AnnotationUtil;

/**
 * @author dingwenbin
 *
 */
public class AnnotationTest {
	
	@Test
	public void testAnnonation() {
		
		try {
			Map<String, Pair<String, Integer>> map = AnnotationUtil.column2Field(TestClass.class);
			
			for(Entry<String, Pair<String, Integer>> entry : map.entrySet()) {
				System.out.println(String.format("column: %s, field: %s, scale: %s", 
						entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAnnonation1() {
		
		try {
			Map<String, Pair<String, Integer>> map = AnnotationUtil.field2Column(TestClass.class);
			
			for(Entry<String, Pair<String, Integer>> entry : map.entrySet()) {
				System.out.println(String.format("field: %s, column: %s, scale: %s", 
						entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


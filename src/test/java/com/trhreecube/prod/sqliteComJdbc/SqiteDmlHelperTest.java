/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author dingwenbin
 *
 */
public class SqiteDmlHelperTest {

	@Test
	public void testInsertSql() throws Exception {
		
		TestClass testClass = constructClazz();
		System.out.println(SqliteDmlHelper.genInsert(testClass));
	}
	
	@Test
	public void testSelectAllSql() throws Exception {
		
		System.out.println(SqliteDmlHelper.genSelectAll(TestClass.class));
	}
	
	@Test
	public void testSelectAllWhereSql() throws Exception {
		
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("Id", 10);
		System.out.println(SqliteDmlHelper.genSelectAllWithWhere(conditions, TestClass.class));
	}
	
	private TestClass constructClazz() throws Exception {
		
		TestClass testClazz = new TestClass();
		testClazz.setId(10);
		testClazz.setPrinterQueueId(100);
		testClazz.setCreateTime(new Date());
		testClazz.setNewPrice(new BigDecimal(30.2345));
		testClazz.setOriginPrice(35.1221);
		testClazz.setPrice(31.12234);
		return testClazz;
	}
}

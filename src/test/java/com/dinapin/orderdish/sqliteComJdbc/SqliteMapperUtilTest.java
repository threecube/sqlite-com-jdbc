/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.dinapin.orderdish.sqliteComJdbc.utils.SqliteMapperUtil;

/**
 * @author dingwenbin
 *
 */
public class SqliteMapperUtilTest {
	
	@Test
	public void testJson2DO() {
		
		try {
			JSONObject jsonData = genJsonData();
			System.out.println(jsonData.toJSONString());
			TestClass exceptTestClazz = constructClazz();
			
			TestClass testClazz = SqliteMapperUtil.json2DO(jsonData, TestClass.class);
			
			Assert.assertNotNull(testClazz);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JSONObject genJsonData() throws Exception {
		
		JSONObject jsonData = new JSONObject();
		TestClass clazz = constructClazz();
		jsonData.put("id", clazz.getId());
		jsonData.put("printer_queue_Id", clazz.getPrinterQueueId());
		jsonData.put("create_time", clazz.getCreateTime());
		jsonData.put("price", clazz.getPrice());
		jsonData.put("origin_price", clazz.getOriginPrice());
		jsonData.put("new_price", clazz.getNewPrice());
		jsonData.put("table_title", clazz.getTableTile());
		return jsonData;
	}
	
	private TestClass constructClazz() throws Exception {
		
		TestClass testClazz = new TestClass();
		testClazz.setId(10);
		testClazz.setPrinterQueueId(100);
		testClazz.setCreateTime(new Date());
		testClazz.setNewPrice(new BigDecimal(30.2345));
		testClazz.setOriginPrice(35.1221);
		testClazz.setPrice(31.12234);
		testClazz.setTableTile("贵宾001");
		return testClazz;
	}
}


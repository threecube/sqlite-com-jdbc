/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc;

import java.math.BigDecimal;
import java.util.Date;

import com.dinapin.orderdish.sqliteComJdbc.annotation.TableColumn;
import com.dinapin.orderdish.sqliteComJdbc.annotation.TableName;

/**
 * @author dingwenbin
 *
 */
@TableName("t_test_table")
public class TestClass {
	
	private Integer id;
	
	@TableColumn(name="printer_queue_Id")
	private Integer printerQueueId;
	
	@TableColumn(scale=2)
	private double price;
	
	@TableColumn(name="origin_price", scale=2)
	private double originPrice;
	
	@TableColumn(name="new_price", scale=2)
	private BigDecimal newPrice;
	
	@TableColumn(name="create_time", scale=1)
	private Date createTime;
	
	@TableColumn(name="table_title")
	private String tableTile;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the printerQueueId
	 */
	public Integer getPrinterQueueId() {
		return printerQueueId;
	}

	/**
	 * @param printerQueueId the printerQueueId to set
	 */
	public void setPrinterQueueId(Integer printerQueueId) {
		this.printerQueueId = printerQueueId;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the originPrice
	 */
	public double getOriginPrice() {
		return originPrice;
	}

	/**
	 * @param originPrice the originPrice to set
	 */
	public void setOriginPrice(double originPrice) {
		this.originPrice = originPrice;
	}

	/**
	 * @return the newPrice
	 */
	public BigDecimal getNewPrice() {
		return newPrice;
	}

	/**
	 * @param newPrice the newPrice to set
	 */
	public void setNewPrice(BigDecimal newPrice) {
		this.newPrice = newPrice;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the tableTile
	 */
	public String getTableTile() {
		return tableTile;
	}

	/**
	 * @param tableTile the tableTile to set
	 */
	public void setTableTile(String tableTile) {
		this.tableTile = tableTile;
	}

	
	
}

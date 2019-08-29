/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.enums;

/**
 * COM组件提供的方法枚举
 * 
 * @author dingwenbin
 *
 */
public enum DllMethodEnum {
	
	SAY_HELLO("sayHello", "检查方法"),
	
	DB_INIT("dbInitial", "数据库初始化"),
	
	BEGIN_TRANS("beginTransaction", "开启一个事务"),
	
	EXECUTE_QUERY("executeQuery", "执行查询"),
	
	EXECUTE_NON_QUERY("execute", "执行insert/delete/update操作"),
	
	COMMIT("commit", "事务提交"),
	
	ROLLBACK("rollback", "回滚事务");
	
	private String methodName;
	
	private String desc;
	
	DllMethodEnum(String methodName, String desc) {
		this.methodName = methodName;
		this.desc = desc;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}

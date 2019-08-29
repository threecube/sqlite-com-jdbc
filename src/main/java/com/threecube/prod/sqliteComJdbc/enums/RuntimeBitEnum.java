/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.enums;

/**
 * 运行环境的位数
 * 
 * @author dingwenbin
 *
 */
public enum RuntimeBitEnum {
	
	BITS_32("x86", "32位"),
	
	BITS_64("x64", "64位");
	
	/**
	 * 对应的资源所在目录
	 */
	private String dirName;
	
	private String desc;
	
	RuntimeBitEnum(String dirName, String desc) {
		this.dirName = dirName;
		this.desc = desc;
	}

	/**
	 * @return the dirName
	 */
	public String getDirName() {
		return dirName;
	}

	/**
	 * @param dirName the dirName to set
	 */
	public void setDirName(String dirName) {
		this.dirName = dirName;
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

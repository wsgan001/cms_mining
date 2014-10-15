/** 
 * Project Name : cms_mining 
 * File Name : NameRule.java 
 * Package Name : cms.mining.village 
 * Date : Oct 15, 2014 5:05:54 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village;

/**
 * ClassName : NameRule <br/>
 * Description : define the general naming rule for town and village. <br/>
 * date: Oct 15, 2014 5:05:54 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class NameRule {

	/**
	 * @param s
	 * @return if match the general town naming rule.
	 */
	public static boolean matchGeneralTownNaming(String s) {
		if (s == null)
			return false;
		return s.matches("[^0-9a-zA-Z]+(村|社区|居委会)$");
	}

	/**
	 * @param s
	 * @return if match the general village naming rule.
	 */
	public static boolean matchGeneralVillageNaming(String s) {
		if (s == null)
			return false;
		return s.matches("[^0-9a-zA-Z]+(镇|乡|街道|办事处|开发区)$");
	}

}

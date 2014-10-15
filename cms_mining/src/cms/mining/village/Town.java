/** 
 * Project Name : cms_mining 
 * File Name : Town.java 
 * Package Name : cms.mining.village 
 * Date : Oct 13, 2014 4:56:08 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village;

import java.util.HashSet;
import java.util.Set;

/**
 * ClassName : Town <br/>
 * Description : town Class. <br/>
 * date: Oct 13, 2014 4:56:08 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class Town {

	private String name;
	private String parent;
	private Set<String> villages;
	
	/**
	 * 
	 */
	public Town() {
		this.name = "";
		this.parent = "";
		this.villages = new HashSet<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the villages
	 */
	public Set<String> getVillages() {
		return villages;
	}

	/**
	 * @param villages
	 *            the villages to set
	 */
	public void setVillages(Set<String> villages) {
		this.villages = villages;
	}

	/**
	 * 
	 * @param village
	 */
	public void appendVillage(String village) {
		this.villages.add(village);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Town town = new Town();
		System.out.println(town);
		town.setName("向阳镇");
		town.setParent("辽宁 铁岭");
		town.appendVillage("前村");
		town.appendVillage("后村");
		System.out.println(town);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("\t");
		sb.append(parent);
		sb.append("\t");

		for (String village : villages) {
			sb.append(village);
			sb.append(";");
		}
		
		return new String(sb).replaceAll(";$", "");
	}

}

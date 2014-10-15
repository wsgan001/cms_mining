/** 
 * Project Name : cms_mining 
 * File Name : DiqudaimaTidy.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 13, 2014 4:55:54 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.mining.village.Town;
import cms.mining.village.TownConstants;
import cms.mining.village.TownTidyTask;

/**
 * ClassName : DiqudaimaTidy <br/>
 * Description : tidy diqudaima data. <br/>
 * date: Oct 13, 2014 4:55:54 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class DiqudaimaTidy  extends TownTidyTask{

	/* (non-Javadoc)
	 * @see cms.mining.village.TownTidyTask#analyse(java.lang.String)
	 */
	@Override
	public int analyse(String line) {
		
		// not_crawled
		if (line.contains("http 400 bad request") || line.contains("找不到您访问的页面"))
			return TownConstants.NOT_CRAWLED;

		// some process
		line = line.replaceAll("\\(", "（").replaceAll("\\)", "）")
				.replaceAll("（[^（）]*）", "").replaceAll("\\*", "")
				.replaceAll("\\?", "");

		// check column length
		String[] columnArray = line.split("\t");
		if (columnArray.length < 5)
			return TownConstants.LACK_COLUMN;
		String name = columnArray[2];
		if (!name.endsWith("地区代码-行政区划代码")) {
			return TownConstants.FORMAT_ERROR;
		}
		name = name.replaceAll("地区代码-行政区划代码$", "");

		// navi format
		String navi = columnArray[3];
		if (!navi.matches("网站导航:首页 -->[^ ]+ -->[^ ]+ -->[^ ]+ -->.+地区代码大全")) {
			return TownConstants.FORMAT_ERROR;
		}

		// name match with navi
		String[] naviArray = navi.split(" -->");
		String shortName = naviArray[4].replaceAll("地区代码大全$", "");
		String concatString = naviArray[1] + naviArray[3] + shortName;
		String concatString2 = naviArray[1] + naviArray[2] + naviArray[3]
				+ shortName;
		if (!name.equals(concatString) && !name.equals(concatString2)) {
			return TownConstants.FORMAT_ERROR;
		}
		String naviString = navi.replaceAll(
				"网站导航:首页 -->([^ ]+ -->[^ ]+ -->[^ ]+ -->.+)地区代码大全", "$1")
				.replaceAll("（.*", "");

		// town match with village
		String villagesString = columnArray[4];
		if (!villagesString.startsWith(name)) {
			return TownConstants.FORMAT_ERROR;
		}

		String shortNameOut = shortName.replaceAll("（.*", "")
				.replaceAll("街道办(事处)?", "街道").replaceAll("直辖地域", "")
				.replaceAll("政府$", "").replaceAll("^[0-9]+、", "").trim();

		String[] villageArray = villagesString.split("] ");
		Pattern pattern = Pattern.compile("^" + name + ".*\\[[0-9]+\\]?");
		Matcher matcher;
		town = new Town();
		town.setName(shortNameOut);
		town.setParent(naviString);
		boolean villageSpecial = false;
		for (String village : villageArray) {
			matcher = pattern.matcher(village);
			if (!matcher.matches()) {
				return TownConstants.FORMAT_ERROR;
			}
			village = village.replaceAll("^" + name, "");
			village = village.replaceAll("\\[[0-9]+\\]?$", "");
			village = village.replaceAll("村民?委*员?会?", "村")
					.replaceAll("村村", "村").replaceAll("居民?委员?会?", "居委会")
					.replaceAll("社区居委会", "社区").trim();
			if (!village.equals("") && !village.equals(shortName)
					&& !village.matches("^(村|社区|居委会)$")) {
				town.appendVillage(village.replaceAll("（.*", ""));
				if (!village.matches("[^0-9a-zA-Z]+(村|社区|居委会)$")) {
					villageSpecial = true;
				}
			}
		}

		if (town.getVillages().size() == 0) {
			return TownConstants.NO_VILLAGE;
		}

		if (!shortNameOut.matches("[^0-9a-zA-Z]+(镇|乡|街道|办事处|开发区)$")) {
			town.setLevelFlag(TownConstants.NAMING_SPECTOWN);
			return TownConstants.SUCCESS;
		}

		if (villageSpecial) {
			town.setLevelFlag(TownConstants.NAMING_SPECVIL);
			return TownConstants.SUCCESS;
		}

		town.setLevelFlag(TownConstants.NAMING_STANDARD);
		return TownConstants.SUCCESS;
	}
	
	public static void main(String[] args) throws IOException {
		DiqudaimaTidy dt = new DiqudaimaTidy();
		String fileName =args[0];
		dt.exec(fileName);
	}
}

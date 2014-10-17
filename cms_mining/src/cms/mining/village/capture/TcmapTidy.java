/** 
 * Project Name : cms_mining 
 * File Name : TcmapTidy.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 17, 2014 10:53:07 AM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.IOException;

import cms.mining.tidy.TidyTask;
import cms.mining.village.NameRule;
import cms.mining.village.Town;
import cms.mining.village.TownConstants;
import cms.mining.village.TownTidyTask;

/**
 * ClassName : TcmapTidy <br/>
 * Description : Tidy tcmap town-village file. <br/>
 * date: Oct 17, 2014 10:53:07 AM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class TcmapTidy extends TownTidyTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.village.TownTidyTask#analyse(java.lang.String)
	 */
	@Override
	public int analyse(String line) {
		line = line.replaceAll("\\(", "（").replaceAll("\\)", "）")
				.replaceAll("（[^（）0-9]*）", "");
		String[] array = line.split("\t");
		if (array.length != 3) {
			return TownConstants.LACK_COLUMN;
		}

		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] == null || array[i].equals("")) {
				return TownConstants.LACK_COLUMN;
			}
		}
		if (array[array.length - 1] == null
				|| array[array.length - 1].equals("")) {
			return TownConstants.NO_VILLAGE;
		}

		town = new Town();
		String townName = array[0];
		town.setName(townName);
		String navi = array[1];
		town.setParent(navi.replaceAll("^> ", ""));
		String[] villages = array[array.length - 1].split(";");
		boolean specialVillage = false;
		for (String village : villages) {
			village = village.replaceAll("（[^）]*$", "");
			village = village.replaceAll("村民?委员?会?", "村")
					.replaceAll("村村", "村").replaceAll("居民?委员?会?", "居委会")
					.replaceAll("社区居委会", "社区").trim();
			town.appendVillage(village);
			if (!NameRule.matchGeneralVillageNaming(village)) {
				specialVillage = true;
			}
		}

		if (!NameRule.matchGeneralTownNaming(townName)) {
			town.setLevelFlag(TownConstants.NAMING_SPECTOWN);
			return TownConstants.SUCCESS;
		}

		if (specialVillage) {
			town.setLevelFlag(TownConstants.NAMING_SPECVIL);
			return TownConstants.SUCCESS;
		}

		town.setLevelFlag(TownConstants.NAMING_STANDARD);
		return TownConstants.SUCCESS;
	}

	public static void main(String[] args) throws IOException {
		TidyTask task = new TcmapTidy();
		String fileName = args[0];
		task.exec(fileName);
	}

}

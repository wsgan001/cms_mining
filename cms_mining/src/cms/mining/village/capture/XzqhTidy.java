/** 
 * Project Name : cms_mining 
 * File Name : XzqhTidy.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 23, 2014 2:39:37 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cms.mining.tidy.TidyTask;
import cms.mining.village.NameRule;
import cms.mining.village.Town;
import cms.mining.village.TownConstants;
import cms.mining.village.TownTidyTask;

/**
 * ClassName : XzqhTidy <br/>
 * Description : TODO ADD FUNCTION. <br/>
 * date: Oct 23, 2014 2:39:37 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class XzqhTidy extends TownTidyTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.village.TownTidyTask#analyse(java.lang.String)
	 */
	@Override
	public int analyse(String line) {
		line = line.replaceAll("\\(", "（").replaceAll("\\)", "）")
				.replaceAll("（[^（）]*）", "").replaceAll(":", "：")
				.replaceAll("：", " ");

		String[] array = line.split("\t");

		if (array.length != 5) {
			return TownConstants.LACK_COLUMN;
		}

		String naviString = array[3];
		if (!naviString.startsWith("首页")) {
			return TownConstants.FORMAT_ERROR;
		}
		naviString = naviString.replaceAll("^首页\\s+", "").trim();

		String name = array[2];

		String content = array[4];
		String waveRegExp = ".*\\s～\\d{3}[^0-9.].*";
		String digit12RegExp = ".*\\d{12}.*";
		String multiRegExp = ".*(、|\\|,|，|/).*";
		if (content.matches(waveRegExp)) {
			if (!name.matches(multiRegExp)) {
				town = xzqhTown(name, naviString, content,
						XzqhTidy.PATTERN_WAVE);
				extractedString = town.toString();
				return TownConstants.SUCCESS;
			} else {
				content = content.replaceAll("【", "［").replaceAll("】", "］");
				String[] nameArray = name.split("[,/\\，、]");
				String contentSplitExp = "［[^］]*代码[^］]*］";
				String[] contentArray = content.split(contentSplitExp);

				if (contentArray.length == nameArray.length + 1) {
					town = xzqhTown(nameArray[0], naviString, contentArray[1],
							XzqhTidy.PATTERN_WAVE);
					extractedString = town.toString();
					for (int i = 1; i < nameArray.length; i++) {
						Town t = xzqhTown(nameArray[i], naviString,
								contentArray[i + 1], XzqhTidy.PATTERN_WAVE);
						extractedString += "\n";
						extractedString += t.toString();
					}
					return TownConstants.SUCCESS;
				} else {
					return TownConstants.FORMAT_ERROR;
				}
			}

		} else if (content.matches(digit12RegExp)) {
			String digitRegExp1 = ".*\\d{9,12}\\s*" + name + "\\s.*";
			if (content.matches(digitRegExp1)) {
				town = xzqhTown(name, naviString, content,
						XzqhTidy.PATTERN_DIGIT);
				extractedString = town.toString();
				return TownConstants.SUCCESS;
			} else if (name.matches(".*(镇|乡|街道)$")
					&& !name.matches(multiRegExp)) {
				town = xzqhTown(name, naviString, content,
						XzqhTidy.PATTERN_DIGIT);
				extractedString = town.toString();
				return TownConstants.SUCCESS;
			} else if (!name.matches(".*(区划|沿革|代码)$")) {
				String[] townArray = content
						.split("(?<![0-9])\\d{9}(0{3})?(?![0-9])");
				String concatString = "";
				for (int i = 1; i < townArray.length; i++) {
					String[] villageArray = townArray[i]
							.split("\\s*\\d{12}[ 0-9]*");
					String townName = villageArray[0].trim();
					town = xzqhTown(townName, naviString, townArray[i],
							XzqhTidy.PATTERN_DIGIT);
					concatString += town.toString();
					concatString += "\n";
				}
				extractedString = concatString.replaceAll("\n$", "");
				return TownConstants.SUCCESS;
			} else {
				if (array[0] == null) {//has been processed
					
				}else{
					return TownConstants.NOT_INTEGRATED;
				}
			}
		}

		return TownConstants.UNKNOWN_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.village.TownTidyTask#extract()
	 */
	@Override
	public String extract() {
		return extractedString;
	}

	private Town xzqhTown(String name, String parent, String content,
			int pattern) {
		Town town = new Town();
		town.setName(name);
		town.setParent(parent);

		boolean vilSpec = false;

		switch (pattern) {
		case XzqhTidy.PATTERN_WAVE:
			content = content.replaceAll("[【】］［]", " ");
			String[] matchArray = content.split("\\s～[ 0-9]+");
			for (int i = 1; i < matchArray.length; i++) {
				String[] unit = matchArray[i].split("\\s");
				String village = unit[0].trim();
				if (!NameRule.matchGeneralVillageNaming(village)) {
					vilSpec = true;
				}
				town.appendVillage(village);
			}
			break;
		case XzqhTidy.PATTERN_DIGIT:
			content = content.replaceAll("[【】］［]", " ");
			String[] villageArray = content.split("\\s*\\d{12}[ 0-9]*");
			for (int i = 1; i < villageArray.length; i++) {
				String village = villageArray[i].split("\\s+")[0].trim();
				if (!NameRule.matchGeneralVillageNaming(village)) {
					vilSpec = true;
				}
				town.appendVillage(village);
			}
			break;

		default:
			break;
		}

		if (!NameRule.matchGeneralTownNaming(name)) {
			town.setLevelFlag(TownConstants.NAMING_SPECTOWN);
		} else if (vilSpec) {
			town.setLevelFlag(TownConstants.NAMING_SPECVIL);
		} else {
			town.setLevelFlag(TownConstants.NAMING_STANDARD);
		}

		return town;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TidyTask xzqhTidyTask = new XzqhTidy();
		String fileName = args[0];
		xzqhTidyTask.exec(fileName);

		File notIntegratedFile = new File(fileName + "_out_",
				TownConstants.NOT_INTEGRATED + "_notintegrated");
		if (notIntegratedFile.exists()) {
			fixNotIntegrated(notIntegratedFile, "gbk");
		}
	}

	/**
	 * @param notIntegratedFile
	 * @throws IOException
	 * 
	 */
	private static void fixNotIntegrated(File notIntegratedFile, String charSet)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(notIntegratedFile), charSet));
		String line = "";
		
		while ((line = reader.readLine()) != null) {
			String[] array = line.split("\t");
			String key = array[2] + "+" + array[3];
		}
	}

	public static final int PATTERN_WAVE = 1;
	public static final int PATTERN_DIGIT = 2;

}

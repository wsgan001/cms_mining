/** 
 * Project Name : cms_mining 
 * File Name : Diqudaima.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 13, 2014 4:55:54 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.mining.village.Town;

/**
 * ClassName : Diqudaima <br/>
 * Description : TODO ADD FUNCTION. <br/>
 * date: Oct 13, 2014 4:55:54 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class Diqudaima {

	/**
	 * 
	 */
	public Diqudaima() {
		// TODO Auto-generated constructor stub
	}

	public final static int SUCCESS = 0;
	public final static int NOT_CRAWLED = 1;
	public final static int LACK_COLUMN = 2;
	public final static int NO_VILLAGE = 3;
	public final static int SPECIAL_TOWN = 4;
	public final static int SPECIAL_VILLAGE = 5;

	public final static int FORMAT_ERROR = -1;
	public final static int UNKNOWN_ERROR = -2;

	public final static String SAMPLE_NOTCRAWLED = "11037292987818446984	http://www.diqudaima.com/address/410327209000.html	http 400 bad request	";
	public final static String SAMPLE_COLUMNLACK = "7184101516388980536	http://www.diqudaima.com/address/530427000000.html	云南省新平彝族傣族自治县新平县地区代码-行政区划代码	网站导航:首页 -->云南省 -->玉溪市 -->新平彝族傣族自治县 -->新平县地区代码大全	";

	private Town town;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String fileName = args[0];
		String outFileName = fileName + "_out";
		String outFileNameErr = fileName + "_out_error";
		String charSet = "gbk";
		int batchWriteSize = 100;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(fileName)), charSet));
		// ignore table head
		reader.readLine();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFileName), charSet));
		BufferedWriter writerErr = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFileNameErr), charSet));
		// clear
		File inFile = new File(fileName);
		File dir = inFile.getParentFile();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getAbsolutePath().startsWith(fileName)
					&& file.getAbsolutePath().contains("_out")) {
				file.delete();
			}
		}

		Diqudaima diqudaima = new Diqudaima();
		String line = "";
		int count = 0;
		int countErr = 0;
		int countNotCrawled = 0;
		int countLackColumn = 0;
		int countNoVillageTown = 0;
		int countSpecialTown = 0;
		int countSpecialVillage = 0;
		while ((line = reader.readLine()) != null && !"".equals(line)) {

			switch (diqudaima.matchRule(line)) {
			case Diqudaima.NOT_CRAWLED:
				writeTofile(line, fileName + "_out_", Diqudaima.NOT_CRAWLED
						+ "_notcrawled", charSet);
				countNotCrawled++;
				break;
			case Diqudaima.LACK_COLUMN:
				writeTofile(line, fileName + "_out_", Diqudaima.LACK_COLUMN
						+ "_lackcolumn", charSet);
				countLackColumn++;
				break;
			case Diqudaima.NO_VILLAGE:
				line = diqudaima.extract();
				writeTofile(line, fileName + "_out_", Diqudaima.NO_VILLAGE
						+ "_novillage", charSet);
				countNoVillageTown++;
				break;
			case Diqudaima.SPECIAL_TOWN:
				line = diqudaima.extract();
				writeTofile(line, fileName + "_out_", Diqudaima.SPECIAL_TOWN
						+ "_specialtown", charSet);
				countSpecialTown++;
				break;
			case Diqudaima.SPECIAL_VILLAGE:
				line = diqudaima.extract();
				writeTofile(line, fileName + "_out_", Diqudaima.SPECIAL_VILLAGE
						+ "_specialvillage", charSet);
				countSpecialVillage++;
				break;
			case Diqudaima.SUCCESS:
				line = diqudaima.extract();
				writer.write(line);
				writer.write("\n");
				if (++count % batchWriteSize == 0) {
					writer.flush();
				}
				break;
			default:
				writerErr.write(line);
				writerErr.write("\n");
				if (++countErr % batchWriteSize == 0) {
					writerErr.flush();
				}
			}
		}
		writer.flush();
		writer.close();
		writerErr.flush();
		writerErr.close();

		reader.close();

		System.out.println("未抓取到数据: " + countNotCrawled + " 条");
		System.out.println("未抓取到数据示例: " + SAMPLE_NOTCRAWLED);
		System.out.println("字段数不足: " + countLackColumn + " 条");
		System.out.println("字段数不足示例: " + SAMPLE_COLUMNLACK);
		System.out.println("不包含村: " + countNoVillageTown + " 条");
		System.out.println("特殊乡镇名: " + countSpecialTown + " 条");
		System.out.println("特殊村名: " + countSpecialVillage + " 条");
		System.out.println("未匹配规则: " + countErr + " 条");
	}

	/**
	 * @param line
	 * @param fileName
	 * @param string
	 * @param charSet
	 * @throws IOException
	 */
	private static void writeTofile(String line, String fileName,
			String endfix, String charSet) throws IOException {
		@SuppressWarnings("resource")
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName + endfix, true), charSet));
		writer.write(line);
		writer.write("\n");
		writer.flush();
	}

	/**
	 * @param line
	 * @return
	 */
	private String extract() {
		return town.toString();
	}

	private int matchRule(String line) {

		// not_crawled
		if (line.contains("http 400 bad request") || line.contains("找不到您访问的页面"))
			return Diqudaima.NOT_CRAWLED;

		// some process
		line = line.replaceAll("\\(", "（").replaceAll("\\)", "）")
				.replaceAll("（[^（）]*）", "").replaceAll("\\*", "")
				.replaceAll("\\?", "");
		// if (line.matches(".*[()（）].*")){
		// return Diqudaima.LACK_PARENTHESIS;
		// }

		// check column length
		String[] columnArray = line.split("\t");
		if (columnArray.length < 5)
			return Diqudaima.LACK_COLUMN;
		String name = columnArray[2];
		if (!name.endsWith("地区代码-行政区划代码")) {
			return Diqudaima.FORMAT_ERROR;
		}
		name = name.replaceAll("地区代码-行政区划代码$", "");

		// navi format
		String navi = columnArray[3];
		if (!navi.matches("网站导航:首页 -->[^ ]+ -->[^ ]+ -->[^ ]+ -->.+地区代码大全")) {
			return Diqudaima.FORMAT_ERROR;
		}

		// name match with navi
		String[] naviArray = navi.split(" -->");
		String shortName = naviArray[4].replaceAll("地区代码大全$", "");
		String concatString = naviArray[1] + naviArray[3] + shortName;
		String concatString2 = naviArray[1] + naviArray[2] + naviArray[3]
				+ shortName;
		if (!name.equals(concatString) && !name.equals(concatString2)) {
			return Diqudaima.FORMAT_ERROR;
		}
		String naviString = navi.replaceAll(
				"网站导航:首页 -->([^ ]+ -->[^ ]+ -->[^ ]+ -->.+)地区代码大全", "$1")
				.replaceAll("（.*", "");

		// town match with village
		String villagesString = columnArray[4];
		if (!villagesString.startsWith(name)) {
			return Diqudaima.FORMAT_ERROR;
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
				return Diqudaima.FORMAT_ERROR;
			}
			village = village.replaceAll("^" + name, "");
			village = village.replaceAll("\\[[0-9]+\\]?$", "");
			village = village.replaceAll("村民?委*员?会?", "村")
					.replaceAll("村村", "村").replaceAll("居民?委员?会?", "居委会")
					.replaceAll("社区居委会", "社区").trim();
			if (!village.equals("") && !village.equals(shortName)
					&& village.matches("^(村|社区|居委会)$")) {
				town.appendVillage(village.replaceAll("（.*", ""));
				if (!village.matches("[^0-9a-zA-Z]+(村|社区|居委会)$")) {
					villageSpecial = true;
				}
			}
		}

		if (town.getVillages().size() == 0) {
			return Diqudaima.NO_VILLAGE;
		}

		if (!shortNameOut.matches("[^0-9a-zA-Z]+(镇|乡|街道|办事处|开发区)$")) {
			return Diqudaima.SPECIAL_TOWN;
		}

		if (villageSpecial) {
			return Diqudaima.SPECIAL_VILLAGE;
		}

		return Diqudaima.SUCCESS;

	}
}

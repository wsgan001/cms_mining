/** 
 * Project Name : cms_mining 
 * File Name : TownTidyTask.java 
 * Package Name : cms.mining.village 
 * Date : Oct 14, 2014 5:53:43 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cms.mining.tidy.TidyTask;

/**
 * ClassName : TownTidyTask <br/>
 * Description : town tidy process entrance. <br/>
 * date: Oct 14, 2014 5:53:43 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public abstract class TownTidyTask extends TidyTask {

	protected static int batchWriteSize = 100;
	protected Town town;

	/**
	 * 
	 * @param fileName
	 * @param charSet
	 * @throws IOException
	 */
	public void exec(String fileName, String charSet, boolean hasTitle)
			throws IOException {
		String outFileName = fileName + "_out";
		String outFileNameErr = fileName + "_out_error";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(fileName)), charSet));

		// ignore title
		if (hasTitle) {
			reader.readLine();
		}

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

		String line = "";
		int countTotal = 0;
		int countSuc = 0;
		int countErr = 0;
		int countNotCrawled = 0;
		int countLackColumn = 0;
		int countNoVillageTown = 0;
		int countFormatErr = 0;
		while ((line = reader.readLine()) != null && !"".equals(line)) {

			countTotal++;
			switch (analyse(line)) {
			case TownConstants.NOT_CRAWLED:
				writeTofile(line, fileName + "_out_", TownConstants.NOT_CRAWLED
						+ "_notcrawled", charSet);
				countNotCrawled++;
				break;
			case TownConstants.LACK_COLUMN:
				writeTofile(line, fileName + "_out_", TownConstants.LACK_COLUMN
						+ "_lackcolumn", charSet);
				countLackColumn++;
				break;
			case TownConstants.NO_VILLAGE:
				line = extract();
				writeTofile(line, fileName + "_out_", TownConstants.NO_VILLAGE
						+ "_novillage", charSet);
				countNoVillageTown++;
				break;
			// case TownConstants.SPECIAL_TOWN:
			// line = extract();
			// writeTofile(line, fileName + "_out_", TownConstants.SPECIAL_TOWN
			// + "_specialtown", charSet);
			// countSpecialTown++;
			// break;
			// case TownConstants.SPECIAL_VILLAGE:
			// line = extract();
			// writeTofile(line, fileName + "_out_",
			// TownConstants.SPECIAL_VILLAGE
			// + "_specialvillage", charSet);
			// countSpecialVillage++;
			// break;
			case TownConstants.SUCCESS:
				line = extract();
				writer.write(line);
				writer.write("\n");
				if (++countSuc % batchWriteSize == 0) {
					writer.flush();
				}
				break;
			case TownConstants.FORMAT_ERROR:
				writeTofile(line, fileName + "_out_", TownConstants.FORMAT_ERROR
						+ "_formaterror", charSet);
				countFormatErr++;
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

		System.out.println("共处理: " + countTotal + " 条");
		System.out.println("成功: " + countSuc + " 条");
		System.out.println("未抓取: " + countNotCrawled + " 条");
		System.out.println("字段数不足: " + countLackColumn + " 条");
		System.out.println("不包含村: " + countNoVillageTown + " 条");
		System.out.println("格式错误: " + countFormatErr + " 条");
		System.out.println("其它错误: " + countErr + " 条");
	}

	/**
	 * default hasTitle: true
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void exec(String fileName, String charSet) throws IOException {
		exec(fileName, charSet, true);
	}

	/**
	 * default charset: gbk
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void exec(String fileName) throws IOException {
		exec(fileName, "gbk");
	}

	/**
	 * Analyse original info, and construct town object.
	 * @param line
	 * @return analyse flag to identify which class this line belongs to.
	 */
	public abstract int analyse(String line);

	/**
	 * @param line
	 * @param fileName
	 * @param string
	 * @param charSet
	 * @throws IOException
	 */
	public final static void writeTofile(String line, String fileName,
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
	 * @return output String.
	 */
	public String extract() {
		return town.toString();
	}

}

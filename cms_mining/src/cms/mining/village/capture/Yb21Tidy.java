/** 
 * Project Name : cms_mining 
 * File Name : Yb21Tidy.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 16, 2014 10:40:05 AM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.IOException;

import cms.mining.village.TownConstants;
import cms.mining.village.TownTidyTask;

/**
 * ClassName : Yb21Tidy <br/>
 * Description : Yb21 tidy task. <br/>
 * date: Oct 16, 2014 10:40:05 AM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class Yb21Tidy extends TownTidyTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.village.TownTidyTask#analyse(java.lang.String)
	 */
	@Override
	public int analyse(String line) {
		String array[] = line.split("\t");

		if (array.length != 5) {
			return TownConstants.FORMAT_ERROR;
		}

		String code = array[2];
		if (!code.matches("^[0-9]{6}$")) {
			return TownConstants.FORMAT_ERROR;
		}

		String navi = array[3];
		if (!navi.matches("^[^-]+-[^-]+ -[^-]+$")) {
			return TownConstants.FORMAT_ERROR;
		}
		navi = navi.replaceAll(" -", "-");

		String content = array[4];
		content = content.replaceAll("邮政编码为[ 0-9]+的行政单位共有[ 0-9]+个$", "")
				.replaceAll("邮政编码", "").replaceAll("邮编", "")
				.replaceAll("  ", " ").trim();

		extractedString = code + "\t" + navi + "\t" + content;

		return TownConstants.SUCCESS;
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

	public static void main(String[] args) throws IOException {
		Yb21Tidy yb21Tidy = new Yb21Tidy();
		String fileName = args[0];
		yb21Tidy.exec(fileName);
	}

}

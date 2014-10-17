/** 
 * Project Name : cms_mining 
 * File Name : TcmapCrawl.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 16, 2014 3:44:43 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */ 
package cms.mining.village.capture;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cms.mining.crawl.CommonCrawlTask;
import cms.mining.crawl.CrawlTask;

/** 
 * ClassName : TcmapCrawl <br/> 
 * Description : TODO ADD FUNCTION. <br/> 
 * date: Oct 16, 2014 3:44:43 PM <br/> 
 * 
 * @author zhanglei01 
 * @version  
 * @since JDK 1.6 
 */
public class TcmapCrawl extends CommonCrawlTask {

	/**
	 * @param sourceFileName
	 * @param sourceFileCharset
	 * @param outputFileName
	 * @param outputFileCharset
	 */
	public TcmapCrawl(String sourceFileName, String sourceFileCharset,
			String outputFileName, String outputFileCharset) {
		super(sourceFileName, sourceFileCharset, outputFileName,
				outputFileCharset);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sourceFileName
	 * @param outputFileName
	 */
	public TcmapCrawl(String sourceFileName, String outputFileName) {
		super(sourceFileName, outputFileName);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see cms.mining.crawl.CommonCrawlTask#format(java.lang.String)
	 */
	@Override
	public String format(String text) {
		Document document = Jsoup.parse(text);
		Elements navi = document.getElementsByAttributeValue("style", "margin:3px 0 0 5px;");
		String naviString = navi.text();
		String[] naviArray = naviString.split("> ");
		String shortName = naviArray[naviArray.length -1 ];
		
		Elements detail = document.getElementsByAttributeValue("style", "padding:0 0 0 10px;");
		String dataString = detail.text();
		dataString = dataString.replaceAll("\\d{12}\\s\\d{3}\\s", "").trim().replaceAll("\\s", ";");
		
		String result = shortName + "\t" + naviString + "\t" + dataString;
		return result;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String inFile = args[0];
		String outFIle = args[1];
		CrawlTask task = new TcmapCrawl(inFile, "gbk", outFIle, "gbk");
		task.exec();
	}

}

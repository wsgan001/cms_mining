/** 
 * Project Name : cms_mining 
 * File Name : DiqudaimaCrawl.java 
 * Package Name : cms.mining.village.capture 
 * Date : Oct 15, 2014 2:47:00 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.village.capture;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cms.mining.crawl.CommonCrawlTask;

/**
 * ClassName : DiqudaimaCrawl <br/>
 * Description : crawl diqudaima data by url list. <br/>
 * date: Oct 15, 2014 2:47:00 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class DiqudaimaCrawl extends CommonCrawlTask {

	/**
	 * @param sourceFileName
	 * @param sourceFileCharset
	 * @param outputFileName
	 * @param outputFileCharset
	 */
	public DiqudaimaCrawl(String sourceFileName, String sourceFileCharset,
			String outputFileName, String outputFileCharset) {
		super(sourceFileName, sourceFileCharset, outputFileName,
				outputFileCharset);
	}

	/**
	 * @param sourceFileName
	 * @param outputFileName
	 */
	public DiqudaimaCrawl(String sourceFileName, String outputFileName) {
		super(sourceFileName, outputFileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.crawl.CommonCrawlTask#format(java.lang.String)
	 */
	@Override
	public String format(String text) {
		Document doc = Jsoup.parse(text);

		Elements head = doc.getElementsByTag("title");
		String title = head.text();
		Elements span = doc.getElementsByTag("span");
		String navi = span.text();
		navi = navi.replaceAll("-->", " -->");

		Elements li = doc.getElementsByTag("li");
		String content = "";
		for (Element element : li) {
			content += element.text();
			content += " ";
		}
		content = content.trim();

		return title + "\t" + navi + "\t" + content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.crawl.CommonCrawlTask#process()
	 */
	@Override
	protected void process() throws IOException {
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			String[] array = line.split("\t");
			String id = array[0];
			String url = array[1];
			String text = crawl(url);
			String formatedText = format(text);
			writer.write(id + "\t" + url + "\t" + formatedText);
			writer.write("\n");

			if (++count % batchWriteSize == 0) {
				writer.flush();
				System.out.println("DiqudaimaCrawl: 已处理 " + count + " 条");
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String inFile = args[0];
		String outFIle = args[1];
		DiqudaimaCrawl task = new DiqudaimaCrawl(inFile, "gbk", outFIle, "gbk");
		task.exec();
	}

}

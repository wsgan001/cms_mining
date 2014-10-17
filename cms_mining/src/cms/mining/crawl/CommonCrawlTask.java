/** 
 * Project Name : cms_mining 
 * File Name : CommonCrawlTask.java 
 * Package Name : cms.mining.crawl 
 * Date : Oct 15, 2014 12:02:06 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ClassName : CommonCrawlTask <br/>
 * Description : CommonCrawlTask. Read urlList From a plain file,simple download
 * url, non-format, write content to a text file, one row for each url <br/>
 * date: Oct 15, 2014 12:02:06 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class CommonCrawlTask extends CrawlTask {

	protected String sourceFileName;
	protected String sourceFileCharset;
	protected String outputFileName;
	protected String outputFileCharset;

	protected BufferedReader reader;
	protected BufferedWriter writer;
	protected int batchWriteSize = 10;

	protected HttpURLConnection conn;

	/**
	 * @param sourceFileName
	 * @param sourceFileCharset
	 * @param outputFileName
	 * @param outputFileCharset
	 */
	public CommonCrawlTask(String sourceFileName, String sourceFileCharset,
			String outputFileName, String outputFileCharset) {
		super();
		this.sourceFileName = sourceFileName;
		this.sourceFileCharset = sourceFileCharset;
		this.outputFileName = outputFileName;
		this.outputFileCharset = outputFileCharset;
	}

	/**
	 * default charset : utf-8
	 * 
	 * @param sourceFileName
	 * @param outputFileName
	 */
	public CommonCrawlTask(String sourceFileName, String outputFileName) {
		this.sourceFileName = sourceFileName;
		this.sourceFileCharset = "UTF-8";
		this.outputFileName = outputFileName;
		this.outputFileCharset = "UTF-8";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.crawl.CrawlTask#crawl()
	 */
	@Override
	public String crawl(String urlString) throws IOException {
		// TODO Auto-generated method stub
		URL url = new URL(urlString);

		conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60000);
		conn.setReadTimeout(60000);
		
		conn.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), outputFileCharset));

		String lines = "";
		String line;
		while ((line = reader.readLine()) != null) {
			lines += line;
		}

		reader.close();
		conn.disconnect();
		return lines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cms.mining.crawl.CrawlTask#format()
	 */
	@Override
	public String format(String text) {
		return text;
	}

	@Override
	public void exec() throws IOException {
		if (!init()) {
			System.err.println("Initial failed! Please check the file path!");
			return;
		}
		
		process();

		writer.flush();

		if (!close()) {
			System.err.println("Close failed for IOException!");
		}
	}

	/**
	 * 
	 */
	protected boolean close() {
		try {
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	protected boolean init() {
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(sourceFileName), sourceFileCharset));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFileName), outputFileCharset));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws IOException {
		String inFile = args[0];
		String outFIle = args[1];
		CommonCrawlTask cTask = new CommonCrawlTask(inFile, "utf-8", outFIle,
				"gbk");
		cTask.exec();
	}
	
	protected void process() throws IOException {
		String url;
		int count = 0;
		while ((url = reader.readLine()) != null) {
			String text = crawl(url);
			String formatedText = format(text);
			writer.write(formatedText);
			writer.write("\n");

			if (++count % batchWriteSize == 0) {
				writer.flush();
				System.out.println("CommonCrawlTask: 已处理 " + count + " 条");
			}
		}
	}

}

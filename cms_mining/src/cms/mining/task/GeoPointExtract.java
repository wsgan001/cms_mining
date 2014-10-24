/** 
 * Project Name : cms_mining 
 * File Name : GeoPointExtract.java 
 * Package Name : cms.mining.task 
 * Date : Oct 22, 2014 12:03:54 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpConnection;
import org.apache.http.client.ClientProtocolException;

import cms.mining.utils.http.HttpClientUtils;

/**
 * ClassName : GeoPointExtract <br/>
 * Description : Extract geo point by inputting addrs. <br/>
 * date: Oct 22, 2014 12:03:54 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public class GeoPointExtract {

	private BufferedReader reader;
	private ArrayBlockingQueue<String> queue;

	/**
	 * @param fileName
	 * @param charSet
	 */
	public GeoPointExtract(String fileName, String charSet) {
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), charSet));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		queue = new ArrayBlockingQueue<>(100000);

	}

	public static void main(String[] args) throws IOException {
		String fileName = args[0];
		String charSet = args[1];
		File outFile = new File("geoPoint_out.txt");
		outFile.delete();
		GeoPointExtract gpt = new GeoPointExtract(fileName, charSet);
		gpt.exec();
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void exec() throws IOException {

		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < 30; i++) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("geoPoint_out_" + i + ".txtn", true), "gbk"));
			Thread getPoint = new GetPoint(queue, writer);
			threadList.add(getPoint);
			getPoint.start();
			System.out.println("Thread " + getPoint.getId() + " start!");
		}

		String line;
		while ((line = reader.readLine()) != null) {
			inner: while (true) {
				if (queue.size() < 50000) {
					queue.add(line);
					break inner;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class GetPoint extends Thread {

	private ArrayBlockingQueue<String> queue;
	private BufferedWriter writer;
	private String urlString;
	
	private InputStreamReader reader;
	private HttpURLConnection connection;

	/**
	 * @param queue
	 * @param writer
	 */
	public GetPoint(ArrayBlockingQueue<String> queue, BufferedWriter writer) {
		this.queue = queue;
		this.writer = writer;
		urlString = "http://10.16.31.23/geo";
	}

	@Override
	public void run() {
		int count = 0;
		while (true) {
			if (!queue.isEmpty()) {
				String line = queue.poll();
				try {
					exec(line);
					if (++count % 1000 == 0) {
						writer.flush();
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param line
	 * @throws IOException
	 * @throws ClientProtocolException
	 * 
	 */
	private void exec(String line) throws ClientProtocolException, IOException {
		if (line == null)
			return;
		String[] array = line.split("\t");
		Map<String, String> params = new HashMap<String, String>();
		params.put("op", "geo");
		params.put("name", array[1]);
		params.put("addr", array[2]);
		params.put("citycode", array[4]);

		// String result = HttpClientUtils.doGet(urlString, params, "utf-8");
		String url = urlString + "?" + "op=geo&name="
				+ URLEncoder.encode(array[1], "utf-8") + "&addr="
				+ URLEncoder.encode(array[2], "utf-8") + "&citycode="
				+ array[4];

		URL url2 = new URL(url);
		connection = (HttpURLConnection) url2
				.openConnection();
		connection.connect();
		
		long begin = System.currentTimeMillis();
		reader = new InputStreamReader(
				connection.getInputStream(), "gbk");
		
		int offset = 0;
		int length = 100;
		int readNum;
		char[] cbuf = new char[2048];
		while ((readNum = reader.read(cbuf, offset, length)) > 0) {
			offset += readNum;
		}
		String result = String.valueOf(cbuf);
		reader.close();
		connection.disconnect();
		System.out.println(System.currentTimeMillis() - begin);

		if (result.startsWith("{\"status\": \"0\"")) {
			result = result
					.replaceAll(
							".*\"key_name\"[^\"]*\"([^\"]*)\".*\"point_name\"[^\"]*\"([^\"]*)\".*",
							"$1\t$2");
		}

		String outputString = array[0] + "\t" + array[1] + "\t" + array[2]
				+ "\t" + array[4] + "\t" + result;

		writer.write(outputString);
		writer.write("\n");
	}

}

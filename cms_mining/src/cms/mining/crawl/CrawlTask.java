/** 
 * Project Name : cms_mining 
 * File Name : CrawlTask.java 
 * Package Name : cms.mining.crawl 
 * Date : Oct 15, 2014 11:38:22 AM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */ 
package cms.mining.crawl;

import java.io.IOException;

/** 
 * ClassName : CrawlTask <br/> 
 * Description : Crawl Task, get resource by a url list and output formated result. <br/> 
 * date: Oct 15, 2014 11:38:22 AM <br/> 
 * 
 * @author zhanglei01 
 * @version  
 * @since JDK 1.6 
 */
public abstract class CrawlTask {
	
	public abstract String crawl(String url) throws IOException;
	
	public abstract String format(String text);
	
	public abstract void exec() throws IOException;
	

}

/** 
 * Project Name : cms_mining 
 * File Name : TidyTask.java 
 * Package Name : cms.mining.tidy 
 * Date : Oct 14, 2014 6:03:58 PM 
 * Copyright (c) 2014, zhanglei083@gmail.com All Rights Reserved. 
 */
package cms.mining.tidy;

import java.io.IOException;

/**
 * ClassName : TidyTask <br/>
 * Description : TODO ADD FUNCTION. <br/>
 * date: Oct 14, 2014 6:03:58 PM <br/>
 * 
 * @author zhanglei01
 * @version
 * @since JDK 1.6
 */
public abstract class TidyTask {

	public abstract void exec(String fileName, String charSet, boolean hasTitle) throws IOException;

	public abstract void exec(String fileName, String charSet) throws IOException;

	public abstract void exec(String fileName) throws IOException;

	public abstract int analyse(String line);

}

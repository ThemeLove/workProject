package cc.ak.sdk.tool.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cc.ak.sdk.tool.bean.Channel;
import cc.ak.sdk.tool.bean.Operation;

/**
 * 渠道解析器
 */
public class ChannelXMLParser {
	
	public static Channel parser(String xmlPath) {
		Channel channel = new Channel();
		try {
			SAXReader saxReader = new SAXReader();
	        Element root = saxReader.read(new File(xmlPath)).getRootElement();
	        
	        Element operations = root.element("operations");
        	if(operations != null) {
        		List<Operation> operationList = new ArrayList<Operation>();
        		for(Element operation : operations.elements()) {
        			Operation oper = new Operation();
        			oper.setType(operation.attributeValue("type"));
        			oper.setFrom(operation.attributeValue("from"));
        			oper.setTo(operation.attributeValue("to"));
        			operationList.add(oper);
        		}
        		channel.setOperations(operationList);
        	}
	        
        	Element applications = root.element("applications");
        	if(applications != null) {
        		List<String> applicationList = new ArrayList<String>();
        		for(Element application : applications.elements()) {
        			applicationList.add(application.attributeValue("name"));
        		}
        		channel.setApplications(applicationList);
        	}
        	
        	Element plugins = root.element("plugins");
        	if(plugins != null) {
        		Map<String, String> pluginMap = new HashMap<String, String>();
        		for(Element plugin : plugins.elements()) {
        			pluginMap.put(plugin.attributeValue("type"), plugin.attributeValue("name"));
        		}
        		channel.setPlugins(pluginMap);
        	}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return channel;
	}
	
}

package cc.ak.sdk.tool.bean;

import java.util.List;
import java.util.Map;

public class Channel {
	
	private List<String> applications;
	private Map<String, String> plugins;
	private List<Operation> operations;
	
	public List<String> getApplications() {
		return applications;
	}
	public void setApplications(List<String> applications) {
		this.applications = applications;
	}
	public Map<String, String> getPlugins() {
		return plugins;
	}
	public void setPlugins(Map<String, String> plugins) {
		this.plugins = plugins;
	}
	public List<Operation> getOperations() {
		return operations;
	}
	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
	
}

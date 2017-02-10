package online.dinghuiye.icanseeupload.core;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public interface HttpServletRequestPlus {
	
	List<UploadFile> getFiles();

	Enumeration<String> getParameterNames();
	
	String getParameter(String name);
	
	String[] getParameterValues(String name);
	
	Map<String, String[]> getParameterMap();
}

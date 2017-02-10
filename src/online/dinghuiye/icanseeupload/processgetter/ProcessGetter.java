package online.dinghuiye.icanseeupload.processgetter;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * ProcessGetter.
 * use def
 */
public class ProcessGetter {

	public static Object getProcess(HttpServletRequest req, String sessionKey) {
		Object process = req.getSession(true).getAttribute(sessionKey);
		if (process == null) {
			process = 0;
		} else {
			String processStr = String.valueOf(process);
			
			// if exception occur, write String message in session
			// finish or String message occur, it is stop
			if ("100".equals(processStr) || !processStr.matches("[0-9]+")) {
				req.getSession(true).removeAttribute(sessionKey);
			}
		}
		return process;
	}
}

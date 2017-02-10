package online.dinghuiye.icanseeupload.impl.process;

import java.util.Observable;

import javax.servlet.http.HttpServletRequest;

import online.dinghuiye.icanseeupload.core.processobserve.ProcessObserver;

/**
 * 
 * DefProcessObserver.
 * write process percent to session with custom session key
 */
public class DefProcessObserver implements ProcessObserver {

	private HttpServletRequest req;
	private String sessionKey;
	private ProcessBar bar;
	
	/**
	 * use default "process" for session key
	 */
	public DefProcessObserver(HttpServletRequest req) {
		this(req, "process");
	}
	
	public DefProcessObserver(HttpServletRequest req, String sessionKey) {
		this.req = req;
		this.sessionKey = sessionKey;
		this.bar = new ProcessBar(0, req.getContentLength());
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof Integer)) {
			req.getSession(true).setAttribute(sessionKey, "error");
			return;
		}
		int inCome = Integer.valueOf(String.valueOf(arg));
		bar.setProcess(bar.getProcess() + inCome);
		long processPer = bar.getProcess() * 100 / bar.getTotal();
		req.getSession(true).setAttribute(sessionKey, processPer);
	}
}

package online.dinghuiye.icanseeupload.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.oreilly.servlet.multipart.FileRenamePolicy;

import online.dinghuiye.icanseeupload.core.kit.PathKit;
import online.dinghuiye.icanseeupload.core.processobserve.ProcessObservable;
import online.dinghuiye.icanseeupload.core.processobserve.ProcessObserver;
import online.dinghuiye.icanseeupload.core.proxy.ServletInputStreamPorxy;
import online.dinghuiye.icanseeupload.impl.process.DefProcessObserver;

/**
 * 
 * instead request when upload files.
 * if upload file, use this object to get files and parameters.
 * usage: 
 * 	1, globally initialize object and parameters with: 
 * 		FileUploadRequest req = new FileUploadRequest(..).set..(..);
 *  2, get what you want: 
 * 		req.get..(..);
 *  3, of course, you can also get file(s) without global initialization with:
 *  	req.get..(.., .., ..)
 *  4, upload parameters can be set both at initializing and getting files.
 *  	but settings will be valid when uploading really realize, 
 *  	and all get..(..) will use the same settings.
 */
public class FileUploadRequest extends HttpServletRequestWrapper {
	
	private ProcessObservable procsObsvb;
	private HttpServletRequestPlus caller;
	private String sessionKey;
	
	/**
	 * 
	 * only use file upload function, no process listened
	 */
	public FileUploadRequest(HttpServletRequest realRequest) {
		super(realRequest);
		autoGlobalConfig();
	}
	
	public FileUploadRequest(HttpServletRequest realRequest, String sessionKey) {
		this(realRequest, new DefProcessObserver(realRequest, sessionKey));
		this.sessionKey = sessionKey;
	}
	
	/**
	 * constructor did not consider remove session with sessionKey defined by user 
	 */
	public FileUploadRequest(HttpServletRequest realRequest, final ProcessObserver procsObsv) {
		this(realRequest);
		this.procsObsvb = new ProcessObservable() {
			@Override
			public void change(int inCome) {
				procsObsv.update(this, inCome);
			}
		};
		autoGlobalConfig();
	}
	
	/**
	 * constructor did not consider remove session with sessionKey defined by user 
	 */
	public FileUploadRequest(HttpServletRequest realRequest, ProcessObservable procsObsvb) {
		this(realRequest);
		this.procsObsvb = procsObsvb;
		autoGlobalConfig();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		ServletInputStream in = this.getRequest().getInputStream();
		ServletInputStream inProxy = new ServletInputStreamPorxy(in, procsObsvb);
		return inProxy;
	};
	
	
	
	/*
	 * private setting for this request
	 */
	/**
	 * private upload parameters for this request, if all is not set, use default values in MultipartRequestCaller, 
	 * sub path of baseUploadPath or current set instead of baseUploadPath (if absolute path).
	 */
	private String uploadPath;
	private int maxPostSize;
	private String encoding;
	private FileRenamePolicy fileRenamePolicy;
	
	public FileUploadRequest setUploadPath(String uploadPath) {
		this.uploadPath = calcSubUploadPath(uploadPath);
		return this;
	}
	public FileUploadRequest setMaxPostSize(int maxPostSize) {
		this.maxPostSize = maxPostSize;
		return this;
	}
	public FileUploadRequest setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}
	public FileUploadRequest setFileRenamePolicy(FileRenamePolicy fileRenamePolicy) {
		this.fileRenamePolicy = fileRenamePolicy;
		return this;
	}
	
	/**
	 * calculate uploadPath for request, 
	 * set sub upload path and get absolute path
	 * 
	 * "new GlobalConfig()" must be first
	 * 
	 * @param uploadPath
	 * 				for private setting
	 * 				relationship of value and folder shows below  
	 * 					values	- folders
	 * 					null	- "WebRoot/upload" (default)
	 * 					"path"	- "WebRoot/upload/path"
	 * 					"fi le"	- "WebRoot/upload/fi_le"
	 * 					"" " "	- "WebRoot"
	 * 					"/"	"\"	- linux: "/", windows: deploy root driver path, eg: "D:/"
	 * 					"/path"	- linux： "/path", windows: sub path of deploy root driver, eg: "D:/path"
	 * 					"X:\"	- "D:/" only for windows 
	 * @return
	 * 		if uploadPath is not a absolute path: WebRoot/uploadPath
	 * 		else uploadPath
	 * 		after base upload path no "/" or "\" except "/" and "X:\"
	 * 	detail see annotation of setBaseUploadPath() 
	 */
	private String calcSubUploadPath(String uploadPath) {
		if (uploadPath == null) {
			// setting default baseUploadPath
			// MultipartRequestCaller.baseUploadPath is not started with "/"
			return GlobalConfig.baseUploadPath;
		}
		uploadPath = uploadPath.trim().replaceAll("\\s", "_").replaceAll("\\\\", "/");
		
		String subUploadPath;
		if (PathKit.isAbsolutelyPath(uploadPath)) {
			subUploadPath = uploadPath;
		} else {
			subUploadPath = GlobalConfig.baseUploadPath + "/" + uploadPath;
		}
		
		// remove "/" postfix
		if (subUploadPath.endsWith("/") && !(subUploadPath.equals("/") || subUploadPath.endsWith(":/"))) {
			subUploadPath = subUploadPath.substring(0, subUploadPath.length() - 1);
		}
		
		return subUploadPath;
	}
	
	
	
	/**
	 * initialize MultipartRequest when it is really used
	 * one request only one MultipartRequestCaller constructed.
	 */
	private void getterInit() {

		if (uploadPath == null) uploadPath = GlobalConfig.baseUploadPath;
		if (maxPostSize <= 0) maxPostSize = GlobalConfig.maxPostSize;
		if (encoding == null) encoding = GlobalConfig.encoding;
		if (fileRenamePolicy == null) fileRenamePolicy = GlobalConfig.defFileRenamePolicy;
		
		/*
		 * initialize MultipartRequestCaller default parameters if did not run "new GlobalConfig()"
		 */
		if (caller == null) {
			caller = new MultipartRequestCaller(this, uploadPath, maxPostSize, encoding, fileRenamePolicy);
		}
	}

	private void autoGlobalConfig() {
		// if it does not initialize parameters of file upload request (with "new GlobalConfig()"), use default.
		if (GlobalConfig.baseUploadPath == null || 
				GlobalConfig.maxPostSize <= 0 || 
				GlobalConfig.encoding == null || 
				GlobalConfig.defFileRenamePolicy == null) {
			new GlobalConfig();
		}
	}

	
	/*
	 * Override method:
	 * get parameters.
	 */
	
	@Override
	public Enumeration<String> getParameterNames() {
		try {
			getterInit();
			return caller.getParameterNames();
		} catch (Exception e) {
			removeProcessSession();
			RuntimeException re = getPostedContentLengthIllegalException(e);
			if (re != null) throw re;
			throw e;
		}
	}
	
	@Override
	public String getParameter(String name) {
		try {
			getterInit();
			return caller.getParameter(name);
		} catch (Exception e) {
			removeProcessSession();
			RuntimeException re = getPostedContentLengthIllegalException(e);
			if (re != null) throw re;
			throw e;
		}
	}
	
	@Override
	public String[] getParameterValues(String name) {
		try {
			getterInit();
			return caller.getParameterValues(name);
		} catch (Exception e) {
			removeProcessSession();
			RuntimeException re = getPostedContentLengthIllegalException(e);
			if (re != null) throw re;
			throw e;
		}
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		try {
			getterInit();
			return caller.getParameterMap();
		} catch (Exception e) {
			removeProcessSession();
			RuntimeException re = getPostedContentLengthIllegalException(e);
			if (re != null) throw re;
			throw e;
		}
	}
	

	private RuntimeException getPostedContentLengthIllegalException(Exception e) {
		if (e != null && e instanceof RuntimeException && 
				e.getMessage() != null && e.getMessage().indexOf("Posted content length") > -1
			) {
			return new RuntimeException(
					"FileUploadRequest need larger maxPostSize (recommend) "
					+ "or method getParemeter should be called after getFile(s)", e);
		}
		return null;
	}
	
	private void removeProcessSession() {
		this.getSession().removeAttribute(sessionKey);
	}
	
	/*
	 * Extra method: 
	 * Get upload file.
	 */
	
	public UploadFile getFile() {
		List<UploadFile> uploadFiles = getFiles();
		return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
	}
	
	public UploadFile getFile(String parameterName) {
		List<UploadFile> uploadFiles = getFiles();
		for (UploadFile uploadFile : uploadFiles) {
			if (uploadFile.getParameterName().equals(parameterName)) {
				return uploadFile;
			}
		}
		return null;
	}

	public UploadFile getFile(String parameterName, String uploadPath) {
		return getFile(parameterName, uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}
	
	public UploadFile getFile(String parameterName, String uploadPath, int maxPostSize) {
		return getFile(parameterName, uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}

	public UploadFile getFile(String parameterName, String uploadPath, Integer maxPostSize, FileRenamePolicy fileRenamePolicy) {
		return getFile(parameterName, uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}
	
	public UploadFile getFile(String parameterName, String uploadPath, Integer maxPostSize, FileRenamePolicy fileRenamePolicy, String encoding) {
		getFiles(uploadPath, maxPostSize, fileRenamePolicy, encoding);
		return getFile(parameterName);
	}
	
	public List<UploadFile> getFiles() {
		return getFiles(uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}
	
	public List<UploadFile> getFiles(String uploadPath) {
		return getFiles(uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}

	public List<UploadFile> getFiles(String uploadPath, int maxPostSize) {
		return getFiles(uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}
	
	public List<UploadFile> getFiles(String uploadPath, Integer maxPostSize, FileRenamePolicy fileRenamePolicy) {
		return getFiles(uploadPath, maxPostSize, fileRenamePolicy, encoding);
	}
	
	/*
	 * CORE METHOD for getFile(s).
	 */
	public List<UploadFile> getFiles(String uploadPath, Integer maxPostSize, FileRenamePolicy fileRenamePolicy, String encoding) {
		try {
			setUploadPath(uploadPath);
			setMaxPostSize(maxPostSize);
			setEncoding(encoding);
			setFileRenamePolicy(fileRenamePolicy);
			getterInit();
			return caller.getFiles();
		} catch (Exception e) {
			removeProcessSession();
			throw e;
		}
	}
	
}

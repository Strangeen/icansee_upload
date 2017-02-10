package online.dinghuiye.icanseeupload.core;

import com.oreilly.servlet.multipart.FileRenamePolicy;

import online.dinghuiye.icanseeupload.core.consts.Consts;
import online.dinghuiye.icanseeupload.core.kit.PathKit;


/**
 * 
 * Initialize parameters for file upload.
 * usage:
 * 	1, set global upload parameters:
 * 		new GlobalConfig().set..(..)
 *  2, if it does not set, it will be set automatically at FileUploadRequest being constructed
 */
public class GlobalConfig {
	
	/*
	 * global default params
	 */
	protected static String baseUploadPath;
	protected static int maxPostSize;
	protected static String encoding;
	protected static FileRenamePolicy defFileRenamePolicy;
	
	public GlobalConfig() {
		// initialize default values, ran only one time
		initDefParam();
	}
	
	/**
	 * global setting
	 * 
	 * @param baseUploadPath
	 * @return
	 */
	public GlobalConfig setBaseUploadPath(String baseUploadPath) {
		GlobalConfig.baseUploadPath = calcBaseUploadPath(baseUploadPath);
		return this;
	}
	
	/**
	 * global setting
	 * 
	 * @param maxPostSize
	 * @return
	 */
	public GlobalConfig setMaxPostSize(Integer maxPostSize) {
		if (maxPostSize <= 0) {
			// setting default value
			maxPostSize = Consts.DEF_MAX_POST_SIZE;
			//throw new IllegalArgumentException("maxPostSize can not be less than or equal 0.");
		}
		GlobalConfig.maxPostSize = maxPostSize;
		return this;
	}
	
	/**
	 * global setting
	 * 
	 * @param encoding
	 * @return
	 */
	public GlobalConfig setEncoding(String encoding) {
		if (encoding == null) {
			// setting default value
			encoding = Consts.DEF_ENCODING;
			//throw new IllegalArgumentException("encoding can not be null.");
		}
		GlobalConfig.encoding = encoding;
		return this;
	}
	
	/**
	 * global setting
	 * 
	 * @param fileRenamePolicy
	 * @return
	 */
	public GlobalConfig setFileRenamePolicy(FileRenamePolicy fileRenamePolicy) {
		if (fileRenamePolicy == null) {
			// setting default value
			fileRenamePolicy = Consts.DEF_FILE_REN_POLICY;
			//throw new IllegalArgumentException("fileRenamePolicy can not be null.");
		}
		GlobalConfig.defFileRenamePolicy = fileRenamePolicy;
		return this;
	}
	

	private static boolean hasInitDef = false; 
	/**
	 * global setting
	 */
	private synchronized void initDefParam() {
		if (hasInitDef) return;
		hasInitDef = true;
		try {
			Class.forName("com.oreilly.servlet.MultipartRequest");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		setBaseUploadPath(Consts.DEF_BASE_UPLOAD_PATH);
		setMaxPostSize(Consts.DEF_MAX_POST_SIZE);
		setEncoding(Consts.DEF_ENCODING);
		setFileRenamePolicy(Consts.DEF_FILE_REN_POLICY);
	}
	
	
	/**
	 * calculate uploadPath to baseUploadPath, 
	 * set baseUploadPath and get absolute path of baseUploadPath
	 * @param uploadPath
	 * 				for global setting
	 * 				relationship of value and folder shows below  
	 * 					values	- folders
	 * 					null	- "WebRoot/upload" (default)
	 * 					"path"	- "WebRoot/path" (recommend)
	 * 					"" " "	- "WebRoot"
	 * 					"fi le"	- "WebRoot/fi_le"
	 * 					"/"	"\"	- linux: "/", windows: deploy root driver path, eg: "D:\"
	 * 					"/path"	- linux： "/path", windows: sub path of deploy root driver, eg: "D:\path"
	 * 					"X:\"	- "D:\" only for windows 
	 * @return
	 * 		if uploadPath is not a absolute path: WebRoot/uploadPath
	 * 		else uploadPath
	 * 		after base upload path no "/" or "\" except "/" and "X:\"
	 * 	detail see annotation of setBaseUploadPath() 
	 */
	private String calcBaseUploadPath(String uploadPath) {
		if (uploadPath == null) {
			// setting default value
			// Consts.DEF_BASE_UPLOAD_PATH is not started with "/"
			return PathKit.getWebRootPath() + "/" + Consts.DEF_BASE_UPLOAD_PATH;
		}
		uploadPath = uploadPath.trim().replaceAll("\\s", "_").replaceAll("\\\\", "/");
		
		String baseUploadPath;
		if (PathKit.isAbsolutelyPath(uploadPath)) {
			baseUploadPath = uploadPath;
		} else {
			baseUploadPath = PathKit.getWebRootPath() + "/" + uploadPath;
		}
		
		// remove "/" postfix
		if (baseUploadPath.endsWith("/") && !(baseUploadPath.equals("/") || baseUploadPath.endsWith(":/"))) {
			baseUploadPath = baseUploadPath.substring(0, baseUploadPath.length() - 1);
		}
		
		return baseUploadPath;
	}
	
}

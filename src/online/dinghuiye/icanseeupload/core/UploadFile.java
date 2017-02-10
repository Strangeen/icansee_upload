package online.dinghuiye.icanseeupload.core;
import java.io.File;

/**
 * UploadFile.
 */
public class UploadFile {
	
	private String parameterName;
	
	private String uploadPath;
	private String fileName;
	private String originalFileName;
	private String contentType;
	
	public UploadFile(String parameterName, String uploadPath, String filesystemName, String originalFileName, String contentType) {
		this.parameterName = parameterName;
		this.uploadPath = uploadPath;
		this.fileName = filesystemName;
		this.originalFileName = originalFileName;
		this.contentType = contentType;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getOriginalFileName() {
		return originalFileName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getUploadPath() {
		return uploadPath;
	}
	
	public File getFile() {
		if (uploadPath == null || fileName == null) {
			return null;
		} else {
			return new File(uploadPath + File.separator + fileName);
		}
	}
}

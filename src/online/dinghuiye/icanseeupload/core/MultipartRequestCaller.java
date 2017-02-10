package online.dinghuiye.icanseeupload.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FileRenamePolicy;

/**
 * MultipartRequestCaller.
 * The CORE CLASS of upload, to call com.oreilly.servlet.MultipartRequest to get files and get parameters.
 */
public class MultipartRequestCaller implements HttpServletRequestPlus {
	
	
	private List<UploadFile> uploadFiles;
	private MultipartRequest multipartRequest;

	protected MultipartRequestCaller(HttpServletRequest request, String uploadPath, int maxPostSize, String encoding, FileRenamePolicy fileRenamePolicy) {
		// one request one multipartRequest only 
		if (multipartRequest == null) {
			wrapMultipartRequest(request, uploadPath, maxPostSize, encoding, fileRenamePolicy);
		}
	}
	
	/**
	 * CORE METHOD to get files.
	 * 	and this method can be ran only once in a request!
	 * 	or it will occur exception "Corrupt form data: premature ending".
	 */
	private void wrapMultipartRequest(HttpServletRequest request, String uploadPath, int maxPostSize, String encoding, FileRenamePolicy fileRenamePolicy) {
		
		File dir = new File(uploadPath);
		if ( !dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + uploadPath + " not exists and can not create directory.");
			}
		}
		
        uploadFiles = new ArrayList<UploadFile>();
		
		try {
			multipartRequest = new MultipartRequest(request, uploadPath, maxPostSize, encoding, fileRenamePolicy);
			@SuppressWarnings("unchecked")
			Enumeration<String> files = multipartRequest.getFileNames();
			while (files.hasMoreElements()) {
				String name = (String)files.nextElement();
				String filesystemName = multipartRequest.getFilesystemName(name);
				
				// follow jfinal: file no uploaded, no generate UploadFile, it's diffrent from cos
				if (filesystemName != null) {
					String originalFileName = multipartRequest.getOriginalFileName(name);
					String contentType = multipartRequest.getContentType(name);
					UploadFile uploadFile = new UploadFile(name, uploadPath, filesystemName, originalFileName, contentType);
					if (isSafeFile(uploadFile))
						uploadFiles.add(uploadFile);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isSafeFile(UploadFile uploadFile) {
		String fileName = uploadFile.getFileName().trim().toLowerCase();
		if (fileName.endsWith(".jsp") || fileName.endsWith(".jspx")) {
			uploadFile.getFile().delete();
			return false;
		}
		return true;
	}
	
	public List<UploadFile> getFiles() {
		return uploadFiles;
	}
	
	/**
	 * CORE METHOD.
	 * Methods to replace HttpServletRequest methods
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> getParameterNames() {
		return multipartRequest.getParameterNames();
	}
	
	public String getParameter(String name) {
		return multipartRequest.getParameter(name);
	}
	
	public String[] getParameterValues(String name) {
		return multipartRequest.getParameterValues(name);
	}
	
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<>();
		Enumeration<String> enumm = getParameterNames();
		while (enumm.hasMoreElements()) {
			String name = (String) enumm.nextElement();
			map.put(name, multipartRequest.getParameterValues(name));
		}
		return map;
	}
}

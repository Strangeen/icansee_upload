package online.dinghuiye.icanseeupload.core.kit;

import java.io.File;

/**
 * PathKit.
 */
public class PathKit {
	
	public static String getWebRootPath() {
		String path = null;
		try {
			path = PathKit.class.getResource("/").toURI().getPath();
			path = new File(path).getParentFile().getParentFile().getCanonicalPath().replaceAll("\\\\", "/");
			if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
			return path;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isAbsolutelyPath(String path) {
		return path.startsWith("/") || path.indexOf(":/") == 1 || path.indexOf(":\\") == 1;
	}
	
}

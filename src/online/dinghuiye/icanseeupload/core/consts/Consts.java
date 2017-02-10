package online.dinghuiye.icanseeupload.core.consts;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;

public class Consts {

	public static String DEF_BASE_UPLOAD_PATH = "upload";
	
	public static int DEF_MAX_POST_SIZE = 10 * 1024 * 1024;
	
	public static String DEF_ENCODING = "UTF-8";
	
	public static FileRenamePolicy DEF_FILE_REN_POLICY = new DefaultFileRenamePolicy();
	
	public static final int K = 1024;
	
	public static final int M = 1048576;
	
	public static final int G = 1073741824;
}

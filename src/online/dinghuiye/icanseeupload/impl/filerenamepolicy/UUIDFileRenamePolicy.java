package online.dinghuiye.icanseeupload.impl.filerenamepolicy;

import java.io.File;
import java.util.UUID;

import com.oreilly.servlet.multipart.FileRenamePolicy;

/**
 * 
 * Rename upload file to UUID.suffix
 */
public class UUIDFileRenamePolicy implements FileRenamePolicy {

	@Override
	public File rename(File file) {
		String originalName = file.getName();
		int dotPos = originalName.lastIndexOf(".");
		String suffix = "";
		if (dotPos > -1) {
			suffix = originalName.substring(dotPos);
		}
		String newName = UUID.randomUUID() + suffix; 
		return new File(file.getParent() + "/" + newName);
	}
}

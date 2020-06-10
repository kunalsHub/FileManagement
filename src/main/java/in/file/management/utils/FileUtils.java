/**
 * 
 */
package in.file.management.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author cropdata-user
 *
 */
public class FileUtils {

	public static File multipartToFile(MultipartFile file) {
		File convFile = new File(file.getOriginalFilename());
		try {
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convFile;
	}

	/**
	 * String getExtension(String fileName)
	 * 
	 * @param fileName -
	 * @return String - extension of file
	 */
	public static String getExtension(String fileName) {
		return fileName.split("\\.(?=[^\\.]+$)")[1];
	}

	/**
	 * String getFileName(String fileName)
	 * 
	 * @param fileName
	 * @return String file name without extension
	 */
	public static String getFileName(String fileName) {
		return fileName.split("\\.(?=[^\\.]+$)")[0];
	}

	public static String getFileNameFromAbsulatePath(String absulatePath) {
		return absulatePath.split(".+?/(?=[^/]+$)")[1];
	}

	public static String getParentDirFromAbsulatePath(String absulatePath) {
		return absulatePath.split(".+?/(?=[^/]+$)")[0];
	}

	/**
	 * 
	 * @param tempDir
	 * @return boolean
	 */
	public static boolean emptyDir(String tempDir) {
		File fin = new File(tempDir);
		if (fin != null && fin.exists()) {
			for (File file : fin.listFiles()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		}

		return true;
	}// emptyDir

}

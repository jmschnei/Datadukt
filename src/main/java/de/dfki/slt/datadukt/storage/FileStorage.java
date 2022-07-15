package de.dfki.slt.datadukt.storage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import de.dfki.slt.datadukt.data.Format;
import de.dfki.slt.datadukt.data.Format.*;

/**
 * @author julianmorenoschneider
 * @project Datadukt
 * @date 17.04.2020
 * @date_modified 15.07.2022
 * @company DFKI
 * @description 
 *
 */
@SuppressWarnings("unused")
@Component
public class FileStorage {

	HashMap<String, File> temporaryFiles = new HashMap<String, File>();

	public FileStorage() {
	}

	@PostConstruct
	public void setup() {
	}

	/**
	 * Method that generates a temporary file in the dictionary storage
	 * @param is Inputstream of the content to be included in the temporary file
	 * @param iFormat format of the content, and therefore the temporary 
	 * @return a Identifier of the temporary file
	 */
	public String generateTemporaryFile(InputStream is,Format iFormat) {
		String fileId = "";
		String fileSuffix = "";
		fileId = (new Date().getTime())+"";
		switch (iFormat) {
		case WAV:
			fileSuffix = "_tmp.wav";
			break;
		case MP3:
			fileSuffix = "_tmp.mp3";
			break;
		case UNK:
			fileSuffix = "_tmp.unk";
			break;
		default:
			break;
		}
		try {
			File tempFile = File.createTempFile("qwm-", fileSuffix);
			//File tempFile = File.createTempFile("MyAppName-", ".tmp");
			System.out.println(tempFile.getAbsolutePath());
			java.nio.file.Files.copy(
					is, 
					tempFile.toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
			//IOUtils.closeQuietly(is);
			tempFile.deleteOnExit();
			temporaryFiles.put(fileId, tempFile);
			return fileId;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method that generates a temporary file in the dictionary storage
	 * @param file MultipartFile to be included in the temporary file
	 * @param iFormat format of the content, and therefore the temporary 
	 * @return a Identifier of the temporary file
	 */
	public String generateTemporaryFile(MultipartFile file,Format iFormat) {
		String fileId = "";
		String fileSuffix = "";
		fileId = (new Date().getTime())+"";
		try {
			File tempFile = File.createTempFile("qwm-", file.getName());
			//File tempFile = File.createTempFile("MyAppName-", ".tmp");
			System.out.println(tempFile.getAbsolutePath());
			java.nio.file.Files.copy(
					file.getInputStream(), 
					tempFile.toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
			//IOUtils.closeQuietly(is);
			tempFile.deleteOnExit();
			temporaryFiles.put(fileId, tempFile);
			return fileId;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method that generates a temporary file in the dictionary storage
	 * @param s String content to be included in the temporary file
	 * @param iFormat format of the content, and therefore the temporary 
	 * @return a Identifier of the temporary file
	 */
	public String generateTemporaryFile(String s,Format iFormat) {
		String fileId = "";
		String fileSuffix = "";
		fileId = (new Date().getTime())+"";
		switch (iFormat) {
		case WAV:
			fileSuffix = "_tmp.wav";
			break;
		case MP3:
			fileSuffix = "_tmp.mpg";
			break;
		case UNK:
			fileSuffix = "_tmp.unk";
			break;
		default:
			break;
		}
		try {
			File tempFile = File.createTempFile("qwm-", fileSuffix);
			//File tempFile = File.createTempFile("MyAppName-", ".tmp");
			FileUtils.writeStringToFile(tempFile, s, "UTF-8");
			//IOUtils.closeQuietly(is);
			tempFile.deleteOnExit();
			System.out.println(tempFile.getAbsolutePath());
			temporaryFiles.put(fileId, tempFile);
			return fileId;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method that returns a temporary file of the dictionary storage
	 * @param id Identification of the file to be returned
	 * @return a File object if the file exists, null otherwise.
	 */
	public File getFile(String id) {
		if(temporaryFiles.containsKey(id)) {
			return temporaryFiles.get(id);
		}
		return null;
	}

	/**
	 * Method that includes or modifies a temporary file in the dictionary storage
	 * @param id Identification of the file to be included or modified
	 * @param f The file itself
	 * @param override Boolean value that determines if the document must be overrided in case that it exists
	 * @return true if the file could be added or modified correctly, false otherwise.
	 */
	public boolean setFile(String id, File f, boolean override) {
		if(!temporaryFiles.containsKey(id)) {
			temporaryFiles.put(id, f);
		}
		else if(override) {
			temporaryFiles.put(id, f);
		}
		else {
			return false;
		}
		return true;
	}
}

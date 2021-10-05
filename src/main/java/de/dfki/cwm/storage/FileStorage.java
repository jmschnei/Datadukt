package de.dfki.cwm.storage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.Format.*;

@SuppressWarnings("unused")
@Component
public class FileStorage {

	HashMap<String, File> temporaryFiles = new HashMap<String, File>();

	public FileStorage() {
	}

	@PostConstruct
	public void setup() {
	}

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

	public File getFile(String id) {
		
		if(temporaryFiles.containsKey(id)) {
			return temporaryFiles.get(id);
		}
		return null;
	}

	public boolean setFile(String id, File f, boolean override) {
		if(!temporaryFiles.containsKey("id")) {
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

package MultyThread2.fileencrypt;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FolderWorker {
	
	private Encrypter encrypter;

	private List<File> getFilesFromFolder(String folderName){
		File file = new File(folderName);
		return Arrays.asList(file.listFiles());
	}
	
	public void encryptFolder(String folderName, String password) {
		encrypter = new Encrypter(password);
		List<File> listOfFiles = getFilesFromFolder(folderName);
		for(File file : listOfFiles) {
			encrypter.encrypt(file);
		}
	}
	
	public void decryptFolder(String folderName, String password) {
		encrypter = new Encrypter(password);
		List<File> listOfFiles = getFilesFromFolder(folderName);
		for(File file : listOfFiles) {
			encrypter.decrypt(file);
		}
	}
}

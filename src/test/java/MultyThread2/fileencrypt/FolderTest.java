package MultyThread2.fileencrypt;

import java.io.File;

import org.junit.Test;

public class FolderTest {
	private static final String FOLDER = "C:/Users/Rusla/Desktop/F1";
	private static final String PASSWORD = "password";

	@Test
	public void testEncrypt() throws InterruptedException{
		FolderWorker2 folderWorker = new FolderWorker2();
		folderWorker.encryptFolder(FOLDER, PASSWORD);
		folderWorker.decryptFolder(FOLDER, PASSWORD);
    }
}

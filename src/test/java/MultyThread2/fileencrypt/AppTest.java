package MultyThread2.fileencrypt;

import java.io.File;

import org.junit.Test;

public class AppTest {

	@Test
	public void testEncrypt(){
		Encrypter encrypter = new Encrypter("password");
		File input = new File("C:/Users/Rusla/Desktop/F1/file1.txt");
		System.out.println(encrypter.getSecretKey());
		encrypter.encrypt(input);
    }

	@Test
	public void testDecrypt(){	
		Encrypter encrypter = new Encrypter("password");
		File input = new File("C:/Users/Rusla/Desktop/F1/file1.txt");
		System.out.println(encrypter.getSecretKey());
		encrypter.decrypt(input);
    }

}

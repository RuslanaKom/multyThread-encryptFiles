package MultyThread2.fileencrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Encrypter {

	private SecretKey secretKey;
	private Cipher cipher;
	private IvParameterSpec iv;

	private static Map<String, String> hashMapOfEncryptedFiles = new HashMap<>();

	public Encrypter(String password) {
		try {
			//this.secretKey = KeyGenerator.getInstance("AES").generateKey();
			byte[] salt = {
				    (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
				    (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
				};
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		    this.secretKey=secret;
		    
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.iv = new IvParameterSpec(new byte[16]);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	public void encrypt(File inputFile) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			performOperationOnFile(inputFile);
			addToMd5Map(inputFile);
		} catch (InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	public void decrypt(File inputFile) {
			try {
				cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				performOperationOnFile(inputFile);
			} catch (InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException
					| InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
	}

	private void performOperationOnFile(File inputFile)
			throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		byte[] inputBytes = new byte[(int) inputFile.length()];
		inputStream.read(inputBytes);
		byte[] outputBytes = cipher.doFinal(inputBytes);

		FileOutputStream outputStream = new FileOutputStream(inputFile);
		outputStream.write(outputBytes);

		inputStream.close();
		outputStream.close();
		System.out.println("operation on file: " + inputFile.getName());
	}
	
	private void addToMd5Map(File file) {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			String md5 = DigestUtils.md5Hex(inputStream);
			hashMapOfEncryptedFiles.put(file.getAbsolutePath(), md5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isFileInMap(File file) {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			String md5 = DigestUtils.md5Hex(inputStream);
			String md5FromMap = hashMapOfEncryptedFiles.get(file.getAbsolutePath());
			boolean result = StringUtils.equals(md5, md5FromMap);
			if(!result) {
				System.out.printf("md5 of file %s not in map", file.getName());
			}
			return StringUtils.equals(md5, md5FromMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public static Map<String, String> getHashMapOfEncryptedFiles() {
		return hashMapOfEncryptedFiles;
	}

}

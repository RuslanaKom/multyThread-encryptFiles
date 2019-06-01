package MultyThread2.fileencrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FolderWorker2 {

	private int numberOfThreads = 4;
	private ExecutorService executor;

	private List<File> getFilesFromFolder(String folderName) {
		File file = new File(folderName);
		return Arrays.asList(file.listFiles());
	}

	public void encryptFolder(String folderName, String password) throws InterruptedException {
		executor = Executors.newFixedThreadPool(numberOfThreads);
		List<File> listOfFiles = getFilesFromFolder(folderName);
		CountDownLatch latch = new CountDownLatch(listOfFiles.size());
		for (File file : listOfFiles) {
			Runnable run = () -> {
				Encrypter encrypter = new Encrypter(password);
				encrypter.encrypt(file);
				latch.countDown();
			};
			executor.execute(run);
		}
		latch.await();
		executor.shutdown();
		writeHashToFile(folderName, password);
	}

	public void decryptFolder(String folderName, String password) throws InterruptedException {
		List<String> hashes = readHashFromFile(folderName, password);
		/*printing*/
		for (String hash : hashes) {
			System.out.println("Hash from file: " + hash);
		}
		/**/
		executor = Executors.newFixedThreadPool(numberOfThreads);
		List<File> listOfFiles = getFilesFromFolder(folderName);
		CountDownLatch latch = new CountDownLatch(listOfFiles.size());
		for (File file : listOfFiles) {
			Runnable run = () -> {
				Encrypter encrypter = new Encrypter(password);
					if(isHashInList(file,hashes )) {
						encrypter.decrypt(file);
					}
				latch.countDown();
			};
			executor.execute(run);
		}
		latch.await();
		executor.shutdown();
	}

	private boolean isHashInList(File file, List<String> hashes ) {
		String md5 = null;
			try {
				FileInputStream inputStream = new FileInputStream(file);
				md5 = DigestUtils.md5Hex(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return hashes.contains(md5);
	}

	private void writeHashToFile(String folderName, String password) {
		File file = new File(folderName + "/md5.bin");
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write((Encrypter.getHashMapOfEncryptedFiles().values().stream().collect(Collectors.joining(","))).getBytes());
			Encrypter encrypter = new Encrypter(password);
			encrypter.encrypt(file);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String hash:Encrypter.getHashMapOfEncryptedFiles().values()) {
			System.out.println("Hash from map: " +hash);
		}
	}

	private List<String> readHashFromFile(String folderName, String password) {
		File file = new File(folderName + "/md5.bin");
		Encrypter encrypter = new Encrypter(password);
		encrypter.decrypt(file);
		List<String> hashes = null;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			byte[] inputBytes = new byte[(int) file.length()];
			inputStream.read(inputBytes);
			String s = new String(inputBytes);
			hashes = Arrays.asList(s.split(","));
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		file.delete();
		return hashes;
	}
	
//	private void writeHashToFile(String folderName, String password) {
//		ObjectMapper mapper = new ObjectMapper();
//		File file = new File(folderName+"/md5.json");
//		 try {
//			mapper.writeValue(file, Encrypter.getHashMapOfEncryptedFiles().values());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		 Encrypter encrypter = new Encrypter(password);
//		 encrypter.encrypt(file);
//	}
	
//	public void encryptFolder(String folderName, String password) throws InterruptedException {
//	List<File> listOfFiles = getFilesFromFolder(folderName);
//	File lastFile = null;
//	if(listOfFiles.size()%2!=0) {
//		lastFile = listOfFiles.get(listOfFiles.size());
//		listOfFiles.remove(lastFile);
//	}
//	int partitionSize = listOfFiles.size()/numberOfThreads;
//	List<List<File>> partitions = ListUtils.partition(listOfFiles, partitionSize);
//	if(lastFile !=null) {
//		partitions.get(0).add(lastFile);
//	}
//	 CountDownLatch latch = new CountDownLatch(partitions.size());
//	List<Runnable> runnables = new ArrayList<>();
//	for(List<File> fileSubList :partitions) {
//		Runnable run = () -> {
//			System.out.println(Thread.currentThread());
//			Encrypter encrypter = new Encrypter(password);
//			for (File file : fileSubList) {	
//					encrypter.encrypt(file);
//				}
//			latch.countDown();
//		};
//		runnables.add(run);
//	}
//	for(Runnable run:runnables) {
//	executor.execute(run);
//	}
//	latch.await();
//	executor.shutdown();
// }
}

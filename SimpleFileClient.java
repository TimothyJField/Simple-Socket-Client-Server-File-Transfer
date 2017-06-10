/*
 * NOTE: Run this program on your Client Machine  
 * @author Timothy Field
 * Description: Socket Program for Simple File Transfer between client and server
 * Class/School: IT 382 , ILLINOIS STATE UNIVERSITY  
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class SimpleFileClient {

	public static String FILENAME_TO_SEND;
	public static String FILE_DIRECTORY = "C:/Users/tfiel/Desktop/382FT/";	// you may change this

	public final static int SOCKET_PORT = 7005; // you may change this
	public final static String SERVER = "walnut.ad.ilstu.edu";	// you may change this



	public final static int FILE_SIZE = 25000000; // you may change this
	public static int choice = 0;
	public static String fileToBeDownloaded;
	// should bigger than the file to be downloaded

	public static void main(String[] args) throws IOException {
		String quit = "";
		//Removed because Desktop may be changed
//		Scanner keyboard2 = new Scanner(System.in);
//		System.out.println("Please Under the Name of Your Folder Located on your Desktop");
//		keyboard2.nextLine();
		while (true) {

			Scanner keyboard = new Scanner(System.in);
			System.out.println("\t\tMENU\n--------------------------------------------------\n");
			System.out.println(
					"1. Upload a File To Server\n2. Download a File from the Server\n3. List All Files Currently in Directory");
			System.out.println("--------------------------------------------------\n");

			while (choice == 0) {
				System.out.println("What would you like to do?");
				try {
					choice = keyboard.nextInt();
					keyboard.nextLine();

					if (choice == 1 || choice == 2 || choice == 3) {
						break;
					} else {
						System.err.println("Not a valid input. Please enter a number that is listed on the menu!!");
						choice = 0;
						continue;
					}
				} catch (InputMismatchException e) {
					System.err.println("Not a valid input. Error : InputMismatchException: Please enter a number!!");
					keyboard.nextLine();
					continue;
				}

			}

			switch (choice) {
			case 1:
				fileUpload();
				break;
			case 2:
				fileDownload();
				break;
			case 3:
				listfiles();
				break;
			default:
				System.err.println("Something Went Wrong!");
				System.exit(-1);
				break;
			}
			System.out.println("Would you like to do anything else? [Press Q to Quit] [Enter to Continue]");

			quit = keyboard.nextLine();
			if (quit.equals("Q") || quit.equals("q")) {
				System.out.println("GoodBye!");
				System.exit(-1);

			} else {
				System.out.println("Back to the menu!");
				choice = 0;
			}

		}
	}

	private static void listfiles() throws IOException {

		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket fileSocket = null;
		int command = choice;

		try {
			fileSocket = new Socket(SERVER, SOCKET_PORT);
			String modifiedSentence;
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(fileSocket.getInputStream()));
			// BufferedReader inFromUser = new BufferedReader( new
			// InputStreamReader(System.in));
			DataOutputStream outToServer = new DataOutputStream(fileSocket.getOutputStream());
			outToServer.writeBytes(command + "" + '\n');
			System.out.println("Connecting to Server  Name:" + SERVER);
			System.out.println("");

			modifiedSentence = inFromServer.readLine();
			if (modifiedSentence == null) {
				System.out.println("Empty Directory!\n");

			} else {
				String[] FileDirect = modifiedSentence.split("_");
				Arrays.sort(FileDirect);
				System.out.println("FileDirect.length: " + FileDirect.length);
				System.out.println("\tFROM SERVER\n-------------------------------- ");

				if (FileDirect.length < 1) {
					System.out.println("Empty Directory!\n");
				} else {
					for (int k = 0; k < FileDirect.length; k++) {
						System.out.println(FileDirect[k]);
					}
				}
				fileSocket.close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Close Everything
		finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
			if (fileSocket != null)
				fileSocket.close();
		}
	}

	private static void fileDownload() throws IOException {
		Scanner keyboard2 = new Scanner(System.in);
		System.out.println("What is the name of file you want to be downloaded");
		fileToBeDownloaded = keyboard2.nextLine();
		byte[] fNameArray = fileToBeDownloaded.getBytes();
		System.out.println("What would you like the name of file to be when it's downloaded");
		Object in_fileName = keyboard2.nextLine();
		final String FILENAME_WHEN_RECEIVED = "C:/Users/tfiel/Desktop/382FT/" + in_fileName;
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket fileSocket = null;
		InputStream is = null;
		int command = choice;
		try {
			fileSocket = new Socket(SERVER, SOCKET_PORT);
			DataOutputStream outToServer = new DataOutputStream(fileSocket.getOutputStream());
			outToServer.writeBytes(command + "" + '\n');
			outToServer.write(fNameArray);
			System.out.println("Connecting to Server  Name:" + SERVER);

			// Receive file
			byte[] mybytearray = new byte[FILE_SIZE];

			// FileOutputStream
			File myFile = new File(FILENAME_WHEN_RECEIVED);
			fos = new FileOutputStream(myFile);
			bos = new BufferedOutputStream(fos);

			try {
				is = fileSocket.getInputStream();
			} catch (IOException e) {
				System.out.println("IO Exception!" + e.getMessage());

			}  
			bytesRead = is.read(mybytearray, 0, mybytearray.length);
			current = bytesRead;
			System.out.println("Downloading Begun");
			
			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
				System.out.print("-");
				
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead > -1);

			bos.write(mybytearray, 0, current);
			bos.flush();
			System.out.println("");
			System.out.println("File " + FILENAME_WHEN_RECEIVED + " downloaded (" + current + " bytes read)");
			fileToBeDownloaded = "";
			
			//Exception Handling
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("That file does not exist in your directory!");
			System.out.println("Return to the menu, list your files and select a valid file");

		}

		// Close Everything
		finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
			if (fileSocket != null)
				fileSocket.close();
		}
	}

	private static void fileUpload() throws IOException {
		File folder = new File(FILE_DIRECTORY);
		File[] listOfFiles = folder.listFiles();
		System.out.println("\tCURRENT FILES IN DIRECTORY\n--------------------------------------------------\n");
		System.out.println("");
		Arrays.sort(listOfFiles);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File: " + listOfFiles[i].getName() + "");
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Fold: " + listOfFiles[i].getName() + "");
			}
		}
		System.out.println("--------------------------------------------------\n");
		Scanner keyboard2 = new Scanner(System.in);
		System.out.println("What is the name of file you want to be uploaded");
		fileToBeDownloaded = keyboard2.nextLine();
		byte[] fNameArray2 = fileToBeDownloaded.getBytes();
		Socket fileSocket = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream file_os = null;
		OutputStream os = null;
		int command = choice;

		FileInputStream fileIn = null;
		try {
			fileSocket = new Socket(SERVER, SOCKET_PORT);
			DataOutputStream outToServer = new DataOutputStream(fileSocket.getOutputStream());
			outToServer.write(command);
			outToServer.write(fNameArray2.length);
			outToServer.write(fNameArray2);

			File file = new File("C:/Users/tfiel/Desktop/382FT/" + fileToBeDownloaded);

			// Get the size of the file
			long length = file.length();

			// System.out.println("length" + " (" + length + " bytes) ");

			byte[] bytes = new byte[(int) length];

			InputStream in = new FileInputStream(file);
			OutputStream out = fileSocket.getOutputStream();
			// FILE_TO_SEND = FILE_DIRECTORY;
			int count;

			bis = new BufferedInputStream(in);

			System.out.println("Uploading File..");
			bis.read(bytes, 0, bytes.length);
			out.write(bytes, 0, bytes.length);
			out.flush();
			out.close();
			in.close();
			fileSocket.close();
		}

		catch (FileNotFoundException e) {
			System.out.println("That file doesn't exist, was not sent to the Server!  ");

		} finally {
			if (fis != null)
				fis.close();
			if (bis != null)
				bis.close();
			if (fileIn != null)
				fileIn.close();
			if (fileSocket != null)
				fileSocket.close();
		}
	}

}

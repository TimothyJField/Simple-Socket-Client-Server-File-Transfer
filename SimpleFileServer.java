
/*
 * NOTE: Run this program on your Server Machine
 * @author Timothy Field
 * Description: Socket Program for Simple File Transfer between client and server   
 * Class/School: IT 382 , ILLINOIS STATE UNIVERSITY 
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class SimpleFileServer {

	public final static int SOCKET_PORT = 7005; // you may change this
	public static String FILENAME_TO_SEND;
	public static String FILE_DIRECTORY = "/home/ADILSTU/tfield/IT382/FTS/"; /// you may change this
																			 
	public final static String FILE_TO_SEND_DIRECT = "/home/ADILSTU/tfield/IT382/FTS/"; // you may change this
																						 
	public final static int FILE_SIZE = 4000000; // you may change this

	public static void main(String[] args) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		ServerSocket servsock = null;
		Socket sock = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			while (true) {
				System.out.println("Waiting For Next Client...");
				try {
					sock = servsock.accept();
					System.out.println("Accepted connection : " + sock);

					// Listen to Client Command
					in = sock.getInputStream();
					os = sock.getOutputStream();
					String clientCommand = in.read() + "";
					System.out.println("Received: " + clientCommand);
					String file_name = "";
					String cli_str = "";
					if (clientCommand.equals("1")) {
						int fileName_size = in.read();

						System.out.println(fileName_size);
						byte[] fileBArray = new byte[fileName_size];
						in.read(fileBArray, 0, fileName_size);

						file_name = new String(fileBArray);
						System.out.println("'" + file_name + "'");

					}
					// Other String Client Sent
					else {
						byte[] strByte2 = new byte[16 * 1024];
						byte[] b = new byte[16 * 1024];
						int count5;

						while ((count5 = in.read(strByte2)) > 0) {
							// out.write(strByte2, 0, count5);
							// cli_str = new String(strByte2, "UTF-8");
							cli_str = new String(strByte2);
							System.out.println("count5: " + count5);
							break;
						}
					}
					// Client Uploads a File to Directory
					// ====================================================================================================================================
					// Recognize Command From Client
					if (clientCommand.equals("1")) {
						System.out.println("Command 1! Client Wants to Upload a File to Directory!");
						System.out.println("Chop chop!");
						byte[] strByte = new byte[16 * 1024];
						int count;
						// String str = "";

						// String FileName =
						// cli_str.replaceAll("[^A-Za-z/.123456789]+", "");
						System.out.println("FileName from array: " + file_name);

						File folder = new File(FILE_DIRECTORY);
						File[] listOfFiles = folder.listFiles();
						Arrays.sort(listOfFiles);
						for (int i = 0; i < listOfFiles.length; i++) {
							while (listOfFiles[i].getName().equals(file_name.toString())) {
								file_name = file_name + (i);

							}
						}

						// FILENAME_WHEN_RECEIVED);
						String FILENAME_WHEN_RECEIVED = FILE_DIRECTORY + "test";
						try {
							out = new FileOutputStream("/home/ADILSTU/tfield/IT382/FTS/" + file_name);
						} catch (FileNotFoundException ex) {
							System.out.println("File not found. ");
						}

						byte[] bytes = new byte[16 * 1024];

						int count2;

						while ((count2 = in.read(bytes)) > 0) {
							//
							out.write(bytes, 0, count2);
						}

						out.close();
						in.close();
						sock.close();

					}

					// Send File To Client DOWNLOAD STUFF
					// ====================================================================================================================================
					else if (clientCommand.equals("50")) {

						try {

							// Recognize Command From Client
							System.out.println("Command 2! Client Wants to Download A File!");
							System.out.println("Chop chop!");

							String clientSentence2 = cli_str;
							String FileNameFromBytes = "";
							System.out.println("Received: " + clientSentence2);
							String[] filenames = clientSentence2.split("");

							for (int i = 1; i < filenames.length; i++) {
								if (!filenames[i].equals("") && !filenames[i].equals(null) && !filenames[i].isEmpty()) {
									// System.out.println("Add: '" +
									// filenames[i] + "'");
									FileNameFromBytes = FileNameFromBytes + filenames[i];
								}

							}
							FileNameFromBytes = FileNameFromBytes.replaceAll("[^A-Za-z/.123456789]+", "");
							System.out.println("FileNameFromBytes: '" + FileNameFromBytes + "'");
							System.out.println("FileNameFromBytes.length(): '" + FileNameFromBytes.length() + "'");
							String FILE_TO_SEND = FILE_DIRECTORY + FileNameFromBytes;
							// FILE_TO_SEND =
							// FILE_TO_SEND.replaceAll("[^A-Za-z/.]+", "");
							System.out.println("FILE_TO_SEND: '" + FILE_TO_SEND + "'");
							String FileNAMEHARDCODE = "/home/ADILSTU/tfield/IT382/FTS/5Obhonwa.jpg";
							System.out.println("FileNAMEHARDCODE.length: '" + FileNAMEHARDCODE.length() + "'");

							System.out.println("FILE_TO_SEND.length: '" + FILE_TO_SEND.length() + "'");

							File myFile = new File(FILE_TO_SEND);

							byte[] mybytearray = new byte[(int) myFile.length()];
							System.out.println("(int)myFile.length(): '" + (int) myFile.length() + "'");
							fis = new FileInputStream(myFile);
							bis = new BufferedInputStream(fis);
							bis.read(mybytearray, 0, mybytearray.length);

							System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
							os.write(mybytearray, 0, mybytearray.length);
							os.flush();

							// FILE_TO_SEND = FILE_DIRECTORY;
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("Client requested invalid file");
							System.out.println("Ignoring request");
						}

					}
					// Send list of files to Client
					else if (clientCommand.equals("51")) {
						System.out.println("Command 3! Client Wants a List of Files!");
						System.out.println("Chop chop!");

						// String capitalizedSentence;
						DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());

						os = sock.getOutputStream();
						File myFile2 = new File(FILE_TO_SEND_DIRECT);
						File[] listOfFiles = myFile2.listFiles();

						for (int i = 0; i < listOfFiles.length; i++) {
							if (listOfFiles[i].isFile()) {
								System.out.println("File: " + listOfFiles[i].getName());

								outToClient.writeBytes("File: " + listOfFiles[i].getName() + "_");

							} else if (listOfFiles[i].isDirectory()) {
								System.out.println("Directory: " + listOfFiles[i].getName());
								outToClient.writeBytes("Directory: " + listOfFiles[i].getName() + "_");
							}

						};
					}
					System.out.println("Done.");
				} finally {
					if (bis != null)
						bis.close();
					if (fis != null)
						fis.close();
					if (os != null)
						os.close();
					if (sock != null)
						sock.close();
				}
			}
		} finally {
			if (servsock != null)
				servsock.close();
		}
	}
}
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientHandler extends Thread {
    private final int FILE_SIZE = 50000; //Max file size for file transfer
    private Socket socket = null;


    public ClientHandler(Socket socket) {
		super("ClientHandler");
		this.socket = socket;
    }

    public void run() {

	try {
	    PrintWriter textOut = new PrintWriter(socket.getOutputStream(), true); //text output to client
	    BufferedReader textIn = new BufferedReader(new InputStreamReader(socket.getInputStream())); //text input from  client

        InetAddress inet = socket.getInetAddress();
        Date date = new Date();

        System.out.println("\nDate " + date.toString() );
        System.out.println("Connection made from " + inet.getHostName() );

	    textOut.println("Connected"); //Print to client when connected

		String input;
	    while ((input = textIn.readLine()) != null) {

	    	String [] splitInput = input.split(" "); //Split client input into command + arg

			if (splitInput[0].equals("bye")) { //Exit client
                log(inet, "BYE");
	    		break; //Stop client
			}

			else if (splitInput[0].equals("list")) { //Generate a list of files in serverFiles
                log(inet, "LIST"); //Log request
				File dir = new File("server/serverFiles");
				String[] files = dir.list();
				String fileList = "";
                System.out.println(files.length);
                for (int i = 0; i < files.length; i++) { //Append each file to list
						fileList += files[i] + " ";
				}
                if (files.length == 0) {
                    textOut.println("The directory is empty");
                }
                else{
                    textOut.println(fileList);
                }

			}

			else if (splitInput[0].equals("put")) { //Transfer file from  client to server
                log(inet, "PUT"); //Log request
				textOut.println(splitInput[0] + " " + splitInput[1]); //Send user input back to user

				InputStream dataIn = socket.getInputStream(); //Get data from client for server
				OutputStream dataOut = new FileOutputStream("server/serverFiles/" + splitInput[1]); //Create empty file

				byte [] fileByte = new byte[FILE_SIZE]; //Generate byte array
				int count;
				count = dataIn.read(fileByte, 0, fileByte.length);
				dataOut.write(fileByte, 0, count); //Write client file data to empty server file

				dataOut.flush();

            }
            else if  (splitInput[0].equals("get")) { //Transfer file from server to client
                log(inet, "GET"); //Log request
                File file = new File("server/serverFiles/" + splitInput[1]); //Location of server file

				if (file.exists() && !file.isDirectory()) { //Check server file exists
                    textOut.println(splitInput[0] + " " + splitInput[1]); //Send user input back to user

                    int fileSize = (int)file.length(); //Generate dynamic file size

				    byte [] fileByte = new byte[fileSize]; //Create byte array for data
				    InputStream dataIn = new FileInputStream(file); //Input server file data
				    OutputStream dataOut = socket.getOutputStream(); //Output server file data over stream

				    int count;
				    count = dataIn.read(fileByte, 0, fileByte.length);
				    dataOut.write(fileByte, 0, count); //Write data over stream

				    dataOut.flush();

                }
                else {
				    textOut.println("Invalid filename");
                }
			}
			else {
			    textOut.println("Invalid input");
            }
		}


	    textOut.close();
	    textIn.close();
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void log (InetAddress inet, String request) throws IOException { //Log requests
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy ':' HH:mm:ss");
        File file = new File("server/log.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
        out.write(ft.format(date) + " : " + inet.getHostAddress() + " : " + request);
        out.newLine();
        out.close();
    }

}

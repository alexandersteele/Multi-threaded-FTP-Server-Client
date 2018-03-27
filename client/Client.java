/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.*;
import java.net.*;

public class Client {

    private Socket clSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;
    private final int FILE_SIZE = 50000;

    public void startClient() {

        try {
            clSocket = new Socket( "localhost", 8888 ); // try and create the socket

            socketOutput = new PrintWriter(clSocket.getOutputStream(), true); // Output writing stream
            socketInput = new BufferedReader(new InputStreamReader(clSocket.getInputStream())); // Input reading stream

        } 
        catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n");
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host.\n");
            System.exit(1);
        }

        // chain a reader from the keyboard
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        try {
          while ((fromServer = socketInput.readLine()) != null) { // read from server

              String [] splitInput = fromServer.split(" "); //Get user input without interruption

              System.out.println("Server: " + fromServer);

              if (fromServer.equals("bye")) { //Exit client
                  break;
              }
              else if (splitInput[0].equals("put")) { //Transfer file from  client to server
                  File file = new File("client/clientFiles/" + splitInput[1]);

                  if (file.exists() && !file.isDirectory()) { //Check client file exists
                      int fileSize = (int)file.length();
                      byte [] fileByte = new byte[fileSize]; //Create byte array for data stream
                      InputStream dataIn = new FileInputStream(file); //data input stream from client file
                      OutputStream dataOut = clSocket.getOutputStream(); //data output stream to server

                      int count;
                      count = dataIn.read(fileByte, 0, fileByte.length);
                      dataOut.write(fileByte, 0, count); //Write to data output stream

                      dataOut.flush();
                  }
                  else {
                      System.out.println("Client: Invalid filename");
                  }


              }
              else if (splitInput[0].equals("get")) { //Transfer file from server to client
                  InputStream dataIn = clSocket.getInputStream(); //Data input from stream from server
                  OutputStream dataOut = new FileOutputStream("client/clientFiles/" + splitInput[1]); //Data output stream to client file

                  byte [] fileByte = new byte[FILE_SIZE]; //Create byte array for data stream
                  int count;
                  count = dataIn.read(fileByte, 0, fileByte.length);
                  dataOut.write(fileByte, 0, count); //write data to client from server

                  dataOut.flush();
              }
              // client types in response 
              fromUser = stdIn.readLine();

    	      if (fromUser != null) {
                  // echo client string
                  System.out.println("Client: " + fromUser);
                  // write to server
                  socketOutput.println(fromUser);
              }
          }
          socketOutput.close();
          socketInput.close();
          stdIn.close();
          clSocket.close();
        }
        catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
      Client cl = new Client();
      cl.startClient();
    }

}


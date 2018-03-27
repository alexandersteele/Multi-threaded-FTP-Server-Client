import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket server = null;
        ExecutorService service = null;
        
		// Try to open up the listening port
        try {
            server = new ServerSocket(8888); //Open server on socket specified
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8888.");
            System.exit(-1);
        }

		service = Executors.newFixedThreadPool(10); // Initialise the executor to 10 fixed threads

		while( true ) // For each new client, submit a new handler to the thread pool.
		{
			Socket client = server.accept();
			service.submit( new ClientHandler(client) );
		}
    }
}

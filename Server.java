//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

//Tayfun Turanligil


/*
 * A server that delivers status messages to other users.
 */
public class Server {

	// Create a socket for the server 
	private static ServerSocket serverSocket = null;
	// Create a socket for the server 
	private static Socket userSocket = null;
	// Maximum number of users 
	private static int maxUsersCount = 5;
	// An array of threads for users
	private static userThread[] threads = null;


	public static void main(String args[]) {

		// The default port number.
		int portNumber = 8000;
		if (args.length < 2) {
			System.out.println("Usage: java Server <portNumber>\n"
					+ "Now using port number=" + portNumber + "\n" +
					"Maximum user count=" + maxUsersCount);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
			maxUsersCount = Integer.valueOf(args[1]).intValue();
		}

		System.out.println("Server now using port number=" + portNumber + "\n" + "Maximum user count=" + maxUsersCount);
		
		
		userThread[] threads = new userThread[maxUsersCount];


		/*
		 * Open a server socket on the portNumber (default 8000). 
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a user socket for each connection and pass it to a new user
		 * thread.
		 */
		while (true) {
			try {
				userSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxUsersCount; i++) {
					if (threads[i] == null) {
						threads[i] = new userThread(userSocket, threads);
						threads[i].start();
						break;
					}
				}
				if (i == maxUsersCount) {
					PrintStream output_stream = new PrintStream(userSocket.getOutputStream());
					output_stream.println("#busy");
					output_stream.close();
					userSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/*
 * Threads
 */
class userThread extends Thread {

	private String userName = null;
	private BufferedReader input_stream = null;
	private PrintStream output_stream = null;
	private Socket userSocket = null;
	private final userThread[] threads;
	private int maxUsersCount;

	public userThread(Socket userSocket, userThread[] threads) {
		this.userSocket = userSocket;
		this.threads = threads;
		maxUsersCount = threads.length;
	}


	//This function sends 'message' to all online users
	public void broadcastMessage(String message){

			synchronized (userThread.class) {
				for (int i = 0; i < maxUsersCount; i++) {
					if (threads[i] != null && threads[i] != this) 
						threads[i].output_stream.println(message);
				}
			}


	}

	public void run() {
		int maxUsersCount = this.maxUsersCount;
		userThread[] threads = this.threads;


		try {
			/*
			 * Create input and output streams for this client.
			 * Read user name.
			 */

			/* Welcome the new user. */
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
			output_stream = new PrintStream(userSocket.getOutputStream());

			/* Start the conversation. */
			while (true) {
				String inMessage = input_stream.readLine();




				//this parts seperated the incoming message from protocol prefix
				String arr[] = inMessage.split(" ", 2);
				String command; //prefix
				String text; //messsage
				
				if (arr.length > 1){
					 command = arr[0]; 
					 text = arr[1];
				} else {
					 command = inMessage;
					 text = "";
				}




				// these are all the legit prefixes and their resresentative actions
				if (command.equals("#status")){
					broadcastMessage("#newStatus " + userName + " " + text);
					output_stream.println("#statusPosted");

				}else if (command.equals("#join")){
					userName = text;
					broadcastMessage("#newuser " + userName);
					output_stream.println("#welcome");


				}else if (command.equals("#newStatus")){
					broadcastMessage("#newStatus " + text);

				}else if (command.equals("#Bye")){
					output_stream.println("#Bye");
					broadcastMessage("#Leave " + userName);
					break;

				//if unknown protocol tag receved, terminate connection
				} else {
					System.out.println("User " + userName + "sent an unknown command: " + command);
					System.out.println("Terminating connection !!");
					output_stream.println("#Bye");
					break;
				}





			}

			// conversation ended.

			/*
			 * Clean up. Set the current thread variable to null so that a new user
			 * could be accepted by the server.
			 */
			synchronized (userThread.class) {
				for (int i = 0; i < maxUsersCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			input_stream.close();
			output_stream.close();
			userSocket.close();
		} catch (IOException e) {
		}
	}
}
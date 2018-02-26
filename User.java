//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;

//Tayfun Turanligil


public class User extends Thread {

	// The user socket
	private static Socket userSocket = null;
	// The output stream
	private static PrintStream output_stream = null;
	// The input stream
	private static BufferedReader input_stream = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;
	private static String name;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 8000;
		// The default host.
		String host = "csa2.bu.edu";

		if (args.length < 2) {
			System.out
			.println("Usage: java User <host> <portNumber>\n"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			userSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			output_stream = new PrintStream(userSocket.getOutputStream());
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host "
					+ host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on port portNumber.
		 */
		if (userSocket != null && output_stream != null && input_stream != null) {
			try {                
				/* Create a thread to read from the server. */
				new Thread(new User()).start(); // Get user name and join the social net
				System.out.print("Enter your name:");
				name = inputLine.readLine();
				output_stream.println("#join " + name);

				

				while (!closed) { 
					String userMessage = new String();
					String userInput = inputLine.readLine().trim();
					if (userInput.equals("Exit")){ //Terminate connection if input is 'Exit'
						output_stream.println("#Bye");
						try {
    					Thread.sleep(500);                 
						} catch(InterruptedException ex) {
 					     Thread.currentThread().interrupt();
 					 	}

						break;
					}
					else
						output_stream.println("#status " + userInput);

					// Read user input and send protocol message to server

				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */

			closed = true;
			output_stream.close();
			input_stream.close();
			userSocket.close();

			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	 * Create a thread to read from the server.
	 */
	public void run() {
		/*
		 * Keep on reading from the socket till we receive a Bye from the
		 * server. Once we received that then we want to break.
		 */
		String responseLine;
		
		try {
			while ((responseLine = input_stream.readLine()) != null) {

				//this parts seperated the incoming message from protocol prefix
				String arr[] = responseLine.split(" ", 2);
				String command; //prefix
				String text; //message


				if (arr.length > 1){
					 command = arr[0]; 
					 text = arr[1];
				} else {
					 command = responseLine;
					 text = "";
				}


				//all the legit messages from server, and thir respective outputs handled here
				if (command.equals("#newuser")){
					System.out.println("New user " + text + " has joined !!!");

				}else if (command.equals("#welcome")){
					System.out.println("Welcome, " + name + " to our soucial media app!");

				}else if (command.equals("#newStatus")){
					String contend[] = text.split(" ", 2);
					System.out.println("< " + contend[0] + " >: " + contend[1]);

				}else if (command.equals("#Leave")){
					System.out.println("The user " + text + " is leaving !!!");

				} else if (command.equals("#Bye")){
					break;

				}else if (command.equals("#statusPosted")){
					System.out.println("Your status was posted successfuly.");
				
				}else if (command.equals("#busy")){
					System.out.println("Server is busy, please try again later.");
					break;

				}else {//if unknown protocol tag receved, terminate connection
					System.out.println("Server sent an unknown command: " + command);
					System.out.println("Terminating connection !!");
					output_stream.println("#Bye");
					break;
				}


			}
			closed = true;
			output_stream.close();
			input_stream.close();
			userSocket.close();
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}



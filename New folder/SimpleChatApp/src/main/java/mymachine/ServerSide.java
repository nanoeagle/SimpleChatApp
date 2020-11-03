package mymachine;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Simple Chat Application.
 * Use it to impress your crush. ^^!
 * 
 * (Java 8 or higher)
 * Server side app must be running before running any client. 
 * To stop running the server, press Ctr + C in terminal.
 * Run each client in a new terminal by using LaunchHelper.
 * 
 * Have a nice moment, good luck !^^.
 * 
 * @author Nanoeagle
 * @version 1
 */

class ServerSide {
    public static void main(String[] args) {
        new ServerSide().go();
    }

    private void go() {
        // establish the server socket for connection from client.
        try (ServerSocket serverSocket = new ServerSocket(4242)) {
            // establish writers for sending messages to all clients.
            ArrayList<PrintWriter> clientWriters = new ArrayList<>();

            // the server always run.
            while (true) {
                // establish a socket for connection to new client.
                Socket newClientSocket = serverSocket.accept();

                // establish a thread for listening messages from clients.
                Thread readerThread = new Thread(new Runnable() {
                    @Override   
                    public void run() {
                        try (
                            // to save current client socket.
                            Socket clientSocket = newClientSocket;
                            // establish a writer to send messages to the client.
                            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                            // establish a character buffer stream 
                            // to receive messages from clients.
                            BufferedReader reader = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()))
                        ) {
                            // add the writer to the writer list. 
                            clientWriters.add(writer);
            
                            // message from the client.  
                            String message;
                            while ((message = reader.readLine()) != null ) {
                                // send the received message from the client to all clients. 
                                for (PrintWriter clientWriter : clientWriters)
                                    clientWriter.println("[" + clientSocket.getRemoteSocketAddress() 
                                        + "]: " + message); 
                                
                                    // check.
                                System.out.println("Server received: " + message);
                            }
                        } catch (Exception e) { e.printStackTrace(); } 
                        
                        // check.
                        System.out.println("Disconnected a connection, ended thread.");
                    }
                });

                readerThread.start();
                // check.
                System.out.println("Got a connection, created a new thread.");
            }
        } catch (Exception e) { e.printStackTrace(); }

        // check.
        System.out.println("Server is down.");
    } // end go().
}
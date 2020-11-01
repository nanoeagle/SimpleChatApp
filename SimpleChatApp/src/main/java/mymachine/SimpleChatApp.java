package mymachine;

import java.io.*;
import java.net.*;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
 */

public class SimpleChatApp extends Application {
    private Socket socketToServer;

    private TextArea incoming;
    private BufferedReader reader;
    private TextField outgoing;
    private PrintWriter writer;

    static void launchApp(String[] args) {
        launch(args);
    }
    
    @Override
    public void init() throws Exception {
        setUpNetwork();
    }

    private void setUpNetwork() {
        try {
            // establish a connection to the chat server.
            socketToServer = new Socket("localhost", 4242);
            // establish a character stream to write messages to server.
            writer = new PrintWriter(socketToServer.getOutputStream(), true);
            // establish a character buffer stream to receive messages from the server.
            reader = new BufferedReader(
                new InputStreamReader(socketToServer.getInputStream()));
            
            // check.
            System.out.println("Network established.");

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create JavaFX GUI.
        primaryStage.setTitle("Simple Chat App - Client[" 
            + socketToServer.getLocalSocketAddress() + "]");

        // set primary scene
        FlowPane rootNodeFwPn = new FlowPane(10, 10);
        rootNodeFwPn.setAlignment(Pos.CENTER);
        Scene scene = new Scene(rootNodeFwPn, 400, 250);

        // establish incoming TextArea for receiving messages from the server.
        incoming = new TextArea();
        incoming.setPrefColumnCount(22);
        incoming.setPrefRowCount(8);
        incoming.setWrapText(true);
        incoming.setEditable(false);
        incoming.setFont(new Font(15));

        // establish outgoing TextField to send messages to the server.
        outgoing = new TextField();
        outgoing.setPromptText("enter messages...");
        outgoing.setPrefColumnCount(25);

        // this button has the same function as pressing enter in the outgoing.
        Button sendBtn = new Button("Send");

        // establish an EventHandler for sending messages.
        EventHandler<ActionEvent> sendingHandler = event -> {
            String outgoingMsg = outgoing.getText();

            if ( !outgoingMsg.equals("") ) {
                writer.println(outgoingMsg);
                outgoing.setText("");
                outgoing.requestFocus();
            }
        };

        // set sendingHandler on actions.
        outgoing.setOnAction(sendingHandler);
        sendBtn.setOnAction(sendingHandler);

        // establish a thread for listening messages from the server.
        Thread readerThread = new Thread(() -> {
            // check.
            System.out.println("Reader thread was created.");
            try {
                String incomingMsg;
                
                while ((incomingMsg = reader.readLine()) != null) {
                    // dummy string for use in a lambda or an anonymous class. 
                    String dummyInMsg = incomingMsg;
                    // for accessing JFX App Threzad GUI node from another thread.  
                    Platform.runLater(() -> {
                        // seperate the received message into sender and content.
                        String[] msgParts = dummyInMsg.split("]: ", 2);

                        incoming.appendText(
                            // if the received message was from you, 
                            // it would not need to show who you are. 
                            msgParts[0].equals("[" + socketToServer.getLocalSocketAddress()) 
                                ? msgParts[1] + "\n" : dummyInMsg + "\n");
                    });

                    // check.
                    System.out.println("Client received: " + dummyInMsg);
                }
            } catch (Exception e) { e.printStackTrace(); }

            // check.
            System.out.println("Reader thread was ended.");
        });
        // run the thread.
        readerThread.start();
        
        // set items positions.
        rootNodeFwPn.getChildren().addAll(incoming, outgoing, sendBtn);
        outgoing.requestFocus();

        // do remaining things.
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        // when exit app, stop() will be called.
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        // show up the client application.
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        try { if (socketToServer != null) socketToServer.close(); }
        catch (Exception e) { e.printStackTrace(); }

        try { if (reader != null) reader.close(); } 
        catch (Exception e) { e.printStackTrace(); }

        try { if (writer != null) writer.close(); } 
        catch (Exception e) { e.printStackTrace(); }
       
        // check.
        System.out.println("All resources were closed.");
    }
}

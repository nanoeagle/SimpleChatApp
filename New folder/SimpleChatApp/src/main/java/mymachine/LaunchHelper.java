package mymachine;

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

class LaunchHelper {
    public static void main(String[] args) {
        SimpleChatApp.launchApp(args);
        // check.
        System.out.println("Main thread end.");
    }
}

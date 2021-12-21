import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Vector;

public class ServerThread extends Thread {

    /* ~~~~~~~~~~~~~~~~~~~ Fields ~~~~~~~~~~~~~~~~~~~~~~*/

    // Declaring client Socket
    private final Socket client;

    // Declaring clientList ArrayList
    private Vector<Socket> clientList;

    // Declaring clientIn BufferedReader
    BufferedReader clientIn;

    /* ~~~~~~~~~~~~~~~~~~~ Constructor ~~~~~~~~~~~~~~~~~~~~~~*/

    // Parameterised Constructor
    public ServerThread(Socket client, Vector<Socket> clientList) {

        // Instantiating client
        this.client = client;

        // Instantiating clientList
        this.clientList = clientList;

        try {

            // Instantiating clientIn BufferedReader
            this.clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Primary Method - Run ~~~~~~~~~~~~~~~~~~~~~~*/

    public void run() {

        try {

            // Initialising userInput variable
            String userInput = "";

            // This loop runs until the user wants to leave or the server has issued an "exit" (i.e. shutdown) command
            while (!userInput.equalsIgnoreCase(".leave") && !Thread.currentThread().isInterrupted()) {

                // Checking if there is data in the BufferedReader that is ready to be read
                if (clientIn.ready()) {

                    // Reading data from the BufferedReader
                    userInput = clientIn.readLine();

                    // Getting the current time
                    LocalTime currTime = LocalTime.now();

                    // Sending data received (the message) to each client connected to the server
                    updateClients(userInput, currTime);

                }

            }

            // Shutdown the thread and close dependencies
            shutdownThread();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Method to send the received message to all the other clients connected to the server
    private void updateClients(String userInput, LocalTime currTime) {

        for (Socket user : clientList) {

            try {

                // Sending the data to each client in clientList, including the time it was received by the server thread
                new PrintWriter(user.getOutputStream(), true).println("[" + currTime + "] " + userInput);

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

    // Method to shutdown the thread including closing dependencies and removing the client from the client list
    private void shutdownThread() {

        try {

            // Removing the current client's socket from the ArrayList of client connections
            this.clientList.remove(this.client);

            // Closing the BufferedReader
            clientIn.close();

            // Closing the socket
            this.client.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}

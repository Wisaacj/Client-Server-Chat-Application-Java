import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Vector;

public class ChatServer {

    /* ~~~~~~~~~~~~~~~~~~~ Fields ~~~~~~~~~~~~~~~~~~~~~~*/

    // Declaring instance of mySocket ServerSocket
    private ServerSocket mySocket;

    // Declaring instance of connections Vector
    private Vector<Socket> connections;

    // Declaring instance of serverThreads Vector
    private Vector<Thread> serverThreads;

    // Declaring instance of adminInput BufferedReader
    private BufferedReader adminInput;

    /* ~~~~~~~~~~~~~~~~~~~ Constructors ~~~~~~~~~~~~~~~~~~~~~~*/

    // Non-parameterised constructor
    public ChatServer() {

        try {

            // Printing message to the admin user
            System.out.println("Starting server with default parameters");

            // Instantiating mySocket on port 14001
            mySocket = new ServerSocket(14001);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Parameterised constructor
    public ChatServer(int port) {

        try {

            // Printing message to the admin user
            System.out.println("Starting server on port: " + port);

            // Instantiating ServerSocket with given port
            mySocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Primary Method - Go ~~~~~~~~~~~~~~~~~~~~~~*/

    // Primary method to control the server
    public void go() {

        System.out.println("Server listening for new clients...");

        // Instantiating all dependencies
        this.instantiateDependencies();

        // This loop runs until the admin user enters the "exit" command
        while (!this.exitCommandGiven()) {

            // Calling the acceptConnections() method to allow clients to connect to the server
            this.acceptConnections();

        }

        // Admin user has called the exit command and thus the loop is broken, beginning the process of shutting down the server
        // Exit command has been given - close client connections
        this.closeClientConnections();

        // Shutdown the BufferedReader and server socket
        try {

            System.out.println("Shutting down server..."); // Printing message for the admin user
            this.adminInput.close(); // Closing the input BufferedReader
            this.mySocket.close(); // Closing the server's socket

        } catch (IOException j) {

            j.printStackTrace();

        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Supporting Methods ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to instantiate dependencies
    private void instantiateDependencies() {

        // Instantiating adminInput BufferedReader
        this.adminInput = new BufferedReader(new InputStreamReader(System.in));

        /* Using vectors due to them having synchronisation features which improve the reliability of multi-threading */

        // Instantiating an Vector to hold a list of client socket connections
        this.connections = new Vector<>();

        // Instantiating an Vector to hold a list of server threads
        this.serverThreads = new Vector<>();

    }

    // Method to detect if the admin user has inputted an exit command
    private boolean exitCommandGiven() {

        try {

            // Checks if the BufferedReader has data waiting to be read
            if (adminInput.ready()) {

                // Reading admin input from the terminal
                // Returning Boolean value representing whether the exit command has been given or not
                return adminInput.readLine().equalsIgnoreCase("EXIT");

            }

            // Exit command NOT given
            return false;

        } catch (IOException e) {

            e.printStackTrace();
            // Return false if any errors are thrown
            return false;

        }

    }

    // Method to accept client connections
    private void acceptConnections() {

        try {

            // Setting the mySocket.accept() timeout to be 1 second
            mySocket.setSoTimeout(1000);

            try {

                // Accepting connection from client
                Socket clientSocket = mySocket.accept();

                // Printing client connection message to the terminal
                System.out.println("Socket accepted on port: " + mySocket.getLocalPort() + " ; " + clientSocket.getPort());

                // Adding the client's socket to the connections ArrayList
                this.connections.add(clientSocket);

                // Creating a thread to manage the new client's connection
                ServerThread serverThread = new ServerThread(clientSocket, this.connections);

                // Adding the new thread to the clientThreads ArrayList
                this.serverThreads.add(serverThread);

                // Starting the client's server thread
                serverThread.start();

            } catch (SocketTimeoutException ignored) {

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    // Method to close client sockets and threads
    private void closeClientConnections() {

        try {

            // Printing confirmation message to the admin user
            System.out.println("Shutting down client threads and resources...");

            // Closing all the client connections
            for (Thread client : serverThreads) {
                client.interrupt();
            }

            // Pausing for five seconds to allow all threads and sockets to cleanly close
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Main Method ~~~~~~~~~~~~~~~~~~~~~~*/

    public static void main(String[] args) {

        // Declaring instance of myServer ChatServer
        ChatServer myServer;

        // Checking for command-line arguments
        if (validArgument(args)) {
            myServer = new ChatServer(Integer.parseInt(args[1]));
        } else {
            myServer = new ChatServer();
        }

        // Running the go method from the ChatServer class
        myServer.go();

    }

    /* ~~~~~~~~~~~~~~~~~~~ Parsing Methods ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to check if the command-line arguments are valid
    private static boolean validArgument(String[] args) {
        try {
            return args[0].equals("-csp") && !args[1].equals("-csp");
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

}

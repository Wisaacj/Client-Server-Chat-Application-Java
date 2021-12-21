import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Client {

    /* ~~~~~~~~~~~~~~~~~~~ Fields ~~~~~~~~~~~~~~~~~~~~~~*/

    // Declaring instance of serverSocket Socket
    protected Socket serverSocket;

    // Declaring instance of sendMessage Thread
    protected Thread sendMessage;

    // Declaring instance of readResponse Thread
    protected Thread readServerResponse;

    // Declaring instance of userInput BufferedReader
    protected BufferedReader userInput;

    // Declaring instance of serverOut PrintWriter
    protected PrintWriter serverOut;

    // Declaring instance of serverIn BufferedReader
    protected BufferedReader serverIn;

    /* ~~~~~~~~~~~~~~~~~~~ Primary Method - Go ~~~~~~~~~~~~~~~~~~~~~~*/

    public void go(String name) {

        try {

            // Starting the serverResponse Thread
            this.createServerResponseThread();

            // Starting the sendMessage Thread
            this.createSendMessageThread(name);

            // Printing welcome message to the user
            System.out.println("[Server] Welcome to the server " + name + ". We hope your enjoy your stay");

            // Blocking until the threads complete
            this.readServerResponse.join();
            this.sendMessage.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            try {

                this.serverSocket.close(); // Closing the socket with the server

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Methods to create Threads ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to create a thread which receives data from the server and prints it out to the terminal
    protected void createServerResponseThread() {

        // Creating a thread to manage receiving data from the server
        this.readServerResponse = new Thread(() -> {

            try {

                // This loop runs until the server sends null - indicating that the server is shutting down
                while(true) {

                    // Read data from the server
                    String serverResponse = serverIn.readLine();

                    // If the server has sent null, break from the loop and close the thread
                    if (serverResponse == null) {

                        // Sending an interrupt to the sendMessage thread to let it know the server is shutting down
                        this.sendMessage.interrupt();
                        break;

                    }

                    // Print data from the server in the terminal
                    System.out.println(serverResponse);

                    // Sleep for 50 milliseconds to check for an interrupt
                    Thread.sleep(50);

                }

            } catch (IOException | InterruptedException e) {

                try {

                    this.serverIn.close(); // Closing the BufferedReader

                } catch (IOException ioException) {

                    ioException.printStackTrace();

                }

            }

        });

        // Starting the thread
        this.readServerResponse.start();

    }

    // Method to create a thread which takes a user input from the terminal and sends the data to the server
    protected void createSendMessageThread(String name) {

        // Creating a thread to manage userInput and sending data to the server
        this.sendMessage = new Thread(() -> {

            try {

                String userInputString = "";

                while (!userInputString.equalsIgnoreCase(".leave") && !Thread.currentThread().isInterrupted()) {

                    // Checking if there is data in the BufferedReader ready to be read
                    if (userInput.ready()) {
                        userInputString = userInput.readLine();
                        this.serverOut.println("<" + name + "> " + userInputString);
                    }

                }

                // Interrupting the readServerResponse thread
                this.readServerResponse.interrupt();
                // Giving the server a second to close it's connections
                Thread.sleep(1000);

                // Closing the userInput and serverOut objects
                this.userInput.close();
                this.serverOut.close();

            } catch (InterruptedException interrupt) {

                // If the thread gets interrupted during its sleep, it means that the server is closing down and thus, a message is printed to the user to inform them
                System.out.println("Server closing down...");

            } catch (IOException e) {

                e.printStackTrace();

            }

        });

        // Starting the thread
        this.sendMessage.start();

    }

    /* ~~~~~~~~~~~~~~~~~~~ Supporting Methods ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to instantiate read/write objects to/from the server
    protected void instantiateReadWrite() {

        try {

            // Instantiating userInput
            this.userInput = new BufferedReader(new InputStreamReader(System.in));
            // Instantiating serverOut
            this.serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
            // Instantiating serverIn
            this.serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Parsing methods ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to parse the port from the command-line arguments
    protected static int parsePort(String[] args) {

        // Setting the default port to be 14001
        int port = 14001;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ccp")) {
                port = Integer.parseInt(args[i+1]);
                break;
            }
        }

        return port;

    }

    // Method to parse the address from the command-line arguments
    protected static String parseAddress(String[] args) {

        // Setting the default address to be "localhost"
        String address = "localhost";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-cca")) {
                address = args[i+1];
                break;
            }
        }

        return address;

    }

    // Method to parse the username from the command-line arguments
    protected static String parseName(String[] args) {

        // Setting the default name to be "Anon"
        String name = "Anon";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-name")) {
                name = args[i+1];
                break;
            }
        }

        return name;

    }

}

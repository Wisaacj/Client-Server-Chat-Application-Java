import java.io.IOException;
import java.net.Socket;

public class ChatBot extends Client {

    // Initialising outputString variable
    private String outputString = "";

    // Initialising numMessages variable
    private int numMessages = 0;

    /* ~~~~~~~~~~~~~~~~~~~ Constructor Methods ~~~~~~~~~~~~~~~~~~~~~~*/

    // Non-parameterised constructor
    public ChatBot() {

        try {

            // Instantiating serverSocket
            this.serverSocket = new Socket("localhost", 14001);

            // Setting up the ability to read from the console, send data to the server, and receive data from the server
            this.instantiateReadWrite();

            // Running the primary method go() with argument name="Anon" (default)
            this.go("Elon (Bot)");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Parameterised constructor
    public ChatBot(String address, int port) {

        try {

            // Instantiating serverSocket
            this.serverSocket = new Socket(address, port);

            // Setting up the ability to read from the console, send data to the server, and receive data from the server
            this.instantiateReadWrite();

            // Running the primary method go() with argument name="Anon" (default)
            this.go("Elon (Bot)");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Accessors ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method which returns the current value of outputString
    public String getOutputString() {
        return this.outputString;
    }

    // Method which returns the current value of numMessages
    public int getNumMessages() {
        return this.numMessages;
    }

    /* ~~~~~~~~~~~~~~~~~~~ Mutators ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to update the variable outputString
    public void setOutputString(String output) {
        this.outputString = output;
    }

    // Method to update the variable numMessages
    public void incrementNumMessages() {
        this.numMessages++;
    }

    /* ~~~~~~~~~~~~~~~~~~~ Methods to create Threads ~~~~~~~~~~~~~~~~~~~~~~*/

    // Method to create a thread which receives data from the server and prints it out to the terminal
    // Overriding the method in the Client superclass
    @Override
    protected void createServerResponseThread() {

        // Creating a thread to manage receiving data from the server
        this.readServerResponse = new Thread(() -> {

            try {

                // This loop runs until the server sends null - indicating that the server is shutting down
                while(true) {

                    // Read data from the server
                    String serverResponse = serverIn.readLine();

                    // Increment the number of messages sent by one
                    this.incrementNumMessages();

                    // If the server has sent null, break from the loop and close the thread
                    if (serverResponse == null) {

                        // Sending an interrupt to the sendMessage thread to let it know the server is shutting down
                        this.sendMessage.interrupt();
                        break;

                    }

                    // Print data from the server in the terminal
                    System.out.println(serverResponse);

                    // Parse the data from the server and produce a response to the user's input
                    // Passing the serverResponse and the total number of messages sent so far as arguments
                    this.setOutputString( ChatBotCommands.getOutput( serverResponse.toLowerCase(), this.getNumMessages() ) );

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
    // Overriding the method in the Client superclass
    @Override
    protected void createSendMessageThread(String name) {

        // Creating a thread to manage userInput and sending data to the server
        this.sendMessage = new Thread(() -> {

            try {

                while (!Thread.currentThread().isInterrupted()) {

                    // Checking if there is data in the BufferedReader ready to be read
                    if (!this.getOutputString().equals("")) {
                        this.serverOut.println("<" + name + "> " + this.getOutputString());
                        this.setOutputString("");
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

    /* ~~~~~~~~~~~~~~~~~~~ Main Method ~~~~~~~~~~~~~~~~~~~~~~*/

    // Main method
    public static void main(String[] args) {

        // Initialising port, address and name variables to the command-line arguments
        int port = parsePort(args);
        String address = parseAddress(args);

        // Checking for command-line arguments
        if (port != 14001 || !address.equals("localhost")) {
            new ChatBot(address, port);
        } else {
            new ChatBot();
        }

    }

}

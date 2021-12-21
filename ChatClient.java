import java.io.IOException;
import java.net.Socket;

public class ChatClient extends Client {

    /* ~~~~~~~~~~~~~~~~~~~ Constructors ~~~~~~~~~~~~~~~~~~~~~~*/

    // Non-parameterised constructor
    public ChatClient() {

        try {

            // Instantiating serverSocket
            this.serverSocket = new Socket("localhost", 14001);

            // Setting up the ability to read from the console, send data to the server, and receive data from the server
            this.instantiateReadWrite();

            // Running the primary method go() with argument name="Anon" (default)
            this.go("Anon");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Parameterised constructor
    public ChatClient(String address, int port, String name) {

        try {

            // Instantiating serverSocket
            this.serverSocket = new Socket(address, port);

            // Setting up the ability to read from the console, send data to the server, and receive data from the server
            this.instantiateReadWrite();

            // Running the primary method go()
            this.go(name);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* ~~~~~~~~~~~~~~~~~~~ Main Method ~~~~~~~~~~~~~~~~~~~~~~*/

    // Main method
    public static void main(String[] args) {

        // Initialising port, address and name variables to the command-line arguments
        int port = parsePort(args);
        String address = parseAddress(args);
        String name = parseName(args);

        // Checking for command-line arguments
        if (port != 14001 || !address.equals("localhost") || !name.equals("Anon")) {
            new ChatClient(address, port, name);
        } else {
            new ChatClient();
        }

    }

}

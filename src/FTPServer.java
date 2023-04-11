import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public FTPServer() throws IOException {
        serverSocket = new ServerSocket(21);
        while (true) {
            clientSocket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream());

            // Read the command from the client
            String command = bufferedReader.readLine();
            while (command != null) {
                // Process the command
                switch (command) {
                    case "LIST":
                        listFiles();
                        break;
                    case "RETR":
                        getFileInfo();
                        break;
                    case "STOR":
                        putFile();
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }

                // Read the next command
                command = bufferedReader.readLine();
            }

            // Close the connection
            clientSocket.close();
        }
    }

    private void listFiles() throws IOException {
        // Get the list of files in the current directory
        File[] files = new File(".").listFiles();

        // Send the list of files to the client
        for (File file : files) {
            printWriter.println(file.getName());
        }
    }

    private void getFileInfo() throws IOException {
        // Get the name of the file to retrieve
        String fileName = bufferedReader.readLine();

        // Open the file for reading
        File file = new File("./" + fileName);
        InputStream inputStream = new FileInputStream(file);

        // Send the file size to the client
        printWriter.println(file.length());

        // Send the file to the client
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            printWriter.print(new String(buffer, 0, bytesRead));
        }
    }

    private void putFile() throws IOException {
        // Get the name of the file to upload

        String fileName = bufferedReader.readLine();

        // Open the file for writing
        File file = new File("./" + fileName);
        OutputStream outputStream = new FileOutputStream(file);

        // Get the data from the client
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bufferedReader.read()) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    public static void main(String[] args) throws IOException {
        new FTPServer();
    }
}
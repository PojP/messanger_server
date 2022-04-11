import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ServerCore {
    public static LinkedList<ServerThread> serverList = new LinkedList<>();
    private ServerSocket serverSocket;
    private Socket clientSocket;

    ServerCore(int port, int connections) throws IOException {
        serverSocket = new ServerSocket(port, connections);
        System.out.println("!!! SERVER STARTED 0_o v0.0.1 !!!");
    }
    public void GetConnections() throws IOException {
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                try   {serverList.add(new ServerThread(clientSocket));} //adding connected socket to serverList
                catch (IOException e) {clientSocket.close();}
            }
        } finally {serverSocket.close();}

    }
}
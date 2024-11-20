package another;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * The class to reduce boilerplate code 
 * And provide better testability
 */
public class Globals {
    public static final String host = "localhost";
    public static final String file = "test.txt";
    public static final int port = 12014;

    public static void start(Socket socket, BiCons<DataInputStream, DataOutputStream> consumer) {
        try(DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream())) 
        {
            System.out.println("Connection established!");
            consumer.accept(input, output);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void startClient(BiCons<DataInputStream, DataOutputStream> consumer) {
        System.out.println("Client started!");
        try(Socket socket = new Socket(host, port)) {
            start(socket, consumer);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void startServer(BiCons<DataInputStream, DataOutputStream> consumer) {
        try(ServerSocket socket = new ServerSocket(port)) {
            System.out.println("Server started!");

            while(true) {
                Socket socket2 = socket.accept();
                System.out.println("Accepted a client!");
                new Thread(() -> start(socket2, consumer)).start();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String file, String content) {
        System.out.println(file + "'s content written!");
        System.out.println("content:\n" + content);
    }

    public static String readFromFile(String file) {
        return (file + " is actually a file!").repeat(22);
    }

    public static void main(String[] args) {
        switch(args[0]) {
            case "cat1c": Cat1.client(); break;
            case "cat1s": Cat1.server(); break;
            case "cat2c": Cat2.client(); break;
            case "cat2s": Cat2.server(); break;
        }
    }
}

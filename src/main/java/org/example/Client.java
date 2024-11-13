package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(5000)) {

            Socket socket = server.accept();
            try(DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())
            ) {
                String name = in.readUTF();
                int size = in.readInt();

                if(size > 1024) {
                    out.writeUTF("Too big");
                }
                else {
                    out.writeUTF("Успішно");
                    byte[] bytes = new byte[size];
                    in.readFully(bytes);
                    Files.writeString(new File(name).toPath(), new String(bytes));
                }
            }

        } catch(Throwable ignored) {
            throw new RuntimeException(ignored);
        }
    }
}
package org.example;

import lib.IOL;
import lib.IOLServer;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Lab4Server {
    public static String tmp;

    public static void main(String[] args) {
        IOL.load();
        IOLServer.start(connection -> {
            connection.onServerPacketReceived("FileSend", serverPacket -> {
                String fileContent = serverPacket.value();

                if(fileContent.getBytes().length > 1024) {
                    connection.sendServerPacket("FileSendFailure", "File too large");
                    tmp = fileContent;
                } else {
                    connection.sendServerPacket("FileSendSuccess", "");
                }
            });

            connection.onServerPacketReceived("FileChecksumCheck", serverPacket -> {
                String hash = serverPacket.value();
                try {
                    String serverHash = Lab3Client.calculateHash(tmp.getBytes());

                    if(Objects.equals(hash, serverHash)) {
                        connection.sendServerPacket("FileChecksumSuccess", "");
                    } else {
                        connection.sendServerPacket("FileChecksumFailure", "");
                    }
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });
        }, ignored -> {});
    }
}
package org.example;

import lib.IOL;

import java.awt.desktop.SystemSleepEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Lab3Client {
    public static String calculateHash(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileContent);
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }

    public static void main(String[] args) {
        IOL.load();
        IOL.connect(connection -> {
            try {
                String fileContent = Files.readString(new File("text.txt").toPath());

                connection.onServerPacketReceived("FileChecksumSuccess", serverPacket -> {
                    System.out.println("File checksum is correct.");
                });

                connection.onServerPacketReceived("FileChecksumFailure", serverPacket -> {
                    System.out.println("File checksum is incorrect.");
                });

                connection.onServerPacketReceived("FileSendSuccess", serverPacket -> {
                    try {
                        connection.sendServerPacket("FileChecksumCheck", calculateHash(fileContent.getBytes()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                });

                connection.onServerPacketReceived("FileSendFailure", serverPacket -> {
                    System.out.println("File upload failed: " + serverPacket.value());
                });

                connection.sendServerPacket("FileSend", fileContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
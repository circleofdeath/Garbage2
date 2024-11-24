package another;

public class Cat3 {
    public static void client() {
        Globals.startClient((input, output) -> {
            while(true) {
                byte[] content = Globals.readFromFile(Globals.file).getBytes();
                output.writeInt(content.length);

                if(input.readBoolean()) {
                    output.writeUTF(Globals.file);
                    output.writeUTF(Globals.calculateHash(content));
                    output.write(content);
                    
                    if(input.readBoolean()) {
                        int len = input.readInt();
                        byte[] content2 = new byte[len];
                        input.readFully(content2);
                        Globals.writeToFile(Globals.file, new String(content2));
                        break;
                    } else {
                        System.out.println("Invalid hash, reaploading...");
                    }
                }
            }
        });
    }
 
    public static void server() {
        Globals.startServer((input, output) -> {
            while(true) {
                int len = input.readInt();

                if(len >= 1024) {
                    output.writeBoolean(false);
                } else {
                    output.writeBoolean(true);
                    String name = input.readUTF();
                    String hash = input.readUTF();
                    byte[] content = new byte[len];
                    input.readFully(content);
                    String fileHash = Globals.calculateHash(content);
                    
                    if(fileHash.equals(hash)) {
                        output.writeBoolean(true);
                        Globals.writeToFile(name, new String(content));
                        output.writeInt(len);
                        output.write(content);
                    } else {
                        output.writeBoolean(false);
                    }
                }
            }
        });
    }
}

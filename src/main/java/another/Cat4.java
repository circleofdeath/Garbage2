package another;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Cat4 {
    public static final int BLOCK_SIZE = 128;

    public static void clientHandleCache(
        String hash, byte[] buffer, 
        DataOutputStream out,
        DataInputStream in
    )
    throws Exception 
    {
        out.writeUTF(hash);
        out.write(buffer);

        if(!in.readBoolean()) {
            clientHandleCache(hash, buffer, out, in);
        }
    }

    public static void client() {
        Globals.startClient((in, out) -> {
            byte[] content = Globals.readFromFile(Globals.file).getBytes();

            out.write(content.length);
            if(in.readBoolean()) {
                out.writeUTF(Globals.file);

                byte[] buffer = new byte[BLOCK_SIZE];
                for(int i = 0; i < content.length; i += BLOCK_SIZE) {
                    System.arraycopy(content, i, buffer, 0, content.length - i * BLOCK_SIZE);
                    String hash = Globals.calculateHash(buffer);

                    clientHandleCache(hash, buffer, out, in);
                }
            } else {
                System.out.println("Too big");
            }
        });
    }

    public static void server() {
        Globals.startServer((in, out) -> {
            int len = in.readInt();

            if(len > 1024) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                String name = in.readUTF();
                byte[] content = new byte[1024];
                int sectors = len / BLOCK_SIZE + 1;
                int readen = 0;

                while(readen < sectors) {
                    String hash = in.readUTF();
                    byte[] buffer = new byte[BLOCK_SIZE];
                    in.readFully(buffer);

                    if(Globals.calculateHash(buffer).equals(hash)) {
                        System.arraycopy(buffer, 0, content, readen * BLOCK_SIZE, Math.min(BLOCK_SIZE, len - readen * BLOCK_SIZE));
                        readen++;
                    }
                }

                Globals.writeToFile(name, new String(content));
            }
        });
    }
}

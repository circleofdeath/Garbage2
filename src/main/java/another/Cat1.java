package another;

public class Cat1 {
    public static void client() {
        Globals.startClient((input, output) -> {
            byte[] content = Globals.readFromFile(Globals.file).getBytes();
            output.writeInt(content.length);

            if(input.readBoolean()) {
                output.write(content);
                int len = input.readInt();
                byte[] content2 = new byte[len];
                input.readFully(content2);
                Globals.writeToFile(Globals.file, new String(content2));
            }
        });
    }

    public static void server() {
        Globals.startServer((input, output) -> {
            int len = input.readInt();

            if(len >= 1024) {
                output.writeBoolean(false);
            } else {
                output.writeBoolean(true);
                byte[] content = new byte[len];
                input.readFully(content);
                Globals.writeToFile(Globals.file, new String(content));
                output.writeInt(len);
                output.write(content);
            }
        });
    }
}

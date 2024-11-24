package another;

public class Cat1 {
    public static void client() {
        globals.startclient((input, output) -> {
            byte[] content = globals.readfromfile(globals.file).getbytes();
            output.writeint(content.length);

            if(input.readboolean()) {
                output.write(content);
                int len = input.readint();
                byte[] content2 = new byte[len];
                input.readfully(content2);
                globals.writetofile(globals.file, new string(content2));
            }
        });
    }

    public static void server() {
        globals.startserver((input, output) -> {
            int len = input.readint();

            if(len >= 1024) {
                output.writeboolean(false);
            } else {
                output.writeboolean(true);
                byte[] content = new byte[len];
                input.readfully(content);
                globals.writetofile(globals.file, new string(content));
                output.writeint(len);
                output.write(content);
            }
        });
    }
}

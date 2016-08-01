import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Client implements Runnable {
    static int num = 0;
    private int id;
    private String host = "fe80::46dd:29b3:a7d9:e4c4";
    int port = 5026;
    private String name;
    private String password = "enter1206";
    Writer out;
    BufferedReader in;

    public void run() {
        try (Socket socket = new Socket(host, port)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Thread thread=new Thread() {
                public void run() {
                    try {
                        while (true) {
                            String str = in.readLine();           //readLine returns null if the stream is end
                            if (str == null) break;
                            //System.out.println("window " + id + ":" + str);
                        }
                    } catch (Exception e) {
                    }
                    System.out.println("exiting read thread "+id);
                }
            };
            thread.setDaemon(true);
            thread.start();
            login(name, password);
            while (true) {
                out.write("Message is from\t" + name + "\t" + id + "\r\n");
                out.flush();
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
        }
        System.out.println("exit write "+id);
        //System.exit(0);         //the thread cannot stop without this sentence.
    }

    public Client(String name) {
        num++;
        id = num;
        this.name = name;

    }

    public void login(String name, String password) {
        try {
            out.write("login\r\n" + name + "\r\n" + password + "\r\n");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String arg[]) {
        int N = 150;
        ExecutorService pool = Executors.newFixedThreadPool(N);
        for (int i = 0; i < N; i++) {
            String str;
            if ((i & 1) == 1)
                str = "wmz";
            else str = "wmz0001";
            pool.execute(new Client(str));
        }
        System.out.println("exit main");
    }
}

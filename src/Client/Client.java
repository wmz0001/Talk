package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Client implements Runnable {
    static int num = 0;
    private int id;
    private String host = "fe80::eeaf:8f10:a841:a3a6";
    int port = 5026;
    private String name;
    private String password = "enter1206";
    static final int msTime = 60;
    static final int N = 6000;
    Writer out;
    BufferedReader in;

    public void run() {
        try (Socket socket = new Socket(host, port)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            login(name, password);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            String str = in.readLine();           //readLine returns null if the stream is end
                            if (str == null) break;
                            //System.out.println("window " + id + ":" + str);
                        }
                    } catch (Exception e) {
                        //System.out.println(e+" read run");
                    }
                    System.out.println("exit read thread " + id);
                }
            };
            //thread.setDaemon(true);
            thread.start();
            int messageNum = 0;
            while (true) {
                messageNum++;
                String str = "Message " + messageNum + " is from\t" + name + " " + id + "\r\n";
                out.write(str);
                out.flush();
                //System.out.println(str);
                TimeUnit.SECONDS.sleep(msTime);
            }
        } catch (Exception e) {
            //System.out.println(e+" write run");
        }
        System.out.println("exit write thread" + id);
        //System.exit(0);         //the thread cannot stop without this sentence.|pool.shutdown is ok.
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
        int timeSleep = msTime * 1000 / N;
        ExecutorService pool = Executors.newFixedThreadPool(N);
        for (int i = 0; i < N; i++) {
            String str = "wmz";
            pool.execute(new Client(str));
            try {
                TimeUnit.MILLISECONDS.sleep(timeSleep);
            } catch (Exception e) {
            }
        }
        pool.shutdown();    //the thread cannot stop without this sentence.
    }
}

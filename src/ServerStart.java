import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ServerStart extends Thread {
    private int port;

    //private Map<String,String>UserOnline;
    public ServerStart(int port) {
        this.port = port;
    }


    public void run() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            while (ServerMain.isCanceled()) {
                try {
                    Socket connection = server.accept();
                    pool.submit(new ServerThread(connection));
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdown();
        boolean isTerminated = false;
        try {
            isTerminated = pool.awaitTermination(250, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }
        if (!isTerminated)
            System.out.println("Some task were not terminated!");


    }
}

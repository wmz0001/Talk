import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ServerStart extends Thread {
    private int port;
    private int onlineNum = 0;

    private List<ServerThread> list = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();

    public ServerStart(int port) {
        this.port = port;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public Map<String, String> getOnlineUser() {
        return map;
    }

    public synchronized void login(String name, String date) {
        onlineNum++;
        map.put(name, date);
    }

    public synchronized void logout(String name) {
        onlineNum--;
        map.remove(name);
    }

    public synchronized void sendAll(String data) {
        for (ServerThread serverThread : list)
            serverThread.sendInfo(data);
    }

    public void run() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket connection = server.accept();    //this blocks so cancel is meaningless!
                    ServerThread thread = new ServerThread(connection, this);
                    synchronized (this) {
                        list.add(thread);   //at the same time some thread may call sendAll and cause ConcurrentModificationException
                    }
                    pool.execute(thread);
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
//        pool.shutdown();
//        boolean isTerminated = false;
//        try {
//            isTerminated = pool.awaitTermination(250, TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (!isTerminated)
//            System.out.println("Some task were not terminated!");
//        System.out.println("exit serverstart");
    }
}

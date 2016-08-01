import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ServerStart extends Thread {
    private int port;
    private int onlineNum = 0;
    private DbConnection dbConn = null;

    private Set<ServerThread> list = new HashSet<>();
    private Map<String, String> map = new HashMap<>();

    public DbConnection getDBcon(){return dbConn;}
    public ServerStart(int port) {
        this.port = port;
    }

    public synchronized int getOnlineNum() {
        return onlineNum;
    }

    public Map<String, String> getOnlineUser() {
        return map;
    }

    public synchronized void login(ServerThread thread) {
        onlineNum++;
        list.add(thread);
        //map.put(name, date);
    }

    public synchronized void logout(ServerThread thread) {
        onlineNum--;
        list.remove(thread);
        //map.remove(name);
    }

    public synchronized void sendAll(String data) {
        for (ServerThread serverThread : list) {
            if (serverThread.statusInfo())
                serverThread.sendInfo(data);
        }
    }

    public void run() {
        dbConn = new DbConnection();
        try {
            dbConn.openConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket connection = server.accept();    //this blocks so cancel is meaningless!
                    ServerThread thread = new ServerThread(connection, this);
//                    synchronized (this) {   //terrible ! tread haven't run!
//                        list.add(thread);   //at the same time some thread may call sendAll and cause ConcurrentModificationException
//                    }
                    pool.execute(thread);
                } catch (Exception e) {
                    System.out.println(e+" ServerStart while");
                }
            }
        } catch (IOException e) {
            System.out.println(e+" ServerStart run");
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

package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerStart extends Thread {
    private int port;
    private int onlineNum;
    private DbConnection dbConn;
    private ServerDispatch dispatcher;

    private Map<String, String> map = new HashMap<>();

    public DbConnection getDbConn() {
        return dbConn;
    }

    public String getInfo() {
        int disCnt = dispatcher.waitMs();
        int totalCnt = dispatcher.totalWaitMs();
        return "Dispatcher waiting: " + disCnt + "\n" + "num: " + onlineNum
                + "\ttotal waiting: " + totalCnt;
    }

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
        dispatcher.addClient(thread.getClientWriter());
        //list.add(thread);
        //map.put(name, date);
    }

    public synchronized void logout(ServerThread thread) {
        onlineNum--;
        dispatcher.delClient(thread.getClientWriter());
        //list.remove(thread);
        //map.remove(name);
    }

    public void sendAll(String str) {
        dispatcher.addMessage(str);
    }


    public void run() {
        dbConn = new DbConnection();
        try {
            dbConn.openConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispatcher = new ServerDispatch();
        dispatcher.setDaemon(true);
        dispatcher.start();
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
                    System.out.println(e + " ServerStart while");
                }
            }
        } catch (IOException e) {
            System.out.println(e + " ServerStart run");
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

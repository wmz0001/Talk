import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ServerStart extends Thread {
    private int port;
    private int onlineNum=0;
    private Map<String,String> map =new HashMap<>();
    public ServerStart(int port) {
        this.port = port;
    }
    public int getOnlineNum(){
        return onlineNum;
    }
    public Map<String,String> getOnlineUser(){return map;}
    public synchronized void login(String name,String date){
        onlineNum++;
        map.put(new String(name),new String(date));
    }
    public synchronized void logout(String name){
        onlineNum--;
        map.remove(name);
    }
    public void run() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            while (ServerMain.isCanceled()) {
                try {
                    Socket connection = server.accept();
                    pool.submit(new ServerThread(connection,this));
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

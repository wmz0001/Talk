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
    private int onlineNum=0;
    private List<ServerThread> list=new ArrayList<>();
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
        map.put(name,date);
    }
    public synchronized void logout(String name){
        onlineNum--;
        map.remove(name);
    }
    public synchronized void sendAll(String data){
        for(ServerThread serverThread :list)
            serverThread.sendInfo(data);
    }
    public void run() {
        ExecutorService pool= Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            while (ServerMain.isCanceled()) {
                try {
                    Socket connection = server.accept();
                    ServerThread thread=new ServerThread(connection,this);
                    list.add(thread);
                    pool.submit(thread);
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

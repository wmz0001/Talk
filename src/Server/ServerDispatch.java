package Server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

public class ServerDispatch extends Thread {
    private Set<ClientWriter> clientSet = new HashSet<>();
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public synchronized int waitMs(){
        return messageQueue.size();
    }
    public synchronized int totalWaitMs(){
        int cnt=0;
        for (ClientWriter clientWriter:clientSet){
            cnt+=clientWriter.waitMs();
        }
        return cnt;
    }
    public void addMessage(String str) {
        try {
            messageQueue.put(str);
//            System.out.println("addMs" + str);
        } catch (Exception e) {
            System.out.println(e + " ServerDispatch addMessage");
        }
    }

    public synchronized void addClient(ClientWriter thread) {
        clientSet.add(thread);
    }

    public synchronized void delClient(ClientWriter client) {
        clientSet.remove(client);
    }

    public synchronized void dispatch(String str) throws Exception {
        for (ClientWriter client : clientSet) {
            client.getMessage(str);
        }
    }

    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try {
            while (true) {
                String str = messageQueue.take();
                dispatch(str);
            }
        } catch (Exception e) {
            //System.out.println(e + " ServerDispatch run");
            e.printStackTrace();
        }
    }
}

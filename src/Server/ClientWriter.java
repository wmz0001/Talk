package Server;

import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by WMZ on 2016/8/2.
 */
public class ClientWriter extends Thread {
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private Writer out;

    public int waitMs(){
        return messageQueue.size();
    }
    public ClientWriter(Writer out) {
        this.out = out;
    }

    public void getMessage(String ms) {
        try {
//            System.out.println("getMs " + ms);
            messageQueue.put(ms);
        } catch (Exception e) {
            System.out.println(e + " ClientWriter getMessage");
        }
    }

    public void sendMessage(String ms) throws Exception {
//        System.out.println("sendMs " + ms);
        out.write(ms + "\r\n");
        out.flush();
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                String ms = messageQueue.take();    //it doesn't work if get the lock before take because take will block
                sendMessage(ms);
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            System.out.println(e + " ClientWrite run");
        }
    }
}

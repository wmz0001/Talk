package Server;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ServerMain {
    private static final int PORT = 5026;
    private static boolean canceled = true;

//    public static boolean isCanceled() {
//        return canceled;
//    }

    public static void main(String[] args) throws InterruptedException {
        ServerStart server = new ServerStart(PORT);
        server.start();
        Scanner cin = new Scanner(System.in);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        while (canceled && cin.hasNext()) {
            String str = cin.nextLine().trim();
            switch (str) {
                case "quit": {
                    canceled = false;
                    //server.interrupt();
                    break;
                }
                case "num": {
                    System.out.println(server.getOnlineNum());
                    break;
                }
                case "users": {
                    System.out.println(server.getOnlineUser());
                    break;
                }
                case "info":{
                    System.out.println(server.getInfo());
                    break;
                }
            }
        }
//        TimeUnit.SECONDS.sleep(1);
        System.out.println("Bye!");
        System.exit(0);
    }
}

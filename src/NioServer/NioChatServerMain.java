package NioServer;

import java.util.Scanner;

public class NioChatServerMain {
    private static final int PORT = 5026;
    private static boolean canceled = true;

    public static void main(String[] args) throws InterruptedException {
        NioChatServer server = new NioChatServer(PORT);
        new Thread(server).start();
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
//                case "users": {
//                    System.out.println(server.getOnlineUser());
//                    break;
//                }
//                case "info":{
//                    System.out.println(server.getInfo());
//                    break;
//                }
            }
        }
        System.out.println("Bye!");
        System.exit(0);
    }
}

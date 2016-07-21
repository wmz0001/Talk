import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ServerMain {
    private static final int PORT=5026;
    private static volatile boolean canceled=true;
    public static boolean  isCanceled(){return canceled;}
    public static void main(String[] args) throws InterruptedException {
        ServerStart server=new ServerStart(PORT);
        server.start();
        Scanner cin= new Scanner(System.in);

        while(canceled&&cin.hasNext()){
            String str=cin.nextLine().trim();
            System.out.println(str);
            switch (str){
                case "quit":{
                    System.out.println("Bye!");
                    canceled=false;
                    break;
                }
            }
        }
        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }
}

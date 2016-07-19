import java.util.Scanner;

public class ServerMain {
    private static final int PORT=5026;

    public static void main(String[] args){
        ServerStart server=ServerStart.startServer(PORT);
        server.run();
        Scanner cin= new Scanner(System.in);
        boolean flag=true;
        while(flag&&cin.hasNext()){
            String str=cin.nextLine().trim();
            System.out.println(str);
            switch (str){
                case "quit":{
                    System.out.println("Bye!");
                    server.pauseThread();
                    flag=false;
                    break;
                }
            }
        }

    }
}

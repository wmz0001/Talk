import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerStart extends Thread{
    private static int PORT;
    private boolean flag=true;
    private static ServerStart server=new ServerStart();
    //private Map<String,String>UserOnline;
    //private ServerStart(){}
    public void pauseThread(){
        flag=false;
    }

    public static ServerStart startServer(int port){
        PORT=port;
        return server;
    }
    public void run(){
        ExecutorService pool= Executors.newCachedThreadPool();
        try(ServerSocket server=new ServerSocket(PORT)){
            while(flag){
                try{
                    Socket connection=server.accept();
                    pool.submit(new ServerThread(connection));
                }catch (IOException e){}
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        pool.shutdown();
    }
    public static class ServerThread implements Callable<Void> {
        private Socket socket;
        private DbConnection dbConn=null;
        private BufferedReader in=null;
        private Writer out=null;
        public ServerThread(Socket socket){
            this.socket=socket;
            dbConn = new DbConnection();
            try{
                dbConn.openConn();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out=new OutputStreamWriter(socket.getOutputStream());
            }catch (Exception e){e.printStackTrace();}
        }

        public Void call() {
            try {
                while (true) {
                    String str = in.readLine();
                    switch (str) {
                        case "registerNewUser": {
                            System.out.println("get request!");
                            registerNewUser();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        private boolean registerNewUser(){
            boolean res=false;
            try{
                String userName=in.readLine();
                String passWord=in.readLine();
                res=dbConn.registerNewUser(userName,passWord);
                out.write(res+" Registered\r\n");
                out.flush();
                System.out.println("REGISTER"+res);
            }catch (Exception e){}
            return res;
        }
    }
}

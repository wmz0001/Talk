import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by WMZ on 2016/7/21.
 */
public class ServerThread implements Callable<Void> {
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
        }finally {
            dbConn.close();
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

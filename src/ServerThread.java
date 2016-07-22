import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;

public class ServerThread implements Callable<Void> {
    private Socket socket;
    private ServerStart serverStart;
    private DbConnection dbConn = null;
    private BufferedReader in = null;
    private Writer out = null;
    private String user=null;
    private boolean logState=false;
    public ServerThread(Socket socket,ServerStart serverStart) {
        this.serverStart=serverStart;
        this.socket = socket;
        dbConn = new DbConnection();
        try {
            dbConn.openConn();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Void call() {
        try {
            while (true) {
                String str = in.readLine();
                switch (str) {
                    case "registerNewUser": {
                        //System.out.println("get request!");
                        registerNewUser();
                        break;
                    }
                    case "login":{
                        login();
                        break;
                    }
                    case "logout":{
                        logout();
                        break;
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            dbConn.close();
        }
        return null;
    }

    private void registerNewUser() {
        boolean res=false;
        try {
            String userName = in.readLine();
            String passWord = in.readLine();
            res = dbConn.registerNewUser(userName, passWord);
            out.write(res + " Registered\r\n");
            out.flush();
            System.out.println("REGISTER " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void login(){
        boolean res=false;
        try {
            String userName = in.readLine();
            String passWord = in.readLine();
            res = dbConn.login(userName, passWord);
            out.write(res + " login\r\n");
            out.flush();
            System.out.println("login " + res);
            if(res){
                logState=true;
                user=new String(userName);
                String date=new Date().toString();
                serverStart.login(userName,date);
            }
        }catch (Exception e){e.printStackTrace();}
    }
    private void logout(){
        logState=false;
        serverStart.logout(user);
    }
}

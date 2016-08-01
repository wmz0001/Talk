import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;

public class ServerThread implements Runnable {
    private int threadId;
    private DbConnection dbConn;
    private Socket socket;
    private ServerStart serverStart;
    private BufferedReader in = null;
    private Writer out = null;
    private String user = null;
    private volatile boolean logStatus = false;

    public boolean statusInfo(){return logStatus;}
    public ServerThread(Socket socket, ServerStart serverStart) {
        this.serverStart = serverStart;
        this.socket = socket;
        dbConn = serverStart.getDBcon();
    }

    public void sendInfo(String data) {
        try {
            out.write(data+"\r\n");
            out.flush();
        } catch (Exception e) {
            System.out.println(e+" sendInfo");
        }
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String str = in.readLine(); //this will throw exception?
                if(str==null){
                    System.out.println("thread break of read");   //never run to this because exception throws
                    break;                                        //why is it different with client?
                }                                            //still need because it may happen if has buffered input?
                switch (str) {
                    case "registerNewUser": {
                        //System.out.println("get request!");
                        registerNewUser();
                        break;
                    }
                    case "login": {
                        login();
                        break;
                    }
                    case "logout": {
                        logout();
                        break;
                    }
                    default: {
                        //System.out.println(threadId+'\t'+user+socket+str);
                        serverStart.sendAll(str);
                    }
                }
            }
        } catch (Exception e) {
            logout();
            System.out.println("exit thread" + threadId);
            //System.out.println(e+" ServerThread run");
        }
    }

    private void registerNewUser() {
        boolean res = false;
        try {
            String userName = in.readLine();
            String passWord = in.readLine();
            res = dbConn.registerNewUser(userName, passWord);
            out.write(res + " Registered\r\n");
            out.flush();
            System.out.println("REGISTER " + res);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void login() {
        boolean res = false;
        try {
            String userName = in.readLine();
            String passWord = in.readLine();
            res = dbConn.login(userName, passWord);
            out.write(res + " login\r\n");
            out.flush();
            if (res) {
                logStatus = true;
                user = userName;
                //String date = new Date().toString();
                synchronized (serverStart) {              //the two steps must be together otherwise same id occurs
                    serverStart.login(this);
                    threadId = serverStart.getOnlineNum();
                }
                System.out.println("login " + threadId);
            }
        } catch (Exception e) {
            System.out.println(e+" login");
        }
    }

    public void logout() {
        logStatus = false;
        serverStart.logout(this);
    }
}

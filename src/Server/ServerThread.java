package Server;


import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {
    private int threadId;
    private ClientWriter clientWriter;
    private DbConnection dbConn;
    private Socket socket;
    private ServerStart serverStart;
    private BufferedReader in;
    private Writer out;
    private String user;
    private boolean status = true;
    private boolean logStatus = false;


    //    public boolean statusInfo() {
//        return logStatus;
//    }
    public ClientWriter getClientWriter() {
        return clientWriter;
    }

    public ServerThread(Socket socket, ServerStart serverStart) {
        this.serverStart = serverStart;
        this.socket = socket;
        dbConn = serverStart.getDbConn();
    }

//    public boolean sendInfo(String data) {
//        if (!logStatus) return false;
//        try {
//            out.write(data + "\r\n");
//            out.flush();
//        } catch (Exception e) {
//            System.out.println(e + " sendInfo");
//            return false;
//            //Thread.yield();      //yield cannot release the syn, it does not help
//        }
//        return true;
//    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (status) {
                String str = in.readLine(); //this will throw exception?
//                System.out.println(str);
                if (str == null) {
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
                        status = false;
                        break;
                    }
                    default: {
//                        System.out.println(threadId+'\t'+str);
                        serverStart.sendAll(str);
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println(e+" ServerThread run");
        }
        logout();
        System.out.println("exit thread" + threadId);
    }

    private void registerNewUser() {
        boolean res;
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

    private void login() {
        if (logStatus) return;
        boolean res;
        try {
            String userName = in.readLine();
            String passWord = in.readLine();
            res = dbConn.login(userName, passWord);
//            System.out.println(userName+" "+res);
            out.write(res + " login\r\n");
            out.flush();
            if (res) {
                logStatus = true;
                user = userName;
                //String date = new Date().toString();
                clientWriter = new ClientWriter(out);
                clientWriter.start();
                synchronized (serverStart) {              //the two steps must be together otherwise same id occurs
                    serverStart.login(this);
                    threadId = serverStart.getOnlineNum();
                }
                System.out.println("login " + threadId);
            }
        } catch (Exception e) {
            System.out.println(e + " login");
        }
    }

    public void logout() {
        clientWriter.interrupt();
        serverStart.logout(this);
    }
}

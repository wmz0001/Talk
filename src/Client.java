import java.io.*;
import java.net.Socket;


public class Client {
    public static void main(String arg[]){
        String host="fe80::9237:a368:1a5c:4f92";
        int port=5026;
        try(Socket socket=new Socket(host,port)){
            Writer out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write("registerNewUser\r\n");
            out.write("wmz\r\n");
            out.write("enter1206\r\n");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(in.readLine());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

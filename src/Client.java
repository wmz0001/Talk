import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Client implements Runnable{
    static int num=0;
    private int id;
    private String host="fe80::6866:9623:9c12:a520";
    int port=5026;
    private String name;
    private String password="enter1206";
    Socket socket;
    Writer out;
    BufferedReader in;
    public void run(){
        while (true){
            try {
                out.write("name\t"+id+new Date());
                out.flush();
                TimeUnit.SECONDS.sleep(1);
            }catch (Exception e){}
        }
    }
    public Client(String name){
        num++;
        id=num;
        this.name=name;
        try{
            socket=new Socket(host,port);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(){
                public void run(){
                    while(true){
                        try {
                            System.out.println(in.readLine());
                        }catch (Exception e){}
                    }
                }
            }.start();
            login(name,password);
        }catch (Exception e){}
    }
    public void login(String name,String password){
        try{
            out.write("login\r\n"+name+"\r\n"+password+"\r\n");
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String arg[]){
        int N=1;
        ExecutorService pool= Executors.newFixedThreadPool(1);
        for(int i=0;i<N;i++){
            String str;
            if((i&1)==1)
                str="wmz";
            else str="wmz0001";
            pool.submit(new Client(str));
        }
    }
}

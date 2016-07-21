import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbConnection {
    private final  String connectionURL="jdbc:mysql://localhost:3306/TalkDB?autoReconnect=true&useSSL=false";
    private final  String user="root";
    private final  String password="enter1206";
    private Connection con=null;
    public void openConn(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(connectionURL, user, password);
            //System.out.println(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            con.close();
        }catch (Exception e){}
    }
    public boolean registerNewUser(String user,String password){
        String sql="INSERT INTO USER_INFO (ID,NAME,PASSWORD)"+
                "VALUES('"+user+"','default','"+password+"');";
        System.out.println(sql);
        System.out.println(con);
        try(Statement stmt=con.createStatement()){
            stmt.executeUpdate(sql);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

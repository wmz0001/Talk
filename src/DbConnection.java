import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
        String query="SELECT ID FROM USER_INFO WHERE ID='"+user+"'";
        String insert="INSERT INTO USER_INFO (ID,PASSWORD)"+
                "VALUES('"+user+"','"+password+"');";
        ResultSet rs=null;
//        System.out.println(sql);
//        System.out.println(con);
        try(Statement stmt=con.createStatement()){
            rs=stmt.executeQuery(query);
            if(rs.next()) {
                System.out.println("exsited!");
                return false;
            }
            stmt.executeUpdate(insert);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean login(String user, String password){
        String sql="SELECT * FROM USER_INFO WHERE ID='"+user+
                "' AND PASSWORD='"+password+"'";
        try(Statement stmt=con.createStatement()){
            ResultSet rs=stmt.executeQuery(sql);
            if(rs.next())return true;
        }catch (Exception e){e.printStackTrace();}
        return false;
    }
}

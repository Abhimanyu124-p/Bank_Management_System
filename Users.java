import java.sql.*;
import java.util.Scanner;

public class Users {
    private Connection con;
    private Scanner sc;
    public Users(Connection con,Scanner sc)
    {
        this.con=con;
        this.sc=sc;
    }
    public String login()
    {
        if(sc.hasNextLine())
            sc.nextLine();
        System.out.println("Enter your gmail: ");
        String mail=sc.nextLine();
        System.out.println("Enter your password: ");
        String pass=sc.nextLine();
        try{
            String query="select * from users where email=? and password=? ;";
            PreparedStatement stmt= con.prepareStatement(query);
            stmt.setString(1,mail);
            stmt.setString(2,pass);
            ResultSet rs=stmt.executeQuery();
            if(rs.next())
            {
                System.out.println("LOGGED IN SUCCESSFULLY...");
                return mail;
            }
            else {
                System.out.println("INVALID CREDENTIALS!!");
                return null;
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public void register()
    {
        if(sc.hasNextLine())
            sc.nextLine();
        System.out.println("Enter your Full Name: ");
        String name=sc.nextLine();
        System.out.println("Enter your Gmail: ");
        String mail=sc.nextLine();
        System.out.println("Enter your password: ");
        String pass=sc.next();
        if(user_exist(mail))
        {
            System.out.println("USER ALREADY EXIST FOR THIS EMAIL ADDRESS!!");
            return;
        }
        String query="Insert into users(full_name,email,password) values(?,?,?);";
        try{
          PreparedStatement stmt=con.prepareStatement(query);
          stmt.setString(1,name);
          stmt.setString(2,mail);
          stmt.setString(3,pass);
          int rowsAffected=stmt.executeUpdate();
          if(rowsAffected>0)
          {
              System.out.println("USER REGISTERED SUCCESSFULLY..");
          }
          else {
              System.out.println("REGISTRATION FAILED!!");
          }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public boolean user_exist(String email)
    {
        if(sc.hasNextLine())
         sc.nextLine();
        try{
            String query="select * from users where email=?;";
            PreparedStatement stmt= con.prepareStatement(query);
            stmt.setString(1,email);
            ResultSet rs=stmt.executeQuery();
            if(rs.next())
            {
                return true;
            }
            else{
                return false;
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static void main(String[] args)
    {

    }
}

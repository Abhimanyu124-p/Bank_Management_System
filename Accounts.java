import java.sql.Connection;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
public class Accounts {
    private Connection con;
    private Scanner sc;
    public Accounts(Connection con,Scanner sc)
    {
        this.con=con;
        this.sc=sc;
    }
    public long openAccount(String email)
    {
        if(AccountExists(email)) {
            throw new RuntimeException("ACCOUNT ALREADY EXISTS!!");
        }
            String query = "insert into accounts(account_number,full_name,email,balance,security_pin) values(?,?,?,?,?);";
            if(sc.hasNextLine())
                sc.nextLine();
            System.out.println("Enter your full name: ");
            String name = sc.nextLine();
            System.out.println("Enter Initial Amount you want to Deposit: ");
            double amt = sc.nextDouble();
            System.out.println("Enter your new account security pin: ____");
            String pin = sc.next();
            try {
                long newAccount_no = generateAccountNumber();
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setLong(1, newAccount_no);
                stmt.setString(2, name);
                stmt.setString(3, email);
                stmt.setDouble(4, amt);
                stmt.setString(5, pin);
                int rowsAffected=stmt.executeUpdate();
                if(rowsAffected>0)
                {
                    System.out.println("ACCOUNT CREATED...");
                    return newAccount_no;
                }
                else {
                    throw new RuntimeException("ACCOUNT CREATION FAILED!!");
                }
            } catch (SQLException e) {
                throw new RuntimeException("DATABASE ERROR DURING ACCOUNT CREATION! "+e.getMessage());
            }
    }
    public long getAccountNumber(String email)
    {
      String query="select account_number from accounts where email= ?;";
      try {
          PreparedStatement stmt = con.prepareStatement(query);
          stmt.setString(1, email);
          ResultSet rs = stmt.executeQuery();
          if (rs.next()) {
              return rs.getLong("account_number");
          }
      }
      catch (SQLException e)
      {
          System.out.println(e.getMessage());
      }
      throw new RuntimeException("ACCOUNT DOES NOT EXIST!!");
    }
    private long generateAccountNumber() {
        try(Statement stmt=con.createStatement()){
            String query = "select account_number from accounts order by account_number desc limit 1;";
            try(ResultSet rs=stmt.executeQuery(query))
            {
                if(rs.next())
                {
                    return rs.getLong("account_number")+1;
                }
                else
                    return 10000100;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return 10000100;
    }
    public boolean AccountExists(String email)
    {
    String query="Select account_number from accounts where email= ?";
    try( PreparedStatement stmt=con.prepareStatement(query)) {
        stmt.setString(1, email);
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }
    catch(SQLException e)
    {
        System.out.println(e.getMessage());
    }
    return false;
    }

}

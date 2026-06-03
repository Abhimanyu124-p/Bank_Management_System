import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
public class AccountManager {
    private Connection con;
    private Scanner sc;
    public AccountManager(Connection con,Scanner sc)
    {
     this.con=con;
     this.sc=sc;
    }
    public void creditMoney(long account_no) throws SQLException
    {
        if(sc.hasNextLine())
          sc.nextLine();
        System.out.println("Enter the amount: ");
        double amt=sc.nextDouble();
        sc.nextLine();
        System.out.println("Enter Security pin: ");
        String pin=sc.next();
        sc.nextLine();
        try{
           con.setAutoCommit(false);
           if(account_no!=0)
             {
               String query="select * from accounts where account_number=? and security_pin=?;";
                   PreparedStatement stmt = con.prepareStatement(query);
                   stmt.setLong(1,account_no);
                   stmt.setString(2,pin);
                   ResultSet rs = stmt.executeQuery();
                   if (rs.next()) {
                     String query2="update accounts set balance=balance+? where account_number=?;";
                     PreparedStatement stmt1= con.prepareStatement(query2);
                     stmt1.setDouble(1,amt);
                     stmt1.setLong(2,account_no);
                     int rowsAffected=stmt1.executeUpdate();
                     if(rowsAffected>0)
                     {
                         System.out.println("Rs. "+amt+" CREDITED SUCCESSFULLY..");
                         con.commit();
                         con.setAutoCommit(true);
                         return;
                     }
                     else{
                         System.out.println("TRANSACTION FAILED!!");
                         con.rollback();
                         con.setAutoCommit(true);
                     }
                   }
                   else{
                       System.out.println("INVALID SECURITY PIN..");
                   }
           }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        con.setAutoCommit(true);
    }
    public void debitMoney(long account_no) throws SQLException
    {
       if(sc.hasNextLine())
         sc.nextLine();
        System.out.println("Enter the amount: ");
        double amt=sc.nextDouble();
        sc.nextLine();
        System.out.println("Enter your Pin: ");
        String pin=sc.next();
        sc.nextLine();
        try {
            con.setAutoCommit(false);
            if (account_no != 0) {
                String query1 = "Select * from accounts where account_number=? and security_pin=? ;";
                PreparedStatement stmt=con.prepareStatement(query1);
                stmt.setLong(1,account_no);
                stmt.setString(2,pin);
                ResultSet rs= stmt.executeQuery();
                if(rs.next())
                {
                  double avl_balance=rs.getDouble("balance");
                  if(avl_balance>=amt)
                  {
                      String query3="update accounts set balance=balance-? where account_number=?;";
                      PreparedStatement stmt2=con.prepareStatement(query3);
                      stmt2.setDouble(1,amt);
                      stmt2.setLong(2,account_no);
                      int rowsAffected=stmt2.executeUpdate();
                      if(rowsAffected>0)
                      {
                          System.out.println("Rs. "+amt+" DEBITED FROM YOUR ACCOUNT..");
                          con.commit();
                          con.setAutoCommit(true);
                          return;
                      }
                      else {
                          System.out.println("TRANSACTION FAILED!!");
                          con.rollback();
                          con.setAutoCommit(true);
                      }
                  }
                  else {
                      System.out.println("INSUFFICIENT BALANCE!!");
                      con.setAutoCommit(true);
                  }
                }
                else{
                    System.out.println("INVALID SECURITY PIN!!");
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        con.setAutoCommit(true);
    }
    public void transferMoney(long sender_account_no) throws SQLException
    {
        if(sc.hasNextLine())
           sc.nextLine();
        System.out.println("Enter Receivers Account No.: ");
        long rec_account=sc.nextLong();
        sc.nextLine();
        try {
            con.setAutoCommit(false);
            String query = "select * from accounts where account_number=?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setLong(1, rec_account);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Enter the amount to be transferred: ");
                double amt = sc.nextDouble();
                sc.nextLine();
                System.out.println("Enter Your Security Pin: XXXX");
                String pin = sc.next();
                sc.nextLine();
                String query1="select * from accounts where account_number=? and security_pin=?;";
                PreparedStatement stmt1= con.prepareStatement(query1);
                stmt1.setLong(1,sender_account_no);
                stmt1.setString(2,pin);
                ResultSet rs1=stmt1.executeQuery();
                if(rs1.next())
                {
                    if(rs1.getDouble("balance")>=amt) {
                        String debit_query = "update accounts set balance=balance-? where account_number=?";
                        String credit_query = "update accounts set balance=balance+? where account_number=?";
                        PreparedStatement debit_stmt = con.prepareStatement(debit_query);
                        PreparedStatement credit_stmt = con.prepareStatement(credit_query);
                        debit_stmt.setLong(2, sender_account_no);
                        debit_stmt.setDouble(1, amt);
                        credit_stmt.setLong(2, rec_account);
                        credit_stmt.setDouble(1, amt);
                        int rowsAffected=debit_stmt.executeUpdate();
                        int rowsAffected1=credit_stmt.executeUpdate();
                        if(rowsAffected1>0 && rowsAffected>0)
                        {
                            System.out.println("Rs. "+amt+" TRANSFERRED SUCCESSFULLY TO ACCOUNT NO.: "+rec_account);
                            con.commit();
                            con.setAutoCommit(true);
                            return;
                        }
                        else {
                            System.out.println("TRANSACTION FAILED!");
                            con.rollback();
                            con.setAutoCommit(true);
                        }

                    }
                    else{
                        System.out.println("INSUFFICIENT BALANCE!");
                        con.setAutoCommit(true);
                    }
                }
                else {
                    System.out.println("INVALID SECURITY PIN!!");
                    con.setAutoCommit(true);
                }
            }
            else{
                System.out.println("RECEIVER'S ACCOUNT DOES NOT EXIST!!");
                con.setAutoCommit(true);
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        con.setAutoCommit(true);
    }
    public void getBalance(long account_no) throws SQLException
    {
        if(sc.hasNextLine())
           sc.nextLine();
        System.out.println("Enter security pin: ");
        String pin=sc.next();
        sc.nextLine();
     try{
         String query="Select balance from accounts where account_number=? and security_pin=?;";
         PreparedStatement stmt= con.prepareStatement(query);
         stmt.setLong(1,account_no);
         stmt.setString(2,pin);
         ResultSet rs= stmt.executeQuery();
         if(rs.next()) {
             double balance = rs.getDouble("balance");
             System.out.println("AVAILABLE BALANCE: Rs." + balance);
         }
         else {
             System.out.println("INVALID PIN!!");
         }
     }
     catch (SQLException e)
     {
         System.out.println(e.getMessage());
     }
    }
}

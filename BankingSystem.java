import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class BankingSystem{
    public static Connection getConnection() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Error: Could not find or load config.properties file!", e);
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed! Check your config.properties details.", e);
        }
    }
    public static void main(String[] args) throws ClassNotFoundException,SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers Loaded Succesfully...");
            Thread.sleep(450);
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Failed to Load Drivers!  "+e.getMessage());
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
        try{
            Connection con=getConnection();
            System.out.println("Connection Established Succesfully...");
            Thread.sleep(450);
            Scanner sc=new Scanner(System.in);
            Users user=new Users(con,sc);
            Accounts accounts=new Accounts(con,sc);
            AccountManager manager=new AccountManager(con,sc);
            String mail;
            long account_no;
            while(true)
            {
                System.out.println();
                System.out.println("---WELCOME TO BANKING SYSTEM---");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login   ");
                System.out.println("3. Exit ");
                System.out.println("Enter your choice: ");
                int choice=sc.nextInt();
                switch(choice)
                {
                    case 1:
                        user.register();
                        Thread.sleep(450);
                        break;
                    case 2:
                        mail=user.login();
                        if(mail!=null)
                        {
                            System.out.println();
                            System.out.println("User Logged in..");
                            Thread.sleep(450);
                            if(!accounts.AccountExists(mail))
                            {
                                System.out.println("1. Create new Account");
                                System.out.println("2. Exit");
                                if(sc.nextInt()==1)
                                {
                                    account_no= accounts.openAccount(mail);
                                    System.out.println("Account created Successfully..");
                                    System.out.println("Your Account No. is: "+account_no);
                                }
                                else{
                                    break;
                                }
                            }
                            else{
                               account_no=accounts.getAccountNumber(mail);
                               int choice1=0;
                               while(choice1!=5)
                               {
                                   System.out.println();
                                   System.out.println("1. Credit Money");
                                   System.out.println("2. Debit Money");
                                   System.out.println("3. Transfer Money");
                                   System.out.println("4. Check Balance");
                                   System.out.println("5. LogOut");
                                   System.out.println("Enter your choice: ");
                                   choice1=sc.nextInt();
                                   Thread.sleep(450);
                                   switch(choice1)
                                   {
                                       case 1:
                                           manager.creditMoney(account_no);
                                           break;
                                       case 2:
                                           manager.debitMoney(account_no);
                                           break;
                                       case 3:
                                           manager.transferMoney(account_no);
                                           break;
                                       case 4:
                                           manager.getBalance(account_no);
                                           break;
                                       case 5:
                                           break;
                                       default:
                                           System.out.println("Enter valid choice!!");
                                           break;
                                   }
                               }

                            }
                        }
                        else{
                            System.out.println("Incorrect Email Or Password!!");
                        }
                        break;
                    case 3:
                        System.out.println("THANKYOU FOR USING OUR BANKING SYSTEM!!!");
                        System.out.print("EXITING SYSTEM");
                        for(int i=0;i<5;i++)
                        {
                            System.out.print(".");
                            Thread.sleep(450);
                        }
                        return;
                    default:
                        System.out.println("Invalid Choice! Please try again...");
                        break;
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

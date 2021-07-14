
package medicinetrackingsystem;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.util.Date;


public class SQLDatabaseConnection {
    private static Object stmt;
     private Connection conn ;
     public SQLDatabaseConnection() throws SQLException, ClassNotFoundException
     {
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

         conn  = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=Medicine Tracking System","Rana","");
            
     }
     public void Submit_medicine(int marker_id ,String medicine_name, int dose_num ){
        
         try{
             /*
         int id= 4; // instead of 3 (markerID)
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the name");
            String medicineName = input.next();
            System.out.println("Enter The doseNumber");
            int DoseNumber = input.nextInt();
            */
            PreparedStatement ps=conn.prepareStatement("insert into Medicine (medicineID,medicineName,doseNumber) values("+marker_id+",'"+medicine_name+"',"+dose_num+")");
            ps.executeUpdate();
            System.out.println("inserted");
         }
         catch (Exception e) {
            e.printStackTrace();
        }
     
    }
     public void generate_report(int marker_id){
         try{
         java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            
            Statement stmt = conn.createStatement();
            String SQL = "SELECT ID,m.medicineID,medicineName, Date, Time\n" +
                         "   FROM Medicine m, DailyDose d  \n" +
                         "   WHERE m.medicineID =d.medicineID and Date LIKE '" + currentDate + "'";
            ResultSet rs = stmt.executeQuery(SQL);
            
            while (rs.next()) {
                
               int Medicine_id = rs.getInt("medicineID");
               
               if(Medicine_id== marker_id){
                System.out.println("you took today " + rs.getString("medicineName")+" At Time: " + rs.getTime("Time"));
            }
            }
         }
         catch (Exception e) {
            e.printStackTrace();
        }
     }
     public void take_pill(int marker_id){
         try{
         java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
             java.util.Date date=new java.util.Date();
             java.sql.Timestamp sqlTime=new java.sql.Timestamp(date.getTime());
             
            
            Statement stmt = conn.createStatement();
            String SQL = "SELECT medicineID, COUNT(Date) AS count\n" +
                          "FROM DailyDose\n" +
                          "WHERE Date LIKE '"+ currentDate + "'" +
                          "GROUP BY medicineID";

            ResultSet rs = stmt.executeQuery(SQL);
             
            while(rs.next()) {
             int medicine_id = rs.getInt("medicineID");
             int date_count = rs.getInt("count");
              
             if(medicine_id== marker_id){ //instead of 2 (markerID)
             System.out.println("medicineID " + medicine_id + " The number of doses you have took is : " + date_count);
             
             Statement st = conn.createStatement();
             String sql = "SELECT doseNumber\n" +
                         "   FROM Medicine\n" +
                         "   WHERE medicineID ="+ medicine_id;
            ResultSet result = st.executeQuery(sql);
            while (result.next()) {
                int dose_number= result.getInt("doseNumber");
                System.out.println("The Dose Number: " +dose_number );
                
                if(date_count < dose_number){ // 2 is the id of the marker sympol
                   
                    PreparedStatement ps=conn.prepareStatement("insert into DailyDose (medicineID,Date,Time) values(2,?,?)");
                    ps.setDate(1,currentDate);
                    ps.setTimestamp(2,sqlTime);
                    ps.executeUpdate();
                    System.out.print("You have taken your dose successfully");

                } else{
                   System.out.println("you have exceeded the number of doses today!!!");
                }
            }
            }
            }
         }
         catch (Exception e) {
            e.printStackTrace();
        }
     }
    
            /*
            // GENERATE REPORT 
            
           java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            
            Statement stmt = conn.createStatement();
            String SQL = "SELECT m.medicineID,medicineName, Date, Time\n" +
                         "   FROM Medicine m, DailyDose d  \n" +
                         "   WHERE m.medicineID =d.medicineID and Date LIKE '" + currentDate + "'";
            ResultSet rs = stmt.executeQuery(SQL);
            
            while (rs.next()) {
                
               int Medicine_id = rs.getInt("medicineID");
               
               if(Medicine_id==2){
                System.out.println("you took today " + rs.getString("medicineName")+" At Time: " + rs.getTime("Time"));
            }
            }
            */
            /*
            // TAKE A PILL
             
             java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
             java.util.Date date=new java.util.Date();
             java.sql.Timestamp sqlTime=new java.sql.Timestamp(date.getTime());
             
            
            Statement stmt = conn.createStatement();
            String SQL = "SELECT medicineID, COUNT(Date) AS count\n" +
                          "FROM DailyDose\n" +
                          "WHERE Date LIKE '"+ currentDate + "'" +
                          "GROUP BY medicineID";

            ResultSet rs = stmt.executeQuery(SQL);
             
            while(rs.next()) {
             int medicine_id = rs.getInt("medicineID");
             int date_count = rs.getInt("count");
              
             if(medicine_id==2){ //instead of 2 (markerID)
             System.out.println("medicineID " + medicine_id + " The number of doses you have took is : " + date_count);
             
             Statement st = conn.createStatement();
             String sql = "SELECT doseNumber\n" +
                         "   FROM Medicine\n" +
                         "   WHERE medicineID ="+ medicine_id;
            ResultSet result = st.executeQuery(sql);
            while (result.next()) {
                int dose_number= result.getInt("doseNumber");
                System.out.println("The Dose Number: " +dose_number );
                
                if(date_count < dose_number){ // 2 is the id of the marker sympol
                   
                    PreparedStatement ps=conn.prepareStatement("insert into DailyDose (medicineID,Date,Time) values(2,?,?)");
                    ps.setDate(1,currentDate);
                    ps.setTimestamp(2,sqlTime);
                    ps.executeUpdate();
                    System.out.print("You have taken your dose successfully");

                } else{
                   System.out.println("you have exceeded the number of doses today!!!");
                }
            }
            }
            }
            */ 
            /*
            //  ADD NEW MEDICINE
            
            int id= 4; // instead of 3 (markerID)
            Scanner input = new Scanner(System.in);
            System.out.println("Enter the name");
            String medicineName = input.next();
            System.out.println("Enter The doseNumber");
            int DoseNumber = input.nextInt();
            
            PreparedStatement ps=conn.prepareStatement("insert into Medicine (medicineID,medicineName,doseNumber) values("+id+",'"+medicineName+"',"+DoseNumber+")");
            ps.executeUpdate();
            System.out.println("inserted");
            
        }
        */
         
    
    
}

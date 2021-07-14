
import static java.lang.Math.abs;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

public class SQLDatabaseConnection {

    private static Object stmt;
    private Connection conn;

    public SQLDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=Medicine Tracking System", "Rana", "");

    }

    public String medicineName(int marker_id) throws SQLException {
        String name = null;
        Statement stmt = conn.createStatement();

        String SQL = "SELECT medicineName\n"
                + "   FROM Medicine where medicineID = " + marker_id;

        ResultSet rs = stmt.executeQuery(SQL);
        if (rs.next()) {
            name = rs.getString("medicineName");
        }

        return name;
    }
    public Time getLastDoseTime(int marker_id) throws SQLException
    {
        Time time = null;
         Statement stmt = conn.createStatement();
            java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        String SQL = "SELECT Time\n"
                + "   FROM DailyDose where medicineID = " + marker_id+
                "and Date = '"+currentDate+"' Order by Time DESC ";
        ResultSet rs = stmt.executeQuery(SQL);
        if(rs.next())
        {
            time = rs.getTime("Time");
        }

        return time;
    }
    public boolean medicineExist(int marker_id) throws SQLException {
        Statement stmt = conn.createStatement();
        String SQL = "SELECT medicineID\n"
                + "   FROM Medicine where medicineID = " + marker_id;

        ResultSet rs = stmt.executeQuery(SQL);
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean Submit_medicine(int marker_id, String medicine_name, int dose_num) {

        try {

            PreparedStatement ps = conn.prepareStatement("insert into Medicine (medicineID,medicineName,doseNumber) values(" + marker_id + ",'" + medicine_name + "'," + dose_num + ")");
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[][] generate_report_all(int marker_id) {
        String [][] data = null;
        int dose_num = 0;
        try {
            Statement stmt = conn.createStatement();
            String SQL = "SELECT count(ID) as c \n"
                    + "   FROM DailyDose\n"
                    + "where medicineID = " + marker_id;

            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {

                dose_num = rs.getInt("c");
            }
            data = new String[dose_num][];
            Statement stmt2 = conn.createStatement();
            String SQL2 = "SELECT ID,m.medicineID,medicineName, Date, Time\n"
                    + "   FROM Medicine m, DailyDose d  \n"
                    + "   WHERE m.medicineID =d.medicineID order by Date ASC , Time ASC";
            ResultSet rs2 = stmt2.executeQuery(SQL2);
            int i = 0;
            while (rs2.next()) {
                String[]adapt = new String[3]; 

                int Medicine_id = rs2.getInt("medicineID");

                if (Medicine_id == marker_id) {
                    
                    adapt[0]= rs2.getString("medicineName");
                    adapt[1] = addDays(rs2.getDate("Date"),2);                    
                    adapt[2] = String.valueOf(rs2.getTime("Time"));
                    data[i] = adapt;
                    i++;
                    System.out.println("Medicine Name" + rs2.getString("medicineName") + "On day: " + rs2.getDate("Date") + " At Time: " + rs2.getTime("Time"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        
        Date newDate = new Date(c.getTimeInMillis());
        
        DateFormat Date = DateFormat.getDateInstance();
        String currentDate = Date.format(c.getTimeInMillis());
        return currentDate;
    }

    public void generate_report(int marker_id) {
        try {
            java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

            Statement stmt = conn.createStatement();
            String SQL = "SELECT ID,m.medicineID,medicineName, Date, Time\n"
                    + "   FROM Medicine m, DailyDose d  \n"
                    + "   WHERE m.medicineID =d.medicineID and Date LIKE '" + currentDate + "'";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {

                int Medicine_id = rs.getInt("medicineID");

                if (Medicine_id == marker_id) {
                    System.out.println("you took today " + rs.getString("medicineName") + " At Time: " + rs.getTime("Time"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int taken_doses(int marker_id) {
        int count = 0;
        try {
            java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

            Statement stmt = conn.createStatement();
            String SQL = "SELECT COUNT(Date) as c,m.medicineID,Date\n"
                    + "   FROM Medicine m ,DailyDose d\n"
                    + "   WHERE m.medicineID = d.medicineID and Date LIKE '" + currentDate + "'"
                    + "GROUP BY m.medicineID, d.Date";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {

                int Medicine_id = rs.getInt("medicineID");

                if (Medicine_id == marker_id) {
                    System.out.println("The Number Of Doses: " + rs.getInt("c"));
                    count = rs.getInt("c");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int remainig_doses(int marker_id) {
        int dose_num = 0;

        try {
            java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

            Statement stmt = conn.createStatement();
            String SQL = "SELECT doseNumber \n"
                    + "   FROM Medicine\n"
                    + "where medicineID = " + marker_id;

            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {

                dose_num = rs.getInt("doseNumber");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Dose numer of medicine table " + dose_num);
        System.out.println("Remaning from method taken doses" + taken_doses(marker_id));

        return (dose_num - taken_doses(marker_id));
    }

    public boolean take_pill(int marker_id) {
        boolean excess = false;

        try {
            java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());

            Statement stmt = conn.createStatement();
            String SQL = "SELECT medicineID, COUNT(Date) AS count\n"
                    + "FROM DailyDose\n"
                    + "WHERE Date LIKE '" + currentDate + "'"
                    + "GROUP BY medicineID";

            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                int medicine_id = rs.getInt("medicineID");
                int date_count = rs.getInt("count");

                if (medicine_id == marker_id) { //instead of 2 (markerID)
                    System.out.println("medicineID " + medicine_id + " The number of doses you have took is : " + date_count);

                    Statement st = conn.createStatement();
                    String sql = "SELECT doseNumber\n"
                            + "   FROM Medicine\n"
                            + "   WHERE medicineID =" + medicine_id;
                    ResultSet result = st.executeQuery(sql);
                    while (result.next()) {
                        int dose_number = result.getInt("doseNumber");
                        System.out.println("The Dose Number: " + dose_number);

                        if (date_count < dose_number) { // 2 is the id of the marker sympol

                            PreparedStatement ps = conn.prepareStatement("insert into DailyDose (medicineID,Date,Time) values(" + marker_id + ",?,?)");
                            ps.setDate(1, currentDate);
                            ps.setTimestamp(2, sqlTime);
                            ps.executeUpdate();
                            System.out.print("You have taken your dose successfully");
                            excess = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excess;
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

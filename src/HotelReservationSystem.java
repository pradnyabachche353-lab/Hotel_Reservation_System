import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static String url="jdbc:mysql://localhost:3306/hotel_db";
    private static String username="username";
    private static String password="password";

    public static void main(String[] args) throws Exception{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Scanner sc=new Scanner(System.in);

            while(true){
                System.out.println();
                System.out.println("Hotel Management System");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Enter your choice");
                int choice = sc.nextInt();

                switch(choice){
                    case 1:
                        reserveRoom(connection,sc);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomnumber(connection,sc);
                        break;
                    case 4:
                        updateReservation(connection,sc);
                        break;
                    case 5:
                        deleteReservation(connection,sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private static void reserveRoom(Connection connection,Scanner sc){
        try{
            sc.nextLine();
            System.out.print("Enter Name : ");
            String name=sc.nextLine();
            System.out.print("Enter Room Number : ");
            int roomNumber=sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Contact Number : ");
            String contactNumber=sc.nextLine();

            String query="INSERT INTO RESERVATIONS (guest_name,room_number,contact_number) VALUES(?,?,?);";

            try{

                PreparedStatement statement=connection.prepareStatement(query);
                statement.setString(1,name);
                statement.setInt(2,roomNumber);
                statement.setString(3,contactNumber);

                int rows=statement.executeUpdate();
                if(rows>0){
                    System.out.println("Reservation Successfully");
                }else{
                    System.out.println("Reservation Failed!");
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void viewReservation(Connection connection){
        try{
            String query="SELECT * FROM RESERVATIONS";
            PreparedStatement statement=connection.prepareStatement(query);
            ResultSet rs=statement.executeQuery();

            System.out.println();
            System.out.println("================================================================================");
            System.out.printf("%-15s %-20s %-15s %-20s %-25s%n",
                    "Reservation ID",
                    "Guest Name",
                    "Room Number",
                    "Contact Number",
                    "Reservation Date");
            System.out.println("================================================================================");
            while(rs.next()){
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                Timestamp reservationDate = rs.getTimestamp("reservation_date");

                System.out.printf("%-15d %-20s %-15d %-20s %-25s%n",
                        reservationId,
                        guestName,
                        roomNumber,
                        contactNumber,
                        reservationDate);
            }

            System.out.println("================================================================================");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void getRoomnumber(Connection connection,Scanner sc){
        try{
            String query="SELECT room_number FROM RESERVATIONS WHERE reservation_id=? AND guest_name=?";

            System.out.println("Enter Reservation ID : ");
            int reservationId=sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Guest Name : ");
            String guestName=sc.nextLine();

            PreparedStatement statement=connection.prepareStatement(query);
            statement.setInt(1,reservationId);
            statement.setString(2,guestName);
            ResultSet rs=statement.executeQuery();

            if(rs.next()){
                int room_Number=rs.getInt("room_number");
                System.out.println("Room number for reservation ID : "+reservationId +" and Guest Name : "+guestName+ " is "+room_Number);
            }else{
                System.out.println("Room Number not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void updateReservation(Connection connection,Scanner sc){
        try{
            System.out.println("Enter the reservation ID to be updated : ");
            int reservationId=sc.nextInt();
            String query="UPDATE reservations SET room_number=?,guest_name=?,contact_number=? WHERE reservation_id=?";

            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation Not Found");
                return;
            }

            System.out.println("Enter the new room number : ");
            int roomNumber=sc.nextInt();
            sc.nextLine();
            System.out.println("Enter the new guest name : ");
            String newGuest=sc.nextLine();
            System.out.println("Enter the new contact number : ");
            String newContactNumber=sc.nextLine();

            PreparedStatement statement=connection.prepareStatement(query);
            statement.setInt(1, roomNumber);
            statement.setString(2, newGuest);
            statement.setString(3, newContactNumber);
            statement.setInt(4, reservationId);

            int rows=statement.executeUpdate();
            if(rows>0){
                System.out.println("Reservation Updated Successfully!");
            }else{
                System.out.println("Reservation Update Failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void deleteReservation(Connection connection,Scanner sc){
        try{
            String query="DELETE FROM reservations WHERE reservation_id=?";

            System.out.println("Enter Reservation ID : ");
            int reservationId=sc.nextInt();

            PreparedStatement statement=connection.prepareStatement(query);
            statement.setInt(1, reservationId);
            int rows=statement.executeUpdate();
            if(rows>0){
                System.out.println("Reservation Deleted Successfully!");
            }else{
                System.out.println("Reservation Delete Failed!");
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    private static boolean reservationExists(Connection connection,int reservationId){
        try{
            String query = "Select reservation_id from reservations WHERE reservation_id=?";

            PreparedStatement statement=connection.prepareStatement(query);
            statement.setInt(1,reservationId);

            ResultSet rs=statement.executeQuery();
            return rs.next();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using Hotel Reservation System");
    }
}

package DAO;

import GUI.DKFORM;
import GUI.DNFORM;
import Model.BeanDTO;
import Model.Room;
import Model.Users;
import Utils.ConnectJDBC;

import javax.swing.*;
import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Utils.Set.setRoom;
import static Utils.Set.setUsers;

public class UserDAO {
    public static void insert(Users user){
        Connection conn= ConnectJDBC.getConnection();
        String sql="Insert into Users(Username,Password,Role,FullName,Email,Phone) Values(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getRole());
            ps.setString(4,user.getFullname());
            ps.setString(5,user.getEmail());
            ps.setString(6,user.getPhone());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Đã Tạo Tài Khoản !");
            conn.close();
        } catch (SQLException ex) {
            // Lỗi trùng khóa (SQL Server)
            String msg = ex.getMessage();

            if (msg.contains("Username")) {
                JOptionPane.showMessageDialog(null, "Tên đăng nhập đã tồn tại!");
            } else if (msg.contains("Email")) {
                JOptionPane.showMessageDialog(null, "Email đã được sử dụng!");
            } else {
                JOptionPane.showMessageDialog(null, "Dữ liệu bị trùng!");
            }
        }

    }
    public static Users dNhap(String Username,String Password) {
        String sql =
                "SELECT * FROM Users \n" +
                        "WHERE Username = ? AND Password = ? ";


        Connection conn = ConnectJDBC.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, Username);
            ps.setString(2, Password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công");
            Users user=setUsers(rs);
                if(user.getStatus().equals("0")) {
                    JOptionPane.showMessageDialog(null,"Tài khoản của bạn đã bị khóa !");
                    return null;
                }
                conn.close();
                    return user;
            }
            else {
                JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }


    public static List<Room> selectRoom(BeanDTO bean){
        Connection conn=ConnectJDBC.getConnection();
        StringBuilder sql=new StringBuilder( "Select r.* from Room r Where 1=1 ");
        List<Object> params = new ArrayList<>();
        if(bean!=null){

            if(!bean.getRoomType().equals("Tất cả")) {
                sql.append(" AND r.RoomType COLLATE Latin1_General_CI_AI LIKE ? ");
                params.add("%" + bean.getRoomType() + "%");
            };
            if (bean.getDateNhan() != null || bean.getDateTra() != null) {
                sql.append(
                        " AND NOT EXISTS ( " +
                                "   SELECT 1 FROM Status s " +
                                "   JOIN RoomBooking rb ON rb.BookingID = s.BookingID " +
                                "   WHERE s.RoomID = r.RoomID " +
                                "     AND ? < DATEADD(day, rb.TotalDays, rb.BookingDate) " +
                                "     AND ? > rb.BookingDate " +
                                "   AND s.Status = N'Đã Đặt' "+
                                " )"
                );
                if (bean.getDateNhan() == null) {
                    bean.setDateNhan(
                            bean.getDateTra().minusDays(bean.getSoNgay())
                    );
                }

                if (bean.getDateTra() == null) {
                    bean.setDateTra(
                            bean.getDateNhan().plusDays(bean.getSoNgay())
                    );
                }
                    params.add(java.sql.Date.valueOf(bean.getDateNhan()));
                    params.add(java.sql.Date.valueOf(bean.getDateTra()));
            }




        }

        List<Room>res=new ArrayList<>();

        try {
            PreparedStatement ps=conn.prepareStatement(sql.toString());
            //gán giá trị vào
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                Room room=setRoom(rs);
                res.add(room);
            }
            conn.close();
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void booking(String userId, Room room, BeanDTO sub) {
        Connection conn = null;
        String bookingID=generateMaDon();
        try {
            conn = ConnectJDBC.getConnection();
            conn.setAutoCommit(false);

            String sql1 = "INSERT INTO RoomBooking (BookingID, UserID, BookingDate, RoomType, TotalDays) VALUES (?,?,?,?,?)";

            Statement stm= conn.createStatement();
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, bookingID);
            ps1.setString(2, userId);
            ps1.setDate(3, java.sql.Date.valueOf(sub.getDateNhan()));
            ps1.setString(4, room.getRoomType());
            ps1.setInt(5, sub.getSoNgay());
            ps1.executeUpdate();

            String sql2 = "INSERT INTO Status (BookingID, RoomID, Status) VALUES (?,?,?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, bookingID);
            ps2.setString(2, room.getRoomId());
            ps2.setString(3, "Đã đặt");
            ps2.executeUpdate();

            conn.commit(); //  CHỈ khi không lỗi mới commit

            JOptionPane.showMessageDialog(null, "Đặt phòng thành công !");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "Đặt phòng thất bại !");
            e.printStackTrace();
            return;
        } finally {
            ConnectJDBC.closeConnection(conn);
        }
    }

    public static List<Users> selectUsers(){
        Connection conn=ConnectJDBC.getConnection();
        String sql="Select * from Users Where 1=1 ";

        try {
            PreparedStatement ps=conn.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            List<Users>listUser=new ArrayList<>();
            while (rs.next()) {
                Users user=setUsers(rs);

               listUser.add(user);
            }
            return listUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateMaDon() {
        // Lấy ngày hiện tại và format thành 4 ký tự: MMdd
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
        String datePart = today.format(formatter);

        // Tạo 4 ký tự số ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 0 - 9999
        String randomPart = String.format("%04d", randomNumber); // đảm bảo 4 chữ số

        // Kết hợp thành mã đơn 8 ký tự
        return datePart + randomPart;
    }

}

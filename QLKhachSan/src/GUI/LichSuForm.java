package GUI;

import Model.Users;
import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class LichSuForm extends JFrame{
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel mainPanel;
    private JButton btnHuy;
    private DefaultTableModel model;

    public LichSuForm(Users user) {
        setTitle("Lịch Sử Đặt Phòng");

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setSize(700, 400);

        model = new DefaultTableModel(
                new String[]{"BookingID","Loại Phòng","Số phòng","Ngày Nhận","Ngày Trả","Số ngày","Trạng Thái"}, 0
        );
        table.setModel(model);

        loadData(user.getUserId());
        btnHuy.addActionListener(e -> huy(user.getUserId()));
    }


    private void createUIComponents() {
        table = new JTable();   // chỉ tạo component
    }


    public void loadData(String userID){

        model.setRowCount(0);
        String sql = """
        SELECT rb.BookingID, rb.RoomType, r.RoomNumber,
               rb.BookingDate,
               DATEADD(DAY, rb.TotalDays, rb.BookingDate) AS CheckOutDate,
               rb.TotalDays,
               s.Status
        FROM RoomBooking rb
        JOIN Status s ON rb.BookingID = s.BookingID
        JOIN Room r ON s.RoomID = r.RoomID
        WHERE rb.UserID = ?
        """;

        try (Connection conn = ConnectJDBC.getConnection();
             PreparedStatement ps= conn.prepareStatement(sql);)
             {
             ps.setString(1,userID);
                 ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getDate(5),
                        rs.getInt(6),
                        rs.getString(7)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void huy(String userId){
        Connection conn= ConnectJDBC.getConnection();
        int sl=table.getSelectedRow();
        String sql= "UPDATE s\n" +
                "SET s.Status = N'Đã hủy'\n" +
                "FROM Status s\n" +
                "JOIN RoomBooking rb ON s.BookingID = rb.BookingID\n" +
                "WHERE rb.BookingDate >= CAST(GETDATE() AS DATE)\n" +
                "  AND s.BookingID =  '"+table.getValueAt(sl,0)+"'";
        try {
            Statement stm= conn.createStatement();
            stm.executeUpdate(sql);
            JOptionPane.showMessageDialog(null,"Đã hủy thành công !");
            loadData(userId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

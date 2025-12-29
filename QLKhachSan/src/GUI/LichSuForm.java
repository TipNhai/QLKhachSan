package GUI;

import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LichSuForm extends JFrame{
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel mainPanel;
    private JButton btnHuy;
    private DefaultTableModel model;

    public LichSuForm() {
        setTitle("Lịch Sử Đặt Phòng");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setSize(700, 400);

        model = new DefaultTableModel(
                new String[]{"BookingID","Loại Phòng","Số phòng","Ngày Nhận","Ngày Trả","Số ngày","Trạng Thái"}, 0
        );
        table.setModel(model);

        loadData();
        btnHuy.addActionListener(e -> huy());
    }


    private void createUIComponents() {
        table = new JTable();   // chỉ tạo component
    }


    public void loadData(){

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
        WHERE rb.UserID = 11
        """;

        try (Connection conn = ConnectJDBC.getConnection();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

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
    public void huy(){
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
            loadData();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
            new LichSuForm().setVisible(true);
    }
}

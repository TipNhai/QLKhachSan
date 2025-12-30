package GUI;

import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RevenueGUI extends JFrame {

    JTable table;
    DefaultTableModel model;
    JLabel lblTotal;

    public RevenueGUI() {
        setTitle("Thống kê doanh thu");
        setSize(900, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("THỐNG KÊ DOANH THU", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{
                "Booking ID", "Khách hàng", "Phòng",
                "Ngày nhận", "Ngày trả", "Tổng tiền"
        }, 0);

        table = new JTable(model);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel bottom = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        bottom.add(lblTotal, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        loadRevenue();
    }
    private void loadRevenue() {
        model.setRowCount(0);
        double totalRevenue = 0;

        String sql = """
            SELECT rb.BookingID, u.FullName, r.RoomType,
                   rb.BookingDate, 
                   DATEADD(DAY, rb.TotalDays, rb.BookingDate) AS CheckoutDate,
                   (r.[Price/Date] * rb.TotalDays) AS TotalPrice
            FROM RoomBooking rb
            JOIN Users u ON rb.UserID = u.UserID
            JOIN Status s ON rb.BookingID = s.BookingID
            JOIN Room r ON s.RoomID = r.RoomID
            WHERE s.Status = N'Đã thanh toán'
            ORDER BY rb.BookingDate DESC
        """;

        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double price = rs.getInt("TotalPrice");
                totalRevenue += price;

                model.addRow(new Object[]{
                        rs.getInt("BookingID"),
                        rs.getString("FullName"),
                        rs.getString("RoomType"),
                        rs.getDate("BookingDate"),
                        rs.getDate("CheckoutDate"),
                        price
                });
            }

            lblTotal.setText("Tổng doanh thu: " + String.format("%,.0f VNĐ", totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



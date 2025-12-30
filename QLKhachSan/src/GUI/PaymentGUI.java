package GUI;

import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


    public class PaymentGUI extends JDialog  {

        JTable table;
        DefaultTableModel model;
        JButton btnPay;

        public PaymentGUI(JFrame parent,String bookingID) {

            super(parent, "Thanh toán", true); // true = modal
            setSize(900, 400);
            setLocationRelativeTo(parent);

            setLayout(new BorderLayout(10, 10));

            // ===== HEADER =====
            JLabel lblTitle = new JLabel("DANH SÁCH THANH TOÁN", JLabel.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblTitle.setForeground(new Color(33, 150, 243));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            add(lblTitle, BorderLayout.NORTH);

            // ===== TABLE =====
            model = new DefaultTableModel(
                    new String[]{"Booking ID", "Khách hàng", "Phòng", "Giá/Ngày", "Số ngày", "Tổng tiền"}, 0
            ) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            table = new JTable(model);
            table.setRowHeight(28);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setSelectionBackground(new Color(220, 235, 252));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(33, 150, 243));
            table.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            // ===== BUTTON PANEL =====
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            btnPay = new JButton("Thanh Toán");
            btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnPay.setBackground(new Color(76, 175, 80));
            btnPay.setForeground(Color.WHITE);
            btnPay.setFocusPainted(false);
            btnPay.setPreferredSize(new Dimension(140, 40));

            btnPay.addActionListener(e -> thanhToan());

            bottomPanel.add(btnPay);

            add(scroll, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            loadData(bookingID);
        }
        private void loadData(String bookingID) {
            model.setRowCount(0);
            String sql = """
        SELECT rb.BookingID, u.FullName, r.RoomType,
                                                        r.[Price/Date],
                                                        rb.TotalDays,
                                                        (r.[Price/Date] * rb.TotalDays) AS TotalPrice
                                                 FROM RoomBooking rb
                                                 JOIN [Users] u ON rb.UserID = u.UserID
                                                 JOIN Status s ON rb.BookingID = s.BookingID
                                                 JOIN Room r ON s.RoomID = r.RoomID
                                                 WHERE rb.BookingID  = ? AND s.Status=N'Đã đặt'  
                                                 AND DATEADD(DAY, rb.TotalDays, rb.BookingDate) = CAST(GETDATE() AS DATE);
    """;

            try (Connection con = ConnectJDBC.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql);)
                  {
                 ps.setString(1,bookingID);
                      ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("BookingID"),
                            rs.getString("FullName"),
                            rs.getString("RoomType"),
                            rs.getInt("Price/Date"),
                            rs.getInt("TotalDays"),
                            rs.getDouble("TotalPrice")
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void thanhToan() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn!");
                return;
            }

            String bookingID =(String) model.getValueAt(row, 0);

            String sql = "UPDATE Status SET Status = 'Đã thanh toán' WHERE BookingID = ?";

            try (Connection con = ConnectJDBC.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, bookingID);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                loadData(bookingID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

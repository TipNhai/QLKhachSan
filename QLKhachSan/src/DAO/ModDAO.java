package DAO;

import Model.Room;
import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

public class ModDAO {
    public static void loadBookingTable(DefaultTableModel modelBooking) {
        modelBooking.setRowCount(0); // clear bảng


        String sql = """
        SELECT rb.BookingID, u.FullName,r.RoomNumber,  r.RoomType,
              rb.BookingDate, DATEADD(day, rb.TotalDays, rb.BookingDate) AS CheckoutDate,
               s.Status
        FROM RoomBooking rb
        Inner Join Users u on u.UserID=rb.UserID
        Inner Join Status s on s.BookingID=rb.BookingID
        Inner Join Room r on r.RoomID=s.RoomID
        where 1=1 
    """;

        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelBooking.addRow(new Object[]{
                        rs.getString("BookingID"),
                        rs.getString("FullName"),
                        rs.getString("RoomNumber"),
                        rs.getString("RoomType"),
                        rs.getDate("BookingDate"),
                        rs.getDate("CheckoutDate"),
                        rs.getString("Status")
                });
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadData(List<Room> listRoom, DefaultTableModel modelRoom){
        modelRoom.setRowCount(0);
        for(Room r:listRoom){

            Object[] row={
                    r.getRoomId(), r.getRoomNumber(),r.getRoomType(),r.getPrice()
            };
            modelRoom.addRow(row);
        }



    }

    public static void handleCheckIn(JTable tblBooking,DefaultTableModel modelBooking) {
        int row = tblBooking.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một booking!");
            return;
        }
        Object dateObj = tblBooking.getValueAt(row, 4);
        LocalDate bookingDate;

        if (dateObj instanceof java.sql.Date) {
            bookingDate = ((java.sql.Date) dateObj).toLocalDate();
        } else {
            bookingDate = LocalDate.parse(dateObj.toString());
        }

// Lấy ngày hôm nay
        LocalDate today = LocalDate.now();

// So sánh
        if (!bookingDate.isEqual(today)) {
            JOptionPane.showMessageDialog(null,
                    "Chỉ được thao tác với booking trong ngày hôm nay!");
            return;
        }

        String bookingId = tblBooking.getValueAt(row, 0).toString(); // BookingID dạng String

        String sql = """
        UPDATE RoomBooking
        SET CheckInTime = GETDATE()
          
        WHERE BookingID = ?
    """;

        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Check-in thành công!");
            loadBookingTable(modelBooking);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi check-in!");
        }
    }


    public static void handleCheckOut(JTable tblBooking,DefaultTableModel modelBooking) {
        int row = tblBooking.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một booking!");
            return;
        }
        Object dateObj = tblBooking.getValueAt(row, 5);
        LocalDate dateCheckOut;

        if (dateObj instanceof java.sql.Date) {
            dateCheckOut = ((java.sql.Date) dateObj).toLocalDate();
        } else {
            dateCheckOut = LocalDate.parse(dateObj.toString());
        }

// Lấy ngày hôm nay
        LocalDate today = LocalDate.now();

// So sánh
        if (!dateCheckOut.isEqual(today)) {
            JOptionPane.showMessageDialog(null,
                    "Chỉ được thao tác với booking trong ngày hôm nay!");
            return;
        }
        String bookingId = tblBooking.getValueAt(row, 0).toString();

        String sql = """
        UPDATE RoomBooking
        SET CheckOutTime = GETDATE()
            
        WHERE BookingID = ?
    """;

        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Check-out thành công!");
            loadBookingTable(modelBooking);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi check-out!");
        }
    }

    public static void filterByStatus(JComboBox<String>  cboStatus, TableRowSorter<DefaultTableModel> sorter) {
        String status = cboStatus.getSelectedItem().toString();
        LocalDate today = LocalDate.now();

        if (status.equals("Tất cả")) {
            sorter.setRowFilter(null);
            return;
        }

        RowFilter<Object, Object> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<?, ?> entry) {

                try {
                    // Cột 4: Ngày nhận | Cột 5: Ngày trả
                    LocalDate checkIn  = LocalDate.parse(entry.getStringValue(4));
                    LocalDate checkOut = LocalDate.parse(entry.getStringValue(5));

                    if (status.equals("Check-in hôm nay")) {
                        return checkIn.equals(today);
                    }

                    if (status.equals("Check-out hôm nay")) {
                        return checkOut.equals(today);
                    }

                } catch (Exception e) {
                    return false;
                }

                return true;
            }
        };

        sorter.setRowFilter(rf);
    }

}

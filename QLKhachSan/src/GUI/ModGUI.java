package GUI;

import DAO.UserDAO;
import Model.BeanDTO;
import Model.Room;
import Model.Users;
import Utils.Change;
import Utils.Check;
import Utils.ConnectJDBC;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static Utils.Change.changeTypeDate;

public class ModGUI extends JFrame {

    // Components
    private JTable tblRoom, tblBooking;
    private JTextField txtSearch, txtBookingID, txtCustomer;
    private JComboBox<String> cboRoomType, cboStatus,cboType;
    private JSpinner spRoomNumber;
    private JButton btnSearch, btnAdd, btnUpdate, btnCancel,btnCheckIn,btnCheckOut,btnLogout;
    private JDateChooser dateNhan,dateTra;
    private BeanDTO sub;
    private  DefaultTableModel modelRoom,modelBooking;
    private Integer soNgay;
    private TableRowSorter<DefaultTableModel> sorter;
    private Timer searchTimer;
    private boolean isUpdating = false;

    public ModGUI(Users user) {
        setTitle("MOD - Quản Lý Phòng Khách Sạn");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        initHeader();
        initCenter();
        initBottom();
        btnUpdate.setFocusPainted(false);
        btnAdd.setFocusPainted(false);
        btnSearch.setFocusPainted(false);
        btnCancel.setFocusPainted(false);
        btnCheckIn.setFocusPainted(false);
        btnLogout.setFocusPainted(false);
        loadData(UserDAO.selectRoom(null));
        loadBookingTable();
        setupSearch();
        //ActionListener
        btnSearch.addActionListener(e->loadData(UserDAO.selectRoom(search())));

        btnAdd.addActionListener(e->UserDAO.booking("10",subcribe(),sub));

        tblBooking.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isUpdating) return;

            int viewRow = tblBooking.getSelectedRow();
            if (viewRow == -1) return;

            int modelRow = tblBooking.convertRowIndexToModel(viewRow);

            isUpdating = true;   //  khóa tạm

            SwingUtilities.invokeLater(() -> {
                txtBookingID.setText(modelBooking.getValueAt(modelRow, 0).toString());
                txtCustomer.setText(modelBooking.getValueAt(modelRow, 1).toString());
                spRoomNumber.setValue(Integer.parseInt(modelBooking.getValueAt(modelRow, 2).toString()));
                cboType.setSelectedItem(modelBooking.getValueAt(modelRow, 3).toString());

                isUpdating = false; //  mở khóa
            });
        });


        cboStatus.addActionListener(e -> filterByStatus());



        btnCancel.addActionListener(e -> {
            txtBookingID.setText("");
            txtCustomer.setText("");
            spRoomNumber.setValue(1);
            cboType.setSelectedIndex(0);
            tblBooking.clearSelection();
        });

        btnUpdate.addActionListener(e ->update());

        btnCheckIn.addActionListener(e->handleCheckIn());
        btnCheckOut.addActionListener(e->handleCheckOut());
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // đóng cửa sổ hiện tại
                new UserGUI(null).setVisible(true); // mở lại màn hình đăng nhập
            }
        });
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        };

        txtCustomer.getDocument().addDocumentListener(dl);
        txtBookingID.getDocument().addDocumentListener(dl);

    }



    // ================= HEADER =================
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TIÊU ĐỀ =====
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHÁCH SẠN - MOD", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        // ===== THÔNG TIN USER =====
        JLabel lblUser = new JLabel("Xin chào: MOD01");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));

        // ===== NÚT ĐĂNG XUẤT =====
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(new Color(220, 53, 69)); // đỏ nhẹ
        btnLogout.setForeground(Color.WHITE);



        // ===== PANEL PHẢI =====
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(lblUser);
        rightPanel.add(btnLogout);

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ================= CENTER =================
    private void initCenter() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // LEFT: ROOM MANAGEMENT
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Quản lý phòng"));

        JPanel filterPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        filterPanel.add(new JLabel("Loại phòng:"));
        cboRoomType = new JComboBox<>(new String[]{"Tất cả", "Phòng đơn", "Phòng đôi", "VIP"});
        filterPanel.add(cboRoomType);

        filterPanel.add(new JLabel("Ngày nhận:"));
        dateNhan = new JDateChooser();
        dateNhan.setDateFormatString("yyyy-MM-dd");
        filterPanel.add(dateNhan);

        filterPanel.add(new JLabel("Ngày trả:"));
        dateTra = new JDateChooser();
        dateTra.setDateFormatString("yyyy-MM-dd");
        filterPanel.add(dateTra);
        dateNhan.setMinSelectableDate(new Date());
        dateTra.setMinSelectableDate(new Date());
        btnSearch = new JButton("Tìm phòng");
        filterPanel.add(btnSearch);

        leftPanel.add(filterPanel, BorderLayout.NORTH);
        modelRoom=new DefaultTableModel(
                new Object[]{"ID", "Số phòng", "Loại", "Giá/Ngày"}, 0);
        tblRoom = new JTable(modelRoom);
        leftPanel.add(new JScrollPane(tblRoom), BorderLayout.CENTER);

        // ===== RIGHT: CHECK-IN / CHECK-OUT MANAGEMENT =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Quản lý Check-in / Check-out"));

// ===== FORM =====
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));

        form.add(new JLabel("Mã Booking:"));
        txtBookingID = new JTextField();
        form.add(txtBookingID);

        form.add(new JLabel("Tên khách hàng:"));
        txtCustomer = new JTextField();
        form.add(txtCustomer);

        form.add(new JLabel("Ngày hôm nay:"));
        JTextField txtToday = new JTextField(LocalDate.now().toString());
        txtToday.setEnabled(false);
        form.add(txtToday);

        form.add(new JLabel("Trạng thái:"));
         cboStatus = new JComboBox<>(
                new String[]{"Tất cả", "Check-in hôm nay", "Check-out hôm nay"}
        );
        form.add(cboStatus);

        form.add(new JLabel("Số phòng:"));
        spRoomNumber = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        form.add(spRoomNumber);

        form.add(new JLabel("Loại phòng:"));
        cboType = new JComboBox<>(new String[]{"Phòng đơn", "Phòng đôi", "Phòng VIP","Phòng gia đình"});
        form.add(cboType);

        rightPanel.add(form, BorderLayout.NORTH);

// ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAdd = new JButton("Tạo đơn");          // GIỮ NGUYÊN
        btnUpdate = new JButton("Cập nhật");
        btnCancel = new JButton("Làm mới");
        btnCheckIn =new JButton("Check In");
        btnCheckOut = new JButton("Check Out");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnCancel);
        btnPanel.add(btnCheckIn);
        btnPanel.add(btnCheckOut);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);


        String[] columnNames = {
                "Booking ID", "Khách hàng", "Số phòng", "Loại phòng",
                "Ngày nhận", "Ngày trả", "Trạng thái"
        };

         modelBooking = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // không cho sửa trực tiếp trên bảng
            }
        };

        // Tạo table
        tblBooking = new JTable(modelBooking);
        tblBooking.setRowHeight(28);
        tblBooking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// Tạo sorter SAU KHI đã có model
        modelBooking = (DefaultTableModel) tblBooking.getModel();
        sorter = new TableRowSorter<>(modelBooking);
        tblBooking.setRowSorter(sorter);

// Scroll
        JScrollPane scrollBooking = new JScrollPane(tblBooking);
        scrollBooking.setPreferredSize(new Dimension(500, 200));


        // ---------- TABLE ----------
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách đặt phòng"));
        tablePanel.add(scrollBooking, BorderLayout.CENTER);


// Gộp button + table vào CENTER
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(btnPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        rightPanel.add(centerPanel, BorderLayout.CENTER);


// ===== ADD TO MAIN =====
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);



    }

    // ================= FOOTER =================
    private void initBottom() {
        JLabel footer = new JLabel("© 2025 Hotel Management System", JLabel.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(footer, BorderLayout.SOUTH);
    }

    //Function

    public BeanDTO search() {
        BeanDTO beanTim = new BeanDTO();

        // Lấy loại phòng
        beanTim.setRoomType(Check.getValueOrNull(cboRoomType.getSelectedItem().toString()));

        // Kiểm tra ngày có được chọn chưa
        if (dateNhan.getDate() == null || dateTra.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đủ ngày!");
            return null;
        }

        // Chuyển sang LocalDate
        LocalDate ldateNhan = changeTypeDate(dateNhan);
        LocalDate ldateTra = changeTypeDate(dateTra);

        // Kiểm tra logic ngày
        if (ldateTra.isBefore(ldateNhan)) {
            JOptionPane.showMessageDialog(null, "Ngày trả phải lớn hơn ngày nhận!");
            return null;
        }

        // Tính số ngày
        soNgay = (int) ChronoUnit.DAYS.between(ldateNhan, ldateTra);

        // Gán vào bean
        beanTim.setDateNhan(ldateNhan);
        beanTim.setDateTra(ldateTra);
        beanTim.setSoNgay(soNgay);

        return beanTim;
    }

    public Room subcribe(){
        int selectedRow = tblRoom.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần đặt !");
            return null;
        }
        if (dateNhan.getDate() == null || dateTra.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng tìm và nhập đủ ngày!");
            return null;
        }
        sub=new BeanDTO();
        sub.setDateNhan(changeTypeDate(dateNhan));
        sub.setDateTra(changeTypeDate(dateTra));
        sub.setSoNgay(soNgay);
        Room res = null;
        Object value = tblRoom.getValueAt(selectedRow, 1); // giá trị trong JTable
        if(value != null) {
            String roomNum = value.toString().trim();
            List<Room>listRoom=UserDAO.selectRoom(null);
            for (Room r : listRoom) {
                if (r.getRoomNumber().toString().trim().equals(roomNum)) {
                    res = r;
                    break;
                }
            }
        }

        return res;
    }

    public void loadData(List<Room> listRoom){
        modelRoom.setRowCount(0);
        for(Room r:listRoom){

            Object[] row={
                   r.getRoomId(), r.getRoomNumber(),r.getRoomType(),r.getPrice()
            };
            modelRoom.addRow(row);
        }



    }

    public void loadBookingTable() {
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

    public void update(){
        String sql="UPDATE Status\n" +
                "SET RoomID = ? "+
                "WHERE BookingID = ?";
        search();
        int selectedRow=tblRoom.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần đặt !");
            return ;
        }
        if(txtBookingID.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Vui lòng nhập BookingID !");
            return ;
        }
        Connection conn=ConnectJDBC.getConnection();
        try {
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, (String) tblRoom.getValueAt(selectedRow,0));
            ps.setString(2,txtBookingID.getText());
            spRoomNumber.setValue(Integer.parseInt(modelRoom.getValueAt(selectedRow, 1).toString()));
            cboType.setSelectedItem(modelRoom.getValueAt(selectedRow, 2).toString());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Đã cập nhật thành công !");
            loadBookingTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void filterTable() {
        if (isUpdating) return;

        String username = txtCustomer.getText().trim();
        String bookingId = txtBookingID.getText().trim();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!username.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(username), 1));
        }

        if (!bookingId.isEmpty()) {
            filters.add(RowFilter.regexFilter(Pattern.quote(bookingId), 0));
        }

        RowFilter<Object, Object> rf =
                filters.isEmpty() ? null : RowFilter.andFilter(filters);

        sorter.setRowFilter(rf);
    }

    private void setupSearch() {
        searchTimer = new Timer(300, e -> filterTable());
        searchTimer.setRepeats(false);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { restart(); }
            public void removeUpdate(DocumentEvent e) { restart(); }
            public void changedUpdate(DocumentEvent e) { restart(); }

            private void restart() {
                searchTimer.restart();
            }
        };

        txtCustomer.getDocument().addDocumentListener(dl);
        txtBookingID.getDocument().addDocumentListener(dl);
    }


    private void handleCheckIn() {
        int row = tblBooking.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một booking!");
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
            JOptionPane.showMessageDialog(this,
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

            JOptionPane.showMessageDialog(this, "Check-in thành công!");
            loadBookingTable();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi check-in!");
        }
    }

    private void handleCheckOut() {
        int row = tblBooking.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một booking!");
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
            JOptionPane.showMessageDialog(this,
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

            JOptionPane.showMessageDialog(this, "Check-out thành công!");
            loadBookingTable();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi check-out!");
        }
    }

    private void filterByStatus() {
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





    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModGUI(null).setVisible(true));
    }
}


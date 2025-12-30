package GUI;

import DAO.UserDAO;
import Model.Room;
import Model.Users;
import Utils.ConnectJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
public class AdminGUI extends JFrame {

    JTextField txtSearch;
    JComboBox<String> cbRole;
    JTable table;
    DefaultTableModel model;
    List<Users>allUsers;
    public AdminGUI(Users user) {
        setTitle("ADMIN DASHBOARD");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel top = new JPanel();
        txtSearch = new JTextField(15);
        cbRole = new JComboBox<>(new String[]{"ALL", "ADMIN", "MOD", "USER"});
        JButton btnSearch = new JButton("Tìm");

        top.add(new JLabel("Tìm kiếm:"));
        top.add(txtSearch);
        top.add(new JLabel("Role:"));
        top.add(cbRole);
        top.add(btnSearch);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"ID", "Username", "Password","Role","FullName" ,"Email","Phone","Status"}, 0);
        table = new JTable(model);

        // ===== BOTTOM =====
        JButton btnRevenue = new JButton("Xem doanh thu");
        JButton btnReload = new JButton("Làm mới");
        JButton btnStatus = new JButton("Sửa trạng thái");
        JButton btnSetMOD = new JButton("Set MOD");
        JPanel bottom = new JPanel();
        bottom.add(btnRevenue);
        bottom.add(btnReload);
        bottom.add(btnSetMOD);
        bottom.add(btnStatus);

        add(top, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        JPanel mainPanel = new JPanel(new BorderLayout());


        loadData(UserDAO.selectUsers());

        btnReload.addActionListener(e->loadData(UserDAO.selectUsers()));
        btnRevenue.addActionListener(e->{
            new RevenueGUI().setVisible(true);
        });
        btnSetMOD.addActionListener(e->updateUserRole());
        btnStatus.addActionListener(e->setUserStatus());
        //Listener
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterData();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterData();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterData();
            }
        });

    }

    public void loadData(List<Users>listUser){
        model.setRowCount(0);
        allUsers = listUser;
        for(Users u:listUser){

            Object[] row={
                    u.getUserId(),u.getUsername(),u.getPassword(),u.getRole(),u.getFullname(),u.getEmail(),u.getPhone(),u.getStatus()
            };
            model.addRow(row);
        }
    }
    private void filterData() {
        String keyword = txtSearch.getText().toLowerCase();
        String role = cbRole.getSelectedItem().toString();

        model.setRowCount(0);

        for (Users u : allUsers) {
            boolean matchText =
                    u.getUsername().toLowerCase().contains(keyword)
                            || u.getFullname().toLowerCase().contains(keyword)
                            || u.getEmail().toLowerCase().contains(keyword);

            boolean matchRole =
                    role.equals("ALL") || u.getRole().equalsIgnoreCase(role);

            if (matchText && matchRole) {
                model.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername(),
                        u.getPassword(),
                        u.getRole(),
                        u.getFullname(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getStatus()
                });
            }
        }
    }

    public void updateUserRole() {
        String sql = "UPDATE Users SET Role = 'MOD' WHERE UserID = ?";
            int selected =table.getSelectedRow();
            if(selected==-1){
                JOptionPane.showMessageDialog(null,"Vui lòng chọn USER cần SET !");
                return ;
            }
            String userId= table.getValueAt(selected,0).toString();
        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setString(1, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Đã cập nhật quyền thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi cập nhật quyền!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setUserStatus() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn USER!");
            return;
        }

        String userId = table.getValueAt(selected, 0).toString();
        int status = Integer.parseInt(table.getValueAt(selected, 7).toString());

        int newStatus = (status == 1) ? 0 : 1;

        String sql = "UPDATE Users SET Status = ? WHERE UserID = ?";

        try (Connection con = ConnectJDBC.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newStatus);
            ps.setString(2, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    newStatus == 1 ? "Đã mở khóa tài khoản" : "Đã khóa tài khoản");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không thể cập nhật trạng thái!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    
}

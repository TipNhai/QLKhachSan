package GUI;

import DAO.UserDAO;
import Model.Room;
import Model.Users;

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
    public AdminGUI() {
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

        JPanel bottom = new JPanel();
        bottom.add(btnRevenue);
        bottom.add(btnReload);

        add(top, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        JPanel mainPanel = new JPanel(new BorderLayout());


        loadData(UserDAO.selectUsers());


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
    public static void main(String[] args) {
        new AdminGUI().setVisible(true);
    }
}

package GUI;

import DAO.UserDAO;
import Model.Users;
import Utils.ConnectJDBC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DKFORM extends JFrame{
    private JTextField txtUserName;
    private JPasswordField txtCPassword;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JButton btnDK;
    private JPasswordField txtPassword;
    private JPanel MainPanel;

    public DKFORM() {
        setTitle("Form Đăng Ký");
        setContentPane(MainPanel);
        setLocationRelativeTo(null);
        setSize(400,350);
        btnDK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cPassword=new String(txtCPassword.getPassword());
                String password=new String(txtPassword.getPassword());
                if(!txtPhone.getText().trim().isEmpty()&&!txtEmail.getText().trim().isEmpty()&&!txtUserName.getText().trim().isEmpty()&&!txtFullName.getText().trim().isEmpty()) {
                    if (password.trim().isEmpty() || !cPassword.equals(password)) {
                        JOptionPane.showMessageDialog(DKFORM.this, "Mật khẩu không khớp !");
                    } else {
                        Users user=new Users();
                        user.setEmail(txtEmail.getText().trim());
                        user.setPhone(txtPhone.getText().trim());
                        user.setFullname(txtFullName.getText().trim());
                        user.setPassword(password.trim());
                        user.setUsername(txtUserName.getText().trim());
                        user.setRole("USER");
                        UserDAO.insert(user);
                        DKFORM.this.dispose();
                        new DNFORM().setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(DKFORM.this,"Vui lòng điền đủ thông tin !");
                }

            }
        });
    }

    public static void main(String[] args) {
        new DKFORM().setVisible(true);
    }
}

package GUI;

import DAO.UserDAO;
import Model.Users;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DNFORM extends JFrame{
    private JTextField txtUserName;
    private JButton btnDN;
    private JPasswordField txtPassword;
    private JPanel MainPanel;

    public DNFORM() {
        setTitle("Form Đăng Nhập");
        setContentPane(MainPanel);
        setLocationRelativeTo(null);
        setSize(400,350);
        btnDN.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String password=new String(txtPassword.getPassword());
                if(password.isEmpty()||txtUserName.getText().trim().isEmpty())
                    JOptionPane.showMessageDialog(DNFORM.this,"Vui lòng điền đầy đủ thông tin !");
                else{
                    Users user= UserDAO.dNhap(txtUserName.getText(),password);
                    if(user.getRole().equals("USER")){
                        new UserGUI(user).setVisible(true);
                    }
                    DNFORM.this.dispose();
                }


            }
        });
    }


    public static void main(String[] args) {
        new DNFORM().setVisible(true);
    }
}

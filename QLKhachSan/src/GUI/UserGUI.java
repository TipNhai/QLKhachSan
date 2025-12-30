package GUI;

import DAO.UserDAO;
import Model.BeanDTO;
import Model.Room;
import Model.Users;
import Utils.Change;
import Utils.Check;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static Utils.Check.getValueOrNull;


public class UserGUI extends JFrame {
    private String[] loaiPhong ;
    private JTable table1, table2;
    private DefaultTableModel model1,model2;
    private JComboBox<String> cbLoaiPhong;
    private JDateChooser dateTra,dateNhan;
    private  SpinnerNumberModel soNgay;
    private JSpinner spinner;
    private List<Room>listRoom;
    private BeanDTO sub;
    public UserGUI(Users user){
        setTitle("Form Đăng Nhập ");
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
        setResizable(false);
        add(new JLabel("HỆ THỐNG ĐẶT PHÒNG KHÁCH SẠN") {{
            setBounds(10, 10, 300, 50);
        }});
        JLabel lblXinChao;
        // toàn bộ list Room
        listRoom= UserDAO.selectRoom(null);

        JButton btnDN=new JButton("Đăng Nhập");
        JButton btnDK=new JButton("Đăng Ký");
        JButton btnDX=new JButton("Đăng Xuất");
        btnDN.setBounds(300, 70, 100, 30);
        btnDK.setBounds(430, 70, 100, 30);
        btnDX.setBounds(400, 70, 100, 30);
        JButton btnLichSu=new JButton("Lịch Sử");
        btnLichSu.setBounds(300, 70, 80, 30);
        add(btnDX);
        add(btnDK);
        add(btnDN);
        btnDX.setBackground(new Color(220, 50, 60));
        btnDX.setForeground(Color.WHITE);
        btnDX.setFocusPainted(false);
        btnDN.setFocusPainted(false);
        btnDX.setFocusPainted(false);
        btnLichSu.setFocusPainted(false);
        add(btnLichSu);
        add(new JLabel("Tìm Phòng"){{
            setBounds(50,120,300,50);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
        }});
        loaiPhong=new String[]{"Tất cả","VIP", "Gia đình", "Đôi","Đơn"};
         cbLoaiPhong= new JComboBox<>(loaiPhong);
        add(new JLabel("Loại phòng"){{setBounds(30,180,80,30);}});
        cbLoaiPhong.setBounds(110,180,80,30);
        add(cbLoaiPhong);
        add(new JLabel("Ngày nhận"){{setBounds(30,220,80,30);}});
        dateNhan=new JDateChooser();
        dateNhan.setMinSelectableDate(new Date());
        dateNhan.setBounds(110,220,150,30);
        add(dateNhan);
        dateTra=new JDateChooser();
        dateTra.setMinSelectableDate(new Date());
        dateTra.setBounds(110,260,150,30);
        add(dateTra);
        add(new JLabel("Ngày trả"){{setBounds(30,260,80,30);}});
        add(new JLabel("Số ngày"){{setBounds(30,300,80,30);}});
        soNgay= new SpinnerNumberModel(
                1,   // giá trị ban đầu
                1,   // giá trị nhỏ nhất
                10,  // giá trị lớn nhất
                1    // bước nhảy
        );

        spinner = new JSpinner(soNgay);
        spinner.setBounds(110,300,80,30);
        add(spinner);
        JButton btnSearch=new JButton("Tìm Phòng");
        btnSearch.setBounds(40,400,110,30);
        btnSearch.setFocusPainted(false);
        add(btnSearch);


        JButton btnAdd=new JButton("Thêm Phòng");
       btnAdd.setBounds(170,400,110,30);
        btnAdd.setFocusPainted(false);
        add(btnAdd);

        add(new JLabel("Danh sách phòng") {{
            setBounds(350,120,300,50);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
        }});
        model1=new DefaultTableModel(new String[]{"Số Phòng","Loại Phòng","Giá/Ngày"},0);
        loadData(listRoom);
        table1=new JTable(model1);
        JScrollPane scrollPhong=new JScrollPane(table1);
        scrollPhong.setBounds(300,180,260,250);
        add(scrollPhong);
        add(new JLabel("THÔNG TIN ĐẶT PHÒNG "){{setBounds(10,450,200,30);}});
        model2=new DefaultTableModel(new String[]{"Khách Hàng","Loại Phòng","Số phòng","Ngày Nhận","Ngày Trả","Số ngày"},0);
        table2=new JTable(model2);
        JScrollPane scrollTTDat=new JScrollPane(table2);
        scrollTTDat.setBounds(30,480,530,230);
        add(scrollTTDat);
        JButton btnDat=new JButton("Đặt Phòng");
        btnDat.setBounds(100,720,100,30);
        add(btnDat);
        JButton btnXoa=new JButton("Xóa Phòng");
        btnXoa.setBounds(350,720,100,30);
        add(btnXoa);


        if (user == null) {
            lblXinChao = new JLabel("Xin Chào , Vui lòng Đăng Nhập để đặt phòng ");
            setSize(600, 490);
            btnDX.setVisible(false);
            btnLichSu.setVisible(false);
        } else {
            lblXinChao = new JLabel("Xin Chào " + user.getFullname());
            setSize(600, 800);
            btnDK.setVisible(false);
            btnDN.setVisible(false);
        }
        lblXinChao.setBounds(10, 60, 300, 50);
        add(lblXinChao);




        //ActionListener
        btnSearch.addActionListener(e->loadData(UserDAO.selectRoom(search())));

       btnAdd.addActionListener(e-> add(user));

        btnDN.addActionListener(e->{
            new DNFORM().setVisible(true);
            UserGUI.this.dispose();
        });

        btnDK.addActionListener(e->{
            new DKFORM().setVisible(true);
            UserGUI.this.dispose();
        });

        btnDX.addActionListener(e->{
            dispose();
            new UserGUI(null).setVisible(true);
        });
        btnXoa.addActionListener(e->dlt());

        btnDat.addActionListener(e->
                {
                    UserDAO.booking(user.getUserId(),subcribe(), sub);
                    dlt();
                }
        );

      }

    //Function
      public void loadData(List<Room>listRoom){
          model1.setRowCount(0);
        for(Room r:listRoom){

                Object[] row={
                        r.getRoomNumber(),r.getRoomType(),r.getPrice()
                };
                model1.addRow(row);
            }



      }



      public BeanDTO search(){
        BeanDTO beanTim=new BeanDTO();
        beanTim.setRoomType(getValueOrNull(cbLoaiPhong.getSelectedItem().toString()));
        //đổi Sang localdate để tính ngày
          if (dateNhan.getDate() != null) {
              beanTim.setDateNhan(
                      Change.changeTypeDate(dateNhan)
              );
          }
          if (dateTra.getDate() != null) {
              beanTim.setDateTra(
                      Change.changeTypeDate(dateTra)
              );
          }
          if(getValueOrNull(dateNhan.getDate())!=null&&getValueOrNull(dateTra.getDate())!=null){
           int days= (int) ChronoUnit.DAYS.between(beanTim.getDateNhan(), beanTim.getDateTra());
            if(days<=0) {
                dateNhan.setDate(null);
                dateTra.setDate(null);
                JOptionPane.showMessageDialog(null,"Ngày Trả phải lớn hơn ngày nhận ");
                return null;
            } else {
                beanTim.setSoNgay(days);
                spinner.setValue(days);
            }
        }else beanTim.setSoNgay(getValueOrNull((int)spinner.getValue()));

        return beanTim;
      }



      public void add(Users user){
        if(user!=null){
            if(getValueOrNull(dateNhan.getDate())==null) dateNhan.setDate(new Date());
            if(getValueOrNull(dateTra.getDate())==null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateNhan.getDate());
                cal.add(Calendar.DAY_OF_MONTH, (int)spinner.getValue()); // +ngày
                dateTra.setDate(cal.getTime());
            }
            if (table1.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất một phòng !");
                return;
            }
            int[] selectedRows = table1.getSelectedRows();
            for(int r:selectedRows){
                Object []row={
                        user.getFullname(),
                        table1.getValueAt(r,1),
                        table1.getValueAt(r,0),
                        Change.changeTypeDate(dateNhan),
                        Change.changeTypeDate(dateTra),
                        spinner.getValue()
                };
                model2.addRow(row);
            }
        } else {
            new DNFORM().setVisible(true);
            UserGUI.this.dispose();
        }
      }



    public void dlt(){
        int selectedRow = table2.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần xóa !");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table2.getModel();
        model.removeRow(selectedRow);


    }

    public Room subcribe(){
        int selectedRow = table2.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần đặt !");
            return null;
        }
        sub=new BeanDTO();
        sub.setDateTra((LocalDate) table2.getValueAt(selectedRow,4));
        sub.setDateNhan((LocalDate) table2.getValueAt(selectedRow,3));
        sub.setSoNgay((Integer) table2.getValueAt(selectedRow,5));
        Room res = null;
        Object value = table2.getValueAt(selectedRow, 2); // giá trị trong JTable
        if(value != null) {
            String roomNum = value.toString().trim();
            for (Room r : listRoom) {
                if (r.getRoomNumber().toString().trim().equals(roomNum)) {
                    res = r;
                    break;
                }
            }
        }

        return res;
    }
    public static void main(String[] args) {
        new UserGUI(null).setVisible(true);
    }
}

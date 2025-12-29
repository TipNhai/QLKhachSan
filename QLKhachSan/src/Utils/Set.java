package Utils;

import Model.Room;
import Model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Set {
    public static Room setRoom(ResultSet rs){
        try {
            Room room=new Room();
            room.setRoomId(rs.getString(1));
            room.setRoomNumber(rs.getString(2));
            room.setRoomType(rs.getString(3));
            room.setStatus(rs.getString(4));
            room.setPrice(rs.getInt(5));
            return room;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Users setUsers(ResultSet rs){
        Users user=new Users();
        try {
            user.setUserId(rs.getString(1));
            user.setUsername(rs.getString(2));
            user.setPassword(rs.getString(3));
            user.setRole(rs.getString(4));
            user.setFullname(rs.getString(5));
            user.setEmail(rs.getString(6));
            user.setPhone(rs.getString(7));
            user.setStatus(rs.getString(8));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

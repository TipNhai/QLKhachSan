package Model;

import java.time.LocalDateTime;

public class RoomBooking {
    private String BookingId;
    private String UserId;

    private String BookingDate;
    private String RoomType;
    private String Quantity;
    private String CreateAt;
    private LocalDateTime CheckIntTime;
    private LocalDateTime CheckOutTime;
    private String TotalDays;

    public RoomBooking() {
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String bookingDate) {
        BookingDate = bookingDate;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    public LocalDateTime getCheckIntTime() {
        return CheckIntTime;
    }

    public void setCheckIntTime(LocalDateTime checkIntTime) {
        CheckIntTime = checkIntTime;
    }

    public LocalDateTime getCheckOutTime() {
        return CheckOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        CheckOutTime = checkOutTime;
    }

    public String getTotalDays() {
        return TotalDays;
    }

    public void setTotalDays(String totalDays) {
        TotalDays = totalDays;
    }
}

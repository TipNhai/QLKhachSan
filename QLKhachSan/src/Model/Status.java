package Model;

public class Status {
    private String BookingId;
    private String RoomId;
    private String Status;
    private Long Price_Date;

    public Status() {
    }

    public Long getPrice_Date() {
        return Price_Date;
    }

    public void setPrice_Date(Long price_Date) {
        Price_Date = price_Date;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

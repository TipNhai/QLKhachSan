package Model;

import java.time.LocalDate;
import java.time.temporal.TemporalAmount;

public class BeanDTO {
    private String RoomType;
    private LocalDate dateNhan;
    private LocalDate dateTra;
    private Integer soNgay;

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }

    public LocalDate getDateNhan() {
        return dateNhan;
    }

    public void setDateNhan(LocalDate dateNhan) {
        this.dateNhan = dateNhan;
    }

    public LocalDate getDateTra() {
        return dateTra;
    }

    public void setDateTra(LocalDate dateTra) {
        this.dateTra = dateTra;
    }

    public Integer getSoNgay() {
        return soNgay;
    }

    public void setSoNgay(Integer soNgay) {
        this.soNgay = soNgay;
    }
}

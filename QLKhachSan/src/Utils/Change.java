package Utils;

import com.toedter.calendar.JDateChooser;

import java.time.LocalDate;
import java.time.ZoneId;

public class Change {
    public static LocalDate changeTypeDate(JDateChooser date){

        LocalDate localDate = date.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate;
    }
}

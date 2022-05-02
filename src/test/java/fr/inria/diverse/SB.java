package fr.inria.diverse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SB {
    public static void main(String[] args) {
        LocalDateTime today = LocalDateTime.now() ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("+yyyy-MM-dd'T'HH:mm:ss'Z'") .withZone(ZoneId.of( "Europe/Paris" ));
        System.out.println(formatter.format(today));
        System.out.println( DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx").format(ZonedDateTime.now()));
    }
}

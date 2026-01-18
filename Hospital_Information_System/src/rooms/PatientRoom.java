package rooms;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PatientRoom extends Room {
    public PatientRoom(String roomNumber, double dailyPrice) {
        super(roomNumber, dailyPrice);
    }

    @Override
    public double calculateCost(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return Math.max(0, days) * dailyPrice;
    }

    @Override
    public RoomTypes getRoomType() { return RoomTypes.PATIENT; }
}

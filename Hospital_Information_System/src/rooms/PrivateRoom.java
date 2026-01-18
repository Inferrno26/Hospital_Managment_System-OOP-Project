package rooms;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrivateRoom extends Room {
    private static final double MULTIPLIER = 1.2;

    public PrivateRoom(String roomNumber, double dailyPrice) {
        super(roomNumber, dailyPrice);
    }

    @Override
    public double calculateCost(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return Math.max(0, days) * dailyPrice * MULTIPLIER;
    }

    @Override
    public RoomTypes getRoomType() { return RoomTypes.PRIVATE; }
}

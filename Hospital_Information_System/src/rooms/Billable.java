package rooms;

import java.time.LocalDate;

public interface Billable {
    double calculateCost(LocalDate startDate, LocalDate endDate);
}

package rooms;

public abstract class Room implements Billable {
    protected String roomNumber;
    protected double dailyPrice;

    public Room(String roomNumber, double dailyPrice) {
        this.roomNumber = roomNumber;
        this.dailyPrice = dailyPrice;
    }

    public String getRoomNumber() { return roomNumber; }
    public double getDailyPrice() { return dailyPrice; }

    public abstract RoomTypes getRoomType();
}

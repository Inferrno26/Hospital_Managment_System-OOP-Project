package rooms;

import java.util.ArrayList;
import java.util.List;

public class Ward {
    private String name;
    private List<Room> rooms = new ArrayList<>();

    public Ward(String name) { this.name = name; }

    public String getName() { return name; }
    public List<Room> getRooms() { return rooms; }

    public void addRoom(Room room) { rooms.add(room); }
}

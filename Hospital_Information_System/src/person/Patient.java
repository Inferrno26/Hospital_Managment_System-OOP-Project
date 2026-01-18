package person;

import java.time.LocalDate;

public class Patient extends Person {
    private double height;
    private double weight;

    public Patient(String id, String fullName, String gender, LocalDate dateOfBirth,
                   ContactInfo contactInfo, double height, double weight) {
        super(id, fullName, gender, dateOfBirth, contactInfo);
        this.height = height;
        this.weight = weight;
    }

    public double getHeight() { return height; }
    public double getWeight() { return weight; }

    public double calculateBMI() {
        if (height <= 0) return 0;
        return weight / (height * height);
    }

    // For TableView PropertyValueFactory("bmi")
    public double getBmi() { return calculateBMI(); }

    @Override
    public String getRole() { return "Patient"; }
}

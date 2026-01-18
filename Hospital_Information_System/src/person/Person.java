package person;

import java.time.LocalDate;

public abstract class Person {
    protected String id;
    protected String fullName;
    protected String gender;
    protected LocalDate dateOfBirth;
    protected ContactInfo contactInfo;

    public Person(String id, String fullName, String gender, LocalDate dateOfBirth, ContactInfo contactInfo) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.contactInfo = contactInfo;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public ContactInfo getContactInfo() { return contactInfo; }

    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public abstract String getRole();
}

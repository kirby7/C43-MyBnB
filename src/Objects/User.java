package Objects;

import java.sql.Date;
import java.time.LocalDate;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String sin;
    private String occupation;
    private String password;
    private Address address;
    private PaymentInfo paymentInfo;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getLocalDoB() {
        if (dateOfBirth != null)
            return dateOfBirth.toLocalDate();
        return null;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean hasSin() {
        if (this.sin == null)
            return false;
        return true;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PaymentInfo getPaymentInfo() {
        return this.paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}

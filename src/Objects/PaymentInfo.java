package Objects;

import java.sql.Date;

public class PaymentInfo {
    private String ccNumber;
    private String securityCode;
    private Date expDate;
    private String cardholder;
    private User user; // Attached to this account

    public PaymentInfo(String ccNumber, String securityCode, Date expDate, String cardholder, User user) {
        this.ccNumber = ccNumber;
        this.securityCode = securityCode;
        this.expDate = expDate;
        this.cardholder = cardholder;
        this.user = user;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

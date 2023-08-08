package Objects;

public class Address {
    private Integer aid;
    private String address;
    private String city;
    private String country;
    private String postalCode;

    public Address(Integer aid, String address, String city, String country, String postalCode) {
        this.aid = aid;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Integer getAid() {
        return this.aid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return address + ", " + city + ", " + country;
    }
}

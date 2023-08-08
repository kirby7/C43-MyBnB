package Objects;

public class Listing {
    public static enum ListingType {
        HOUSE,
        APARTMENT,
        GUESTHOUSE,
        HOTEL
    }
    private Integer lid;
    private ListingType listingType;
    private Double latitude;
    private Double longitude;
    private Address address;
    private User user; // Host's account

    public Listing(Integer lid, ListingType listingType, Double latitude, Double longitude, Address address, User user) {
        this.lid = lid;
        this.listingType = listingType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.user = user;
    }

    public Integer getLid() {
        return lid;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User username) {
        this.user = user;
    }

    @Override
    public String toString() {
        return listingType.toString() + " on " + address.toString() + " (" + latitude + ", " + longitude + ")";
    }
}

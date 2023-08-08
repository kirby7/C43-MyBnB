package Objects;

import java.sql.Date;

public class Booking {
    private Listing listing;
    private Date date; // Date of booking
    private User user; // User that booked the listing

    public Booking(Listing listing, Date date, User user) {
        this.listing = listing;
        this.date = date;
        this.user = user;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

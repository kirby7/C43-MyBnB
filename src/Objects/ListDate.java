package Objects;

import java.sql.Date;

public class ListDate {
    private Listing listing;
    private Date date;
    private Double price;

    public ListDate(Listing listing, Date date, Double price) {
        this.listing = listing;
        this.date = date;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return listing.toString() + " - " + this.date + ": " + String.format("$%.2f", this.price);
    }
}

package com.example.frontendds;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class BookedRooms implements Parcelable {
    private String roomName;
    private int noOfPersons;
    private String area;
    private double stars;
    private int noOfReviews;
    private String roomImage;
    private int price;
    private String dates;
    private String rents; // New field

    // Constructor
    public BookedRooms(String roomName, int noOfPersons, String area, double stars, int noOfReviews, String roomImage, int price, String dates, String rents) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = price;
        this.dates = dates;
        this.rents = rents;
    }

    // Getters
    public String getRoomName() { return roomName; }
    public int getNoOfPersons() { return noOfPersons; }
    public String getArea() { return area; }
    public double getStars() { return stars; }
    public int getNoOfReviews() { return noOfReviews; }
    public String getRoomImage() { return roomImage; }
    public int getPrice() { return price; }
    public String getDates() { return dates; }
    public String getRents() { return rents; } // New getter

    // Parcelable implementation
    protected BookedRooms(Parcel in) {
        roomName = in.readString();
        noOfPersons = in.readInt();
        area = in.readString();
        stars = in.readDouble();
        noOfReviews = in.readInt();
        roomImage = in.readString();
        price = in.readInt();
        dates = in.readString();
        rents = in.readString(); // Read rents from Parcel
    }

    public static final Creator<BookedRooms> CREATOR = new Creator<BookedRooms>() {
        @Override
        public BookedRooms createFromParcel(Parcel in) {
            return new BookedRooms(in);
        }

        @Override
        public BookedRooms[] newArray(int size) {
            return new BookedRooms[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeInt(noOfPersons);
        dest.writeString(area);
        dest.writeDouble(stars);
        dest.writeInt(noOfReviews);
        dest.writeString(roomImage);
        dest.writeInt(price);
        dest.writeString(dates);
        dest.writeString(rents); // Write rents to Parcel
    }

    // Method to convert the Room object to a JSONObject
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("roomName", roomName);
            jsonObject.put("noOfPersons", noOfPersons);
            jsonObject.put("area", area);
            jsonObject.put("stars", stars);
            jsonObject.put("noOfReviews", noOfReviews);
            jsonObject.put("roomImage", roomImage);
            jsonObject.put("price", price);
            jsonObject.put("dates", dates);
            jsonObject.put("rents", rents); // Add rents to JSON
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

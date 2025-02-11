package com.example.frontendds;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class Room implements Parcelable {
    private String roomName;
    private int noOfPersons;
    private String area;
    private double stars;
    private int noOfReviews;
    private String roomImage;
    private boolean availability;
    private int price;
    private String dates;

    // Constructor
    public Room(String roomName, int noOfPersons, String area, double stars, int noOfReviews, String roomImage, boolean availability, int price, String dates) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.availability = availability;
        this.price = price;
        this.dates = dates;
    }

    // Getters
    public String getRoomName() { return roomName; }
    public int getNoOfPersons() { return noOfPersons; }
    public String getArea() { return area; }
    public double getStars() { return stars; }
    public int getNoOfReviews() { return noOfReviews; }
    public String getRoomImage() { return roomImage; }
    public boolean isAvailability() { return availability; }
    public int getPrice() { return price; }
    public String getDates() { return dates; }

    // Parcelable implementation
    protected Room(Parcel in) {
        roomName = in.readString();
        noOfPersons = in.readInt();
        area = in.readString();
        stars = in.readDouble();
        noOfReviews = in.readInt();
        roomImage = in.readString();
        availability = in.readByte() != 0;
        price = in.readInt();
        dates = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
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
        dest.writeByte((byte) (availability ? 1 : 0));
        dest.writeInt(price);
        dest.writeString(dates);
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
            jsonObject.put("availability", availability);
            jsonObject.put("price", price);
            jsonObject.put("dates", dates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}


package pkg.android.skynet.yourcompanion.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ST-3 on 29-09-2017.
 */

public class FollowMeData implements Serializable{

    @SerializedName("fl_id")
    private String flId;

    @SerializedName("sender_id")
    private String senderId;

    @SerializedName("follow_id")
    private String followId;

    @SerializedName("start_point_lat")
    private String startPointLat;

    @SerializedName("start_point_long")
    private String startPointLong;

    @SerializedName("status")
    private String status;

    @SerializedName("move_lat")
    private String moveLat;

    @SerializedName("move_long")
    private String moveLong;

    @SerializedName("fname")
    private String fname;

    @SerializedName("lname")
    private String lname;

    @SerializedName("phone")
    private String phone;

    @SerializedName("user_img")
    private String userImg;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("is_emergency")
    private String isEmergency;


    public String getFlId() {
        return flId;
    }

    public void setFlId(String flId) {
        this.flId = flId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public String getStartPointLat() {
        return startPointLat;
    }

    public void setStartPointLat(String startPointLat) {
        this.startPointLat = startPointLat;
    }

    public String getStartPointLong() {
        return startPointLong;
    }

    public void setStartPointLong(String startPointLong) {
        this.startPointLong = startPointLong;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMoveLat() {
        return moveLat;
    }

    public void setMoveLat(String moveLat) {
        this.moveLat = moveLat;
    }

    public String getMoveLong() {
        return moveLong;
    }

    public void setMoveLong(String moveLong) {
        this.moveLong = moveLong;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(String isEmergency) {
        this.isEmergency = isEmergency;
    }
}

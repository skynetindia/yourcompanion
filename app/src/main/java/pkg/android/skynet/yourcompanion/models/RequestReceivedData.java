package pkg.android.skynet.yourcompanion.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ST-3 on 29-09-2017.
 */

public class RequestReceivedData implements Serializable{

    @SerializedName("inv_id")
    private String invId;

    @SerializedName("inv_sender_no")
    private String invSenderNo;

    @SerializedName("inv_send_id")
    private String invSendId;

    @SerializedName("inv_request_no")
    private String invReqNo;

    @SerializedName("inv_request_id")
    private String invReqId;

    @SerializedName("inv_status")
    private String invStatus;

    @SerializedName("fname")
    private String firstName;

    @SerializedName("lname")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("user_img")
    private String userImg;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("is_checked")
    private boolean isChecked;

    public String getInvId() {
        return invId;
    }

    public void setInvId(String invId) {
        this.invId = invId;
    }

    public String getInvSenderNo() {
        return invSenderNo;
    }

    public void setInvSenderNo(String invSenderNo) {
        this.invSenderNo = invSenderNo;
    }

    public String getInvSendId() {
        return invSendId;
    }

    public void setInvSendId(String invSendId) {
        this.invSendId = invSendId;
    }

    public String getInvReqNo() {
        return invReqNo;
    }

    public void setInvReqNo(String invReqNo) {
        this.invReqNo = invReqNo;
    }

    public String getInvReqId() {
        return invReqId;
    }

    public void setInvReqId(String invReqId) {
        this.invReqId = invReqId;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

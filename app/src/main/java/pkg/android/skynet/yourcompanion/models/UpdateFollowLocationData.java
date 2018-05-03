package pkg.android.skynet.yourcompanion.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ST-3 on 29-09-2017.
 */

public class UpdateFollowLocationData implements Serializable{

    @SerializedName("id")
    private String id;

    @SerializedName("sender_id")
    private String senderId;

    @SerializedName("follow_id")
    private String followId;

    @SerializedName("start_point_lat")
    private String startPointLat;

    @SerializedName("start_point_long")
    private String startPointLong;

    @SerializedName("move_lat")
    private String moveLat;

    @SerializedName("move_long")
    private String moveLong;

    @SerializedName("status")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

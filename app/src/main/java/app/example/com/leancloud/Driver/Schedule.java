package app.example.com.leancloud.Driver;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/18.
 */

public class Schedule {

    private String mStartPoint;
    private String mEndPoint;
    private Date mGoTime;
    private boolean mIdentity;
    private boolean mPubStatus;

    public final static boolean Driver = true;
    public final static boolean Passenger = false;

    public String getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(String mStartPoint) {
        this.mStartPoint = mStartPoint;
    }

    public String getEndPoint() {
        return mEndPoint;
    }

    public void setEndPoint(String mEndPoint) {
        this.mEndPoint = mEndPoint;
    }

    public Date getGoTime() {
        return mGoTime;
    }

    public void setGoTime(Date mGoTime) {
        this.mGoTime = mGoTime;
    }

    public boolean getIdentity() {
        return mIdentity;
    }

    /**
    *@param mIdentity One of {@link Schedule}, {@link  #Driver}, or {@link #Passenger}.
     */

    public void setIdentity(boolean mIdentity) {
        this.mIdentity = mIdentity;
    }

    public boolean getPubStatus() {
        return mPubStatus;
    }


    /**
     *@param mStatus One of {@link Schedule}, {@link  #Driver}, or {@link #Passenger}.
     */
    public void setPubStatus(boolean mStatus) {
        this.mPubStatus = mStatus;
    }

}

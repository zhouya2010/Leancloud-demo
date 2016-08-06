package app.example.com.leancloud.Driver;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/18.
 */

public class Schedule {

    //出发位置
    private String mStartPoint;

    //结束位置
    private String mEndPoint;

    //出发时间
    private Date mGoTime;

    //身份 顾客或司机
    private boolean mIdentity;

    //发布状态
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

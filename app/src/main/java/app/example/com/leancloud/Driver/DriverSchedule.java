package app.example.com.leancloud.Driver;

/**
 * Created by Administrator on 2016/7/18.
 */

public class DriverSchedule extends Schedule {

        private float mCost;
        private int mPassengerNum;

        public DriverSchedule() {
                setIdentity(Schedule.Driver);
        }

}

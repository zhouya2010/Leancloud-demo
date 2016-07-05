package app.example.com.leancloud.route;

import android.content.Context;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;

import java.util.ArrayList;
import java.util.List;

import app.example.com.leancloud.R;
import app.example.com.leancloud.util.AMapUtil;

/**
 * Created by Administrator on 2016/7/5.
 */

public class MyDrivingRouteOverlay extends DrivingRouteOverlay {

    private Context context;
    private AMap aMap;
    private DrivePath drivePath;
    private LatLonPoint startLatLonPoint;
    private LatLonPoint endLatLonPoint;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private PolylineOptions options = new PolylineOptions();

    public MyDrivingRouteOverlay(Context context, AMap aMap, DrivePath drivePath, LatLonPoint latLonPoint, LatLonPoint latLonPoint1) {
        super(context, aMap, drivePath, latLonPoint, latLonPoint1);
        this.context = context;
        this.aMap = aMap;
        this.drivePath = drivePath;
        startLatLonPoint = latLonPoint;
        endLatLonPoint = latLonPoint1;
    }


    @Override
    public void addToMap() {
        List drivePathSteps = drivePath.getSteps();
        options = new PolylineOptions();
        options.color(context.getResources().getColor(R.color.blue));
        int size = drivePathSteps.size();
        Marker marker;
        for (int i = 0; i < size; i++) {
            DriveStep driveStep = (DriveStep) drivePathSteps.get(i);

            for (int j = 0; j < driveStep.getPolyline().size(); j++) {
                LatLng latLng = AMapUtil.convertToLatLng(driveStep.getPolyline().get(j));
                options.add(latLng);
            }
            LatLng latLng = AMapUtil.convertToLatLng(driveStep.getPolyline().get(driveStep.getPolyline().size() - 1));
            if (i == size - 1) {
                marker = aMap.addMarker(new MarkerOptions().position(latLng).title("终点").snippet(driveStep.getInstruction()).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
            } else {
                marker = aMap.addMarker(new MarkerOptions().position(latLng).title("方向:" + driveStep.getAction() + "\n道路:" + driveStep.getRoad()).snippet(driveStep.getInstruction()).icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_car)));
            }
            markers.add(marker);
        }

        marker = aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(startLatLonPoint)).title("起点").icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        markers.add(marker);
        aMap.addPolyline(options);
    }

//    private MarkerOptions getMarkerOptions(int index) {
//        return new MarkerOptions()
//                .position(
//                        new LatLng(mPois.get(index).getLatLonPoint()
//                                .getLatitude(), mPois.get(index)
//                                .getLatLonPoint().getLongitude()))
//                .title(getTitle(index)).snippet(getSnippet(index))
//                .icon(getBitmapDescriptor(index));
//    }

    @Override
    public void removeFromMap() {
        for (Marker mark : markers) {
            mark.remove();
        }
        options.visible(false);
    }


}

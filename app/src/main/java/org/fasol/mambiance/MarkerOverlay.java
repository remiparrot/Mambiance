package org.fasol.mambiance;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by fasol on 02/12/16.
 */

public class MarkerOverlay extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mItems = new ArrayList<OverlayItem>();


    @Override
    protected OverlayItem createItem(int i) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    public MarkerOverlay(Drawable defaultMarker) {

        super(defaultMarker);

        //test
        Double latitude = 48.872808*1E6;

        Double longitude = 2.33517*1E6;

        mItems.add(new OverlayItem("Maison du Site du Zéro", "Simple IT", new GeoPoint(latitude.intValue() , longitude.intValue())));

        populate();

    }

    @Override
    public boolean onSnapToItem(int i, int i1, Point point, IMapView iMapView) {
        return false;
    }


}

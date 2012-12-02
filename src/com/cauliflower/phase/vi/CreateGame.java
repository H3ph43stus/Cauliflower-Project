package com.cauliflower.phase.vi;

import java.util.ArrayList;

import com.cauliflower.phase.vi.R;
import com.example.overlaymanager.ManagedOverlay;
import com.example.overlaymanager.ManagedOverlayGestureDetector;
import com.example.overlaymanager.ManagedOverlayItem;
import com.example.overlaymanager.OverlayManager;
import com.example.overlaymanager.ZoomEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class CreateGame extends MapActivity {
	MyLocationOverlay myLocation;
	OverlayManager overlayManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        
        overlayManager = new OverlayManager(this, mapView);
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_action_locate);
        ManagedOverlay.boundToCenter(drawable);
        ManagedOverlay managedOverlay = overlayManager.createOverlay(drawable);
        
        managedOverlay.setOnOverlayGestureListener(new ManagedOverlayGestureDetector.OnOverlayGestureListener() {
			
			public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1) {
				return false;
			}
			
			public boolean onSingleTap(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				arg1.createItem(arg2);
				return true;
			}
			
			public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3, ManagedOverlay arg4) {
				return false;
			}
			
			public void onLongPressFinished(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				if(arg3 != null){
					arg1.remove(arg3);
				}
			}
			
			public void onLongPress(MotionEvent arg0, ManagedOverlay arg1) {
				
			}
			
			public boolean onDoubleTap(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				return false;
			}
		});
        
        overlayManager.populate();
        
        myLocation = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocation);
        
    }
    
    public void createGame(View view){
    	Intent intent = new Intent(this, ManageGame.class);
    	EditText groupText = (EditText) findViewById(R.id.newGroupText);
    	EditText leaderText = (EditText) findViewById(R.id.leaderText);
    	intent.putExtra("groupName", groupText.getText().toString());
    	intent.putExtra("username", leaderText.getText().toString());
    	ManagedOverlay overlay = overlayManager.getOverlay(0);
    	ArrayList<Integer> xCords = new ArrayList<Integer>();
    	ArrayList<Integer> yCords = new ArrayList<Integer>();
    	
    	for(int i=0; i<overlay.size(); i++) {
    		GeoPoint point1 = overlay.getItem(i).getPoint();
    		xCords.add(point1.getLatitudeE6());
    		yCords.add(point1.getLongitudeE6());
    	}
    	intent.putExtra("xCords", xCords);
    	intent.putExtra("yCords", yCords);
    	startActivity(intent);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// when our activity resumes, we want to register for location updates
    	myLocation.enableMyLocation();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	// when our activity pauses, we want to remove listening for location updates
    	myLocation.disableMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}

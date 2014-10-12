package jles.ezmap;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        googleMap = supportMapFragment.getMap();

        // Getting reference to btn_find of the layout activity_main
        Button btn_find = (Button) findViewById(R.id.btn_find);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Defining button click event listener for the find button
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };

        // Setting button click event listener for the find button
        btn_find.setOnClickListener(findClickListener);

    }

    /*
    ** Feature #1 - Worth 5 points
    ** Click a button to show either the normal view of the map with streets,
    ** or a different button to show the terrain view of the map.
     */
    public void onNormalClick(View v){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onSatelliteClick(View v){
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    /*
    ** Feature #2 - Worth 5 points
    ** Click a button to zoom in close to see the streets surrounding an address.
    ** Negates the need to repeatedly click on the zoom in button to get in close.
     */
    public void onStreetZoomClick(View v){
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                markerOptions.snippet("Latitude: " + address.getLatitude() + " Longitude: " + address.getLongitude());

                googleMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0) {
                    /*
                    ** Feature #3 - Worth 5 points
                    ** Automatically zooms the map in to a level where it views the city in the search
                    ** and its surrounding cities on screen without the user needing to zoom manually.
                     */
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }
}

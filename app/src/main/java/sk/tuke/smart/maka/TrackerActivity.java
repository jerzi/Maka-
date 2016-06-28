package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.util.concurrent.TimeUnit;


public class TrackerActivity extends Activity implements Runnable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private Button startb, pauseb, resumeb, resetb, stopb;
    private TextView timetv, distancetv, pacetv;

    private float pace, distance;

    long startTime, currentTime, pauseTime;

    private Handler handler;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private AchievementDatabaseHandler adbh;

    private double[] lat,lng;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if(checkGooglePlayServices()){
            buildGoogleApiClient();
        }

        distance = 0;
        pace = 0;

        index = 0;

        startb = (Button)findViewById(R.id.startb);
        pauseb = (Button)findViewById(R.id.pauseb);
        resumeb = (Button)findViewById(R.id.resumeb);
        resetb = (Button)findViewById(R.id.resetb);
        stopb = (Button)findViewById(R.id.stopb);

        buttonToggle(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);

        timetv = (TextView) findViewById(R.id.time);
        distancetv = (TextView) findViewById(R.id.distance);
        pacetv = (TextView) findViewById(R.id.pace);
        lat = new double[4];
        lng = new double[4];

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                msg.getCallback().run();
                return true;
            }

        });

        createLocationRequest();

        adbh = new AchievementDatabaseHandler(this,null,null, 1);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    public String format(long time) {
        long millisecond, seconds, minutes, hours;
        hours = TimeUnit.MILLISECONDS.toHours(time);
        if(hours > 0) {
            minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours);
            seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MINUTES.toSeconds(minutes) + TimeUnit.HOURS.toSeconds(hours));
            return String.format("%01d:%02d:%02d", hours, minutes, seconds);
        } else {
            minutes = TimeUnit.MILLISECONDS.toMinutes(time);
            seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes);
            millisecond = time - (TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds));
            return String.format("%02d:%02d.%01d", minutes, seconds, millisecond);
        }
    }

    private void buttonToggle(int start, int pause, int resume, int reset, int stop){
        startb.setVisibility(start);
        pauseb.setVisibility(pause);
        resumeb.setVisibility(resume);
        resetb.setVisibility(reset);
        stopb.setVisibility(stop);
    }

    @Override
    public void run() {
        currentTime = System.currentTimeMillis() - startTime + pauseTime;
        timetv.setText(format(currentTime));
        distancetv.setText(String.format("%.02f", distance / 1000));
        pacetv.setText(String.format("%.02f", pace));
        adbh.workWithValues((String)timetv.getText(), (String)pacetv.getText(), (String)distancetv.getText());
        handler.post(this);
    }

    private boolean checkGooglePlayServices(){

            int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(checkGooglePlayServices != ConnectionResult.SUCCESS) {
            /*

			* google play services is missing or update is required
			*  return code could be
			* SUCCESS,
			* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
			* SERVICE_DISABLED, SERVICE_INVALID.

            */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

            return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {
            if(resultCode == RESULT_OK){
                if(!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null) {
            Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLastLocation != null) {
            distance = distance + mLastLocation.distanceTo(location);
            mLastLocation = location;
            pace = distance/((float)currentTime/1000);
            //lat[index] = mLastLocation.getLatitude();
            //lng[index] = mLastLocation.getLongitude();
            index++;
        } else {
            mLastLocation = location;
            //lat[index] = mLastLocation.getLatitude();
            //lng[index] = mLastLocation.getLongitude();
            index++;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startb:
                buttonToggle(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
                startLocationUpdates();
                startTime = System.currentTimeMillis();
                handler.post(this);
                break;

            case R.id.pauseb:
                buttonToggle(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
                stopLocationUpdates();
                this.handler.removeCallbacks(this);
                pauseTime = currentTime;
                break;

            case R.id.resumeb:
                buttonToggle(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                startLocationUpdates();
                handler.post(this);
                startTime = System.currentTimeMillis();
                break;

            case R.id.resetb:
                buttonToggle(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                stopLocationUpdates();
                this.handler.removeCallbacks(this);
                currentTime = 0;
                distance = 0;
                pace = 0;
                pauseTime = 0;
                distancetv.setText("0.00");
                pacetv.setText("0.00");
                timetv.setText(format(0));
                break;

            case R.id.stopb:
                buttonToggle(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                stopLocationUpdates();
                this.handler.removeCallbacks(this);
                Intent intent = new Intent(this, SummaryActivity.class);
                intent.putExtra("time", format(currentTime));
                intent.putExtra("pace", pace);
                intent.putExtra("distance", distance);
                //intent.putExtra("lat", lat);
                //intent.putExtra("lng", lng);
                distance = 0;
                distancetv.setText("0.00");
                currentTime = 0;
                pace = 0;
                pacetv.setText("0.00");
                timetv.setText(format(0));
                startActivity(intent);
                break;
        }
    }
}

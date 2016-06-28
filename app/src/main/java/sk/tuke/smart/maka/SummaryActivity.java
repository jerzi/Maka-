package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.MailTo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SummaryActivity extends Activity {

    private TextView pacetv, distancetv, timetv;

    private float pace, distance;

    private String paceLabel, timeLabel, distanceLabel;

    private String[] items = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        pacetv = (TextView)findViewById(R.id.pace_summary);
        distancetv = (TextView)findViewById(R.id.distance_summary);
        timetv = (TextView)findViewById(R.id.time_summary);

        pace = getIntent().getFloatExtra("pace", 0);
        distance = getIntent().getFloatExtra("distance", 0);

        timeLabel = getIntent().getStringExtra("time");
        paceLabel = String.format("%.02f", pace);
        distanceLabel = String.format("%.02f", distance/1000);

        pacetv.setText(paceLabel);
        distancetv.setText(distanceLabel);
        timetv.setText(timeLabel);

        items[0] = timeLabel;
        items[1] = paceLabel;
        items[2] = distanceLabel;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void onShareClicked(View v){
        TweetComposer.Builder builder = new TweetComposer.Builder(this);
        builder.text("Hey I just ran for: " + timeLabel + " with pace: " + paceLabel + "m/s and damn dat distance: " + distanceLabel + "km #Run!");
        builder.show();
    }

    public void onUploadClicked(View v){
        Intent intent = new Intent(this, WorldOfMakac.class);
        intent.putExtra("items", items);
        startActivity(intent);
    }

    public void onSendClicked(View v){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Look at this");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your run duration was:" + "\n"
                + timeLabel + "\n"
                + "You ran: " + "\n"
                + distanceLabel + " km" + "\n"
                + "At pace: " + paceLabel + " m/s" + "\n");
        startActivity(Intent.createChooser(emailIntent, "Pick your email"));
    }

}

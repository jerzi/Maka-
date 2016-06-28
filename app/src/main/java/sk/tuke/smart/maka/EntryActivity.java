package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class EntryActivity extends Activity {

    TextView timetv, pacetv, distancetv;
    String timeLabel, paceLabel, distanceLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        timeLabel = getIntent().getStringExtra("time");
        paceLabel = getIntent().getStringExtra("pace");
        distanceLabel = getIntent().getStringExtra("distance");

        timetv = (TextView) findViewById(R.id.time_entry);
        pacetv = (TextView) findViewById(R.id.pace_entry);
        distancetv = (TextView) findViewById(R.id.distance_entry);

        timetv.setText(timeLabel);
        pacetv.setText(paceLabel);
        distancetv.setText(distanceLabel);

    }

    public void onShare2Clicked(View v){
        TweetComposer.Builder builder = new TweetComposer.Builder(this);
        builder.text("Hey I just ran for: " + timeLabel + " with pace: " + paceLabel + "m/s and damn dat distance: " + distanceLabel + "km #Run!");
        builder.show();
    }

    public void onSend2Clicked(View v){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Look at this");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your run duration was:" + "\n"
                + timeLabel + "\n"
                + "You ran: " + "\n"
                + distanceLabel + " km" + "\n"
                + "At pace: " + paceLabel + " m/s" + "\n");
        startActivity(Intent.createChooser(emailIntent, ""));
    }
}

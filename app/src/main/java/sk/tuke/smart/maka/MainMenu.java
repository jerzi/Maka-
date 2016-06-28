package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


public class MainMenu extends Activity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "BIwhKnQ7HGXPaamZ7qEtQeQLL";
    private static final String TWITTER_SECRET = "UzEQgAOEy1EAc2Sb3rGqB1KKxU8OeCUJn3nlvOt40aPpWfQvHs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_menu);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.trackerb :
                intent = new Intent(this, TrackerActivity.class);
                Intent serviceIntent = new Intent(this, AchievementUpdateService.class);
                startService(serviceIntent);
                break;
            case R.id.ahb :
                intent = new Intent(this, AchievementsActivity.class);
                break;
            case R.id.womb :
                intent = new Intent(this, WorldOfMakac.class);
                break;
            default :
                intent = new Intent(this, TrackerActivity.class);
        }
        startActivity(intent);
    }
}

package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AchievementsActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<Achievement> adapter;
    private AchievementDatabaseHandler adbh;
    private List<Achievement> AHlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        listView = (ListView)findViewById(R.id.achievementList);

        adbh = new AchievementDatabaseHandler(this, null, null, 1);
        AHlist = adbh.getAchievements();



        initListView(AHlist);

    }

    private void initListView(List<Achievement> list){
        adapter = new ArrayAdapter<Achievement>(this, android.R.layout.simple_list_item_1,list);

        listView.setAdapter(adapter);

    }
}

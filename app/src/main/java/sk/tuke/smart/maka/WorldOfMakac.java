package sk.tuke.smart.maka;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class WorldOfMakac extends Activity {

    private ListView listView;
    private String time, pace, distance;
    private String[] items;

    private DatabaseHandler dbh;

    private List<Result> results;

    private ArrayAdapter<Result> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wom);
        dbh = new DatabaseHandler(this,null,null,1);
        if(getIntent().getStringArrayExtra("items") != null) {
            items = getIntent().getStringArrayExtra("items");
            time = items[0];
            pace = items[1];
            distance = items[2];
            dbh.addItem(time, pace, distance);
        }

        results = dbh.getAllEntries();
        initListView(results);

    }

    public void onClearClicked(View v) {
        dbh.cleanDatabase();
        results = dbh.getAllEntries();
        initListView(results);
    }

    private void initListView(final List<Result> list){
        adapter = new ArrayAdapter<Result>(this, android.R.layout.simple_list_item_1,list);

        listView = (ListView)findViewById(R.id.makaclv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                intent.putExtra("time", list.get(position).getTime());
                intent.putExtra("pace", list.get(position).getPace());
                intent.putExtra("distance", list.get(position).getDistance());

                startActivity(intent);
            }
        });
    }

}

package ir.shirazmetroapp.metroshiraz;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class StationActivity extends AppCompatActivity {
    List<Station> stations;
    ArrayList<String> stations_string = new ArrayList<String>();
    StationHelper stationHelper = new StationHelper(this);
    Intent go_back_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent.hasExtra("back")){
            String back = intent.getStringExtra("back");
            if (back.equals("main")){
                go_back_intent = new Intent(this,MainActivity.class);
            }
            else if (back.equals("list_times")){
                go_back_intent = new Intent(this,ListTimes.class);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        try {
            stationHelper.openJSONs();
            stations = stationHelper.getAllStations();

        } catch (SQLException sqle) {
            throw sqle;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(this, String.valueOf( stations.size()),Toast.LENGTH_LONG).show();
        Intent myIntent = getIntent();
        for (Station station : stations){
            stations_string.add(station.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter<String> (this,R.layout.station_list,stations_string);
        ListView station_list = findViewById(R.id.station_list);
        station_list.setAdapter(adapter);
        station_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text

//                stationHelper.setCurrentStation(position);
                go_back_intent.putExtra("station_name",((TextView) view).getText());
                go_back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go_back_intent);
            }
        });
    }
}

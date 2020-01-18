package ir.shirazmetroapp.metroshiraz;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListTimes extends AppCompatActivity {
    StationHelper stationHelper = new StationHelper(this);
    Integer start_station_id;
    String to_station;
    String station_name;
    Station start_station;
    TimeList<Date> times;
    TextView from_station_text;
    String is_holiday;


    @Override
    protected void onResume() {
        Intent intent = getIntent();

        if(intent.hasExtra("station_name")){
            station_name = intent.getStringExtra("station_name");
            try {
                start_station = stationHelper.getStationIdByName(station_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Toast.makeText(getApplicationContext(),
//                    start_station.getName(), Toast.LENGTH_SHORT).show();

        }
        from_station_text.setText(getString(R.string.station) + " " + start_station.getName());
        start_station_id = start_station.getId();
        update();

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date now = new Date();
        String weekday = stationHelper.weekDay(now);
        setContentView(R.layout.activity_list_times);
        if (weekday.equals("Friday")){
            stationHelper.forceHoliday();
            Switch s = (Switch) findViewById(R.id.switch2);
            s.setChecked(true);
        }
        from_station_text = findViewById(R.id.from_station2);
        Intent intent = getIntent();
        try {
            stationHelper.openJSONs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(intent.hasExtra("start_station_id")) {
            start_station_id = intent.getIntExtra("start_station_id",19);
            try {
                start_station = stationHelper.getStationById(start_station_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            to_station = intent.getStringExtra("to_station");
            update();
        }
    }

    public void setHoliday(View v) {
        Switch s = (Switch) v;
        if (s.isChecked()) {
            stationHelper.forceHoliday();
        }
        else {
            stationHelper.unforceHoliday();
        }
        update();
    }
    public void update() {


        try {
            times = stationHelper.getStationTimes(start_station_id,to_station);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<String> time_strings = new ArrayList<String>();
        for (Date time:times){
            SimpleDateFormat station_time = new SimpleDateFormat("HH:mm");
            time_strings.add(station_time.format(time));
        }
        ArrayAdapter adapter = new ArrayAdapter<String> (this,R.layout.station_list,time_strings);
        ListView station_list = findViewById(R.id.time_list);
        station_list.setAdapter(adapter);
    }

    public void GoStationActivity(View v){
        Intent intent = new Intent(this,StationActivity.class);
        intent.putExtra("back","list_times");
        this.startActivity(intent);
    }
}

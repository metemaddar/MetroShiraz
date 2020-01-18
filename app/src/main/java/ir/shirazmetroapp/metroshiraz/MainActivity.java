package ir.shirazmetroapp.metroshiraz;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    TextView time_view;
    TextSwitcher to_station_text;
    TextView from_station_text;
    String station_name;
    int target = R.string.ehsan;
    Station start_station;
    String to_station = "E";
    List<Station> stations;
    TimeList<Date> times;
    StationHelper stationHelper = new StationHelper(this);
    View toast_view_to_station_dastgheib;
    View toast_view_to_station_ehsan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            stationHelper.createDatabase();
        } catch (IOException e) {
            throw new Error("Error creating database");
        }

        try {
            stationHelper.openJSONs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stations = stationHelper.getAllStations();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        to_station_text = findViewById(R.id.to_station);
        to_station_text.setInAnimation(in);
        to_station_text.setOutAnimation(out);
        tv = findViewById(R.id.textView);
        time_view = findViewById(R.id.TimeView);

        start_station = stations.get(0);
//        Integer last_station_id = sqLiteHelper.getLastStation();
//        start_station = sqLiteHelper.getStationIdById(last_station_id);
        from_station_text = findViewById(R.id.from_station);
        super.onCreate(savedInstanceState);
        LayoutInflater layoutinflater = getLayoutInflater();
        toast_view_to_station_dastgheib = layoutinflater.inflate(R.layout.to_station_dastgheib, (ViewGroup) findViewById(R.id.to_station_dastgheib_layout));
        toast_view_to_station_ehsan = layoutinflater.inflate(R.layout.to_station_ehsan, (ViewGroup) findViewById(R.id.to_station_ehsan_layout));

    }

    @Override
    protected void onResume() {
        try {
            update();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    public String weekDay(Date input_date) {
        DateFormat format=new SimpleDateFormat("EEEE");
        String finalDay=format.format(input_date);
        return finalDay;
    }



    protected void update() throws JSONException {
        Intent intent = getIntent();

        if(intent.hasExtra("station_name")){
            station_name = intent.getStringExtra("station_name");
            start_station = stationHelper.getStationIdByName(station_name);
//            Toast.makeText(getApplicationContext(),
//                    start_station.getName(), Toast.LENGTH_SHORT).show();

            from_station_text.setText(getString(R.string.station) + " " + start_station.getName());
        }else{
            from_station_text.setText(getString(R.string.station) + " " + start_station.getName());
            // Do something else
        }
        Date now = new Date();

        times = stationHelper.getStationTimes(start_station.getId(),to_station,now);
//        Toast.makeText(getApplicationContext(),
//                String.valueOf(times.size()), Toast.LENGTH_SHORT).show();
        if (times.size()>0){
            time_view.setVisibility(View.VISIBLE);
            SimpleDateFormat station_time = new SimpleDateFormat("HH:mm");
//            station_time.setTimeZone(TimeZone.getTimeZone("UTC+03:30"));
//            Date now = new Date();
//            now.setHours(5);
            String date_string;
            Date next_time = times.getNextTime();

            if (now.after(next_time)){
                now.setHours(0);
                now.setMinutes(1);
                now.setDate(now.getDate()+1);
                times = stationHelper.getStationTimes(start_station.getId(),to_station,now);
                next_time = times.getNextTime();
                date_string = "فردا صبح:\n" + station_time.format(next_time);
                time_view.setTextSize(TypedValue.COMPLEX_UNIT_SP,35);
            }
            else {
                date_string = station_time.format(next_time);
                time_view.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            }
            Log.d("Formating time",date_string);
            time_view.setText( date_string );

        }

    }

//    public void buttonClick(View v){
//        if (v.getId()==R.id.to_station){
//            to_station_text.setText(getString(R.string.ehsan));
//        }
//    }

    public void ToggleTarget(View v) throws JSONException {
//        Toast to_station_toast = Toast.makeText(this,target,Toast.LENGTH_SHORT);
//        to_station_toast.setGravity(Gravity.CENTER,0,0);
        if (target == R.string.dastgheib){
            target = R.string.ehsan;
//            to_station_toast.setView(toast_view_to_station_ehsan);
            to_station = "E";
        }
        else if (target == R.string.ehsan){
            target = R.string.dastgheib;
            to_station = "D";
//            to_station_toast.setView(toast_view_to_station_dastgheib);
        }
        to_station_text.setText(getString(target));
//        to_station_toast.show();

        update();
    }

    public void GoSecondActivity(View v){
        Intent intent = new Intent(this,StationActivity.class);
        intent.putExtra("back","main");
        this.startActivity(intent);
    }
    public void GoToTimes(View v){
        Intent intent = new Intent(this,ListTimes.class);
        intent.putExtra("start_station_id",start_station.getId());
        intent.putExtra("to_station",to_station);
        this.startActivity(intent);
    }

    private void RandomColor() {
        Random random = new Random();
        tv.setTextColor(Color.argb(255,random.nextInt(255),random.nextInt(255),random.nextInt(255)));
    }
}

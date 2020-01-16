package ir.shirazmetroapp.metroshiraz;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class StationHelper {
    private static String JSON_PATH = "/data/data/ir.shirazmetroapp.metroshiraz/databases/";
    private static final int DATABASE_VERSION = 1;
    private static final String STATIONS_NAME = "stations.json";
    private String STOPS_NAME = "stops.json";
    private static final String STOPS_HOLIDAYS_NAME = "stops_holidays.json";
    private final Context myContext;
    private JSONArray STATIONS_JSON = null;
    private JSONArray STOPS_JSON = null;
    private JSONArray STOPS_HOLIDAYS_JSON = null;
    private boolean is_holiday = false;
    public static Integer D2E_START = 7*60+10;
    public static Integer D2E_END = 21*60+10;
    public static Integer E2D_START = 7*60+5;
    public static Integer E2D_END = 21*60+5;
    public Boolean IS_HOLIDAY = false;
    private Boolean FORCE_HOLIDAY = false;


    public List<Station> getAllStations() throws JSONException {
        LinkedList<Station> stations = new LinkedList<Station>();
        Station station;
        JSONObject station0;

        for (int i = 0; i < STATIONS_JSON.length(); i++) {
            station0 = STATIONS_JSON.getJSONObject(i);
            station = new Station();
            station.setId(station0.getInt("id"));
            station.setName(station0.getString("name"));

            stations.add(station);
        }
        return stations;

    }

    public void forceHoliday(){
        FORCE_HOLIDAY = true;
    }
    public void unforceHoliday() {
        FORCE_HOLIDAY = false;
    }

    public Date IntToDate(Integer time){
        Date date = new Date();
        date.setHours(time/60);
        date.setMinutes(time%60);
        return date;
    }

    public String weekDay(Date input_date) {
        DateFormat format=new SimpleDateFormat("EEEE");
        String finalDay=format.format(input_date);
        return finalDay;
    }

//    public Date GetTime(Integer start_station_id, Integer end_station_id, Integer start_time) throws JSONException {
//        String to_station_ = "D";
//
//        if (start_station_id<end_station_id){
//            to_station_ = "E";
//        }
//        Date time = IntToDate(start_time);
//        for (int station=start_station_id+1;station<=end_station_id; station++){
//            TimeList times = getStationTimes(station, to_station_);
//            time = times.getNextTime(IntToDate(start_time));
//        }
//        return time;
//    }


    public TimeList<Date> getStationTimes(Integer station_id, String to_station, Date input_date1) throws JSONException {
//        String FRIDAY = weekDay(new Date(2020,1,17));
        if (weekDay(input_date1).equals("Friday") || FORCE_HOLIDAY) {
            Log.d("MainActivity","is_holiday was set to TRUE");
            is_holiday = true;
        }
        else {
            Log.d("MainActivity","is_holiday was set to FALSE");
            is_holiday = false;
        }
        Log.d("MainActivity","WeekDay is " + weekDay(input_date1));

        JSONArray MY_JSON;
        if (!is_holiday) {
            MY_JSON = STOPS_JSON;
        }
        else {
            MY_JSON = STOPS_HOLIDAYS_JSON;
        }

        JSONObject station = MY_JSON.getJSONObject(station_id-1);

        JSONArray stops;
        TimeList<Date> dates = new TimeList<Date>();
        if (station.isNull(to_station)){
            return dates;
        }
        else {
            stops = station.getJSONArray(to_station);
        }
        Date date = new Date();
        Date date1;
        for (int i = 0; i < stops.length(); i++) {
            Integer stop_time = stops.getInt(i);
            date1 = new Date(date.getTime());
            date1.setHours(stop_time/60);
            date1.setMinutes(stop_time%60);
            dates.add(date1);
        }
        return dates;



    }

    public Station getStationIdByName(String name) throws JSONException {
        Station station = new Station();
        JSONObject station0 = null;

        for (int i = 0; i < STATIONS_JSON.length(); i++) {
            station0 = STATIONS_JSON.getJSONObject(i);
            if (station0.getString("name").equals(name)){
                break;
            }
        }
        station.setId(station0.getInt("id"));
        station.setName(name);
        return station;

    }

    public Station getStationById(Integer id) throws JSONException {
        Station station = new Station();
        JSONObject station0 = STATIONS_JSON.getJSONObject(id-1);
        station.setId(station0.getInt("id"));
        station.setName(station0.getString("name"));
        return station;

    }

    public StationHelper(Context myContext) {
        this.myContext = myContext;
    }

    public void openJSONs() throws Exception {
        String stations = getStringFromFile(JSON_PATH+STATIONS_NAME);
        STATIONS_JSON = new JSONArray(stations);
        String stops = getStringFromFile(JSON_PATH+STOPS_NAME);
        STOPS_JSON = new JSONArray(stops);
        String stops_holidays = getStringFromFile(JSON_PATH+STOPS_HOLIDAYS_NAME);
        STOPS_HOLIDAYS_JSON = new JSONArray(stops_holidays);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }


    private void copyJSON(String file_name, InputStream my_stream) throws IOException {
        InputStream my_input = my_stream;
        String output_file_name = JSON_PATH + file_name;
        OutputStream my_output = new FileOutputStream(output_file_name);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = my_input.read(buffer))>0){
            my_output.write(buffer, 0, length);
        }

        //Close the streams
        my_output.flush();
        my_output.close();
        my_input.close();
    }


    public void onUpgrade() {
        try {
            InputStream stations_input = myContext.getResources().openRawResource(R.raw.stations);
            copyJSON(STATIONS_NAME,stations_input);
            InputStream stops_input = myContext.getResources().openRawResource(R.raw.stops);
            copyJSON(STOPS_NAME,stops_input);
            InputStream stops_holidays_input = myContext.getResources().openRawResource(R.raw.stops_holidays);
            copyJSON(STOPS_HOLIDAYS_NAME,stops_holidays_input);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createDatabase() throws IOException {
        try {
            InputStream stations_input = myContext.getResources().openRawResource(R.raw.stations);
            copyJSON(STATIONS_NAME,stations_input);
            InputStream stops_input = myContext.getResources().openRawResource(R.raw.stops);
            copyJSON(STOPS_NAME,stops_input);
            InputStream stops_holidays_input = myContext.getResources().openRawResource(R.raw.stops_holidays);
            copyJSON(STOPS_HOLIDAYS_NAME,stops_holidays_input);
        } catch (IOException e) {
            throw new Error("Error Copying database");
        }


    }
}

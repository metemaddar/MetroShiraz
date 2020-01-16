package ir.shirazmetroapp.metroshiraz;

import java.util.ArrayList;
import java.util.Date;

public class TimeList<Object> extends ArrayList<Date> {
    private Integer current_index;

    public Date getNextTime(){
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC+03:30"));
        Date time = new Date();
        Date output_time = this.get(0);
        for (Date temp_time :this){
            if (time.before(temp_time)){
                output_time = temp_time;
                break;
            }
        }
        return output_time;
    }

    public Date getNextTime(Date time){
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC+03:30"));
//        Date time = new Date();
        Date output_time = this.get(0);
        for (Date temp_time :this){
            if (time.before(temp_time)){
                output_time = temp_time;
                break;
            }
        }
        return output_time;
    }
}

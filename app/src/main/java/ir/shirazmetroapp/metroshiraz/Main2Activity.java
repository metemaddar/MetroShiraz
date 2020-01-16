package ir.shirazmetroapp.metroshiraz;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main2);
    }

    public void goToMain(View v){
        Intent main_intent = new Intent(this, MainActivity.class);
        this.startActivity(main_intent);
    }
}

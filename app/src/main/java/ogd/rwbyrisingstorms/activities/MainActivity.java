package ogd.rwbyrisingstorms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ogd.rwbyrisingstorms.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void DeckView(View v) {
        Intent i = new Intent(getApplicationContext(), DeckView.class);
        startActivity(i);
    }

    public void FightView(View v) {
        Intent i = new Intent(getApplicationContext(), FightActivity.class);
        i.putExtra("character", "filia");
        startActivity(i);
    }
}

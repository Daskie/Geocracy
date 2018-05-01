package csc309.geocracy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import csc309.geocracy.game.GameActivity;

public class LoadingScreenActivity extends Activity {

    //Introduce an delay
    private final int WAIT_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        findViewById(R.id.loadingSpinner).setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(LoadingScreenActivity.this, GameActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoadingScreenActivity.this.startActivity(mainIntent);
            LoadingScreenActivity.this.finish();
        }, WAIT_TIME);
    }

}

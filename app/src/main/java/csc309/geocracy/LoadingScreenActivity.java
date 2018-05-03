package csc309.geocracy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import csc309.geocracy.game.GameActivity;
import es.dmoral.toasty.Toasty;

public class LoadingScreenActivity extends Activity {

    //Introduce an delay
    private final int WAIT_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        ProgressBar loadingSpinner = findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        Toasty.warning(this, "Your world is being created... hang tight!",  WAIT_TIME - 200).show();

        loadingSpinner.setProgress(0);

        new Handler().postDelayed(() -> {
            loadingSpinner.setProgress(75);
        }, WAIT_TIME - 600);

        new Handler().postDelayed(() -> {
            loadingSpinner.setProgress(50);
        }, WAIT_TIME - 1000);

        new Handler().postDelayed(() -> {
            loadingSpinner.setProgress(25);
        }, WAIT_TIME - 1500);


        new Handler().postDelayed(() -> {
            loadingSpinner.setProgress(10);
        }, WAIT_TIME - 1800);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(LoadingScreenActivity.this, GameActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoadingScreenActivity.this.startActivity(mainIntent);
            LoadingScreenActivity.this.finish();
        }, WAIT_TIME);
    }

}

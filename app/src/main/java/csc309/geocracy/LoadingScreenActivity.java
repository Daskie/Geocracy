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
    private static final int waitTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        ProgressBar loadingSpinner = findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        Toasty.warning(this, "Your world is being created... hang tight!",  waitTime - 200).show();

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(LoadingScreenActivity.this, GameActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoadingScreenActivity.this.startActivity(mainIntent);
            LoadingScreenActivity.this.finish();
        }, waitTime);
    }

}

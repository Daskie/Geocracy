package csc_cccix.geocracy;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.rohitss.uceh.UCEHandler;

import csc_cccix.geocracy.main_menu.MenuActivity;
import es.dmoral.toasty.Toasty;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UCE_Handler Library
        new UCEHandler.Builder(this).addCommaSeparatedEmailAddresses("daskie.q@gmail.com,andrewxton373@gmail.com").build();

        // 8 bit color format
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // No title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Check if device supports OpenGL ES 3.0
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion < 0x30000) {
            Log.e("", "Device does not support OpenGL ES 3.0. Supported version: " + Integer.toHexString(info.reqGlEsVersion));
//            Toasty.error(getApplicationContext(), "Device does not support OpenGL ES 3.0. Supported version: \" + Integer.toHexString(info.reqGlEsVersion)");
        }


        startService(new Intent(this, AudioService.class));

        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

}

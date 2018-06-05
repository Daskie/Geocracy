package csc_cccix.geocracy.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import csc_cccix.R;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.GameData;
import csc_cccix.geocracy.game.Player;
import es.dmoral.toasty.Toasty;

public class GameSetupFragment extends Fragment {

    private int playerCount = 4;
    private TextView playerCountView;

    private ColorPickerDialog colorPicker;
    private ImageView playerColorIcon;
    private int playerColorSelection;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_setup, container, false);

        colorPicker = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(true)
                .setDialogId(10)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(false)
                .create();

        colorPicker.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                Log.i("COLOR_SELECTED", Integer.toHexString((int) color));
                playerColorSelection = Color.parseColor("#" + Integer.toHexString((int) color));
                playerColorIcon.setBackgroundColor(playerColorSelection);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });

        playerCountView = view.findViewById(R.id.playerCount);

        playerColorIcon = view.findViewById(R.id.playerColorIcon);

        Button playerColorBtn = view.findViewById(R.id.playerColorBtn);
        RxView.touches(playerColorBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                colorPicker.show(getActivity().getFragmentManager(), "COLOR_PICKER");
            }
        });

        SeekBar numberofPlayers = view.findViewById(R.id.numberofPlayers);
        numberofPlayers.setProgress(playerCount);

        RxSeekBar.changeEvents(numberofPlayers).subscribe(e -> {
            playerCount = e.view().getProgress() + 4;
            playerCountView.setText(Integer.toString(playerCount));
        });

        Button confirmGameSettings = view.findViewById(R.id.confirmGameSettingsBtn);

        RxView.touches(confirmGameSettings).subscribe(e -> {
            Toasty.warning(this.getContext(), "Your world is being created... hang tight!", Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(this.getContext(), GameActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            GameData newGameData = new GameData();
            newGameData.players = new Player[playerCount];
            newGameData.mainPlayerColor = playerColorSelection;
            mainIntent.putExtra("GAME_LOAD", newGameData);
            startActivity(mainIntent);
        });

        return view;
    }

}
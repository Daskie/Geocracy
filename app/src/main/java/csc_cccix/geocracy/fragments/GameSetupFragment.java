package csc_cccix.geocracy.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.Random;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import es.dmoral.toasty.Toasty;
import glm_.vec3.Vec3;

public class GameSetupFragment extends Fragment {

    private int playerCount = Game.DEFAULT_N_PLAYERS;
    private TextView playerCountView;

    private ColorPickerDialog colorPicker;
    private ImageView playerColorIcon;
    private int playerColorSelection;

    private EditText playerNameField;
    private String playerName;

    private EditText worldSeedField;
    private Long worldSeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_setup, container, false);

        playerName = "ANONYMOUS";

        playerNameField = view.findViewById(R.id.playerNameField);
        playerNameField.setText(playerName);
        RxTextView.textChanges(playerNameField).subscribe((value) -> {
            if (value.length() > 0) {
                playerName = value.toString();
            }
        });

        worldSeedField = view.findViewById(R.id.worldSeedField);
        worldSeedField.setText("309");
        RxTextView.textChanges(worldSeedField).subscribe((value) -> {
            if (value.length() > 0) {
                worldSeed = Long.parseLong(value.toString());
            }
        });


        Vec3[] colorPresets = Util.genDistinctColors(25, 0.0f);
        int[] colorIntPresets = new int[colorPresets.length];
        for (int i = 0; i < colorPresets.length; ++i) colorIntPresets[i] = Util.colorToInt(colorPresets[i]);
        playerColorSelection = colorIntPresets[new Random().nextInt(colorIntPresets.length)];
        colorPicker = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setPresets(colorIntPresets)
                .setColor(playerColorSelection)
                .setAllowCustom(false)
                .setShowColorShades(false)
                .setDialogId(10)
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
        playerColorIcon.setBackgroundColor(playerColorSelection);

        Button playerColorBtn = view.findViewById(R.id.playerColorBtn);
        RxView.touches(playerColorBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                colorPicker.show(getActivity().getFragmentManager(), "COLOR_PICKER");
            }
        });

        SeekBar numberofPlayers = view.findViewById(R.id.numberofPlayers);
        numberofPlayers.setMax(Game.MAX_N_PLAYERS - Game.MIN_N_PLAYERS);
        numberofPlayers.setProgress(playerCount - Game.MIN_N_PLAYERS);

        RxSeekBar.changeEvents(numberofPlayers).subscribe(e -> {
            playerCount = e.view().getProgress() + Game.MIN_N_PLAYERS;
            playerCountView.setText(Integer.toString(playerCount));
        });

        Button confirmGameSettings = view.findViewById(R.id.confirmGameSettingsBtn);

        RxView.touches(confirmGameSettings).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                Toasty.warning(this.getContext(), "Your world is being created... hang tight!", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(this.getContext(), GameActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra("PLAYER_NAME", playerName);
                mainIntent.putExtra("NUM_PLAYERS", playerCount);
                mainIntent.putExtra("MAIN_PLAYER_COLOR", playerColorSelection);
                mainIntent.putExtra("SEED", worldSeed); // TODO: implement seed text field or something
                startActivity(mainIntent);
            }
        });

        return view;
    }

}

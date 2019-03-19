package csc_cccix.geocracy.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javafaker.Faker;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.math.BigInteger;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.backend.Game;
import csc_cccix.geocracy.game.GameActivity;
import es.dmoral.toasty.Toasty;
import glm_.vec3.Vec3;

public class GameSetupFragment extends Fragment {

    private Button playerColorButton;
    private int playerColorSelection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_setup, container, false);

        ColorPickerDialog colorPicker;
        EditText playerNameField;

        TextView playerCountText;
        SeekBar playerCountSeekBar;
        EditText worldSeedField;

        playerNameField = view.findViewById(R.id.playerNameField);
        playerNameField.setText((new Faker()).name().firstName());

        worldSeedField = view.findViewById(R.id.worldSeedField);
        worldSeedField.setText(Long.toHexString((new Random()).nextLong()));


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
                Log.i("COLOR_SELECTED", Integer.toHexString(color));
                playerColorSelection = Color.parseColor("#" + Integer.toHexString(color));
                playerColorButton.setBackgroundTintList(ColorStateList.valueOf(playerColorSelection));
            }

            @Override
            public void onDialogDismissed(int dialogId) {
                // Do nothing when dismissed
            }
        });

        playerCountText = view.findViewById(R.id.playerCount);

        playerColorButton = view.findViewById(R.id.playerColorBtn);
        playerColorButton.setBackgroundTintList(ColorStateList.valueOf(playerColorSelection));
        RxView.touches(playerColorButton).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                colorPicker.show(getActivity().getFragmentManager(), "COLOR_PICKER");
            }
        });

        playerCountSeekBar = view.findViewById(R.id.numberofPlayers);
        playerCountSeekBar.setMax(Game.MAX_N_PLAYERS - Game.MIN_N_PLAYERS);
        playerCountSeekBar.setProgress(Game.DEFAULT_N_PLAYERS - Game.MIN_N_PLAYERS);

        RxSeekBar.changeEvents(playerCountSeekBar).subscribe(e -> playerCountText.setText(Integer.toString(e.view().getProgress() + Game.MIN_N_PLAYERS)));

        Button confirmGameSettings = view.findViewById(R.id.confirmGameSettingsBtn);

        RxView.touches(confirmGameSettings).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                String playerName = playerNameField.getText().toString();
                if (playerName.isEmpty()) {
                    Toasty.error(this.getContext(), "Please choose a name!", Toast.LENGTH_LONG).show();
                    return;
                }
                int playerCount = playerCountSeekBar.getProgress() + Game.MIN_N_PLAYERS;
                long worldSeed;
                try {
                    BigInteger bi = new BigInteger(worldSeedField.getText().toString(), 16);
                    if (bi.bitLength() > 64) {
                        throw new NumberFormatException();
                    }
                    worldSeed = bi.longValue();
                } catch (NumberFormatException ex) {
                    Toasty.error(this.getContext(), "Invalid seed!", Toast.LENGTH_LONG).show();
                    return;
                }

                Toasty.warning(this.getContext(), "Your world is being created... hang tight!", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(this.getContext(), GameActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra("PLAYER_NAME", playerName);
                mainIntent.putExtra("NUM_PLAYERS", playerCount);
                mainIntent.putExtra("MAIN_PLAYER_COLOR", playerColorSelection);
                mainIntent.putExtra("SEED", worldSeed);
                startActivity(mainIntent);
            }
        });

        return view;
    }

}

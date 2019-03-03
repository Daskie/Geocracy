package csc_cccix.geocracy.game;

import android.view.Gravity;
import android.widget.Toast;

import csc_cccix.geocracy.backend.game.Game;
import es.dmoral.toasty.Toasty;

public class Notifications {

    private Game game;

    public Notifications(Game game) {
        this.game = game;
    }

    public void showDefendNotification() {
        game.getActivity().runOnUiThread(() -> {
                Toast toast = Toasty.info(game.getActivity().getBaseContext(), "You are being attacked, select your defense!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 360);
                toast.show();
        });
    }

    public void showSelectTerritoryToAcquireNotification() {
        game.getActivity().runOnUiThread(() -> {
            Toast toast = Toasty.info(game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 180);
            toast.show();
        });
    }

    public void showTerritoryAlreadyAcquiredNotification() {
        game.getActivity().runOnUiThread(() -> {
            Toast toast = Toasty.info(game.getActivity().getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 180);
            toast.show();
        });
    }

    public void showCannotAssignUnitsToAnothersTerritoryNotification() {
        game.getActivity().runOnUiThread(() -> {
            Toast toast = Toasty.info(game.getActivity().getBaseContext(), "Cannot assign units to another players territory!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 180);
            toast.show();
        });
    }

    public void showInsufficentUnitPoolNotification() {
        game.getActivity().runOnUiThread(() -> {
            Toast toast = Toasty.info(game.getActivity().getBaseContext(), "You don't have enough units to add to this territory.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 180);
            toast.show();
        });
    }

    public void showInsufficentTerritoryUnitsNotification() {
        game.getActivity().runOnUiThread(() -> {
            Toast toast = Toasty.info(game.getActivity().getBaseContext(), "Cannot remove units from territory.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 180);
            toast.show();
        });
    }

}

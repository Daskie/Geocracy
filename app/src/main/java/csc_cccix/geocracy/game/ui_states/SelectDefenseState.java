package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import csc_cccix.geocracy.fragments.troop_selection.DefendingTroopSelectionFragment;
import csc_cccix.geocracy.fragments.troop_selection.TroopSelectionFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SelectDefenseState extends IGameplayState {

    private final String TAG = "SELECT_DEFENSE_STATE";

    DefendingTroopSelectionFragment troopSelectionFragment;

    private Territory attackingTerritory;
    private Territory defendingTerritory;

    private DiceRoll attackerDiceRoll;

    public SelectDefenseState(IStateMachine SM, Territory attackingTerritory, Territory defendingTerritory, DiceRoll attackerDiceRoll) {
        super(SM);
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
        this.attackerDiceRoll = attackerDiceRoll;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.d(TAG, "INIT STATE");

        troopSelectionFragment = DefendingTroopSelectionFragment.newInstance(attackingTerritory, defendingTerritory);
        SM.Game.UI.showBottomPaneFragment(troopSelectionFragment);
        SM.Game.getCameraController().targetTerritory(defendingTerritory);

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.UI.hideAllGameInteractionButtons();
            if (SM.Game.getControllingPlayer() instanceof HumanPlayer) {
                SM.Game.Notifications.showDefendNotification();
                SM.Game.UI.getConfirmButton().show();
            }
        });

    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");
        SM.Game.UI.removeActiveBottomPaneFragment();
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CONFIRM_TAPPED:

                if (attackingTerritory != null && defendingTerritory != null) {
                    int selectedNumberOfUnits = troopSelectionFragment.getSelectedNumberOfUnits();
                    if (selectedNumberOfUnits > 0) {
                        DiceRoll defenderDiceRoll = new DiceRoll(defendingTerritory, selectedNumberOfUnits, false);
                        SM.Advance(new BattleInitiatedState(SM, attackerDiceRoll, defenderDiceRoll));
                    }
                }

                break;

            case UNIT_COUNT_SELECTED:

                if (event.payload != null) {
                    troopSelectionFragment.selectUnitCount((int) event.payload);
                }

                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CAN NOT CANCEL A DEFENSE STATE!");
                break;

        }

        return false;
    }

    public Territory getDefendingTerritory() {
        return defendingTerritory;
    }
}

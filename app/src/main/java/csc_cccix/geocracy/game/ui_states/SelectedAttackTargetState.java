package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.troop_selection.AttackingTroopSelectionFragment;
import csc_cccix.geocracy.fragments.troop_selection.TroopSelectionFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.world.Territory;

public class SelectedAttackTargetState extends IGameplayState {

    private final String TAG = "SELECTED_ATTACK_TARGET_STATE";

    AttackingTroopSelectionFragment troopSelectionFragment;

    private Territory attackingTerritory;
    private Territory defendingTerritory;

    public SelectedAttackTargetState(IStateMachine SM, Territory attackingTerritory, Territory defendingTerritory) {
        super(SM);
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.d(TAG, "INIT STATE");

        troopSelectionFragment = AttackingTroopSelectionFragment.newInstance(attackingTerritory, defendingTerritory);
        SM.Game.UI.showBottomPaneFragment(troopSelectionFragment);
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(attackingTerritory);
        SM.Game.getWorld().targetTerritory(defendingTerritory);
        SM.Game.getCameraController().targetTerritory(defendingTerritory);

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.UI.hideAllGameInteractionButtons();
            if (SM.Game.getControllingPlayer() instanceof HumanPlayer) {
                SM.Game.UI.setAttackModeButtonVisibilityAndActiveState(true, true);
                SM.Game.UI.getConfirmButton().show();
                SM.Game.UI.getCancelBtn().show();
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
                        DiceRoll attackerDiceRoll = new DiceRoll(attackingTerritory, selectedNumberOfUnits, true);
                        SM.Advance(new SelectDefenseState(SM, attackingTerritory, defendingTerritory, attackerDiceRoll));
                    }
                }

                break;

            case UNIT_COUNT_SELECTED:

                if (event.payload != null) {
                    troopSelectionFragment.selectUnitCount((int) event.payload);
                }

                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));
                break;

        }

        return false;
    }
}

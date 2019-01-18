package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState extends IGameplayState {

    private final String TAG = "INTENT_TO_ATTACK_STATE";

    private Territory attackingTerritory;

    public IntentToAttackState(IStateMachine SM, Territory attackingTerritory) {
        super(SM);
        this.attackingTerritory = attackingTerritory;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.d(TAG, "INIT STATE");
    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    Territory defendingTerritory = (Territory) event.payload;

                    Log.i(TAG, "ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
                    if (attackingTerritory.getAdjacentEnemyTerritories().contains(defendingTerritory)) {
                        SM.Advance(new SelectedAttackTargetState(SM, attackingTerritory, defendingTerritory));
                    } else {
                        Log.d(TAG, "CANNOT ATTACK THIS TERRITORY!");
                    }

                }

                break;

            case CANCEL_TAPPED:

                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));

                break;

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}

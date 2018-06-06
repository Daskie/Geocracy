package csc_cccix.geocracy.states;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.world.Territory;

public interface GameState {
    // ALL AVAILABLE ACTIONS
    void cancelAction();
    void confirmAction();
    void selectOriginTerritory(Territory territory);
    void selectTargetTerritory(Territory territory);
    void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails);
    void battleCompleted(BattleResultDetails battleResultDetails);
    void addToSelectedTerritoryUnitCount(int amount);

    void enableAttackMode();
    void initState();
}
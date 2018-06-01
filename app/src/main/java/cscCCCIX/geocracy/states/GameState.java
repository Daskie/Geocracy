package cscCCCIX.geocracy.states;

import cscCCCIX.geocracy.world.Territory;

public interface GameState {
    // ALL AVAILABLE ACTIONS
    void cancelAction();
    void selectOriginTerritory(Territory territory);
    void selectTargetTerritory(Territory territory);
    void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails);
    void battleCompleted(BattleResultDetails battleResultDetails);
    void addToSelectedTerritoryUnitCount(int amount);

    void enableAttackMode();
    void initState();
}
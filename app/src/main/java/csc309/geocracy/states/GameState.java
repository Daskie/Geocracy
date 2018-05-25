package csc309.geocracy.states;

import csc309.geocracy.world.Territory;

public interface GameState {
    // ALL AVAILABLE ACTIONS
    void cancelAction();
    void selectOriginTerritory(Territory territory);
    void selectTargetTerritory(Territory territory);
    void enableAttackMode();
    void initState();
}
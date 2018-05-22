package csc309.geocracy.states;

//public enum GameState {
//    SELECT_TERRITORY,
//    ATTACK_TERRITORY,
//    DEFEND_TERRITORY,
//    DISPLAY_SETTINGS
//}

import java.util.HashSet;

import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.GameData;
import csc309.geocracy.world.Territory;

public interface GameState {
    // ALL AVAILABLE ACTIONS
    void cancelAction();
    void selectTerritory(Territory territory);
    void enableAttackMode();
    void initState();
}


//public class GameState {
//
//    public static GameData data;
//    public static StateWithActionHandler currentState;
//
//    public static Territory currentTerritorySelection = null;
//
//
//    public GameState(GameData data) {
//        this.data = data;
//    }
//
//    public void setCurrentState(StateWithActionHandler state) {
//        this.currentState = state;
//    }
//
//    public static StateWithActionHandler getCurrentState() {
//        return currentState;
//    }
//
//
//
//
//
//    class SelectAdjacentTerritoryState implements StateWithActionHandler {
//        public SelectAdjacentTerritoryState(Territory originTerritory) {
//            HashSet<Territory> adjacentTerritories = originTerritory.getAdjacentTerritories();
//        }
//
//        public void handleGameEvent(GameEvent event) {
//
//        }
//    }
//
//    interface StateWithActionHandler {
//        public void handleGameEvent(GameEvent event);
//    }
//
//
//}

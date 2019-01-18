package csc_cccix.geocracy.game.ui_states;

import csc_cccix.geocracy.game.ui_states.GameAction;

public class GameEvent {

    public GameAction action;
    public Object payload;

    public GameEvent(GameAction action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

}

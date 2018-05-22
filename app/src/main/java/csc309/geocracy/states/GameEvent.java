package csc309.geocracy.states;

public class GameEvent {

    public GameAction action;
    public Object payload;

    public GameEvent(GameAction action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

}

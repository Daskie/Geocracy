package csc_cccix.geocracy.game;

import csc_cccix.geocracy.game.ui_states.DefaultState;
import csc_cccix.geocracy.states.GameAction;

public abstract class IStateMachine {

    public Game Game;

    IState currentState;
    IState previousState;

    public IStateMachine(Game game) {
        this.Game = game;
    }

    public abstract IState CurrentState();

    // List of possible transitions we can make from this current state (helpful for debugging)
    public abstract String[] PossibleTransitions();

    // Returns true if the state machine has reached a completion state
    public abstract boolean IsComplete();

    public boolean Advance(IState nextState) {
        if (this.currentState != null) {
            // Previous State Teardown
            this.currentState.DeinitializeState();
            this.previousState = this.currentState;
        }

        // New State Buildup
        this.currentState = nextState;
        this.currentState.InitializeState();

        return true;
    }

    // Pass action handler to current state
    public boolean HandleAction(GameAction action) {
        return currentState.HandleAction(action);
    }

}
package csc_cccix.geocracy.game;

import java.io.Serializable;

import csc_cccix.geocracy.backend.game.Game;
import csc_cccix.geocracy.game.ui_states.GameEvent;

public abstract class IStateMachine implements Serializable {

    public csc_cccix.geocracy.backend.game.Game Game;

    public IState currentState;
    public IState previousState;

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
    public boolean HandleEvent(GameEvent event) {
        return currentState.HandleEvent(event);
    }

}
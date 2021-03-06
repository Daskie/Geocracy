package csc_cccix.geocracy.game;

import java.util.LinkedList;
import java.util.List;

import csc_cccix.geocracy.game.ui_states.DistributeTerritoriesState;

public class GameStateMachine extends IStateMachine {

    public GameStateMachine(Game game) {
        super(game);

        // Create and link all the states here...
    }

    public void Start() {
        Advance(new DistributeTerritoriesState(this));
//        Advance(new DefaultState(this));
    }

    @Override
    public IState CurrentState() {
        return currentState;
    }

    @Override
    public String[] PossibleTransitions() {

        List<String> result = new LinkedList<>();
        for (IState state: currentState.Neighbors) {
            result.add(state.GetName());
        }

        return (String[]) result.toArray();
    }

    @Override
    public boolean IsComplete() {
        return false;
    }

}

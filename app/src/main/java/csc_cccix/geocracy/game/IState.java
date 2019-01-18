package csc_cccix.geocracy.game;

import java.util.List;

import csc_cccix.geocracy.old_states.GameEvent;

public abstract class IState {

    public List<IState> Neighbors;
    public IStateMachine SM;

    public IState(IStateMachine SM) {
        this.SM = SM;
    }

    public abstract String GetName();
    public abstract void InitializeState();
    public abstract void DeinitializeState();
    public abstract boolean HandleEvent(GameEvent event);

    @Override
    public String toString()  {
        return GetName();
    }

}

package csc309.geocracy.states;

import csc309.geocracy.EventBus;
import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.GameData;

public class CurrentState {

    GameActivity activity;
    GameData data;

    GameState current;
    GameState previous;

    public CurrentState(GameActivity activity, GameData data) {
        activity = activity;
        data = data;
        current = GameState.SELECT_TERRITORY;

        EventBus.subscribe("GAME_STATE_CHANGE", this, e -> {
            setState((GameState) e);
        });
    }

    private void setState(GameState newState) {

        previous = current;

        switch (newState) {
            case SELECT_TERRITORY:
                System.out.println("SELECT TERRITORY STATE");
                GameActivity.showBottomPaneFragment(new TerritoryDetailFragment());
                break;
            case ATTACK_TERRITORY:
                System.out.println("ATTACK TERRITORY STATE");
                GameActivity.showBottomPaneFragment(new TroopSelectionFragment());
                break;
            case DEFEND_TERRITORY:
                System.out.println("DEFEND TERRITORY STATE");
                break;
            case DISPLAY_SETTINGS:
                System.out.println("DISPLAY SETTINGS STATE");
                GameActivity.toggleSettingsFragment();
                break;
            default:
                break;

        }

        current = newState;

    }


}

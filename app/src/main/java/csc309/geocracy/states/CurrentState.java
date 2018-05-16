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

//        EventBus.subscribe("GAME_STATE_CHANGE", this, e -> {
//            setState((GameState) e);
//        });

        EventBus.subscribe("USER_ACTION", this, action -> {
            handleUserAction((GameAction) action);
        });
    }

    private GameAction previousAction;

    private void handleUserAction(GameAction action) {

        switch (action) {

            case TOGGLE_SETTINGS_VISIBILITY:
                System.out.println("TOGGLE SETTINGS VISIBILITY ACTION");
                GameActivity.toggleSettingsFragment();
                break;

            case TERRITORY_SELECTED:
                System.out.println("USER SELECTEDTERRITORY");
                GameActivity.showBottomPaneFragment(new TerritoryDetailFragment());
                break;

            case ATTACK_TAPPED:
                System.out.println("USER TAPPED ATTACK");

                if (previousAction == action.TERRITORY_SELECTED) {
                    System.out.println("TERRITORY SELECTED -> ATTACK");
                    GameActivity.showBottomPaneFragment(new TroopSelectionFragment());
                } else {
                    System.out.println("TERRITORY NOT SELECTED -> UNABLE TO DO ANYTHING");
                }

                break;

            case CANCEL_ACTION:
                System.out.println("USER CANCELED ACTION");
                GameActivity.removeActiveBottomPaneFragment();
                break;

            default:
                break;

        }

        previousAction = action;
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

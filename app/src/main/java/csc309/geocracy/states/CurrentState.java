package csc309.geocracy.states;

import csc309.geocracy.EventBus;
import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.GameData;
import csc309.geocracy.world.Territory;

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

        EventBus.subscribe("USER_ACTION", this, event -> {
            handleUserAction((GameEvent) event);
        });
    }

    private GameAction previousAction;
    private Territory currentTerritorySelection;

    private void handleUserAction(GameEvent event) {

        switch (event.action) {

            case TOGGLE_SETTINGS_VISIBILITY:
                System.out.println("TOGGLE SETTINGS VISIBILITY ACTION");
                GameActivity.toggleSettingsFragment();
                break;

            case TERRITORY_SELECTED:
                System.out.println("USER SELECTED TERRITORY");
                GameActivity.showBottomPaneFragment(new TerritoryDetailFragment());
                Territory selectedTerritory = (Territory) event.payload;
                currentTerritorySelection = selectedTerritory;
                GameActivity.game.getWorld().selectTerritory(selectedTerritory);
                GameActivity.game.getWorld().unhighlightTerritories();
                GameActivity.game.cameraController.targetTerritory(selectedTerritory);
                break;

            case ATTACK_TAPPED:
                System.out.println("USER TAPPED ATTACK");

                if (previousAction == event.action.TERRITORY_SELECTED && currentTerritorySelection != null) {
                    System.out.println("TERRITORY SELECTED -> ATTACK");
                    GameActivity.game.getWorld().highlightTerritories(currentTerritorySelection.getAdjacentTerritories());
                    GameActivity.showBottomPaneFragment(new TroopSelectionFragment());
                } else {
                    System.out.println("TERRITORY NOT SELECTED -> UNABLE TO DO ANYTHING");
                }

                break;

            case CANCEL_ACTION:
                System.out.println("USER CANCELED ACTION");
                currentTerritorySelection = null;
                GameActivity.removeActiveBottomPaneFragment();
                GameActivity.game.getWorld().unselectTerritory();
                GameActivity.game.getWorld().unhighlightTerritories();
                break;

            default:
                break;

        }

        previousAction = event.action;
    }

//    private void setState(GameState newState) {
//
//        previous = current;
//
//        switch (newState) {
//            case SELECT_TERRITORY:
//                System.out.println("SELECT TERRITORY STATE");
//                GameActivity.showBottomPaneFragment(new TerritoryDetailFragment());
//                break;
//            case ATTACK_TERRITORY:
//                System.out.println("ATTACK TERRITORY STATE");
//                GameActivity.showBottomPaneFragment(new TroopSelectionFragment());
//                break;
//            case DEFEND_TERRITORY:
//                System.out.println("DEFEND TERRITORY STATE");
//                break;
//            case DISPLAY_SETTINGS:
//                System.out.println("DISPLAY SETTINGS STATE");
//                GameActivity.toggleSettingsFragment();
//                break;
//            default:
//                break;
//
//        }
//
//        current = newState;
//
//    }


}

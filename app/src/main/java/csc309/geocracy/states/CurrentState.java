//package csc309.geocracy.states;
//
//import android.os.Bundle;
//
//import csc309.geocracy.EventBus;
//import csc309.geocracy.fragments.TerritoryDetailFragment;
//import csc309.geocracy.fragments.TroopSelectionFragment;
//import csc309.geocracy.game.Game;
//import csc309.geocracy.game.GameActivity;
//import csc309.geocracy.game.GameData;
//import csc309.geocracy.world.Territory;
//
//public class CurrentState {
//
//    public static GameActivity activity;
//    GameData data;
//
//    GameState state;
////    GameState previousState;
//
//    public CurrentState(GameActivity activity, GameData data) {
//        this.activity = activity;
//        this.data = data;
//
//        state =
//        state.setCurrentState(Game.DefaultState);
//
//
//
//    }
//
//    private GameAction previousAction;
//    private Territory currentTerritorySelection;
//    private boolean attackSelection = false;
//
//
//
//
//
////    private void setState(GameState newState) {
////
////        previous = current;
////
////        switch (newState) {
////            case SELECT_TERRITORY:
////                System.out.println("SELECT TERRITORY STATE");
////                GameActivity.showBottomPaneFragment(new TerritoryDetailFragment());
////                break;
////            case ATTACK_TERRITORY:
////                System.out.println("ATTACK TERRITORY STATE");
////                GameActivity.showBottomPaneFragment(new TroopSelectionFragment());
////                break;
////            case DEFEND_TERRITORY:
////                System.out.println("DEFEND TERRITORY STATE");
////                break;
////            case DISPLAY_SETTINGS:
////                System.out.println("DISPLAY SETTINGS STATE");
////                GameActivity.toggleSettingsFragment();
////                break;
////            default:
////                break;
////
////        }
////
////        current = newState;
////
////    }
//
//
//}

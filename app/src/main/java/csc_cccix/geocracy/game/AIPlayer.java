package csc_cccix.geocracy.game;

import android.util.Log;

import com.github.javafaker.Faker;

import java.util.Random;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.ui_states.BattleResultsState;
import csc_cccix.geocracy.game.ui_states.DefaultState;
import csc_cccix.geocracy.game.ui_states.DistributeTerritoriesState;
import csc_cccix.geocracy.game.ui_states.GameEvent;
import csc_cccix.geocracy.game.ui_states.IGameplayState;
import csc_cccix.geocracy.game.ui_states.IntentToAttackState;
import csc_cccix.geocracy.game.ui_states.PlaceReinforcementsState;
import csc_cccix.geocracy.game.ui_states.SelectedTerritoryState;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

import static csc_cccix.geocracy.game.Game.USER_ACTION;
import static csc_cccix.geocracy.game.ui_states.GameAction.ADD_UNIT_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.ATTACK_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.CANCEL_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.CONFIRM_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.END_TURN_TAPPED;
import static csc_cccix.geocracy.game.ui_states.GameAction.TERRITORY_SELECTED;

public class AIPlayer extends Player {

    private final String TAG = "AI_PLAYER";


    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED
    private static Faker faker = new Faker();


    public AIPlayer(int id, Vec3 color) {
        super(id, color);
        super.name = "CPU: " + faker.name().lastName();
    }

    private static Territory currentlySelectedTerritory = null;

    private static IGameplayState previousState = null;

    public static void handleComputerInputWithState(Game game, IGameplayState state) {
        if (state.getClass() == DistributeTerritoriesState.class) {
            Random rand = new Random();
            int randNum = rand.nextInt(game.getWorld().getUnoccupiedTerritories().size()) + 1;
            Territory terr = game.getWorld().getUnoccTerritory(randNum);
            EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, terr));
            EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));

        } else if (state.getClass() == PlaceReinforcementsState.class) {
            RandomSet<Territory> randomSet = new RandomSet<>(game.getCurrentPlayer().getTerritories());
            Random gen = new Random();

            while (game.getCurrentPlayer().getArmyPool() > 0) {
                Territory terr = randomSet.pollRandom(gen);
                EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, terr));
                EventBus.publish(USER_ACTION, new GameEvent(ADD_UNIT_TAPPED, 1));
            }

            EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));

        } else if (state.getClass() == DefaultState.class) {

            // If AI just performed an attack, lets finish their turn for now... (definitely can be handled better lol)
            if (previousState != null && previousState.getClass() == BattleResultsState.class) {
                EventBus.publish(USER_ACTION, new GameEvent(END_TURN_TAPPED, null));
            } else {
                Territory terr = game.getCurrentPlayer().findTerrWithMaxArmies();
                EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, terr));
                currentlySelectedTerritory = terr;
            }

        } else if (state.getClass() == SelectedTerritoryState.class) {
            if (currentlySelectedTerritory != null) {
                EventBus.publish(USER_ACTION, new GameEvent(ATTACK_TAPPED, null));
            }
        } else if (state.getClass() == IntentToAttackState.class) {
            if (currentlySelectedTerritory != null) {

                RandomSet<Territory> randomSet = new RandomSet<>(currentlySelectedTerritory.getAdjacentEnemyTerritories());
                Random rand = new Random();

                EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, randomSet.pollRandom(rand)));
                EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));
            }
        } else if (state.getClass() == BattleResultsState.class) {
            EventBus.publish(USER_ACTION, new GameEvent(CANCEL_TAPPED, null));
        }

        previousState = state;
    }
}

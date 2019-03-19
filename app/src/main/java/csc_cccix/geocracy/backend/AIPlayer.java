package csc_cccix.geocracy.backend;

import com.github.javafaker.Faker;

import csc_cccix.geocracy.backend.world.Territory;
import glm_.vec3.Vec3;

public class AIPlayer extends Player {

    private final String TAG = "AI_PLAYER";

    private static Faker faker = new Faker();


    public AIPlayer(int id, Vec3 color) {
        super(id, color);
        super.name = "CPU: " + faker.name().lastName();
    }

    private static Territory attackingTerritory = null;
    private static Territory currentlySelectedTerritory = null;

    //private static IGameplayState previousState = null;

    /*public static void handleComputerInputWithState(Game game, IGameplayState state) {
        if (state.getClass() == DistributeTerritoriesState.class) {
            Territory terr = game.getWorld().getRandomUnoccTerritory();
            EventBus.publish(USER_ACTION, new GameEvent(TERRITORY_SELECTED, terr));
            EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));

        } else if (state.getClass() == PlaceReinforcementsState.class) {
            RandomSet<Territory> randomSet = new RandomSet<>(game.getGameData().getCurrentPlayer().getOwnedTerritories());
            Random gen = new Random();

            while (game.getGameData().getCurrentPlayer().getArmyPool() > 0) {
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
                Territory terr = game.getGameData().getCurrentPlayer().findTerrWithMaxArmies();
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

                attackingTerritory = currentlySelectedTerritory;
            }
        } else if (state.getClass() == SelectedAttackTargetState.class) {

            int randomAttackUnits = (new Random().nextInt() % 3) + 2;
            int randomBoundAttackWithUnits = Util.clamp(randomAttackUnits,2,attackingTerritory.getNArmies() - 2);

            EventBus.publish(USER_ACTION, new GameEvent(UNIT_COUNT_SELECTED, randomBoundAttackWithUnits));
            EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));


        } else if (state.getClass() == SelectDefenseState.class) {

            SelectDefenseState selectDefenseState = (SelectDefenseState) state;
            int randomDefendUnits = (new Random().nextInt() % 2) + 1;
            int randomBoundDefendWithUnits = Util.clamp(randomDefendUnits,1,selectDefenseState.getDefendingTerritory().getNArmies());

            EventBus.publish(USER_ACTION, new GameEvent(UNIT_COUNT_SELECTED, randomBoundDefendWithUnits));
            EventBus.publish(USER_ACTION, new GameEvent(CONFIRM_TAPPED, null));

        } else if (state.getClass() == BattleResultsState.class) {
            EventBus.publish(USER_ACTION, new GameEvent(CANCEL_TAPPED, null));
        }

        previousState = state;
    }*/
}

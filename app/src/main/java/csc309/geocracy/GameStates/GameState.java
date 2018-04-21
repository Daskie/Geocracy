package csc309.geocracy.GameStates;

public class GameState {
    StartGameState start;
    DisplayGlobeState display_globe;
    AttackingAdjacentTerritoriesState attack_terr;
    ChoosingNumArmiesAttackingState choosing_num_armies;
    EndAttackState end_attack;
    FortifyTerritoriesState fortify_terr;
    GainArmiesState gain_armies;
    OccupyingTerritoryState occupy_terr;
    PlaceArmiesState place_armies;
    RollDiceState roll_dice;
    SelectArmyUnitsState select_army;
    SetUpInitialTerritoriesState setup_terr;
    TurnState turns;

    void constructor(){
        this.start = new StartGameState();
        this.display_globe = new DisplayGlobeState();
        this.attack_terr = new AttackingAdjacentTerritoriesState();
        this.choosing_num_armies = new ChoosingNumArmiesAttackingState();
        this.end_attack = new EndAttackState();
        this.fortify_terr = new FortifyTerritoriesState();
        this.gain_armies = new GainArmiesState();
        this.occupy_terr = new OccupyingTerritoryState();
        this.place_armies = new PlaceArmiesState();
        this.roll_dice = new RollDiceState();
        this.select_army = new SelectArmyUnitsState();
        this.setup_terr = new SetUpInitialTerritoriesState();
        this.turns = new TurnState();
    }
}

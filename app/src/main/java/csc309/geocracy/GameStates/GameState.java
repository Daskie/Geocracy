package csc309.geocracy.GameStates;

public class GameState{
    StartGameState start;
    DisplayGlobeState display_globe;
    AttackingAdjTerrState attack_terr;
    ChoosingNumArmiesAttackingState choosing_num_armies;
    EndAttackState end_attack;
    FortifyTerrState fortify_terr;
    GainArmiesState gain_armies;
    OccupyingTerrState occupy_terr;
    PlaceArmiesState place_armies;
    RollDiceState roll_dice;
    SelectArmyUnitsState select_army;
    SetUpInitTerrState setup_terr;
    TurnState turns;

    public GameState(){
        this.start = new StartGameState();
        this.display_globe = new DisplayGlobeState();
        this.attack_terr = new AttackingAdjTerrState();
        this.choosing_num_armies = new ChoosingNumArmiesAttackingState();
        this.end_attack = new EndAttackState();
        this.fortify_terr = new FortifyTerrState();
        this.gain_armies = new GainArmiesState();
        this.occupy_terr = new OccupyingTerrState();
        this.place_armies = new PlaceArmiesState();
        this.roll_dice = new RollDiceState();
        this.select_army = new SelectArmyUnitsState();
        this.setup_terr = new SetUpInitTerrState();
        this.turns = new TurnState();
    }
}
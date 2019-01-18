package csc_cccix.geocracy.game.ui_states;

import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class BattleResult {

    public Territory attackingTerritory;
    public Territory defendingTerritory;

    public int attackingUnitLoss;
    public int defendingUnitLoss;

    public BattleResult(Territory attackingTerritory, int attackingUnitLoss, Territory defendingTerritory, int defendingUnitLoss) {
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
        this.attackingUnitLoss = attackingUnitLoss;
        this.defendingUnitLoss = defendingUnitLoss;
    }

}

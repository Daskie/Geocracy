package csc_cccix.geocracy.game.ui_states;

import java.io.Serializable;

import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class BattleResult implements Serializable {

    public Territory attackingTerritory;
    public Territory defendingTerritory;

    public int attackingUnitCount;
    public int defendingUnitCount;

    public int attackingUnitLoss;
    public int defendingUnitLoss;

    public BattleResult(Territory attackingTerritory, int attackingUnitCount, int attackingUnitLoss, Territory defendingTerritory, int defendingUnitCount, int defendingUnitLoss) {
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
        this.attackingUnitCount = attackingUnitCount;
        this.defendingUnitCount = defendingUnitCount;
        this.attackingUnitLoss = attackingUnitLoss;
        this.defendingUnitLoss = defendingUnitLoss;
    }

}

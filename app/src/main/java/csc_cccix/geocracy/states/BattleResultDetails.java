package csc_cccix.geocracy.states;

import csc_cccix.geocracy.world.Territory;

class BattleResultDetails {

    public Territory attackingTerritory;
    public int attackingUnitCount;

    public Territory defendingTerritory;
    public int defendingUnitCount;

    public BattleResultDetails(Territory attackingTerritory, int attackingUnitCount,
                               Territory defendingTerritory, int defendingUnitCount) {
        this.attackingTerritory = attackingTerritory;
        this.attackingUnitCount = attackingUnitCount;
        this.defendingTerritory = defendingTerritory;
        this.defendingUnitCount = defendingUnitCount;
    }

}

package cscCCCIX.geocracy.states;

import cscCCCIX.geocracy.world.Territory;

class DiceRollDetails {

    public Territory territory;
    public int unitCount;

    public DiceRollDetails(Territory territory, int unitCount) {
        System.out.println(territory);
        System.out.println("Battling With " + unitCount + " Units");
    }

}

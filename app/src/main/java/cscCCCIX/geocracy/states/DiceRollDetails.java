package cscCCCIX.geocracy.states;

import android.util.Log;

import cscCCCIX.geocracy.world.Territory;

class DiceRollDetails {

    public Territory territory;
    public int unitCount;

    public DiceRollDetails(Territory territory, int unitCount) {
        Log.i("", "" + territory.getId());
        Log.i("", "Battling With " + unitCount + " Units");
    }

}

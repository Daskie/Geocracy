package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.world.Territory;

class DiceRollDetails {

    public Territory territory;
    public int unitCount;

    public DiceRollDetails(Territory territory, int unitCount) {
        Log.i("", "" + territory.getId());
        Log.i("", "Battling With " + unitCount + " Units");
    }

}

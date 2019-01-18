package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import csc_cccix.geocracy.world.Territory;

public class DiceRoll {

    public Territory territory;
    public int unitCount;
    public boolean isAttacker;

    public Set<Integer> diceValues = null;

    public DiceRoll(Territory territory, int unitCount, boolean isAttacker) {
        Log.d("", "" + territory.getId());
        Log.d("", "Battling With " + unitCount + " Units");

        this.territory = territory;
        this.unitCount = unitCount;
        this.isAttacker = isAttacker;
    }

    private void calculateRoll() {

        diceValues = new HashSet<>();

        int numberOfDice;
        if (isAttacker) {
            numberOfDice = unitCount - 1;
        } else {
            numberOfDice = unitCount;
        }

        Random rGen = new Random();

        for (int i = 0; i < numberOfDice; i++) {
            diceValues.add(rGen.nextInt(6) + 1);
        }

    }

    public Set<Integer> getRolledDiceValues() {

        if (diceValues == null) {
            calculateRoll();
        }

        return diceValues;

    }

}

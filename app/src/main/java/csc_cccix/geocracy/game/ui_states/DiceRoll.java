package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import csc_cccix.geocracy.world.Territory;

public class DiceRoll implements Serializable {

    public Territory territory;
    public int unitCount;
    public boolean isAttacker;

    private List<Integer> diceValues = null;

    public DiceRoll(Territory territory, int unitCount, boolean isAttacker) {
        Log.d("", "" + territory.getId());
        Log.d("", "Battling With " + unitCount + " Units");

        this.territory = territory;
        this.unitCount = unitCount;
        this.isAttacker = isAttacker;
    }

    private void calculateRoll() {

        diceValues = Arrays.asList(-1, -1, -1);

        int numberOfDice;
        if (isAttacker) {
            numberOfDice = unitCount - 1;
        } else {
            numberOfDice = unitCount;
        }

        Random rGen = new Random();

        for (int i = 0; i < numberOfDice; i++) {
            diceValues.set(i, rGen.nextInt(6) + 1);
        }

        diceValues.sort((o1, o2) -> o1 - o2);

    }

    public List<Integer> getRolledDiceValues() {

        if (diceValues == null) {
            calculateRoll();
        }

        return diceValues;

    }

}

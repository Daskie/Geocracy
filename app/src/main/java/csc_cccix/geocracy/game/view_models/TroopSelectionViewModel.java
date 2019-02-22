package csc_cccix.geocracy.game.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import csc_cccix.geocracy.world.Territory;

public class TroopSelectionViewModel extends AndroidViewModel {

    private MutableLiveData<Territory> attackingTerritory = new MutableLiveData<>();
    private MutableLiveData<Territory> defendingTerritory = new MutableLiveData<>();

    public TroopSelectionViewModel(@NonNull Application application) {
        super(application);
    }

    public void setAttackingTerritory(Territory territory) {
        this.attackingTerritory.setValue(territory);
    }

    public void setDefendingTerritory(Territory territory) {
        this.defendingTerritory.setValue(territory);
    }

    public LiveData<Territory> getAttackingTerritory() {
        return attackingTerritory;
    }

    public LiveData<Territory> getDefendingTerritory() {
        return defendingTerritory;
    }

}

package csc_cccix.geocracy.game.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import csc_cccix.geocracy.backend.game.Player;

public class GameViewModel extends AndroidViewModel {
    private MutableLiveData<Player> currentPlayer = new MutableLiveData<>();

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCurrentPlayer(Player current) {
        currentPlayer.setValue(current);
    }

    public LiveData<Player> getCurrentPlayer() {
        return currentPlayer;
    }

}

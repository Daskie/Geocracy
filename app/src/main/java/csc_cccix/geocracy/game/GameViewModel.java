package csc_cccix.geocracy.game;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

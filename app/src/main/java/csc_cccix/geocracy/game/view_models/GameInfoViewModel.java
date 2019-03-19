package csc_cccix.geocracy.game.view_models;

/*import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import es.dmoral.toasty.Toasty;

public class GameInfoViewModel extends AndroidViewModel {

    private MutableLiveData<Long> worldSeed = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Player>> players = new MutableLiveData<>();

    public GameInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Long> getWorldSeed() {
        return worldSeed;
    }

    public LiveData<ArrayList<Player>> getPlayers() {
        return players;
    }

    public void setWorldSeed(Long seed) {
        this.worldSeed.setValue(seed);
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players.setValue(players);
    }

    public void copyWorldSeedToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("GEOCRACY_WORLD_SEED", "" + worldSeed.getValue());
        clipboard.setPrimaryClip(clip);
        Toasty.info(getApplication(), "Copied world seed").show();
    }
}*/

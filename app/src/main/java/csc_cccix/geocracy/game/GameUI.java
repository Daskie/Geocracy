package csc_cccix.geocracy.game;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import com.jakewharton.rxbinding2.view.RxView;
import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.CurrentPlayerFragment;
import csc_cccix.geocracy.game.ui_states.GameAction;
import csc_cccix.geocracy.game.ui_states.GameEvent;

import static csc_cccix.geocracy.game.Game.USER_ACTION;

public class GameUI {

    private GameActivity activity;
    private FragmentManager manager;

    private Fragment activeCurrentPlayerFragment = null;
    private Fragment activeOverlayFragment;
    private Fragment activeBottomPaneFragment;

    public FloatingActionButton endTurnButton;
    public FloatingActionButton attackBtn;
    public FloatingActionButton addUnitBtn;
    public FloatingActionButton removeUnitBtn;
    public FloatingActionButton cancelBtn;
    public FloatingActionButton gameInfoBtn;
    public FloatingActionButton settingBtn;
    public FloatingActionButton closeOverlayBtn;
    public FloatingActionButton confirmButton;
    public FloatingActionButton fortifyButton;

    public GameUI(GameActivity activity, FragmentManager manager) {
        this.activity = activity;
        this.manager = manager;
        setupGameFABs();
    }

    // Initialize Floating Action Buttons
    public void setupGameFABs() {

        // Get Layout Frame +
        CoordinatorLayout frame = activity.findViewById(R.id.gameLayout);
        LinearLayout uiLayout = new LinearLayout(activity);
        uiLayout.setOrientation(LinearLayout.VERTICAL);

        cancelBtn = activity.findViewById(R.id.cancelBtn);
        cancelBtn.hide();
        activity.disposables.add(RxView.touches(cancelBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CANCEL_TAPPED, null));
            }
        }));

        confirmButton = activity.findViewById(R.id.confirmButton);
        confirmButton.hide();
        activity.disposables.add(RxView.touches(confirmButton).subscribe(event -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CONFIRM_TAPPED, null));
            }
        }));

        endTurnButton = activity.findViewById(R.id.endTurnButton);
        endTurnButton.hide();
        activity.disposables.add(RxView.touches(endTurnButton).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.END_TURN_TAPPED, null));
            }
        }));

        attackBtn = activity.findViewById(R.id.attackBtn);
        attackBtn.hide();
        activity.disposables.add(RxView.touches(attackBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ATTACK_TAPPED, null));
            }
        }));

        fortifyButton = activity.findViewById(R.id.fortifyButton);
        fortifyButton.hide();
        activity.disposables.add(RxView.touches(fortifyButton).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.FORTIFY_TAPPED, null));
            }
        }));

        addUnitBtn = activity.findViewById(R.id.addUnitBtn);
        addUnitBtn.hide();
        activity.disposables.add(RxView.touches(addUnitBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ADD_UNIT_TAPPED, null));
            }
        }));

        removeUnitBtn = activity.findViewById(R.id.removeUnitBtn);
        removeUnitBtn.hide();
        activity.disposables.add(RxView.touches(removeUnitBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.REMOVE_UNIT_TAPPED, null));
            }
        }));

        settingBtn = activity.findViewById(R.id.inGameSettingsBtn);
        settingBtn.show();
        activity.disposables.add(RxView.touches(settingBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.SETTINGS_TAPPED, null));
            }
        }));

        gameInfoBtn = activity.findViewById(R.id.gameInfoBtn);
        gameInfoBtn.show();
        activity.disposables.add(RxView.touches(gameInfoBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.GAME_INFO_TAPPED, null));
            }
        }));

        closeOverlayBtn = activity.findViewById(R.id.closeOverlayBtn);
        closeOverlayBtn.hide();
        activity.disposables.add(RxView.touches(closeOverlayBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CLOSE_OVERLAY_TAPPED, null));
            }
        }));

        frame.addView(uiLayout);

    }

    // TODO: ui overlay with new state machine
    public void showOverlayFragment(Fragment overlayFragment) {
        FragmentTransaction ft = manager.beginTransaction();

        if (activeOverlayFragment != null) ft.remove(activeOverlayFragment); // If old overlay fragment is active, remove it
        ft.add(R.id.gameLayout, overlayFragment); // Add new overlay fragment to gui
        ft.commit();

        activeOverlayFragment = overlayFragment;

        activity.runOnUiThread(() -> {
            closeOverlayBtn.show();
            settingBtn.hide();
            gameInfoBtn.hide();
        });
    }

    public void removeOverlayFragment() {
        FragmentTransaction ft = manager.beginTransaction();

        if (activeOverlayFragment != null) {
            ft.remove(activeOverlayFragment); // If old overlay fragment is active, remove it
            activeOverlayFragment = null;
        }

        ft.commit();

        activity.runOnUiThread(() -> {
            closeOverlayBtn.hide();
            settingBtn.show();
            gameInfoBtn.show();
        });
    }


    public void showBottomPaneFragment(Fragment bottomPaneFragment) {
        FragmentTransaction ft = manager.beginTransaction();

        if (activeBottomPaneFragment != null) {
            ft.remove(activeBottomPaneFragment);
            activeBottomPaneFragment = null;
        }

        ft.add(R.id.gameLayout, bottomPaneFragment);
        ft.commit();

        activeBottomPaneFragment = bottomPaneFragment;
    }

    public void removeActiveBottomPaneFragment() {
        if (activeBottomPaneFragment != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(activeBottomPaneFragment);
            ft.commit();
            activeBottomPaneFragment = null;
        }
    }

    public void updateCurrentPlayerFragment() {
        CurrentPlayerFragment currentPlayerFragment = CurrentPlayerFragment.newInstance(GameActivity.game.getCurrentPlayer());
        FragmentTransaction ft = manager.beginTransaction();
        if (activeCurrentPlayerFragment != null) {
            ft.remove(activeCurrentPlayerFragment);
        }
        if (activeCurrentPlayerFragment != currentPlayerFragment) {
            ft.add(R.id.gameLayout, currentPlayerFragment);
            activeCurrentPlayerFragment = currentPlayerFragment;
        }
        ft.commit();
    }

    public void setAttackModeButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(attackBtn, isVisible, isActive);
    }

    public void setConfirmButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(confirmButton, isVisible, isActive);
    }

    public void setFortifyButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(fortifyButton, isVisible, isActive);
    }

    private void setFABVisibilityAndActiveState(FloatingActionButton fab, boolean isVisible, boolean isActive) {
        activity.runOnUiThread(() -> {
            AlphaAnimation alphaChange;
            if (isActive) { alphaChange = new AlphaAnimation(fab.getAlpha(), 1.0f); }
            else { alphaChange = new AlphaAnimation(fab.getAlpha(), 0.4f); }
            alphaChange.setFillAfter(true);
            fab.startAnimation(alphaChange);
            if (isVisible) { fab.show(); }
            else { fab.hide(); }
        });
    }

    public void setUpdateUnitCountButtonsVisibility(boolean isVisible) {
        activity.runOnUiThread(() -> {
            if (isVisible) {
                addUnitBtn.show();
                removeUnitBtn.show();
            } else {
                addUnitBtn.hide();
                removeUnitBtn.hide();
            }
        });
    }

    public void hideAllGameInteractionButtons() {
        activity.runOnUiThread(() -> {
            attackBtn.hide();
            cancelBtn.hide();
            addUnitBtn.hide();
            removeUnitBtn.hide();
            endTurnButton.hide();
            confirmButton.hide();
            fortifyButton.hide();
        });
    }

    public FloatingActionButton getAddUnitBtn() {
        return addUnitBtn;
    }

    public FloatingActionButton getAttackBtn() {
        return attackBtn;
    }

    public FloatingActionButton getCancelBtn() {
        return cancelBtn;
    }

    public FloatingActionButton getConfirmButton() { return confirmButton; }

    public FloatingActionButton getCloseOverlayBtn() {
        return closeOverlayBtn;
    }

    public FloatingActionButton getEndTurnButton() {
        return endTurnButton;
    }

    public FloatingActionButton getGameInfoBtn() {
        return gameInfoBtn;
    }

    public FloatingActionButton getRemoveUnitBtn() {
        return removeUnitBtn;
    }

    public FloatingActionButton getSettingBtn() {
        return settingBtn;
    }

}

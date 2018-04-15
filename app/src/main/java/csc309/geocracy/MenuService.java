package csc309.geocracy;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.HoverView;
import io.mattcarroll.hover.window.HoverMenuService;

public class MenuService extends HoverMenuService {

    @Override
    protected void onHoverMenuLaunched(@NonNull Intent intent, @NonNull HoverView hoverView) {
        // Configure and start your HoverView.
        HoverMenu menu = new Menu();
        hoverView.setMenu(menu);
        hoverView.collapse();
    }

}
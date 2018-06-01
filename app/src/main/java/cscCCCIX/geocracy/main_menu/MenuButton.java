package cscCCCIX.geocracy.main_menu;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;

public class MenuButton extends AppCompatButton {

    public MenuButton(Context context,  String text, OnTouchListener listener) {
        super(context);
        super.setOnTouchListener(listener);
        this.setText(text);
        customize();
    }

    private void customize() {
        this.setTextColor(Color.WHITE);
        this.setTextSize(24);
        this.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        this.setBackgroundColor(Color.argb(20, 0, 0, 0));
        this.setPadding(20, 20, 20, 20);
    }


}

package csc_cccix.geocracy.fragments;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import csc_cccix.R;

public class DiceFaceImageView extends AppCompatImageView {
    public DiceFaceImageView(Context context, Integer faceValue) {
        super(context);
        setLayoutParams(new LinearLayoutCompat.LayoutParams(80, ViewGroup.LayoutParams.MATCH_PARENT));
        setPadding(8,0,0,0);
        requestLayout();
        initResouceWithFaceValue(faceValue);
    }

    private void initResouceWithFaceValue(Integer faceValue) {
        switch (faceValue) {
            case 1:
                setImageResource(R.drawable.ic_dice_six_faces_one);
                break;
            case 2:
                setImageResource(R.drawable.ic_dice_six_faces_two);
                break;
            case 3:
                setImageResource(R.drawable.ic_dice_six_faces_three);
                break;
            case 4:
                setImageResource(R.drawable.ic_dice_six_faces_four);
                break;
            case 5:
                setImageResource(R.drawable.ic_dice_six_faces_five);
                break;
            case 6:
                setImageResource(R.drawable.ic_dice_six_faces_six);
                break;
            default:
                throw new RuntimeException("Invalid dice face value!");
        }
    }
}
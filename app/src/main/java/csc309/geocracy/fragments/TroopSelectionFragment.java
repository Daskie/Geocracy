package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import csc309.geocracy.R;

public class TroopSelectionFragment extends Fragment {

    private static final String TAG = "TROOP_SELECTION_FRAGMENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.troop_selection, container, false);

        // get the bottom sheet view
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);

        return view;
    }
}

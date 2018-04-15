package csc309.geocracy;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import io.mattcarroll.hover.Content;
import io.mattcarroll.hover.HoverMenu;

public class Menu extends HoverMenu {

    private Context mContext;
    private Section mSection;

    private void SingleSectionHoverMenu(@NonNull Context context) {
        mContext = context;

        mSection = new Section(
                new SectionId("1"),
                createTabView(),
                createScreen()
        );
    }

    private View createTabView() {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.tab_background);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return imageView;
    }

    private Content createScreen() {
        return new MyContent(mContext, "Screen 1");
    }

    @Override
    public String getId() {
        return "singlesectionmenu";
    }

    @Override
    public int getSectionCount() {
        return 1;
    }

    @Nullable
    @Override
    public Section getSection(int index) {
        if (0 == index) {
            return mSection;
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Section getSection(@NonNull SectionId sectionId) {
        if (sectionId.equals(mSection.getId())) {
            return mSection;
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public List<Section> getSections() {
        return Collections.singletonList(mSection);
    }

}

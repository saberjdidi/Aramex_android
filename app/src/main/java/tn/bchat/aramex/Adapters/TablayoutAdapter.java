package tn.bchat.aramex.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import tn.bchat.aramex.Fragments.CollisFragment;
import tn.bchat.aramex.Fragments.TarifFragment;
import tn.bchat.aramex.Fragments.TrackMapFragment;

public class TablayoutAdapter extends FragmentPagerAdapter {
    int tabCounts;

    public TablayoutAdapter(@NonNull FragmentManager fm, int tabCounts) {
        super(fm);
        this.tabCounts = tabCounts;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //return new FoodsFragment();
                return new CollisFragment();
            case 1:
                return new TarifFragment();
            case 2:
                return new TrackMapFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCounts;
    }
}

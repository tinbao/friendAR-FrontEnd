package tk.friendar.friendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Simon on 8/29/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = new fragment();
        Bundle arguments = new Bundle();
        arguments.putInt(fragment.ARG_OBJECT, position + 1);
        frag.setArguments(arguments);
        return frag;


    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return "OBJECT " + (position + 1);
    }
}

package de.th_koeln.hgrzesko.virtu.main;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.th_koeln.hgrzesko.virtu.conversationlist.ConversationListFragment;
import de.th_koeln.hgrzesko.virtu.R;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MainSectionsPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    public MainSectionsPagerAdapter(FragmentManager fm, Resources resources) {
        super(fm);
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return 3;
    }

    /**
     * Returns a fragment instance for the given tab page.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CallsListFragment();
            case 1:
                return new ConversationListFragment();
            case 2:
                return new ContactsListFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] titles = resources.getStringArray(R.array.section_titles);
        return titles[position];
    }
}

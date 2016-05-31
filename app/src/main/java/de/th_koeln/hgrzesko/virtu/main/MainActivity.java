package de.th_koeln.hgrzesko.virtu.main;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import de.th_koeln.hgrzesko.virtu.R;


public class MainActivity extends AppCompatActivity {

//    private StoryService service;
    private MainSectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder binder) {
//            Log.d("Main Activity", "game service connected");
//            service = ((StoryService.StoryBinder) binder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName className) {
//            Log.d("Main Activity", "game service disconnected");
//            service = null;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new MainSectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext().getResources());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
//        tabStrip.setEnabled(false);
//        for(int i = 0; i < tabStrip.getChildCount(); i++) {
//            tabStrip.getChildAt(i).setClickable(false);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

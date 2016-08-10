package pt.deti.cavexh.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;

import pt.deti.cavexh.MainActivity;
import pt.deti.cavexh.R;

public class ScreenSlidePagerActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    /**
     * Extras used for the number of pages and page the user pressed.
     */
    public static final String N_PAGES = "NUM_PAGES";
    public static final String PAGE_ON = "PAGE_ON";
    private static int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide_pager_activity);

        // Get the extras.
        Intent intent = getIntent();
        NUM_PAGES = intent.getIntExtra(N_PAGES, 0);
        final int pos = intent.getIntExtra(PAGE_ON, 0);

        // Gets the close button from UI and assigns it the corresponding action.
        AppCompatImageButton close = (AppCompatImageButton)findViewById(R.id.detailedCloseButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScreenSlidePagerActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Instantiate the page viewer.
        mPager = (ViewPager) findViewById(R.id.pager);

        // Creates the page adapter.
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        // Sets the adapter to the element.
        mPager.setAdapter(mPagerAdapter);

        // Add a listener for page changes.
        mPager.addOnPageChangeListener(this);

        // Sets the current item position.
        mPager.setCurrentItem(pos);
        if (pos==0)
            onPageSelected(pos);
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ScreenSlidePagerActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //getSupportActionBar().setTitle(Data.getInstance().getItem(position).getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * The pager adapter.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // Gets the fragment instance, passing the required position.
            Fragment fragment = new ViewPagerFragment().newInstance(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}

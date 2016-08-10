package pt.deti.cavexh.viewpager;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;
import pt.deti.cavexh.game.CropActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment implements ViewPager.OnPageChangeListener{

    /**
     * The page the user is in.
     */
    private int pageNumber;

    /**
     * Database instance.
     */
    private static Data data;

    /**
     * Share dialog used in the image Facebook share.
     */
    ShareDialog shareDialog;

    /**
     * Circular button
     */
    private com.github.clans.fab.FloatingActionMenu menu1;

    /**
     * Circular sub-buttons.
     */
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;

    /**
     * Handler used to make the button animation.
     */
    private Handler mUiHandler = new Handler();

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Static constructor that returns an instance of the created fragment.
     * @param page
     * @return
     */
    public static ViewPagerFragment newInstance(int page) {

        // Initializes the viewpagerfragment.
        ViewPagerFragment fragment = new ViewPagerFragment();

        // Puts the page number on the bundle.
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        fragment.setArguments(args);

        // Gets the database instance.
        data = Data.getInstance();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Initializes the share dialog in order to let the user post to facebook.
        shareDialog = new ShareDialog(this);

        // Gets the page number.
        pageNumber = getArguments().getInt("pageNumber", 0);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detailed_item, menu);
    }

    /**
     * Method used to share an image to facebbok, using the facebook API.
     */
    private void shareImage() {

        // Puts the work in a thread.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //Download the image from the network, transform to bitmap, as requested by facebook
                    URL url_value;
                    Bitmap bitmap = null;
                    try {
                        url_value = new URL(data.getItem(pageNumber).getImageUrl());
                        bitmap = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Actual code to share on facebook
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();

                        ShareDialog.show(getActivity(), content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Starts the thread
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Gets the item from the database singleton.
        ListItem item = data.getItem(pageNumber);

        // Gets the view and inflates it as its elements.
        View view = inflater.inflate(R.layout.detailed_item, container, false);
        TextView title = (TextView) view.findViewById(R.id.detailedTitle);

        // Defines the title font
        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/MuseoSansCyrl.otf");
        title.setTypeface(typeFace);

        TextView desc = (TextView) view.findViewById(R.id.detailedDescription);

        // Instantiate the page viewer.
        mPager = (ViewPager) view.findViewById(R.id.pager);

        // Creates the page adapter.
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());

        // Sets the adapter to the element.
        mPager.setAdapter(mPagerAdapter);

        // Add a listener for page changes.
        mPager.addOnPageChangeListener(this);

        // Sets the current item position.
        mPager.setCurrentItem(0);

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator)view.findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);

        // Sets the title of the item
        title.setText(item.getTitle());

        // Sets the description
        desc.setText(item.getDescription());

        // Inflates the circular button.
        menu1 = (com.github.clans.fab.FloatingActionMenu) view.findViewById(R.id.menu1);

        // Assigns the listener for item click.
        menu1.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu1.toggle(true);
            }
        });

        menu1.hideMenuButton(false);

        // Animation time
        int delay = 400;
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menu1.showMenuButton(true);
            }
        }, delay);

        menu1.setClosedOnTouchOutside(true);

        // Gets the sub buttons.
        fab1 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab3);

        // Assigns them listeners for click.
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        return view;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.fab1:
                    // If click on share on facebook, the network must be available.
                    if (isNetworkAvailable())
                        shareImage();
                    else
                        Toast.makeText(getContext(), getString(R.string.no_internet_connection_share), Toast.LENGTH_SHORT).show();
                    menu1.toggle(true);
                    break;
                case R.id.fab2:
                    // If click on the add favorites button, the user must be logged in.
                    if (data.isLoggedIn() && data.getAuthData() != null) {
                        if (isNetworkAvailable()) {
                            data.addImageToFavorites(pageNumber);
                            Toast.makeText(getContext(), getString(R.string.image_added_to_facebook), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), getString(R.string.no_internet_connection_favorites), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_login_made), Toast.LENGTH_SHORT).show();
                    }
                    menu1.toggle(true);
                    break;
                case R.id.fab3:
                    if (isNetworkAvailable()) {
                        Intent i = new Intent(getActivity(), CropActivity.class);
                        i.putExtra("id", data.getItem(pageNumber).getId());
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.no_internet_connection_share), Toast.LENGTH_SHORT).show();
                    }
                    menu1.toggle(true);
            }
        }
    };

    /**
     * Method that checks if the network is available or not.
     * @return true if network available, false, otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && menu1 != null) {
            menu1.close(true);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

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
            Fragment fragment = new ImageFragment().newInstance(position, pageNumber);
            return fragment;
        }

        @Override
        public int getCount() {
            return data.getItem(pageNumber).getOtherImages().size();
        }
    }
}

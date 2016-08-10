package pt.deti.cavexh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.about.AboutActivity;
import pt.deti.cavexh.game.GameActivity;
import pt.deti.cavexh.options.OptionsActivity;
import pt.deti.cavexh.authorslist.AuthorsListActivity;
import pt.deti.cavexh.favorites.FavoritesActivity;
import pt.deti.cavexh.history.HistoryActivity;
import pt.deti.cavexh.tutorial.Tutorial;
import pt.deti.cavexh.viewpager.ScreenSlidePagerActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * GridView element.
     */
    GridView gridView;

    /**
     * Gallery adapter
     */
    GridViewAdapter mAdapter;

    /**
     * Adapter for the navigation drawer.
     */
    DrawerAdapter adapter;

    /**
     * Button to open the navigation drawer.
     */
    ImageButton drawerButton;

    /**
     * Facebook login button.
     */
    LoginButton loginButton;

    /**
     * Callback used to perform the login/logout from facebook.
     */
    private CallbackManager callbackManager;

    /**
     * Database instance.
     */
    Data db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        Log.d("thispart", "main activity called");

        setContentView(R.layout.activity_main);

        // Get database instance
        db = Data.getInstance(this);

        // Navigation Drawer adapter
        adapter = new DrawerAdapter(this);

        // Initializes the drawer button.
        drawerButton = (ImageButton) findViewById(R.id.drawerButton);

        // Gets the drawer element from UI.
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Listener for the drawer in order to turn the button visible/ invisible accordingly
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerButton.setVisibility(View.INVISIBLE);
                drawerButton.setEnabled(false);
                gridView.setEnabled(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerButton.setVisibility(View.VISIBLE);
                drawerButton.setEnabled(true);
                gridView.setEnabled(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        // Relative layout of the navigation drawer.
        final RelativeLayout navLayout = (RelativeLayout) findViewById(R.id.left_drawer);

        // List in the navigation drawer.
        final ListView navList = (ListView) findViewById(R.id.drawer);

        // Sets the adapter to the navigation list.
        navList.setAdapter(adapter);

        // Assigns a listener for item click
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {

                switch (pos) {
                    /*case 0:
                        // If the option is to read a QR Code.
                        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
                        integrator.setPrompt("Scan something");
                        integrator.setOrientationLocked(false);
                        integrator.setBeepEnabled(false);
                        integrator.initiateScan();
                        break;*/
                    case 0:
                        // If the option is to know about technical details of the app
                        Intent intent6 = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent6);
                        break;
                    case 1:
                        // If the option is to view the exhibition history.
                        Intent intent3 = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivity(intent3);
                        break;
                    case 2:
                        // If the option is to view the authors list.
                        Intent intent2 = new Intent(MainActivity.this, AuthorsListActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        if (isNetworkAvailable()) {
                            // If the option is to play a random game
                            String code = db.getRandomGameCode();
                            Intent intent4 = new Intent(MainActivity.this, GameActivity.class);
                            intent4.putExtra("gameId", code);
                            startActivity(intent4);
                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.game_no_internet, Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 4:
                        // If the option is the favorites.
                        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        // If the option is the favorites.
                        Intent intent7 = new Intent(MainActivity.this, Credits.class);
                        startActivity(intent7);
                        break;
                    case 6:
                        // If the option is to define some options
                        Intent intent5 = new Intent(MainActivity.this, OptionsActivity.class);
                        startActivity(intent5);
                        break;
                    default:
                        break;
                }
                gridView.setEnabled(true);
                drawer.closeDrawer(navLayout);
            }
        });

        // Assigns a listener for the drawer button.
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navLayout);
                drawerButton.setEnabled(false);
                drawerButton.setVisibility(View.INVISIBLE);
                gridView.setEnabled(false);
            }
        });

        // Gets the gridview element from UI:
        gridView = (GridView) findViewById(R.id.gridview);

        // Defines the adapter
        //mAdapter = new GridViewAdapter(this);

        // Assigns the adapter.
        gridView.setAdapter(new GridViewAdapter(this));

        // Defines the column number, as the application started on landscape (by default is 2, so it is good for portrait)
        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            gridView.setNumColumns(3);

        // Assigns the listener for item click on the gridview.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ScreenSlidePagerActivity.class);
                intent.putExtra(ScreenSlidePagerActivity.N_PAGES, Data.getInstance(MainActivity.this).getSize());
                intent.putExtra(ScreenSlidePagerActivity.PAGE_ON, position);
                startActivity(intent);
            }
        });

        // Creates the callback for Facebook login.
        callbackManager = CallbackManager.Factory.create();

        // Gets the Facebook login button
        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);

        // Defines the permissions.
        loginButton.setReadPermissions("user_friends");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                db.onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("1234", "facebook login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("1234", "facebook login error");
            }
        });

        // If the facebbok login is already made, makes the firebase login
        if (db.isLoggedIn() && db.getAuthData() == null) {
            db.onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken());
        }

        // Passes the activity instance to be later used
        db.setMainActivity(this);

    }

    /**
     * Method that checks if there is a internet connection available.
     * @return true if there is, false otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // Handles the QR Code result.
            String strRes = result.getContents();
            if (strRes != null) {

                // Boolean that tells if the qr code is or is not valid
                boolean execute = true;
                int position=0;
                try {
                    // Tries to get the position
                    position = Integer.parseInt(strRes);

                    // If the number is not in the images set, warns the user that the qr code is not valid
                    if (position<0 || position>=db.getSize()) {
                        Toast.makeText(this, getString(R.string.invalid_qrcode), Toast.LENGTH_SHORT).show();
                        execute=false;
                    }
                } catch (Exception e)
                {
                    // If the qr code is not a number, is not valid, so it does nothing, warning the user
                    execute=false;
                    Toast.makeText(this, getString(R.string.invalid_qrcode), Toast.LENGTH_SHORT).show();
                }

                // If the read qr code is valid, performs the operation
                if (execute) {
                    Intent intent = new Intent(MainActivity.this, ScreenSlidePagerActivity.class);
                    intent.putExtra(ScreenSlidePagerActivity.N_PAGES, Data.getInstance(this).getSize());
                    intent.putExtra(ScreenSlidePagerActivity.PAGE_ON, position);
                    startActivity(intent);
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        }
    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void refreshView() {
        Log.d("Refresh", "refresh");
        mAdapter = new GridViewAdapter(this);
        mAdapter.notifyDataSetChanged();
        gridView.setAdapter(mAdapter);


    }

    /**
     * Method called after the main activity is ready, in order to show the tutorial
     */
    public void setTutorial () {

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                Log.d("firststart","firststart: "+isFirstStart);
                if (isFirstStart) {

                    //  Launch app intro
                    Log.d("firststart","starts activity");
                    Intent i = new Intent(MainActivity.this, Tutorial.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

    }
}

package pt.deti.cavexh;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import pt.deti.cavexh.DB.Data;

/**
 * Created by tiago on 21/03/16.
 */
public class SplashActivity extends Activity {

    /**
     * Network TAG.
     */
    private static final String TAG = "network";

    /**
     * Filter used for the service.
     */
    IntentFilter internetFilter;

    /**
     * Boolean to check if the database has already been initialized.
     */
    private boolean dataInit = false;

    /**
     * Database instance.
     */
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            Log.i("BranchConfigTest", "deep link data: " +  Branch.getInstance().getLatestReferringParams().getString("imagecode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("aaa", "1");
        // Creates the filter for connectivity change.
        internetFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        // Registers the receiver.
        registerReceiver(myReceiver, internetFilter);

        data = Data.getInstance(SplashActivity.this, SplashActivity.this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters the broadcast receiver
        unregisterReceiver(myReceiver);
    }

    /**
     * Method used to initiate the main activity.
     */
    public void initiateMainActivity() {
        // The database is ready, the main activity can ben launched
        Log.d("aaa", "3");
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Method called when there is no internet connection nor a storage version.
     */
    public void noInternetNoStorageVersion() {
        Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
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

    /**
     * Broadcast receiver that listens for changes in the internet connection
     */
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {
                final ConnectivityManager connectivityManager =
                        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

                if (ni != null && ni.isConnectedOrConnecting()) {
                    if (!dataInit) {
                        Data data = Data.getInstance(context);
                        dataInit= true;
                    }
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                        Boolean.FALSE)) {
                    Log.d(TAG, "There's no network connectivity");
                }
            }

        }
    };

    @Override
    protected void onResume() {
        Log.d("BranchConfigTest", "resumed");
        super.onResume();
        final Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());

                    try {
                        if (branch.getLatestReferringParams().has("imagecode")) {
                            Log.i("BranchConfigTest", "deep link data: " + branch.getLatestReferringParams().getString("imagecode"));
                        }
                        else {
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, this.getIntent().getData(), this);

        Log.d("splash","onResume");
    }

    @Override
    public void onStart() {
        super.onStart();

        final Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user
                    // clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());

                    try {
                        Log.i("BranchConfigTest", "deep link data: " +
                                branch.getLatestReferringParams().getString("imagecode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

}

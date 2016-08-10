package pt.deti.cavexh.options;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.R;

public class OptionsActivity extends AppCompatActivity {

    private Data data;

    @BindView(R.id.checkBoxNames)
    CheckBox checkBox;

    @BindView(R.id.download_images)
    Button downloadImages;

    private boolean firstRun;

    @OnCheckedChanged(R.id.checkBoxNames) void onChecked(boolean checked) {
        Log.d("state","Checkbox event called");

        if (!firstRun) {
            Log.d("state","Checkbox event called--> entrou");
            boolean res;

            if (data.getGalleryHasNames()) {
                res = false;
                checkBox.setChecked(false);
                data.setGalleryHasNames(false);
            }
            else {
                res = true;
                checkBox.setChecked(true);
                data.setGalleryHasNames(true);
            }

            // Saves the change in the shared preferences
            SharedPreferences prefs = this.getSharedPreferences(
                    getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

            String checkedBox = "com.example.app.checkbox";

            prefs.edit().putBoolean(checkedBox, res).apply();
        }
        firstRun=false;
    }

    @OnClick (R.id.download_images) void onClickDownload() {

        if (isNetworkAvailable()) {
            if (data.hasNewVersionToDownload()) {
                new AlertDialog.Builder(OptionsActivity.this)
                        .setTitle(getResources().getString(R.string.options_alert_dialog_title))
                        .setMessage(getResources().getString(R.string.options_alert_dialog_text))
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                // do the acknowledged action, beware, this is run on UI thread
                                data.writeInformationToStorage();
                                data.writeImagesToInternalStorage();
                            }
                        })
                        .create()
                        .show();
            }
            else {
                Toast.makeText(OptionsActivity.this, R.string.options_already_last_version, Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(OptionsActivity.this, R.string.download_images_no_internet, Toast.LENGTH_LONG).show();
        }



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        data = Data.getInstance();

        if (data.getGalleryHasNames())
            firstRun = true;

        Log.d("state","Checkbox: "+checkBox.isChecked()+"; db: "+data.getGalleryHasNames());
        checkBox.setChecked(data.getGalleryHasNames());
        Log.d("state","Checkbox: "+checkBox.isChecked()+"; db: "+data.getGalleryHasNames());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

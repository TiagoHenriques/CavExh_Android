package pt.deti.cavexh.history;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.History;
import pt.deti.cavexh.R;

public class HistoryActivity extends AppCompatActivity {

    /**
     * Database instance.
     */
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Toolbar definition.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Gets the database instance.
        data = Data.getInstance();

        // Gets the history structure from the DB.
        History history = data.getHistory();

        // Gets the text view and assigns it the corresponding value.
        TextView text = (TextView) findViewById(R.id.aboutDetailedDescription);
        text.setText(history.getText());

        // Gets the image element.
        final ImageView image = (ImageView) findViewById(R.id.history_image);

        // Uses glide to load the corresponding image.
        //Loads image from storage if possible
        if (data.imageOnInternalStorage("history")) {
            //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
            //picture.setImageBitmap(bitmap);
            File outFile = this.getFileStreamPath("history");
            Glide.with(this)
                    .load(Uri.fromFile(outFile))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .into(image);
            Log.d("here", "here");
        }
        else {
            Glide.with(this)
                    .load(history.getImageUrl())
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .into(image);
            Log.d("here", "notjs");
        }

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

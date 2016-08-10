package pt.deti.cavexh.game;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.util.List;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.Image;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;

public class GameCorrectAnswer extends AppCompatActivity {

    /**
     * String used to pass the intent.
     */
    public static String ID = "GAMECORRECT";

    /**
     * Database instance.
     */
    private static Data data;

    /**
     * Id of the object
     */
    private String id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_correct_answer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Associate the back press with the corresponding event.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        data = Data.getInstance();

        // Get the extras.
        Intent intent = getIntent();
        id = intent.getStringExtra(ID);

        ListItem item = data.getitemById(id);

        List<Image> otherImages = item.getOtherImages();

        TextView title = (TextView) findViewById(R.id.detailedTitle);
        title.setText(item.getTitle());

        TextView desc = (TextView)findViewById(R.id.detailedDescription);
        desc.setText(item.getDescription());

        ImageView image = (ImageView)findViewById(R.id.imageGameRightAnswer);

        // Loads the image
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+item.getId()+0)) {
                Log.d("choice","internal");
                File outFile = this.getFileStreamPath("other"+item.getId()+0);
                Glide.with(this)
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .transform( new RotateTransformation( this, 90f ))
                        .into(image);
            }
            else {
                Log.d("choice","external");
                Glide.with(this)
                        .load(otherImages.get(0).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .transform( new RotateTransformation( this, 90f ))
                        .into(image);
            }


        }
        else {

            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+item.getId()+0)) {
                Log.d("choice","internal");
                Log.d("names","file name:"+item.getId()+0);
                File outFile = this.getFileStreamPath("other"+item.getId()+0);
                Glide.with(this)
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .into(image);
            }
            else {
                Log.d("choice","external");
                Glide.with(this)
                        .load(otherImages.get(0).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .into(image);
            }
        }



    }

    @Override
    public void onBackPressed() {
        // If the option is to play a random game
        String code = data.getRandomGameCode();
        Intent intent4 = new Intent(GameCorrectAnswer.this, GameActivity.class);
        intent4.putExtra("gameId", code);
        startActivity(intent4);
        finish();
    }

    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }

}

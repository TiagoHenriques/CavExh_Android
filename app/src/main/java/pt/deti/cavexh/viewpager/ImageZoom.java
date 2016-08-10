package pt.deti.cavexh.viewpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageZoom extends Activity {

    /**
     * Extra passed with the page number.
     */
    public static final String IMAGE = "IMAGE";

    /**
     * Extra with the pagenumber
     */
    public static final String PAGE = "PAGE";

    /**
     * PhotoViewAttacher instance.
     */
    PhotoViewAttacher mAttacher;

    /**
     * Database intance.
     */
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        // Gets the database instance.
        data = Data.getInstance();

        // Gets the extras
        Intent intent = getIntent();
        String itemId = intent.getStringExtra(IMAGE);
        int pageNumber = intent.getIntExtra(PAGE, 0);

        // Gets the element from the UI.
        ImageView image = (ImageView)findViewById(R.id.zoomedImage);

        // Uses Glide to load the image.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            //Loads image from storage if possible
            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+data.getitemById(itemId).getId()+pageNumber)) {
                //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
                //picture.setImageBitmap(bitmap);
                File outFile = this.getFileStreamPath("other"+data.getitemById(itemId).getId()+pageNumber);
                Glide.with(this)
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .fitCenter()
                        .transform( new RotateTransformation(getApplicationContext(), 90f ))
                        .into(image);
            }
            else {
                Glide.with(this)
                        .load(data.getitemById(itemId).getOtherImages().get(pageNumber).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .fitCenter()
                        .transform( new RotateTransformation(getApplicationContext(), 90f ))
                        .into(image);
            }


        }
        else {

            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+data.getitemById(itemId).getId()+pageNumber)) {
                //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
                //picture.setImageBitmap(bitmap);
                File outFile = this.getFileStreamPath("other"+data.getitemById(itemId).getId()+pageNumber);
                Glide.with(this)
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .fitCenter()
                        .into(image);
            }
            else {
                Glide.with(this)
                        .load(data.getitemById(itemId).getOtherImages().get(pageNumber).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .fitCenter()
                        .into(image);
            }
        }

        // Attaches the images to the zoom element.
        mAttacher = new PhotoViewAttacher(image);
        mAttacher.setScaleType(ImageView.ScaleType.CENTER);

        // Assigns a click event to the close button.
        ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

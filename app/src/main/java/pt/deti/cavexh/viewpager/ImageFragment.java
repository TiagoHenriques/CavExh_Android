package pt.deti.cavexh.viewpager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.util.List;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.Image;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;


public class ImageFragment extends Fragment {

    /**
     * The page the user is in.
     */
    private int pageNumber;

    /**
     * Database instance.
     */
    private static Data data;

    /**
     * ListItem
     */
    private ListItem item;

    /**
     * Static constructor that returns an instance of the created fragment.
     *
     * @return
     */
    public static ImageFragment newInstance(int page, int itemNum) {

        // Initializes the viewpagerfragment.
        ImageFragment fragment = new ImageFragment();

        // Puts the page number on the bundle.
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putInt("itemNumber", itemNum);
        fragment.setArguments(args);

        // Gets the database instance.
        data = Data.getInstance();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Gets the page number.
        pageNumber = getArguments().getInt("pageNumber", 0);
        item = data.getItem(getArguments().getInt("itemNumber", 0));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Gets the view and inflates it as its elements.
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        ImageView image = (ImageView)view.findViewById(R.id.imageView_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ImageZoom.class);
                i.putExtra("IMAGE", item.getId());
                i.putExtra("PAGE", pageNumber);
                startActivity(i);
            }
        });

        List<Image> otherImages = item.getOtherImages();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+item.getId()+pageNumber)) {
                Log.d("choice","internal");
                File outFile = getContext().getFileStreamPath("other"+item.getId()+pageNumber);
                Glide.with(getContext())
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .transform( new RotateTransformation( getContext(), 90f ))
                        .into(image);
            }
            else {
                Log.d("choice","external");
                Glide.with(getContext())
                        .load(otherImages.get(pageNumber).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .transform( new RotateTransformation( getContext(), 90f ))
                        .into(image);
            }


        }
        else {

            //Loads image from storage if possible
            if (data.imageOnInternalStorage("other"+item.getId()+pageNumber)) {
                Log.d("choice","internal");
                Log.d("names","file name:"+item.getId()+pageNumber);
                File outFile = getContext().getFileStreamPath("other"+item.getId()+pageNumber);
                Glide.with(getContext())
                        .load(Uri.fromFile(outFile))
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .into(image);
            }
            else {
                Log.d("choice","external");
                Glide.with(getContext())
                        .load(otherImages.get(pageNumber).getImg())
                        .asBitmap()
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.waiting)
                        .into(image);
            }
        }


        return view;
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

package pt.deti.cavexh;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.io.File;

import pt.deti.cavexh.DB.Data;

/**
 * Created by tiago on 12/03/16.
 */
public class GridViewAdapter extends BaseAdapter {

    /**
     * Inflater used for the gridview.
     */
    private LayoutInflater inflater;

    /**
     * Database instance.
     */
    Data data;

    /**
     * Context.
     */
    Context mContext;

    /**
     * Constructor of the adapter.
     * @param context context of the activity.
     */
    public GridViewAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
        mContext = context;

        // Initializes the database.
        data = Data.getInstance();

        Log.d("thispart","gridviewadapter called");
    }

    @Override
    public int getCount() {
        return data.getSize();
    }

    @Override
    public Object getItem(int i)
    {
        return data.getItem(i);
    }

    @Override
    public long getItemId(int i)
    {
        final int resourceId = mContext.getResources().getIdentifier(data.getItem(i).getImageUrl(), "drawable", mContext.getPackageName());
        return resourceId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        final ImageView picture;
        TextView title;

        if(v == null)
        {
            v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        // Gets the title element and defines it.
        title = (TextView)v.findViewById(R.id.grid_item_title);
        title.setText(data.getItem(i).getTitle());

        // Defines the font of the text
        Typeface typeFace=Typeface.createFromAsset(mContext.getAssets(),"fonts/MuseoSansCyrl.otf");
        title.setTypeface(typeFace);

        // Gets the picture element and loads it with Glide.
        picture = (ImageView)v.getTag(R.id.picture);

        android.view.ViewGroup.LayoutParams layoutParams = picture.getLayoutParams();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams.height = display.getWidth()/3;
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.height = display.getWidth()/2;
        }

        picture.setLayoutParams(layoutParams);

        //Loads image from storage if possible
        if (data.imageOnInternalStorage(data.getItem(i).getId())) {
            //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
            //picture.setImageBitmap(bitmap);
            File outFile = mContext.getFileStreamPath(data.getItem(i).getId());
            Glide.with(mContext)
                    .load(Uri.fromFile(outFile))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .into(picture);
            Log.d("choice","internal");
        }
        else {
            Glide.with(mContext)
                    .load(data.getItem(i).getImageUrl())
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .priority(Priority.HIGH)
                    .into(picture);
            Log.d("choice","external");
        }

        if (!data.getGalleryHasNames()) {
            title.setTextColor(Color.parseColor("#FFFFFF"));
        }

        return v;
    }
}

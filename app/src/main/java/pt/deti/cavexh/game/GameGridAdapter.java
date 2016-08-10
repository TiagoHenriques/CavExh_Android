package pt.deti.cavexh.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;

/**
 * Created by tiago on 11/05/16.
 */
public class GameGridAdapter extends BaseAdapter {

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

    private ListItem[] items;

    /**
     * Constructor of the adapter.
     * @param context context of the activity.
     */
    public GameGridAdapter(Context context)
    {
        Log.d("error1", "constructor called");
        inflater = LayoutInflater.from(context);
        mContext = context;

        // Initializes the database.
        data = Data.getInstance();
        items = data.getDataDisorganized();
        int count=0;
        for (ListItem item : items) {
            Log.d("data", (count++)+" -> "+item.getId()+" : "+item.getTitle());
        }
        Log.d("error1", "items array ready. items size: "+items.length);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i)
    {
        return items[i];
    }

    @Override
    public long getItemId(int i)
    {
        final int resourceId = mContext.getResources().getIdentifier(data.getItem(i).getImageUrl(), "drawable", mContext.getPackageName());
        return resourceId;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        final ImageView picture;
        TextView title;

        if(v == null)
        {
            v = inflater.inflate(R.layout.game_grid_view_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        Log.d("error1", "getview wants the "+i+" element");

        // Gets the picture element and loads it with Glide.
        picture = (ImageView) v.getTag(R.id.picture);

        //Loads image from storage if possible
        if (data.imageOnInternalStorage("other"+items[i].getId()+0)) {
            Log.d("choice", "internal");
            Log.d("names", "file name:" + items[i].getId() + 0);
            File outFile = mContext.getFileStreamPath("other"+items[i].getId()+0);
            Glide.with(mContext)
                    .load(Uri.fromFile(outFile))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .transform( new RotateTransformation( mContext, 90f ))
                    .into(picture);
        } else {
            Glide.with(mContext)
                    .load(items[i].getOtherImages().get(0).getImg())
                    .asBitmap()
                    .transform( new RotateTransformation( mContext, 90f ))
                    .into(picture);
        }

        final LinearLayout ll = (LinearLayout)v.findViewById(R.id.game_layout);

        v.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.d("asd", "on drag");
                switch(event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        //layoutParams = (LinearLayout.LayoutParams)v.getLayoutParams();
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_STARTED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_ENTERED");
                        ll.setBackgroundColor(mContext.getResources().getColor(R.color.background_color_blue));
                        break;

                    case DragEvent.ACTION_DRAG_EXITED :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_EXITED");
                        ll.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColor));
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION  :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_LOCATION");

                        break;

                    case DragEvent.ACTION_DRAG_ENDED   :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_ENDED");

                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d("asd", "ACTION_DROP event -- > pos: "+items[i].getTitle());

                        ((GameActivity)mContext).dragAnddropResult(items[i].getId());
                        ll.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColor));

                        // Do nothing
                        break;
                    default: break;
                }
                return true;
            }
        });

        return v;
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
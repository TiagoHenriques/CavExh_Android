package pt.deti.cavexh.favorites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;

/**
 * Created by tiago on 15/03/16.
 */
public class FavoritesAdapter extends BaseExpandableListAdapter {

    /**
     * Activity context.
     */
    private Context _context;

    /**
     * List with the header elements.
     */
    private List<String> listHeaders;

    /**
     * Map with the child elements.
     */
    private HashMap<String, List<String>> listItems;

    /**
     * List with the titles.
     */
    private List<String> titles;

    /**
     * Database instance.
     */
    private Data data;

    /**
     * COnstructor.
     * @param context the activity context.
     * @param listDataHeader the header items.
     * @param listChildData the child items.
     * @param titles the used titles.
     */
    public FavoritesAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData, List<String> titles) {
        this._context = context;
        this.listHeaders = listDataHeader;
        this.listItems = listChildData;
        this.titles = titles;
        data = Data.getInstance();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listItems.get(this.listHeaders.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // Gets the data from the structures.
        final String childText = (String) getChild(groupPosition, childPosition);

        // Inflates the view if not already.
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.favorites_list_item, null);
        }

        // Gets the title element and assigns it the corresponding text.
        final TextView title = (TextView) convertView.findViewById(R.id.favorites_title);
        title.setText(this.titles.get(groupPosition));

        // Defines the font
        Typeface typeFace=Typeface.createFromAsset(_context.getAssets(),"fonts/MuseoSansCyrl.otf");
        title.setTypeface(typeFace);

        // Gets the description element and assigns it the corresponding text.
        //TextView description = (TextView) convertView.findViewById(R.id.favorites_description);
        //description.setText(childText);

        // Gets the button and assigns it the corresponding action.
        Button remove = (Button)convertView.findViewById(R.id.removeFromFavorites);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove an item from the lists
                if (isNetworkAvailable()) {
                    ListItem item = data.getitemByTitle(titles.get(groupPosition));
                    data.removeItemFavorites(item.getId());
                }
                else {
                    Toast.makeText(_context, _context.getString(R.string.no_internet_connection_remove_favorites), Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listItems.get(this.listHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        // Gets the info from the structures.
        String imageUrl = (String) getGroup(groupPosition);

        // Inflates the views if not already
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.favorites_list_header, null);
        }

        // Gets the image view.
        ImageView image = (ImageView) convertView.findViewById(R.id.favorites_image);

        //Loads image from storage if possible
        if (data.imageOnInternalStorage(data.getImageIdByAuthorName("other"+this.titles.get(groupPosition))+"0")) {
            //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
            //picture.setImageBitmap(bitmap);
            File outFile = _context.getFileStreamPath(data.getImageIdByAuthorName("other"+this.titles.get(groupPosition))+"0");
            Glide.with(_context)
                    .load(Uri.fromFile(outFile))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .fitCenter()
                    .transform( new RotateTransformation( _context, 180f ))
                    .into(image);
        }
        else {
            // Loads the image to the imageview
            Glide.with(this._context)
                    .load(imageUrl)
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .fitCenter()
                    .transform( new RotateTransformation( _context, 180f ))
                    .into(image);
        }



        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Method that checks if the network is available or not.
     * @return true if network available, false, otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

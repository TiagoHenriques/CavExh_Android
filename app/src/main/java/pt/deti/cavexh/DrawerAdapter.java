package pt.deti.cavexh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import pt.deti.cavexh.DB.Data;

/**
 * Created by tiago on 14/03/16.
 */
public class DrawerAdapter extends BaseAdapter {

    /**
     * Inflater.
     */
    private LayoutInflater inflater;

    /**
     * Database instance.
     */
    Data data;

    /**
     * String array with the d+navigation drawer items.
     */
    String[] items;

    /**
     * Context.
     */
    Context mContext;

    /**
     * Constructor of the adapter.
     * @param context context of the activity.
     */
    public DrawerAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
        mContext = context;

        // Initializes the database instance.
        data = Data.getInstance();

        // Gets the items from the database.
        items = data.getMenuItems(mContext);
    }

    @Override
    public int getCount() {
        return Data.getInstance().getMenuItems(mContext).length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ImageView picture;
        TextView text;

        // Inflates the child element if not already created.
        if (v == null) {
            // Inflates the view.
            v = inflater.inflate(R.layout.drawer_item_layout, parent, false);
        }

        // Gets the elements.
        picture = (ImageView)v.findViewById(R.id.iv_NavIcon);
        text = (TextView)v.findViewById(R.id.tv_NavTitle);

        // Chose image accordingly
        if (position==0) {
            picture.setImageResource(R.drawable.ic_info_black_24dp);
            //loadImage(R.drawable.ic_favorite_black_24dp, picture);
        } else if (position==1) {
            picture.setImageResource(R.drawable.ic_book_black_24dp);

            //loadImage(R.drawable.ic_people_black_24dp, picture);
        } else if (position==2) {
            picture.setImageResource(R.drawable.ic_people_black_24dp);
            //loadImage(R.drawable.ic_book_black_24dp, picture);
        } else if (position==3) {
            picture.setImageResource(R.drawable.ic_extension_black_24dp);
            //loadImage(R.drawable.ic_gamepad_black_24dp, picture);
        } else if (position==4) {
            picture.setImageResource(R.drawable.ic_favorite_black_24dp);
            //loadImage(R.drawable.ic_info_black_24dp, picture);
        } else if (position==5) {
            picture.setImageResource(R.drawable.ic_web_asset_black_24dp);
            //loadImage(R.drawable.ic_info_black_24dp, picture);
        } else if (position==6) {
            picture.setImageResource(R.drawable.ic_settings_black_24dp);
            //loadImage(R.drawable.ic_play_circle_filled_black_24dp, picture);
        }

        // Sets the text.
        text.setText(items[position]);

        return v;
    }

    private void loadImage(int drawable, ImageView picture) {
        // Loads the image with Glide.
        Glide.with(mContext)
                .load(drawable)
                .dontAnimate()
                .into(picture);

    }
}

package pt.deti.cavexh.authorslist;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.R;

/**
 * Created by tiago on 15/03/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    /**
     * Context.
     */
    private Context mContext;

    /**
     * Information to put on the header.
     */
    private List<String> listHeaders;

    /**
     * Map with the information from both the headers and child views.
     */
    private HashMap<String, List<String>> listItems;

    /**
     * Data instance
     */
    private Data data;


    /**
     * Constructor.
     * @param context of the application.
     * @param listDataHeader information of the headers.
     * @param listChildData information of the child views.
     */
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this.mContext = context;
        this.listHeaders = listDataHeader;
        this.listItems = listChildData;
        data = Data.getInstance(mContext);
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
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // Gets the data from the structures.
        final String childText = (String) getChild(groupPosition, childPosition);

        // Inflates the child element if not already created.
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.authorslist_list_item, null);
        }

        // Gets the text view and assigns the correpsonding text.
        TextView txtListChild = (TextView) convertView.findViewById(R.id.authors_short_bio);
        txtListChild.setText(childText);

        //DocumentView txtListChild = (DocumentView) convertView.findViewById(R.id.authors_short_bio);
        //txtListChild.setText(childText);

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

        // Gets the data from the structures.
        String headerTitle = (String) getGroup(groupPosition);

        // Inflates the view if not already created.
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.authorslist_list_header, null);
        }

        // Gets the text view and assigns it the corresponding text.
        TextView authorName = (TextView) convertView.findViewById(R.id.authors_name);
        authorName.setText(headerTitle);

        ImageView image = (ImageView)convertView.findViewById(R.id.authors_img);

        //Loads image from storage if possible
        if (data.imageOnInternalStorage(data.getImageIdByAuthorName(headerTitle))) {
            //Bitmap bitmap = data.loadBitmap(mContext, data.getItem(i).getId());
            //picture.setImageBitmap(bitmap);
            Log.d("authors","author:"+headerTitle+"file name:"+data.getImageIdByAuthorName(headerTitle));
            File outFile = mContext.getFileStreamPath(data.getImageIdByAuthorName(headerTitle));
            Glide.with(mContext)
                    .load(Uri.fromFile(outFile))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .fitCenter()
                    .into(image);
        }
        else {
            Glide.with(mContext)
                    .load(data.getImageNameByAuthorName(headerTitle))
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.waiting)
                    .fitCenter()
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
}
package pt.deti.cavexh.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.DB.ListItem;
import pt.deti.cavexh.R;

public class FavoritesActivity extends AppCompatActivity {

    /**
     * Database instance.
     */
    Data data;

    /**
     * Firebase instance.
     */
    final Firebase ref = new Firebase("https://radiant-inferno-748.firebaseio.com");

    /**
     * Adapter for the favorite items.
     */
    FavoritesAdapter listAdapter;

    /**
     * Instance of the used expandable list view.
     */
    ExpandableListView expListView;

    /**
     * List with the header elements.
     */
    List<String> listDataHeader;

    /**
     * Map with the child elements, associated with the key (from the header).
     */
    HashMap<String, List<String>> listDataChild;

    /**
     * List with the titles used.
     */
    List<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Toolbar definition.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Gets the database instance.
        data = Data.getInstance(this);

        // Passes the favorites activity instance to the database, for future actions.
        data.setFavoritesActivity(FavoritesActivity.this);

        // Associate the back press with the corresponding event.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // If the user is not logged in, defines the view as a warning to login before
        if (!data.isLoggedIn()) {

            // Gets the stub
            ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);

            // Inflates the appropriate view
            stub.setLayoutResource(R.layout.activity_favorites_no_login);
            View inflated = stub.inflate();

        } else { // if the user is logged in, presents the favorites list
            if (data.getFavorites().size()!=0) {
                setNormalContent();
            }
            else {
                // Gets the stub
                ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);

                // Inflates the appropriate view
                stub.setLayoutResource(R.layout.activity_favorites_no_login);
                View inflated = stub.inflate();
                TextView text = (TextView)findViewById(R.id.favorites_no_login);
                text.setText(getResources().getString(R.string.favorites_no_favorites_added));
            }

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Private method used to present the user with its favorite list, if the user is logged in.
     */
    private void setNormalContent() {

        // Gets the used stub.
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);

        if (stub == null) {
            setContentView(R.layout.activity_favorites);
            stub = (ViewStub) findViewById(R.id.layout_stub);
        }

        // Inflates the proper view.
        stub.setLayoutResource(R.layout.content_favorites);
        View inflated = stub.inflate();


        // Gets the expandable list from xml.
        expListView = (ExpandableListView) inflated.findViewById(R.id.favorites_list_view);

        // Prepares the data to be passed to the adapter.
        prepareListData();

        // Creates the adapter.
        listAdapter = new FavoritesAdapter(this, listDataHeader, listDataChild, titles);

        // Sets the adapter to the elements in the UI.
        expListView.setAdapter(listAdapter);
    }

    /**
     * Method used to refresh the list view when an item is removed from the favorites,
     * by user action.
     */
    public void refreshData() {

        if (expListView==null) {
            setNormalContent();
        }
        else {
            // Retrieves the new elements
            prepareListData();

            // Creates the new adapter
            listAdapter = new FavoritesAdapter(this, listDataHeader, listDataChild, titles);

            // Assigns the new adapter to the list view.
            expListView.setAdapter(listAdapter);
        }
    }

    /**
     * Method that fetches the information from the database instance and prepares it to be
     * passed to the adapter.
     */
    private void prepareListData() {

        // Initializes the structures.
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        titles = new ArrayList<String>();

        // Get favorites from DB.
        Map<String, String> favorites = data.getFavorites();

        Log.d("item", "--> "+favorites.size());

        // Fetches the data from the database and puts it in structures.
        int i = 0;
        for (String key : favorites.keySet()) {
            ListItem item = data.getitemById(key);
            listDataHeader.add(item.getOtherImages().get(0).getImg());
            titles.add(item.getTitle());
            List<String> JC = new ArrayList<String>();
            JC.add(item.getDescription());
            listDataChild.put(listDataHeader.get(i++), JC);
            Log.d("item", "favorite added");


     }
    }
}

package pt.deti.cavexh.authorslist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.R;

public class AuthorsListActivity extends AppCompatActivity {

    /**
     * Adapter used in the view.
     */
    ExpandableListAdapter listAdapter;

    /**
     * ExpandableListView element.
     */
    ExpandableListView expListView;

    /**
     * List with the authors names.
     */
    List<String> listDataHeader;

    /**
     * Map with the authors information names and bio.
     */
    HashMap<String, List<String>> listDataChild;

    /**
     * Database instance.
     */
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors_list);

        // Toolbar definition.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Associate the back press with the corresponding event.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Get data instance
        data = Data.getInstance(this);

        // Get the listview element from xml
        expListView = (ExpandableListView) findViewById(R.id.expand_list_view);

        // Preparing the information to be passed to the adapter
        prepareListData();

        // Create the adapter
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // Sets the adapter to the element in the UI.
        expListView.setAdapter(listAdapter);
    }

    /**
     * Method used to fetch the data from the database and put it in the right structures.
     */
    private void prepareListData() {

        // Initialize elements.
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Get favoites from DB
        Map<String, String> authorsList = data.getAuthorsList();

        // Add the information to the data structures
        int i=0;
        for (String key : authorsList.keySet()) {
            listDataHeader.add(key);
            List<String> JC = new ArrayList<String>();
            JC.add(authorsList.get(key));
            listDataChild.put(listDataHeader.get(i++), JC);
        }
    }

}

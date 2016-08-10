package pt.deti.cavexh.game;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.branch.referral.Branch;
import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.MainActivity;
import pt.deti.cavexh.R;
import pt.deti.cavexh.Shake.ShakeDetector;

public class GameActivity extends AppCompatActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener {

    /**
     * Link for the database.
     */
    final Firebase ref = new Firebase("https://radiant-inferno-748.firebaseio.com");

    @BindView(R.id.grid)
    GridView grid;

    @BindView(R.id.imageRes)
    ImageView imageView;

    private GameGridAdapter mAdapter;
    private ArrayList<String> mData;

    //Shake part
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private Data data;

    private String correctId;

    private String code = null;

    private android.widget.LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        data = Data.getInstance(this);

        data.setGameActivity(this);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                Log.d("Shake", "shake detected!");
                refreshView();
            }
        });

        // Assigns the adapter.
        grid.setAdapter(new GameGridAdapter(this));

        // Defines the column number, as the application started on landscape (by default is 2, so it is good for portrait)
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            grid.setNumColumns(3);

        grid.setOnItemClickListener(this);

        if (data.getGameWarning()) {
            Toast.makeText(this, R.string.game_warning, Toast.LENGTH_LONG).show();
            data.setShowGameWarning(false);
        }


    }

    public void dragAnddropResult(String id) {
        Log.d("result", "result: "+id);
        if (id.equals(correctId)) {
            Toast.makeText(this, getResources().getString(R.string.right_answer), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(GameActivity.this, GameCorrectAnswer.class);
            intent.putExtra(GameCorrectAnswer.ID, correctId);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.wrong_answer), Toast.LENGTH_LONG).show();
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshView() {

        mAdapter = new GameGridAdapter(this);
        mAdapter.notifyDataSetChanged();

        grid.setAdapter(mAdapter);
        grid.setOnScrollListener(this);
        grid.setOnItemClickListener(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grid.setNumColumns(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            grid.setNumColumns(2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {

                if (data.getGotCode()==false) {
                    Log.d("gotValue1", "entrou22");
                    code = Branch.getInstance().getLatestReferringParams().getString("imagecode");
                    data.setCode(code);
                    data.setGotCode(true);
                }

                ref.child("game").child(data.getCode()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Log.d("gotValue1", "key: "+data.getKey());
                            if(data.getKey().equals("image")){
                                String base64Image = (String) data.getValue();
                                byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                                if (imageView.getDrawable()==null) {
                                    Log.d("gotValue1", "entroudef");
                                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                                }
                                System.out.println("Downloaded image with length: " + imageAsBytes.length);
                            }
                            else {
                                correctId = (String) data.getValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {}
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("nondeeplink", "Launched by normal application flow");
            // Launched by the normal flow
            code = getIntent().getStringExtra("gameId");
            ref.child("game").child(code).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Log.d("gotValue2", "key: "+data.getKey());
                        if(data.getKey().equals("image")){
                            String base64Image = (String) data.getValue();
                            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                            System.out.println("Downloaded image with length: " + imageAsBytes.length);
                        }
                        else {
                            correctId = (String) data.getValue();
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError error) {}
            });
        }
        setDragAndDrop();
    }

    private void setDragAndDrop() {

        LinearLayout ll = (LinearLayout)findViewById(R.id.text_and_image);
        ll.setOnDragListener(new View.OnDragListener() {
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
                        break;

                    case DragEvent.ACTION_DRAG_EXITED :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_EXITED");
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION  :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_LOCATION");

                        break;

                    case DragEvent.ACTION_DRAG_ENDED   :
                        Log.d("asd", "Action is DragEvent.ACTION_DRAG_ENDED");

                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d("asd", "ACTION_DROP event");

                        imageView.setVisibility(View.VISIBLE);
                        break;
                    default: break;
                }
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("asd", "ontouch");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);

                    imageView.startDrag(data, shadowBuilder, imageView, 0);
                    imageView.setVisibility(View.INVISIBLE);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Nothing to do!
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onBackPressed() {
        data.setShowGameWarning(true);
        Intent i = new Intent(GameActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

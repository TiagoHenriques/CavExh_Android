package pt.deti.cavexh.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pt.deti.cavexh.MainActivity;
import pt.deti.cavexh.game.GameActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import pt.deti.cavexh.R;
import pt.deti.cavexh.SplashActivity;
import pt.deti.cavexh.favorites.FavoritesActivity;

/**
 * Created by tiago on 19/02/16.
 */
public class Data {

    /**
     * Static instance used across the application.
     */
    private static Data mInstance = null;

    /**
     * Retrofit instance.
     */
    private Retrofit retrofit;

    /**
     * Firebase service.
     */
    FirebaseService service;

    /**
     * Map with all the items fetched from the database.
     */
    private Map<Integer, ListItem> mItems;

    /**
     * Counter used when fetching item from the database.
     */
    private static int i = 0;

    /**
     * Instace from the splash activity, in order to start it when the
     * information from the database has been download and the
     * application is ready to start.
     */
    private static SplashActivity activity;

    /**
     * Instance from the favorites activity, used in order to refresh
     * the application when the information on the database has been
     * updated.
     */
    private FavoritesActivity favActivity;

    /**
     * Instance from the game activity, used in order to refresh
     * the application when the information on the database has been
     * updated.
     */
    private GameActivity gameActivity;

    /**
     * Instance from the gallery activity, used in order to refresh
     * the application when the adds/removes the names
     */
    private MainActivity mainActivity;

    /**
     * Boolean used to detect if the application is running for the first time.
     */
    private static boolean firstRun = true;

    /**
     * * Link for the database.
     */
    final Firebase ref = new Firebase("https://radiant-inferno-748.firebaseio.com");

    /**
     * Authentication information.
     */
    private AuthData authData;

    /**
     * Favorites structure.
     */
    private Map<String, String> favorites;

    /**
     * Authors list structure.
     */
    private Map<String, String> authorsList;

    /**
     * Historu information structure.
     */
    private History history;

    /**
     * Map used as support for the game activity.
     */
    private Map<Integer, ListItem> map;

    /**
     * Used to define the location of the device.
     */
    private static String location = "";

    /**
     * Array that has the game codes.
     */
    private List<String> gameCodes;

    /**
     * Used to see if the game image has been downloaded from firebase
     */
    private boolean gotCode;

    /**
     * Code of the game.
     */
    private String code;

    /**
     * Context passed by the splash activity.
     */
    private static Context mContext;

    /**
     * Boolean that says if gallery has or has not names
     */
    private static boolean galleryHasNames;

    /**
     * Current version of information.
     */
    private static String currentVersion;

    /**
     * Version on the db
     */
    private static String versionOnDB;

    /**
     * Used to check the items that were fetched from the database
     */
    private int count;

    /**
     * Used to check if the app started from link or not
     */
    private boolean fromGame;

    /**
     * List used to check if image has been loaded or not.
     */
    private static ArrayList<String> imageLoaded;

    /**
     * Variable that indicates a new version.
     */
    private static boolean newVersion;

    /**
     * Variable that defines if the game warning is shown or not.
     */
    private boolean showGameWarning;

    /**
     * Private constructor, where the structures are initialized and the data is fetched from the database.
     */
    private Data() {

        count=0;
        showGameWarning = true;

        // Gets the value of the gallery names from shared preferences
        SharedPreferences prefs = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);


        String checkedBox = "com.example.app.checkbox";

        // use a default value using new Date()
        galleryHasNames = prefs.getBoolean(checkedBox, true);
        Log.d("shared", galleryHasNames+"");


        // Initializes all the structures.
        mItems = new HashMap<>();
        favorites = new HashMap<>();
        authorsList = new HashMap<>();
        map = new HashMap<>();
        gameCodes = new ArrayList<>();
        imageLoaded = new ArrayList<>();
        newVersion = false;

        gotCode = false;


        // Initially checks the internet connection.
        // If there is not, checks if there is a version of information on the storage system
        // If there is a valid version, starts the activity, otherwise gives awarning to the user.
        if (!isNetworkAvailable()) {
            readInformationFromStorage();
            getGameCodes();
        }
        // If there is internet, gets the current version from internet
        else {
            getDataFromStorageOrDB();
        }

    }


    private void getDataFromStorageOrDB() {

        // Initializes the retrofit instance, using the Gson as a converter.
        retrofit = new Retrofit
                .Builder()
                .baseUrl(FirebaseService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialized the firebase service.
        service = retrofit.create(FirebaseService.class);

        //Checks if there is a version in the storage
        SharedPreferences prefs = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);


        String version = "com.example.app.version";

        // use a default value using new Date()
        currentVersion = prefs.getString(version, "");

        // Gets the current version from db
        ref.child("dbversion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String version = (String) snapshot.getValue();
                // If the version is the same, uses the data in the internal storage
                if (currentVersion.equals(version)) {

                    readInformationFromStorage();

                    // Get game codes from db
                    getGameCodes();
                }
                // If the version is not the same, gets the info from the database
                else {
                    // Updates the version information
                    versionOnDB = version;
                    newVersion = true;

                    Toast.makeText(mContext, R.string.new_version , Toast.LENGTH_LONG).show();

                    // Get the images
                    getImagesFromDB();

                    // Get authors list
                    getAuthorsListFromDB();

                    // Get history details
                    getHistoryFromDB();

                    // Get game codes from db
                    getGameCodes();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    /**
     * Method called when the user starts without network connection and gets it later
     */
    public void initiateWhenNetworkComesAndAppAlreadyRunning() {

        if (!isNetworkAvailable()) {
            readInformationFromStorage();
        }
        // If there is internet, gets the current version from internet
        else {
            getDataFromStorageOrDB();
        }

    }

    /**
     * Singleton method that returns the only instance, or creates on if not yet created and returns it.
     *
     * @return the singleton instance.
     */
    public static Data getInstance() {

        if (mInstance == null) {
            mInstance = new Data();
        }
        else {
            firstRun = false;
        }
        return mInstance;
    }

    /**
     * Singleton method that returns the only instance, or creates on if not yet created and returns it.
     *
     * @return the singleton instance.
     */
    public static Data getInstance(Context context) {

        mContext = context.getApplicationContext();

        if (mInstance == null) {
            mInstance = new Data();
        }
        else {
            firstRun = false;
        }
        return mInstance;
    }

    /**
     * Singleton method that returns the only instance, or creates one if not yet created and returns it.
     *
     * @return the singleton instance.
     */
    public static Data getInstance(Context context, SplashActivity act) {


        mContext = context.getApplicationContext();
        activity = act;

        // Defines the location
        location = mContext.getResources().getConfiguration().locale.getCountry();

        // Creates the data instance accordingly
        if (mInstance == null) {
            mInstance = new Data();
        }
        else {
            firstRun = false;
            activity.initiateMainActivity();
        }
        return mInstance;
    }

    /**
     * Method that checks if there is a internet connection available.
     * @return true if there is, false otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Method that fetches the images needed for the main list view from the database.
     */
    private void getImagesFromDB() {

        // Creates a call.
        Call<Paintings> requestPaintings;

        // Chooses the data as is in Portugal or not
        if (location.equals("PT")) {
            requestPaintings = service.getPaintingsPortuguese();
        } else {
            requestPaintings = service.getPaintings();
        }

        // Puts that call in a queue (asynchronous method).
        requestPaintings.enqueue(new Callback<Paintings>() {
            @Override
            public void onResponse(Call<Paintings> call, Response<Paintings> response) {
                if (!response.isSuccessful()) {
                    // TODO maybe repeat the request
                } else {
                    // If the request has success, gets the data to the corresponding structures.
                    Paintings paintings = response.body();
                    for (ListItem item : paintings.list) {
                        if (item.getUseInApp().equals("yes")) {
                            mItems.put(i++, item);
                        }
                    }
                }

                count++;
                if (count==3) {
                    writeInformationToStorage();
                    // Checks if the application start is because of the game
                    if (gameActivity!=null) {
                        gameActivity.refreshView();
                    }
                    else {
                        startMainActivity();
                    }

                }

            }

            @Override
            public void onFailure(Call<Paintings> call, Throwable t) {

            }
        });
    }

    /**
     * Method that fetches the authors information from the database.
     */
    private void getAuthorsListFromDB() {

        // Creates the call.
        Call<AuthorsList> requestAuthors;
        // Chooses the data as is in Portugal or not
        if (location.equals("PT")) {
            Log.d("locale", "request PT");
            requestAuthors = service.getAuthorsListPortuguese();
        } else {
            requestAuthors = service.getAuthorsList();
        }

        // Puts that call in a queue (asynchronous method).
        requestAuthors.enqueue(new Callback<AuthorsList>() {
            @Override
            public void onResponse(Call<AuthorsList> call, Response<AuthorsList> response) {
                if (!response.isSuccessful()) {
                    // TODO maybe repeat the request
                    Log.d("item", "no success");
                } else {
                    // If the request has success, gets the data to the corresponding structures.
                    AuthorsList aList = response.body();
                    for (Author author : aList.list) {
                        if (author.getUseInApp().equals("yes")) {
                            authorsList.put(author.getName(), author.getBio());
                        }
                    }
                }

                count++;
                if (count==3) {
                    Log.d("thispart", "last one got info-> writes to internal storage");
                    writeInformationToStorage();
                    if (gameActivity!=null) {
                        gameActivity.refreshView();
                    }
                    else {
                        startMainActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthorsList> call, Throwable t) {

            }
        });
    }

    /**
     * Method that fetches the history information from the database.
     */
    private void getHistoryFromDB() {

        // Creates the call
        Call<History> requestHistory;
        // Chooses the data as is in Portugal or not
        if (location.equals("PT")) {
            Log.d("locale", "request PT");
            requestHistory = service.getHistoryPortuguese();
        } else {
            requestHistory = service.getHistory();
        }

        // Puts that call in a queue (asynchronous method).
        requestHistory.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (!response.isSuccessful()) {
                    // TODO maybe repeat the request
                    Log.d("item", "no success");
                } else {
                    // If the request has success, gets the data to the corresponding structures.
                    history = response.body();
                    storeImageHistoryInternalStorage(history);
                }

                count++;
                if (count==3) {
                    Log.d("thispart", "last one got info-> writes to internal storage");
                    writeInformationToStorage();
                    if (gameActivity!=null) {
                        gameActivity.refreshView();
                    }
                    else {
                        startMainActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {

            }
        });
    }

    /**
     * Method that fetches the history information from the database.
     */
    public void getGameCodes() {

        // Sets up a listener to listen for adds on the favorites
        ref.child("game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    gameCodes.add(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    /**
     * Method that listens to additions on the favorites list (done in the specific item page viewer).
     */
    public void setFavoritesListener() {

        // Sets up a listener to listen for adds on the favorites
        ref.child("favorites").child(this.getAuthData().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    favorites.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
                }

                // Used in case the user opens the favorites after the login before this function executes.
                if (favActivity != null)
                    favActivity.refreshData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /**
     * Method used to add an item to the used favorites.
     *
     * @param pageNumber the element number.
     */
    public void addImageToFavorites(int pageNumber) {

        Firebase usersRef = ref.child("favorites").child(authData.getUid());
        Map<String, Object> userFavorites = new HashMap<String, Object>();
        userFavorites.put(this.getItem(pageNumber).getId(), this.getItem(pageNumber).getImageUrl());
        usersRef.updateChildren(userFavorites);
    }

    /**
     * Method called by the favorites activityas response to the user removing an item from their list.
     *
     * @param id the id of the item to remove.
     */
    public void removeItemFavorites(String id) {
        Log.d("item", "id: " + id);
        ref.child("favorites").child(this.getAuthData().getUid()).child(id).removeValue();
        favorites.remove(id);
        favActivity.refreshData();
    }

    /**
     * Method that returns the favorites, once they are in the structure.
     *
     * @return the list with the user favorites.
     */
    public Map<String, String> getFavorites() {
        return favorites;
    }

    /**
     * Method that returns the authors once they are retrieved from the databse.
     *
     * @return the list with the authors list.
     */
    public Map<String, String> getAuthorsList() {
        return authorsList;
    }

    /**
     * Method that returns the information related to the history page, once it is retrieved from the database.
     *
     * @return a history object with the information.
     */
    public History getHistory() {
        return history;
    }

    /**
     * Method that returns an item given its id.
     *
     * @param id of the item.
     * @return the corresponding item.
     */
    public ListItem getitemById(String id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(id))
                return mItems.get(i);
        }
        return null;
    }

    /**
     * Method that returns an item given its title.
     *
     * @param title of the item.
     * @return the corresponding item.
     */
    public ListItem getitemByTitle(String title) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getTitle().equals(title))
                return mItems.get(i);
        }
        return null;
    }

    /**
     * Method to check if the application is on its first run.
     *
     * @return true if in first run, false otherwise.
     */
    public boolean firstRun() {
        return firstRun;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    /**
     * Method that returns the AUthData object of the application.
     *
     * @return the AuthData instance.
     */
    public AuthData getAuthData() {
        return authData;
    }

    /**
     * Returns a ListItem array with all the elements.
     *
     * @return an array with the items.
     */
    public ListItem[] getData() {
        ListItem[] items = new ListItem[mItems.size()];
        for (int i = 0; i < items.length; i++)
            items[i] = mItems.get(i);
        return items;
    }

    /**
     * Methos that returns as item, given its position on the grid.
     *
     * @param pos the grid position.
     * @return the corresponding item.
     */
    public ListItem getItem(int pos) {
        return mItems.get(pos);
    }

    /**
     * Method that returns the item list size.
     *
     * @return the size of the list of items.
     */
    public int getSize() {
        return mItems.size();
    }

    /**
     * Method that returns the items of the navigation drawer list.
     *
     * @return the elements of the navigation drawer.
     */
    public String[] getMenuItems(Context context) {
        return new String[]{
                context.getResources().getString(R.string.drawer_this_app),
                context.getResources().getString(R.string.drawer_history),
                context.getResources().getString(R.string.drawer_al),
                context.getResources().getString(R.string.drawer_play_game),
                context.getResources().getString(R.string.drawer_favorites),
                context.getResources().getString(R.string.drawer_credits),
                context.getResources().getString(R.string.drawer_definitions)
        };
    }

    /**
     * Method used to get the right instance of the SplashActivity, to be latter used.
     *
     * @param activity the SplashActivity instance.
     */
    public void setActivity(SplashActivity activity) {
        this.activity = activity;
    }

    /**
     * Method used to get the right instance of the FavoritesActivity, to be latter used.
     *
     * @param fav the FavoritesActivity instance.
     */
    public void setFavoritesActivity(FavoritesActivity fav) {
        this.favActivity = fav;
    }

    /**
     * Method used to start the main activity as soon as the data is ready.
     */
    public void startMainActivity() {
        while (activity == null) ;
        activity.initiateMainActivity();
        if (mainActivity!=null) {
            mainActivity.refreshView();
            mainActivity.setTutorial();
        }

    }

    /**
     * Method used to see if the user is logged in by Facebook.
     *
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    /**
     * Firebase login with facebook
     *
     * @param token facebook token
     */
    public void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // The Facebook user is now authenticated with your Firebase app
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("provider", authData.getProvider());
                    if (authData.getProviderData().containsKey("displayName")) {
                        map.put("displayName", authData.getProviderData().get("displayName").toString());
                    }
                    ref.child("users").child(authData.getUid()).setValue(map);

                    setAuthData(authData);
                    setFavoritesListener();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            ref.unauth();
        }
    }

    /**
     * Returns a ListItem array with the terms disorganized as needed for the game.
     * @return the disorganized list of the items.
     */
    public ListItem[] getDataDisorganized(){
        reorganizeData();
        ListItem[] items = new ListItem[mItems.size()];
        for (int i=0; i<map.size(); i++)
            items[i] = map.get(i);
        return items;
    }

    /**
     * Method that gets the game activity to perform the refresh once the data is ready to be shown.
     * @param game
     */
    public void setGameActivity(GameActivity game) {
        this.gameActivity = game;
    }

    /**
     * Method that reorganizes the data to be presented in the game grid view.
     */
    public void reorganizeData() {

        List<Integer> list = new ArrayList<>();
        for (int i=0; i<mItems.size(); i++)
            list.add(i);

        Collections.shuffle(list);

        for (int i=0; i<mItems.size(); i++) {
            map.put(i, mItems.get(list.get(i)));
        }
    }

    /**
     * Gets the item of the disorganized list by its position.
     * @param pos postion on the grid.
     * @return the corresponding item.
     */
    public ListItem getItemGridDisorganized(int pos){
        return map.get(pos);
    }


    public int getPositionById(String id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(id))
                return i;
        }
        return 0;
    }

    /**
     * Gets a random game key to start the game.
     * @return the game code.
     */
    public String getRandomGameCode() {

        // Defines a random number
        Random r = new Random();
        int res = r.nextInt(gameCodes.size());

        return gameCodes.get(res);
    }

    public boolean getGotCode() {
        return gotCode;
    }

    public void setGotCode(boolean gotCode) {
        this.gotCode = gotCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public boolean getGalleryHasNames() {
        return galleryHasNames;
    }

    public void setGalleryHasNames(boolean galleryHasNames) {
        this.galleryHasNames = galleryHasNames;
        this.mainActivity.refreshView();
        Log.d("newValue", "Names: "+galleryHasNames);
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mainActivity.refreshView();
    }

    public void refreshMainView() {
        this.mainActivity.refreshView();
    }

    public String getImageNameByAuthorName(String authName) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getTitle().replace("'", "\\'").equals(authName.replace("'", "\\'")))
                return mItems.get(i).getImageUrl();
        }
        return "";
    }

    public String getImageIdByAuthorName(String authName) {
        for (int i = 0; i < mItems.size(); i++) {
            Log.d("comp","to search"+authName+" ; comp: "+mItems.get(i).getTitle());
            if (mItems.get(i).getTitle().replace("'", "\\'").equals(authName.replace("'", "\\'")))
                return mItems.get(i).getId();
        }
        return "";
    }

    /**
     * Save to internal storage part
     *
     *
     */

    /**
     * Writes the information in the internal storage.
     */
    public void writeInformationToStorage() {

        Log.d("thispart", "writeInformationToStorage");
        //Creates the struture to store
        AllData data = new AllData(mItems, authorsList, history);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = mContext.openFileOutput("information.data", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(data);
            objectOutputStream.close();
            Log.d("thispart", "finished writing to internal storage");

            //Updates the version number on sharedpreferences
            SharedPreferences prefs = mContext.getSharedPreferences(
                    mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

            String version = "com.example.app.version";

            prefs.edit().putString(version, currentVersion).apply();
            Log.d("thispart", "finished writing to shared preferences");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInformationFromStorage() {

        Log.d("thispart", "reads from the storage");
        AllData data = null;

        FileInputStream fileInputStream  = null;
        try {
            fileInputStream  = new FileInputStream(mContext.getFilesDir()+"/information.data");

            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            data = (AllData) objectInputStream.readObject();
            objectInputStream.close();

            Log.d("storage", "data: "+data.getHistory().getText());

            // After the reading, it decides whether there are conditions to start the activity or not
            if (data==null) {
                activity.noInternetNoStorageVersion();
                Log.d("thispart", "no version nor internet connection");
            }
            else { // and if there is, reads the data and initiates the app
                getDBVersion();
                Log.d("thispart", "got data from internal storage, starts mainActivity");
                transformDataToStructures(data);
                if (gameActivity!=null) {
                    gameActivity.refreshView();
                }
                else {
                    startMainActivity();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method that takes the read structure of storage and transforms in the needed strutures.
     * @param data data read from storage.
     */
    public void transformDataToStructures(AllData data) {
        this.mItems = data.getmItems();
        this.authorsList = data.getAuthorsList();
        this.history = data.getHistory();
    }

    /**
     * Stores the images of an item in the internal storage.
     * @param item the item.
     */
    private void storeImageInternalStorage(final ListItem item) {

        // Stores the main image associated the id of the image
        Glide.with(mContext.getApplicationContext())
                .load(item.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        //saveFile(mContext, resource, item.getId());
                        if (resource.getByteCount()>100) {
                            SaveImageTask saveBitmap = new SaveImageTask(mContext, resource, item.getId());
                            saveBitmap.execute();
                        }
                    }
                });

        // Stores the other images
        int i=0;
        for (final Image image : item.getOtherImages()) {
            final int secId=i;
            Glide.with(mContext.getApplicationContext())
                    .load(image.getImg())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            Log.d("names","author name: other"+item.getTitle()+"file name:"+item.getId()+secId);
                            //saveFile(mContext, resource, "other"+item.getId()+secId);
                            SaveImageTask saveBitmap = new SaveImageTask(mContext, resource, "other"+item.getId()+secId);
                            saveBitmap.execute();
                        }
                    });
            i++;
        }

    }

    /**
     * Stores the history image on the internal storage
     * @param hist the history item.
     */
    private void storeImageHistoryInternalStorage(History hist) {

        // Stores the main image associated the id of the image
        Glide.with(mContext.getApplicationContext())
                .load(hist.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        //saveFile(mContext, resource, "history");
                        SaveImageTask saveBitmap = new SaveImageTask(mContext, resource, "history");
                        saveBitmap.execute();
                    }
                });

    }

    /**
     * Writes downloaded bitmap to storage
     * @param context app context.
     * @param b bitmap to save.
     * @param picName name of the image.
     */
    public static void saveFile(Context context, Bitmap b, String picName){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.d("names", "image "+picName+" saved!");
            imageLoaded.add(picName);
        }
        catch (FileNotFoundException e) {
            Log.d("storage", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("storage", "io exception");
            e.printStackTrace();
        }

    }

    /**
     * Reads bitmap from storage
     * @param context app context.
     * @param picName name of the image.
     * @return the bitmap.
     */
    public static Bitmap loadBitmap(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
            Log.d("names", "image "+picName+" loaded!");
        }
        catch (FileNotFoundException e) {
            Log.d("storage", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("storage", "io exception");
            e.printStackTrace();
        }
        return b;
    }

    public boolean imageOnInternalStorage(String picName) {
        File file = mContext.getFileStreamPath(picName);
        if (newVersion)
            return imageLoaded.contains(picName);
        else
            return file.exists();
    }


    public void setShowGameWarning(boolean value) {
        showGameWarning = value;
    }

    public boolean getGameWarning() {
        return showGameWarning;
    }

    public boolean isFromGame() {
        return fromGame;
    }

    public void setFromGame(boolean fromGame) {
        this.fromGame = fromGame;
    }

    /**
     * Method that downloads the images to internal storage
     */
    public void writeImagesToInternalStorage() {
        Toast.makeText(mContext, R.string.download_started, Toast.LENGTH_LONG).show();

        int totalImages = mItems.size();

        for (int i=0; i<mItems.size(); i++) {
            storeImageInternalStorage(mItems.get(i));
        }

        /*
        // separate by 10 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=10; i<20; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 10000);

        // separate by 10 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=20; i<30; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 20000);

        // separate by 10 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=30; i<40; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 30000);

        // separate by 10 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=40; i<50; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 40000);

        // separate by 10 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=50; i<60; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 50000);

        // separate by 10 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i=60; i<70; i++) {
                    storeImageInternalStorage(mItems.get(i));
                }

            }
        }, 60000);
        */

    }

    public boolean hasNewVersionToDownload() {

        //Checks if there is a version in the storage
        SharedPreferences prefs = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);


        String version = "com.example.app.version";

        // use a default value using new Date()
        currentVersion = prefs.getString(version, "");
        Log.d("thispart", "version on shared: "+currentVersion);

        Log.d("versions on","db: "+versionOnDB+"  device: "+currentVersion);
        if(currentVersion.equals(versionOnDB)) {
            return false;
        }
        else {
            return true;
        }
    }

    private void getDBVersion() {
        Log.d("thispart", "getDBVersion called");
        // Gets the current version from db
        ref.child("dbversion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String version = (String) snapshot.getValue();
                Log.d("thispart", "version on db: "+version+"------------------------------------------------------------------");
                // If the version is the same, uses the data in the internal storage
                versionOnDB = version;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    private class SaveImageTask extends AsyncTask<Void, Integer, Boolean> {



        private Bitmap bitmap;

        private Context context;

        private String picName;

        private boolean downloadMade;

        SaveImageTask(Context context, Bitmap bitmap, String picName) {
            this.bitmap = bitmap;
            this.context = context;
            this.picName = picName;
            this.downloadMade=false;
        }

        @Override
        protected Boolean doInBackground(Void...params) {

            FileOutputStream fos;
            try {
                fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Log.d("names", "image "+picName+" saved!");
            }
            catch (FileNotFoundException e) {
                Log.d("storage", "file not found");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d("storage", "io exception");
                e.printStackTrace();
            }

            return true;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean success) {
            imageLoaded.add(picName);
            if (imageLoaded.size()==(mItems.size()*4)) {
                //Updates the version number on sharedpreferences
                currentVersion = versionOnDB;

                SharedPreferences prefs = mContext.getSharedPreferences(
                        mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

                String version = "com.example.app.version";

                prefs.edit().putString(version, currentVersion).apply();
                Log.d("thispart", "finished writing to shared preferences");
                Toast.makeText(mContext, R.string.download_finished, Toast.LENGTH_LONG).show();
            }
        }

    }
}

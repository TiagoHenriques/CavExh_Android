// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package pt.deti.cavexh.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import pt.deti.cavexh.R;

public final class CropResultActivity extends AppCompatActivity {

    /**
     * The image to show in the activity.
     */
    static Bitmap mImage;

    private ImageView imageView;

    private Button btnShare;

    private String uniqueID;

    private String imageId;

    /**
     * Link for the database.
     */
    final Firebase ref = new Firebase("https://radiant-inferno-748.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the back arrow.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageId = getIntent().getStringExtra("id");

        if (mImage != null) {

            imageView = ((ImageView) findViewById(R.id.resultImageView));
            //imageView.setBackgroundResource(R.drawable.backdrop);
            imageView.setImageBitmap(mImage);

            btnShare = (Button)findViewById(R.id.shareBtn);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLink();
                }
            });

        } else {
            Toast.makeText(this, getResources().getString(R.string.no_image_to_show), Toast.LENGTH_LONG).show();
        }
    }

    private void shareLink() {

        // Puts the image on the database
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        //mImage.recycle();
        byte[] byteArray = stream.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Generate the token
        uniqueID = UUID.randomUUID().toString();

        ref.child("game").child(uniqueID).child("id").setValue(imageId);
        ref.child("game").child(uniqueID).child("image").setValue(imageFile);

        //App link part
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("CavGame")
                .setContentDescription("Try and discover to which cavaquinho this image belongs... Can you do it?")
                .setContentImageUrl("https://dl.dropboxusercontent.com/s/ujbtp37xi13jzf8/Inicio.jpg")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("imagecode", uniqueID);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "https://trtn.app.link/90NF1EwSht")
                .addControlParameter("$ios_url", "https://trtn.app.link/90NF1EwSht")
                .addControlParameter("$android_url", "https://trtn.app.link/90NF1EwSht");

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                }
            }
        });

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(CropResultActivity.this, "Cavaquinho Game!", getResources().getString(R.string.message_game))
                .setCopyUrlStyle(getResources().getDrawable(R.drawable.ic_content_copy_black_24dp),"Copy Link","Copy Link")
                .setMoreOptionStyle(getResources().getDrawable(R.drawable.ic_add_black_24dp), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL);

        branchUniversalObject.showShareSheet(CropResultActivity.this,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }

                    @Override
                    public void onShareLinkDialogDismissed() {
                    }

                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                        Log.e("LinkShared", "success");
                    }

                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        releaseBitmap();
        finish();
    }

    public void onImageViewClicked(View view) {
        releaseBitmap();
        finish();
    }

    private void releaseBitmap() {
        if (mImage != null) {
            //mImage.recycle();
            mImage = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());

                }
            }
        }, this.getIntent().getData(), this);
    }


    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

}

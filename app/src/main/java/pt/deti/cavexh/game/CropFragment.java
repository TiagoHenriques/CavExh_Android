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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import pt.deti.cavexh.DB.Data;
import pt.deti.cavexh.R;

/**
 * The fragment that will show the Image Cropping UI by requested preset.
 */
public final class CropFragment extends Fragment
        implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener {

    //region: Fields and Consts

    private CropDemoPreset mDemoPreset;

    private CropImageView mCropImageView;
    //endregion

    // Id of of the image to show
    private String imageId;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static CropFragment newInstance(CropDemoPreset demoPreset, String imageId) {
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putString("DEMO_PRESET", demoPreset.name());
        args.putString("id", imageId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the image to show for cropping.
     */
    public void setImageUri(Uri imageUri) {
        mCropImageView.setImageUriAsync(imageUri);
    }

    /**
     * Set the options of the crop image view to the given values.
     */
    public void setCropImageViewOptions(CropImageViewOptions options) {
        mCropImageView.setScaleType(options.scaleType);
        mCropImageView.setCropShape(options.cropShape);
        mCropImageView.setGuidelines(options.guidelines);
        mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
        mCropImageView.setShowCropOverlay(options.showCropOverlay);
        mCropImageView.setShowProgressBar(options.showProgressBar);
        mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
        mCropImageView.setMaxZoom(options.maxZoomLevel);
    }

    /**
     * Set the initial rectangle to use.
     */
    public void setInitialCropRect() {
        mCropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    /**
     * Reset crop window to initial rectangle.
     */
    public void resetCropRect() {
        mCropImageView.resetCropRect();
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        ((CropActivity) getActivity()).setCurrentOptions(options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        switch (mDemoPreset) {
            case RECT:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            case CUSTOM:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            default:
                throw new IllegalStateException("Unknown preset: " + mDemoPreset);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);

        updateCurrentCropViewOptions();

        String imageUrl = Data.getInstance().getitemById(imageId).getOtherImages().get(0).getImg();

        if (savedInstanceState == null) {

            Glide.with(getActivity())
                    .load(imageUrl)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(100,100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            mCropImageView.setImageBitmap(resource);
                        }
                    });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_crop) {
            mCropImageView.getCroppedImageAsync();
            return true;
        } else if (item.getItemId() == R.id.main_action_rotate) {
            mCropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDemoPreset = CropDemoPreset.valueOf(getArguments().getString("DEMO_PRESET"));
        imageId = getArguments().getString("id");
        ((CropActivity) activity).setCurrentFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnGetCroppedImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getActivity(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        if (error == null) {
            CropResultActivity.mImage = mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                    ? CropImage.toOvalBitmap(bitmap)
                    : bitmap;
            Intent intent = new Intent(getActivity(), CropResultActivity.class);
            intent.putExtra("id", imageId);
            startActivity(intent);
            getActivity().finish();
        } else {
            Log.e("AIC", "Failed to crop image", error);
            Toast.makeText(getActivity(), "Image crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

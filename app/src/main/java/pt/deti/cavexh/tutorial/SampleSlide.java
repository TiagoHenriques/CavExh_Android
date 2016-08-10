package pt.deti.cavexh.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import pt.deti.cavexh.R;

public class SampleSlide extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    public static SampleSlide newInstance(int layoutResId) {
        SampleSlide sampleSlide = new SampleSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int layoutResId;

    public SampleSlide() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(layoutResId, container, false);

        if (layoutResId==R.layout.intro) {
            ImageView img = (ImageView)v.findViewById(R.id.image_intro_1);

            Glide.with(getContext())
                    .load(R.drawable.menu)
                    .dontAnimate()
                    .into(img);

        } else if (layoutResId==R.layout.intro2) {
            ImageView img = (ImageView)v.findViewById(R.id.image_intro_2);

            Glide.with(getContext())
                    .load(R.drawable.workflow_game)
                    .dontAnimate()
                    .into(img);

        } else if (layoutResId==R.layout.intro3) {
            ImageView img = (ImageView)v.findViewById(R.id.image_intro_3);

            Glide.with(getContext())
                    .load(R.drawable.workflow_play_game)
                    .dontAnimate()
                    .into(img);

        } else if (layoutResId==R.layout.game) {
            ImageView img = (ImageView)v.findViewById(R.id.image_game_1);

            Glide.with(getContext())
                    .load(R.drawable.item_menu)
                    .dontAnimate()
                    .into(img);

        } else if (layoutResId==R.layout.game2) {
            ImageView img = (ImageView)v.findViewById(R.id.image_game_2);

            Glide.with(getContext())
                    .load(R.drawable.play_game_1)
                    .dontAnimate()
                    .into(img);

        } else if (layoutResId==R.layout.game3) {
            ImageView img = (ImageView)v.findViewById(R.id.image_game_3);

            Glide.with(getContext())
                    .load(R.drawable.play_game_2)
                    .dontAnimate()
                    .into(img);

        }

        return v;
    }

}

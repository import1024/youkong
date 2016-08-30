package io.github.import1024.youkong.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mingle.widget.LoadingView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.import1024.youkong.R;
import io.github.import1024.youkong.ui.widget.PinchImageView;
import io.github.import1024.youkong.common.utils.ToastUtil;

/**
 * Created by caofeng on 16-8-18.
 */
public class ImageViewFragment extends Fragment {
    public static final String ARGS_IMAGE = "image";
    private String img;

    @Bind(R.id.image_view)
    PinchImageView mImage;

    @Bind(R.id.load_view)
    LoadingView loadingView;


    public static ImageViewFragment getInstance(String img){
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_IMAGE,img);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img = getArguments().getString(ARGS_IMAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_image,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingView.setVisibility(View.VISIBLE);
        Glide.with(view.getContext()).load(img).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                ToastUtil.toastShort(getString(R.string.load_image_error));
                loadingView.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.image_load_error).into(mImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

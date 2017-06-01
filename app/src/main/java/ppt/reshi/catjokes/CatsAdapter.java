package ppt.reshi.catjokes;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Marcin Regulski on 28.05.2017.
 */

public class CatsAdapter extends ArrayAdapter<String> {
    private final static String TAG = "CatsAdapter";
    @LayoutRes
    private int mItemLayout;

    private CatsService mCatsService;
    private List<String> mCatUrls;

    public CatsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects, CatsService catsService) {
        super(context, resource, objects);
        mItemLayout = resource;
        mCatsService = catsService;
        mCatUrls = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(mItemLayout, parent, false);
        }
        String url = getItem(position);
        Picasso.with(getContext())
                .load(url)
                .into((ImageView) v, new RandomCatOnErrorCallback((ImageView) v, position));
        return v;
    }

    private class RandomCatOnErrorCallback implements Callback {
        final ImageView v;
        final int position;

        private RandomCatOnErrorCallback(ImageView v, int position) {
            this.v = v;
            this.position = position;
        }

        @Override
        public void onSuccess() {
            // do nothing
        }

        @Override
        public void onError() {
            Log.w(TAG, "onError: failed to load image, trying another one");
            mCatsService.getCats(1, getContext(), (e, result) -> {
                    if (result.size()>0) {
                        mCatUrls.set(position, result.get(0));
                        Picasso.with(getContext())
                                .load(result.get(0))
                                .into(v, this);
                    } else {
                        Log.e(TAG, "onError: no alternative image available");
                    }
            });
        }
    }
}

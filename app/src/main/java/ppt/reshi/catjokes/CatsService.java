package ppt.reshi.catjokes;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin Regulski on 28.05.2017.
 */

public class CatsService {
    private final static String TAG = "CatsService";
    public Future<String> getCats(int number, Context context, FutureCallback<List<String>> callback) {
        return Ion.with(context)
                .load("http://thecatapi.com/api/images/get")
                .addQuery("format", "xml")
                .addQuery("results_per_page", Integer.toString(number))
                .addQuery("type", "jpg,png")
                .asString()
                .setCallback((e, result) -> {
                    List<String> imgUrls = new ArrayList<>();
                    if (e != null) {
                        Log.e(TAG, "getCats: error getting cats", e);
                    } else {
                        Log.d(TAG, "getCats: result: " + result);
                        imgUrls = XMLCatExtractor.extract(result);
                        Log.d(TAG, "getCats: urls: " + imgUrls);
                    }
                    callback.onCompleted(e, imgUrls);
                });

    }
}

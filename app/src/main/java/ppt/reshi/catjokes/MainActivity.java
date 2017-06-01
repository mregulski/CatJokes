package ppt.reshi.catjokes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private enum Apis {
        Joke, Cats
    }

    private Button mBtnJokes;
    private Button mBtnCats;
    private GridView mCatGrid;
    private TextView mJoke;
    private ProgressBar mProgressBar;

    private CatsAdapter mCatsAdapter;

    private CatsService mCatsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnJokes = (Button) findViewById(R.id.btn_joke);
        mBtnCats = (Button) findViewById(R.id.btn_cats);
        mCatGrid = (GridView) findViewById(R.id.cats_grid);
        mJoke = (TextView) findViewById(R.id.joke);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mBtnJokes.setOnClickListener(v -> getJoke());
        mBtnCats.setOnClickListener(v -> getCats());

        mCatsService = new CatsService();
        mCatsAdapter = new CatsAdapter(this, R.layout.cat_img, new ArrayList<>(), mCatsService);
        mCatGrid.setAdapter(mCatsAdapter);


    }

    private void getCats() {
        showProgress();
        Ion.getDefault(this).cancelAll(this);
        mCatsService.getCats(10, this, (e, result) -> {
            hideProgress(MainActivity.Apis.Cats);
            mCatsAdapter.clear();
            mCatsAdapter.addAll(result);
        });
    }


    private void getJoke() {
        showProgress();
        Ion.getDefault(this).cancelAll(this);
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .addQuery("exclude", "[explicit]")
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e(TAG, "getJoke: error: ", e);
                    }
                    hideProgress(Apis.Joke);
                    Log.d(TAG, "getJoke: " + result);
                    String joke = result.getAsJsonObject("value").get("joke").getAsString();
                    mJoke.setText(Html.fromHtml(joke));
                });
    }

    private void showProgress() {
        mBtnCats.setEnabled(false);
        mBtnJokes.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mCatGrid.setVisibility(View.GONE);
        mJoke.setVisibility(View.GONE);
    }

    private void hideProgress(Apis api) {
        mProgressBar.setVisibility(View.GONE);
        mBtnCats.setEnabled(true);
        mBtnJokes.setEnabled(true);
        switch (api) {
            case Joke:
                mJoke.setVisibility(View.VISIBLE);
                break;
            case Cats:
                mCatGrid.setVisibility(View.VISIBLE);
                break;
        }
    }
}

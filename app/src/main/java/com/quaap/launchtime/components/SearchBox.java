package com.quaap.launchtime.components;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.quaap.launchtime.GlobState;
import com.quaap.launchtime.MainActivity;
import com.quaap.launchtime.R;
import com.quaap.launchtime.db.DB;

/**
 * Created by tom on 8/10/17.
 */

public class SearchBox {
    private ViewGroup mSearchView;

    private int mSearchRememberScrollPos;

    private AppCursorAdapter mSearchAdapter;
    private EditText mSearchbox;

    private MainActivity mMainActivity;
    private InteractiveScrollView mIconSheetScroller;

    public SearchBox(MainActivity mainActivity, InteractiveScrollView iconSheetScroller) {

        mMainActivity = mainActivity;
        mIconSheetScroller = iconSheetScroller;

        mSearchView = (ViewGroup) LayoutInflater.from(mainActivity).inflate(R.layout.search_layout, (ViewGroup) null);

        mSearchbox = (EditText) mSearchView.findViewById(R.id.search_box);

        mSearchAdapter = new AppCursorAdapter(mainActivity, mSearchbox, R.layout.search_item, 0);
        StaticListView list = (StaticListView) mSearchView.findViewById(R.id.search_dropdownarea);

        list.setAdapter(mSearchAdapter);
        list.setOnItemClickListener(mSearchAdapter);

        list.setOnLoadCompleteListener(new StaticListView.OnLoadCompleteListener() {
            @Override
            public void loadComplete() {
                mIconSheetScroller.scrollTo(0, mSearchRememberScrollPos);
                mIconSheetScroller.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIconSheetScroller.scrollTo(0, mSearchRememberScrollPos);

                    }
                }, 100);
            }
        });


        mSearchView.findViewById(R.id.btn_clear_searchbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchbox.setText("");
                mSearchRememberScrollPos = 0;
            }
        });


        mSearchView.findViewById(R.id.btn_clear_recents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity)
                        .setTitle(R.string.clear_recent)
                        .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db().deleteAppLaunchedRecords();
                                mMainActivity.populateRecentApps();
                            }
                        }).setNegativeButton(R.string.cancel, null);
                builder.show();

            }
        });

        refreshSearch(false);

    }

    public void refreshSearch(boolean rememberPos) {
        if (rememberPos) mSearchRememberScrollPos = mIconSheetScroller.getScrollY();
        if (mSearchAdapter!=null) {
            mSearchAdapter.refreshCursor();
        }
    }

    public void closeSeachAdapter() {
        //close our search cursor, if needed
        if (mSearchAdapter!=null){
            mSearchAdapter.close();
        }
    }


    public ViewGroup getSearchView() {
        return mSearchView;
    }

    public String getSeachText() {
        return mSearchbox.getText().toString();
    }

    public void setSearchText(CharSequence text) {
        mSearchbox.setText(text);
    }

    private DB db() {
        return GlobState.getGlobState(mMainActivity).getDB();
    }
}

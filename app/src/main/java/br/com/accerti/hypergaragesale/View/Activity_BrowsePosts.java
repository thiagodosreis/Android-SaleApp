package br.com.accerti.hypergaragesale.View;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import br.com.accerti.hypergaragesale.Control.PostsDbHelper;
import br.com.accerti.hypergaragesale.Model.BrowsePosts;
import br.com.accerti.hypergaragesale.Model.Posts;
import br.com.accerti.hypergaragesale.R;

public class Activity_BrowsePosts extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_posts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //creating the recycle to hold the itens
        mRecyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        db = mDbHelper.getReadableDatabase();

        mAdapter = new Adapter_Posts(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "My floating Action Bar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(getApplicationContext(), Activity_NewPost.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_post_search, menu);


        SearchManager searchManager =
                (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<BrowsePosts> getDataSet() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Posts.PostEntry.COLUMN_NAME_ID,
                Posts.PostEntry.COLUMN_NAME_TITLE,
                Posts.PostEntry.COLUMN_NAME_PRICE,
                Posts.PostEntry.COLUMN_NAME_PICTURE,
                Posts.PostEntry.COLUMN_NAME_DESCRIPTION,
                Posts.PostEntry.COLUMN_NAME_LATITUDE,
                Posts.PostEntry.COLUMN_NAME_LONGITUDE,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Posts.PostEntry.COLUMN_NAME_ID + " DESC";

        Cursor cursor = db.query(
                Posts.PostEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<BrowsePosts> browsePosts = new ArrayList<BrowsePosts>();
        if (cursor.moveToFirst()) {
            do {
                //Log.d("DATABASE","Posts.PostEntry._ID: "+cursor.getString( cursor.getColumnIndex(Posts.PostEntry._ID)+ " ");

                browsePosts.add(new BrowsePosts(
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_PRICE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_PICTURE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_LONGITUDE))));
            } while (cursor.moveToNext());
        }

        return browsePosts;
    }

}

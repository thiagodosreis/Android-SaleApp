package br.com.accerti.hypergaragesale.View;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.accerti.hypergaragesale.Control.PostsDbHelper;
import br.com.accerti.hypergaragesale.Model.BrowsePosts;
import br.com.accerti.hypergaragesale.Model.Posts;
import br.com.accerti.hypergaragesale.R;

public class SearchableActivity extends ListActivity {

    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("MY SEARCH:",query);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        db = mDbHelper.getReadableDatabase();

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
        String whereClause = " upper(" + Posts.PostEntry.COLUMN_NAME_TITLE + ") like '%" + query.toUpperCase() + "%' ";

        Cursor cursor = db.query (
                Posts.PostEntry.TABLE_NAME,  // The table to query
                projection,                  // The columns to return
                whereClause,                 // The columns for the WHERE clause
                null,                        // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                null               // The sort order
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

        Log.d("Query:","done!");

        setListAdapter(new SimpleCursorAdapter(this, R.layout.post_text_view_search_result, cursor,
                new String[] {Posts.PostEntry.COLUMN_NAME_TITLE, Posts.PostEntry.COLUMN_NAME_PRICE, Posts.PostEntry.COLUMN_NAME_ID },
                new int[]{R.id.titleView, R.id.priceView, R.id.idItem}));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_post_search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Toast.makeText(this, "You have chosen item: " + id,
        //        Toast.LENGTH_LONG).show();

        Intent intent =  new Intent(this, Activity_DetailPost.class);
        intent.putExtra("ID", Integer.parseInt(String.valueOf(id)));
        this.startActivity(intent);
    }
}

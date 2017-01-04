package br.com.accerti.hypergaragesale.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

import br.com.accerti.hypergaragesale.Control.PostsDbHelper;
import br.com.accerti.hypergaragesale.Model.BrowsePosts;
import br.com.accerti.hypergaragesale.Model.Posts;
import br.com.accerti.hypergaragesale.R;

public class Activity_DetailPost extends AppCompatActivity implements OnMapReadyCallback {

    private SQLiteDatabase db;
    private GoogleMap mMap;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        int recordPosition = 0;
        int recordID = getIntent().getIntExtra("ID",0);

        // specify an adapter (see also next example)
        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        db = mDbHelper.getReadableDatabase();

        ArrayList<BrowsePosts> mAdapter = new ArrayList<BrowsePosts>();
        mAdapter = getDataSet(String.valueOf(recordID));

        if(mAdapter.size() > 0)
        {
            TextView mTitle = (TextView) findViewById(R.id.txtViewTitle);
            TextView mPrice = (TextView) findViewById(R.id.txtViewPrice);
            TextView mDescr = (TextView) findViewById(R.id.txtViewDescription);
            ImageView img = (ImageView) findViewById(R.id.ImageDetail);

            mTitle.setText(mAdapter.get(recordPosition).mTitle);
            mPrice.setText("U$ " + mAdapter.get(recordPosition).mPrice);
            mDescr.setText(mAdapter.get(recordPosition).mDescr);
            latitude = mAdapter.get(recordPosition).mLatitude;
            longitude = mAdapter.get(recordPosition).mLongitude;

            if(mAdapter.get(recordPosition).mPicture != null){
                File imgFile = new  File(mAdapter.get(recordPosition).mPicture);

                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    img.setImageBitmap(myBitmap);
                }
                else{
                    Log.d("ERROR:", "Couldn't find file: "+imgFile.toString());
                }
            }


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private ArrayList<BrowsePosts> getDataSet(String id) {
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
        String whereClause = Posts.PostEntry.COLUMN_NAME_ID+" = '"+id.toString()+"'";
        String [] whereArgs = {id.toString()};

        Cursor cursor = db.query (
                Posts.PostEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                whereClause,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null               // The sort order
        );

        ArrayList<BrowsePosts> browsePosts = new ArrayList<BrowsePosts>();
        if (cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                browsePosts.add(new BrowsePosts(
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_PRICE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_PICTURE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_LONGITUDE))));

                //Log.d("DATABASE:","Posts.PostEntry._ID: "+cursor.getString(cursor.getColumnIndex(Posts.PostEntry.COLUMN_NAME_TITLE)+ " ");
            } while (cursor.moveToNext());
        }else{
            Log.d("DATABASE:","Sorry, item id "+Posts.PostEntry.COLUMN_NAME_ID+ " not found!");
        }

        return browsePosts;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (latitude != null && longitude != null){
            Log.d("Latitude:",latitude);
            Log.d("Lng:",longitude);
            double _latitude = Double.parseDouble(latitude);
            double _longitude = Double.parseDouble(longitude);

            LatLng sydney = new LatLng(_latitude, _longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Post location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }
}

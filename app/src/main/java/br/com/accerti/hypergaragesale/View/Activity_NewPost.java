package br.com.accerti.hypergaragesale.View;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import br.com.accerti.hypergaragesale.Control.PostsDbHelper;
import br.com.accerti.hypergaragesale.Model.Posts;
import br.com.accerti.hypergaragesale.R;

public class Activity_NewPost extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {

    private SQLiteDatabase db;
    private ContentValues values;

    private EditText titleText;
    private EditText descText;
    private EditText priceText;
    private String latitudeText;
    private String longitudeText;

    private Button takePictureButton;
    private ImageView imageView;
    private Uri file;


    /* Position */
    String provider;
    private static final long MINIMUM_TIME = 10000;  // 10s
    private static final float MINIMUM_DISTANCE = 50; // 50m

    // TTS
    int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech mTts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (EditText) findViewById(R.id.txtTitle);
        descText = (EditText) findViewById(R.id.txtDescription);
        priceText = (EditText) findViewById(R.id.txtPrice);

        // Gets the data repository in write mode
        PostsDbHelper mDbHelper = new PostsDbHelper(this);
        db = mDbHelper.getWritableDatabase();

        //Working with the CAMERA
        takePictureButton = (Button) findViewById(R.id.btnPicture);
        imageView = (ImageView) findViewById(R.id.ImageTaken);

        //if the user doesn't permit access to the camera, disable the TakePicture button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        mTts = new TextToSpeech(this, this);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.myCoordinatorLayout), msg,
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_post) {
            showSnackBar(getResources().getString(R.string.new_post_snackbar) + "!");
            addPost();
        } else if (item.getItemId() == R.id.action_delete_post) {
            ShowMyDialog(getResources().getString(R.string.dialog_title),
                    getResources().getString(R.string.dialog_message),
                    "Here is the New Post!", "New post canceled!");
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowMyDialog(String title, String message, final String positiveText, final String negativeText) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                showSnackBar(positiveText);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                showSnackBar(negativeText);
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addPost() {
        Random rand = new Random();
        int n = rand.nextInt();

        // Create a new map of values, where column names are the keys
        values = new ContentValues();
        values.put(Posts.PostEntry.COLUMN_NAME_ID, String.valueOf(n));
        values.put(Posts.PostEntry.COLUMN_NAME_TITLE, titleText.getText().toString());
        values.put(Posts.PostEntry.COLUMN_NAME_DESCRIPTION, descText.getText().toString());
        values.put(Posts.PostEntry.COLUMN_NAME_PRICE, priceText.getText().toString());
        if (file != null){
            values.put(Posts.PostEntry.COLUMN_NAME_PICTURE, file.getPath());
        }else {
            values.put(Posts.PostEntry.COLUMN_NAME_PICTURE, "");
        }
        values.put(Posts.PostEntry.COLUMN_NAME_LATITUDE, latitudeText);
        values.put(Posts.PostEntry.COLUMN_NAME_LONGITUDE, longitudeText);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                Posts.PostEntry.TABLE_NAME,
                null,
                values);

        SpeakThankYou();

        // Done adding new entry into database, navigate user back to browsing screen
        startActivity(new Intent(this, Activity_BrowsePosts.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    //is the request code defined as an instance variable
    //number of pics the user can get
    private int SELECT_PICTURE = 100;
    private int TAKE_PICTURE = 101;

    public void takePicture(View view) {

        //SOURCE OF THE PICTURE: galery ou camera?
        //ShowDialog
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Where do you want to get the picture?")
                .setTitle("Select picture source");

        // Add the buttons
        builder.setPositiveButton("Galery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button"
                //GALERY:
                //article credits: http://stackoverflow.com/questions/10473823/android-get-image-from-gallery-into-imageview
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                file = Uri.fromFile(getOutputMediaFile());
                Log.e("FILE From Galery:",file.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //CAMERA:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                Log.e("FILE From Camera:",file.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GarageSale");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("GarageSale", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_GS_" + timeStamp + ".jpg");
    }

    //will show up the image in the Activity/Fragment user interface
    Bitmap bitmap = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //present the selected picture
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE  && data != null && data.getData() != null){
            //file = data.getData();

            Uri uri = data.getData();
            Log.d("URI:", uri.toString());

            try {
                File _file = new File(getRealPathFromURI_API19(this, data.getData()));
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap bitmap = BitmapFactory.decodeFile(_file.getAbsolutePath());;
                file = Uri.fromFile(_file);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //imageView.setImageURI(file);
        } else if(requestCode == TAKE_PICTURE) {
                imageView.setImageURI(file);
        }
    }


    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private final int MY_PERMISSION_ACCESS_COURSE_LOCATION=1;

    ///LOCATION
    public void getLocation(View view){
        Log.d("LOCATION","Setting the location!");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();
        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, true);

        //check for permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // No one provider activated: prompt GPS
            if (provider == null || provider.equals("")) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            Log.d("SELECTED PROVIDER",provider.toString());

            locationManager.requestSingleUpdate(provider, this, null);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location == null || location.equals("")){
                Toast.makeText(getBaseContext(), "No position found! Please check if the GPS is enabled.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                // Called when a new location is found by the network location provider.
                Log.d("LATITUDE:", String.valueOf(location.getLatitude()));
                Log.d("LONGITUDE:", String.valueOf(location.getLongitude()));

                latitudeText = String.valueOf(location.getLatitude());
                longitudeText = String.valueOf(location.getLongitude());

                showSnackBar("Location registered! Latitude: "+String.valueOf(location.getLatitude())
                        + " Longitude: "+String.valueOf(location.getLongitude()));
            }
        }else{
            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            Toast.makeText(getBaseContext(), "No permission to access the location! Please, enable " +
                    "the location permission.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLocationChanged(Location location) {
        //Log.d("LATITUDE:", String.valueOf(location.getLatitude()));
        //Log.d("LONGITUDE:", String.valueOf(location.getLongitude()));
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //TTS
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTts.setLanguage(Locale.US);

        }
    }

    @Override
    public void onDestroy(){
        if (mTts != null){
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    private void SpeakThankYou()
    {
        mTts.speak("Thank you for posting " + titleText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}
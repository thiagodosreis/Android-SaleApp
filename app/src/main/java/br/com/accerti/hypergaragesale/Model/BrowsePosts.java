package br.com.accerti.hypergaragesale.Model;

/**
 * Created by thiagodosreis on 8/23/16.
 */
public class BrowsePosts {
    public String mID;
    public String mTitle;
    public String mPrice;
    public String mPicture;
    public String mDescr;
    public String mLatitude;
    public String mLongitude;

    public BrowsePosts (String id, String title, String price, String picture, String description,
                        String latitude, String longitude) {
        this.mID = id;
        this.mTitle = title;
        this.mPrice = price;
        this.mPicture = picture;
        this.mDescr = description;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }
}

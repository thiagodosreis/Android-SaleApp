package br.com.accerti.hypergaragesale.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import br.com.accerti.hypergaragesale.Model.BrowsePosts;
import br.com.accerti.hypergaragesale.R;

/**
 * Created by thiagodosreis on 8/23/16.
 */
    public class Adapter_Posts extends RecyclerView.Adapter<Adapter_Posts.ViewHolder> {

    private ArrayList<BrowsePosts> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        // each data item is just a string in this case
        public TextView mTitle;
        public TextView mPrice;
        public ImageView mMyPicture;
        public TextView mID;
        private final Context context;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) itemView.findViewById(R.id.titleView);
            mPrice = (TextView) itemView.findViewById(R.id.priceView);
            mMyPicture = (ImageView) itemView.findViewById(R.id.myPicture);
            mID = (TextView) itemView.findViewById(R.id.idItem);
            // Implement view click Listener when make each row of RecyclerView clickable

            context = itemView.getContext();
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            //Toast.makeText(context,"The Item Clicked is: "+getAdapterPosition(),Toast.LENGTH_SHORT).show();

            Intent intent =  new Intent(context, Activity_DetailPost.class);
            intent.putExtra("ID", Integer.parseInt(mID.getText().toString()));
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Adapter_Posts(ArrayList<BrowsePosts> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Adapter_Posts.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get elements from your dataset at this position
        // - replace the contents of the views with that elements
        holder.mTitle.setText(mDataset.get(position).mTitle);
        holder.mPrice.setText("U$ " + mDataset.get(position).mPrice);
        holder.mID.setText(mDataset.get(position).mID);

        if(mDataset.get(position).mPicture != null){
            File imgFile = new  File(mDataset.get(position).mPicture);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.mMyPicture.setImageBitmap(myBitmap);
            }
            else{
                Log.d("ERROR:", "Couldn't find file: "+imgFile.toString());
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

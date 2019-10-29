package com.eklee.timestamptable.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eklee.timestamptable.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Judy on 2018-01-29.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private String mAppend;
    private ArrayList<String> imgURLs;
    private String selectedImageURL;

    public RecyclerViewAdapter(Context context,  String append, ArrayList<String> imgURLs, String selectedImageURL) {
        this.mContext = context;
        this.mAppend = append;
        this.imgURLs = imgURLs;
        this.selectedImageURL = selectedImageURL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        //get the view holder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int Position = position;
        String imgURL = imgURLs.get(position);
        selectedImageURL = imgURL;
        Log.d(TAG, "onClick: imgURL: "+ mAppend + imgURL);

        try {
            Glide.with(mContext)
                    .asBitmap()
                    .load(imgURL)
                    .into(holder.image);

        } catch (Exception e) {
            Toast.makeText(mContext, "failed to load the image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.onClick(v, Position);
                }
            }
        });
    }

    //clicking an item
    private ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public ArrayList<String> getImgURLs() {
        return imgURLs;
    }


    @Override
    public int getItemCount() {
        return imgURLs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView galleryImage;
        SquareImageView image;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //attaching id
            galleryImage = itemView.findViewById(R.id.galleryImageView);
            image = itemView.findViewById(R.id.gridImageView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}

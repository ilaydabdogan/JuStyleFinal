package com.pro.android.justyle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;


public  class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Context mContext;
    private final List<Upload> mUploads;
   private static OnItemClickListener mListener;

    ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.article_item, parent,
                false);
        return new ImageViewHolder(v);
    }

    @Override
    /**
     * onBindViewHolder creates an instace of Upload class and
     * gets and sets the image position and name
     */
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());

        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .resize(500,500)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        final TextView textViewName;
        final ImageView imageView;



        ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        /**
         * creates a context menu with two items: "Delete" and "Send to Marketplace"
         */
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");

                MenuItem sendToMarketplace = menu.add(Menu.NONE, 1, 1,
                        "Send to Marketplace");
                MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");
                sendToMarketplace.setOnMenuItemClickListener(this);
                delete.setOnMenuItemClickListener(this);
        }

        @Override
        /**
         * OnMenuItemClick method listens for the clicks on the menu items
         */
        public boolean onMenuItemClick(MenuItem item) {

                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        switch (item.getItemId()) {
                            case 1:
                                mListener.sendToMarketClick(position);
                                return true;
                            case 2:
                                mListener.onDeleteClick(position);
                                return true;
                        }
                    }
                }

            return false;

            }
        }

    public interface OnItemClickListener {
        void onItemClick(int position);
       void sendToMarketClick(int position);
       void onDeleteClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
package com.ithomaslin.caffeinista;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by thomaslin on 8/7/16.
 *
 */
public class NavHeaderAdapter extends RecyclerView.Adapter <NavHeaderAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String name;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int HolderId;

        TextView usernameTextView;
        ImageView userPhotoImageView;

        ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if(ViewType == TYPE_ITEM) {
                usernameTextView = (TextView) itemView.findViewById(R.id.nav_username);
                userPhotoImageView = (ImageView) itemView.findViewById(R.id.nav_userphoto);
                HolderId = 1;
            } else {

            }
        }
    }

    NavHeaderAdapter(String _name, String _email) {

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

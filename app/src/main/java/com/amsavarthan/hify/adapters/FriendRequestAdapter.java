package com.amsavarthan.hify.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsavarthan.hify.R;
import com.amsavarthan.hify.models.FriendRequest;
import com.amsavarthan.hify.ui.activities.FriendRequestActivty;
import com.amsavarthan.hify.ui.activities.FriendsView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amsavarthan on 22/2/18.
 */

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<FriendRequest> usersList;
    private Context context;
    private ViewHolder holderr;
    private String userId;

    public FriendRequestAdapter(List<FriendRequest> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_req_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String id = usersList.get(position).getId();
        final String name = usersList.get(position).getName();
        final String email = usersList.get(position).getEmail();
        final String image = usersList.get(position).getImage();
        final String token = usersList.get(position).getToken();


        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().placeholder(context.getResources().getDrawable(R.mipmap.profile_black)))
                .load(image)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send user data as intent extras to FriendRequestActivity
                FriendRequestActivty.startActivity(context, name, email, image, id, token);
                FriendsView.activity.finish();
            }
        });

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (CircleImageView) itemView.findViewById(R.id.userimage);


        }
    }
}

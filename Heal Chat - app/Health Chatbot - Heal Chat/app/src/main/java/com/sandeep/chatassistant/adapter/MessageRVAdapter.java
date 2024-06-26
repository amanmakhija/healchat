package com.sandeep.chatassistant.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandeep.chatassistant.R;
import com.sandeep.chatassistant.data.Message;

import java.util.ArrayList;

public class MessageRVAdapter extends RecyclerView.Adapter {

    // variable for our array list and context.
    private ArrayList<Message> messageModalArrayList;
    private Context context;

    // constructor class.
    public MessageRVAdapter(ArrayList<Message> messageModalArrayList, Context context) {
        this.messageModalArrayList = messageModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // below code is to switch our
        // layout type along with view holder.
        switch (viewType) {
            case 0:
                // below line we are inflating user message layout.
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
                return new UserViewHolder(view);
            case 1:
                // below line we are inflating bot message layout.
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_item, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // this method is use to set data to our layout file.
        Message modal = messageModalArrayList.get(position);
        switch (modal.getSender()) {
            case "user":
                // Check if the message text is empty and image URI is present
                if (modal.getMessage().isEmpty() && modal.getImageUri() != null) {
                    // Hide the TextView for message and set the image in ImageView
                    ((UserViewHolder) holder).userTV.setVisibility(View.GONE);
                    ((UserViewHolder) holder).userImage.setVisibility(View.VISIBLE);
                    Uri imageUri = Uri.parse(modal.getImageUri());
                    ((UserViewHolder) holder).userImage.setImageURI(imageUri);
                } else {
                    // Set the message text in TextView and hide the ImageView
                    ((UserViewHolder) holder).userTV.setVisibility(View.VISIBLE);
                    ((UserViewHolder) holder).userImage.setVisibility(View.GONE);
                    ((UserViewHolder) holder).userTV.setText(modal.getMessage());
                }
                break;
            case "bot":
            {
                // below line is to set the text to our text view of bot layout
                ((BotViewHolder) holder).botTV.setText(Html.fromHtml(modal.getMessage()));
                ((BotViewHolder) holder).botTV.setMovementMethod(LinkMovementMethod.getInstance());
                break;
            }

        }
    }

    @Override
    public int getItemCount() {
        // return the size of array list
        return messageModalArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // below line of code is to set position.
        switch (messageModalArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        // creating a variable
        // for our text view.
        TextView userTV;
        ImageView userImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing with id.
            userTV = itemView.findViewById(R.id.idTVUser);
            userImage = itemView.findViewById(R.id.imageFromGallery);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {

        // creating a variable
        // for our text view.
        TextView botTV;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing with id.
            botTV = itemView.findViewById(R.id.idTVBot);
        }
    }
}
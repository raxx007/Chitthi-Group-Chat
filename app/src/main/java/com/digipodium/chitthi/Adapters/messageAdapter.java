package com.digipodium.chitthi.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digipodium.chitthi.Models.AllMethods;
import com.digipodium.chitthi.Models.Message;
import com.digipodium.chitthi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class messageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    Context context;
    List<Message> messages;
    DatabaseReference messagedb;
    private OnItemClickListener mlistener;

    public messageAdapter(Context context, List<Message> messages, DatabaseReference messagedb) {
        this.context = context;
        this.messagedb = messagedb;
        this.messages = messages;

    }

    @Override
    public int getItemCount () {
        return messages.size();
    }
    @Override
    public int getItemViewType ( int position){
        Message message = messages.get(position);
        if (message.getName().equals(AllMethods.name)){
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new receivedMessageHolder(view);
        }

        return null;
    }

        @Override
        public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder,int position){
            Message message = messages.get(position);
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(message);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((receivedMessageHolder) holder).bind(message);
            }
        }

         private class receivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
            TextView messageText, timeText, nameText;
            ImageView img;
            ConstraintLayout cl;

            public receivedMessageHolder(@NonNull View itemView) {
                super(itemView);
                messageText =  itemView.findViewById(R.id.text_message_body);
                timeText =  itemView.findViewById(R.id.text_message_time);
                nameText =  itemView.findViewById(R.id.text_message_name);
                img=itemView.findViewById(R.id.imageView);
                cl=itemView.findViewById(R.id.cl);
                img.setOnClickListener(this);
                cl.setOnCreateContextMenuListener(this);

            }

             @Override
             public void onClick(View v) {
                 if(mlistener!=null){
                     int position=getAdapterPosition();
                     if (position !=RecyclerView.NO_POSITION){
                         mlistener.OnItemClick(position);
                     }
                 }
             }

             void bind(Message message) {
                if (message.getMessage().equals("")){
                    nameText.setText(message.getName());
                    messageText.setVisibility(View.GONE);
                    timeText.setText(message.getCreatedAt());
                    Glide.with(context).load(message.getmImageURL()).centerCrop().into(img);
                }else{
                    nameText.setText(message.getName());
                    img.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());
                    timeText.setText(message.getCreatedAt());

                }
            }

             @Override
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
             }
         }

        private class SentMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

            private final TextView messageText;
            private final TextView timeText;
            ImageView img;
            ConstraintLayout cl;

            public SentMessageHolder(@NonNull View itemView) {
                super(itemView);
                cl=itemView.findViewById(R.id.cl);
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
                img=itemView.findViewById(R.id.imageView);
                img.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(mlistener!=null){
                    int position=getAdapterPosition();
                    if (position !=RecyclerView.NO_POSITION){
                        mlistener.OnItemClick(position);
                    }
                }
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem delete= menu.add(Menu.NONE,2,2,"Delete");
                delete.setOnMenuItemClickListener(this);
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(mlistener!=null){
                    int position=getAdapterPosition();
                    if (position !=RecyclerView.NO_POSITION){
                                mlistener.OnDeleteClick(position);
                                return true;
                    }
                }
                return false;
            }

            void bind(Message message) {
                if (message.getMessage().equals("")){
                    messageText.setVisibility(View.GONE);
                    timeText.setText(message.getCreatedAt());
                    Glide.with(context).load(message.getmImageURL()).centerCrop().into(img);
                    img.setOnCreateContextMenuListener(this);

                }else{
                    img.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());
                    timeText.setText(message.getCreatedAt());
                    cl.setOnCreateContextMenuListener(this);
                }
            }
        }

        public interface OnItemClickListener{
        void OnItemClick(int position);
        void OnDeleteClick(int position);
        }
        public void setOnItemClickListener(OnItemClickListener listener){
        mlistener=listener;
    }

    public void removeItem(int position){
        messages.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
     }
    }


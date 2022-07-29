package com.tu.blinvoi11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView notifications_list;
    private DatabaseReference friendRequestRef, contactsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        notifications_list = findViewById(R.id.notifications_list);
        notifications_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

@Override
public void onStart()
{
    super.onStart();

    FirebaseRecyclerOptions options =
            new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(friendRequestRef.child(currentUserId), Contacts.class)
                    .build();

    FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder> firebaseRecyclerAdapter
            = new FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull final NotificationsViewHolder holder, int i, @NonNull Contacts contacts)
        {
            holder.acceptBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setVisibility(View.VISIBLE);

            final String listUserId = getRef(i).getKey();

            DatabaseReference requestTypeRef = getRef(i).child("request_type").getRef();
            requestTypeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        String type = dataSnapshot.getValue().toString();

                        if (type.equals("received"))
                        {
                            holder.cardView.setVisibility(View.VISIBLE);

                            usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.hasChild("Image"))
                                    {
                                        final String imageStr = dataSnapshot.child("Image").getValue().toString();

                                        Picasso.get().load(imageStr).into(holder.ProfileImageView);
                                    }

                                    final String nameStr = dataSnapshot.child("name").getValue().toString();
                                    holder.usernameText.setText(nameStr);

                                    holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            contactsRef.child(currentUserId).child(listUserId)
                                                    .child("Contact").setValue("Saved")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                contactsRef.child(listUserId).child(currentUserId)
                                                                        .child("Contact").setValue("Saved")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    friendRequestRef.child(currentUserId).child(listUserId)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if (task.isSuccessful())
                                                                                                    {
                                                                                                        friendRequestRef.child(listUserId).child(currentUserId)
                                                                                                                .removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                                    {
                                                                                                                        if (task.isSuccessful())
                                                                                                                        {
                                                                                                                            Toast.makeText(NotificationsActivity.this, "تم اضافة جهة اتصال جديده", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            friendRequestRef.child(currentUserId).child(listUserId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                friendRequestRef.child(currentUserId).child(listUserId)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(NotificationsActivity.this, "تم الغاء الطلب", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            holder.cardView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_design, parent, false);
            NotificationsViewHolder viewHolder = new NotificationsViewHolder(view);
            return viewHolder;
        }
    };
    notifications_list.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.startListening();


}
    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        Button acceptBtn,cancelBtn;
        ImageView ProfileImageView;
        RelativeLayout cardView;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.name_notifcation);
            acceptBtn=itemView.findViewById(R.id.request_accept_btn);
            cancelBtn=itemView.findViewById(R.id.request_decline_btn);
            ProfileImageView=itemView.findViewById(R.id.image_notification);
            cardView=itemView.findViewById(R.id.card_view);
        }
    }

}

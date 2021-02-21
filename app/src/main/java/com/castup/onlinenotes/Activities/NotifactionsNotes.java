package com.castup.onlinenotes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.castup.onlinenotes.Models.InfoNotes;
import com.castup.onlinenotes.R;
import com.castup.onlinenotes.databinding.ActivityNotifictionsNotesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotifactionsNotes extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseRecyclerOptions<InfoNotes> options;
    private FirebaseRecyclerAdapter<InfoNotes, HolderNoti> adapter;
    private DividerItemDecoration decoration;
    private ActivityNotifictionsNotesBinding notifactionsNotesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifactionsNotesBinding = ActivityNotifictionsNotesBinding.inflate(getLayoutInflater());
        setContentView(notifactionsNotesBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        back();
        NotificationAdapter();
        SearchNote();

    }

    private void back() {

        notifactionsNotesBinding.backfromNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent BK = new Intent(getBaseContext(), MainActivity.class);
                setResult(RESULT_OK, BK);
                finish();
            }
        });
    }

    private void NotificationAdapter() {

        Query query = databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note");

        options = new FirebaseRecyclerOptions.Builder<InfoNotes>()
                .setQuery(query, InfoNotes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<InfoNotes, HolderNoti>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderNoti holderNoti, int position, @NonNull final InfoNotes notes) {

                String REF = getRef(position).getKey();

                databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").child(REF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            holderNoti.ShowDataNote(notes);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public HolderNoti onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HolderNoti(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notifictions, parent, false));
            }
        };

        decoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        notifactionsNotesBinding.recyclerViewNoti.addItemDecoration(decoration);
        notifactionsNotesBinding.recyclerViewNoti.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        notifactionsNotesBinding.swipRefreshNoti.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter.notifyDataSetChanged();
                adapter.startListening();
                notifactionsNotesBinding.recyclerViewNoti.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
                notifactionsNotesBinding.recyclerViewNoti.setAdapter(adapter);
                notifactionsNotesBinding.swipRefreshNoti.setRefreshing(false);
            }
        });
        adapter.notifyDataSetChanged();
        adapter.startListening();
        notifactionsNotesBinding.recyclerViewNoti.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        notifactionsNotesBinding.recyclerViewNoti.setAdapter(adapter);

    }

    private void NotificationAdapterSearch(String title) {

        Query query = databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").orderByChild("title").startAt(title.trim().toLowerCase()).endAt(title.trim().toLowerCase() + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<InfoNotes>()
                .setQuery(query, InfoNotes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<InfoNotes, HolderNoti>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderNoti holderNoti, int position, @NonNull final InfoNotes notes) {

                String REF = getRef(position).getKey();

                databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").child(REF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            holderNoti.ShowDataNote(notes);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public HolderNoti onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HolderNoti(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notifictions, parent, false));
            }
        };

        notifactionsNotesBinding.recyclerViewNoti.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapter.notifyDataSetChanged();
        adapter.startListening();
        notifactionsNotesBinding.recyclerViewNoti.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        notifactionsNotesBinding.recyclerViewNoti.setAdapter(adapter);

    }

    private void SearchNote(){

        notifactionsNotesBinding.SearchNoti.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                NotificationAdapterSearch(newText);

                return false;
            }
        });

        notifactionsNotesBinding.SearchNoti.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                NotificationAdapter();
                return false;
            }
        });

    }

    public class HolderNoti extends RecyclerView.ViewHolder {

        private ImageView photoNoti ;
        private CircleImageView photoSenderNoti ;
        private TextView  nameSenderNoti , titleNoti , dateNoti , AddNote , DeleteNote;
        private ConstraintLayout expandedableLayout , onClicklayout;

        public HolderNoti(@NonNull View itemView) {
            super(itemView);

            photoNoti = itemView.findViewById(R.id.custom_Image_Noti);
            photoSenderNoti = itemView.findViewById(R.id.image_sender_Noti);
            nameSenderNoti = itemView.findViewById(R.id.name_Sender_Noti);
            titleNoti = itemView.findViewById(R.id.title_note_Noti);
            dateNoti = itemView.findViewById(R.id.date_Noti);
            AddNote = itemView.findViewById(R.id.addNoti);
            DeleteNote = itemView.findViewById(R.id.deleteNoti);
            onClicklayout  = itemView.findViewById(R.id.expanded_click);

        }

        private void ShowDataNote(final InfoNotes noteNoti){

            databaseRef.child("Users").child(noteNoti.getSender()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(photoSenderNoti);
                        String sender = Character.toUpperCase(dataSnapshot.child("name").getValue().toString().charAt(0))+dataSnapshot.child("name").getValue().toString().substring(1).toLowerCase();
                        nameSenderNoti.setText(sender);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(noteNoti.getPathimage() == null){

                photoNoti.setImageResource(R.drawable.ic_no_image_noti100);
                photoNoti.setPadding(30,40,30,40);

            }else {
                Picasso.get().load(noteNoti.getPathimage()).into(photoNoti);
                photoNoti.setBackgroundResource(R.drawable.note_iamge_note);
            }

            String title = Character.toUpperCase(noteNoti.getTitle().charAt(0))+noteNoti.getTitle().substring(1).toLowerCase();
            titleNoti.setText(title);
            dateNoti.setText(noteNoti.getNotedate());

            AddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InfoNotes ShareNote = new InfoNotes(noteNoti.getTitle(), noteNoti.getSubtitle(), noteNoti.getDescription(), noteNoti.getNotedate(), noteNoti.getNameimage(), noteNoti.getPathvoice(), noteNoti.getNamevoice(), noteNoti.getTag(), noteNoti.getRefnote(),noteNoti.getSender(),mAuth.getCurrentUser().getUid() , noteNoti.getPathimage(), noteNoti.getWeburl(), noteNoti.getF(), noteNoti.getS(), noteNoti.getT());

                    databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").child(noteNoti.getRefnote()).setValue(ShareNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").child(noteNoti.getRefnote()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            Toast.makeText(getBaseContext(),"Added successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
            });

            DeleteNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").child(noteNoti.getRefnote()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(getBaseContext(),"Deleted successfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }

    }

    private void Itim() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(NotifactionsNotes.this, R.color.colorDelete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(NotifactionsNotes.this,R.color.colorGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_add_note)
                        .create()
                        .decorate();
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction) {

                    case ItemTouchHelper.RIGHT:




                    break;

                    case ItemTouchHelper.LEFT:



                        break;
                }


            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(notifactionsNotesBinding.recyclerViewNoti);

    } // no Use Now

}


package com.castup.onlinenotes.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appodeal.ads.Appodeal;
import com.castup.onlinenotes.Models.AccountInformation;
import com.castup.onlinenotes.Models.InfoNotes;
import com.castup.onlinenotes.Models.InfoTags;
import com.castup.onlinenotes.R;
import com.castup.onlinenotes.databinding.ActivityMainBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADDITIONNOTESACTIVITY = 10;
    private static final int REQUEST_CODE_UPDATENOTE = 20;
    private static final int REQUEST_CODE_CAMERA = 30;
    private static final int REQUEST_CODE_GALLLERY = 40;

    private static final int REQUEST_CODE_PERMISSION_DOWNLOAD = 100;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 200;
    private static final int REQUEST_CODE_PERMISSION_GALLERY = 300;
    private static final int REQUEST_CODE_PERMISSION_VOICE = 400;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private UploadTask uploadTask;

    private ActivityMainBinding mainBinding;
    private FirebaseRecyclerAdapter<InfoNotes, HolderNotes> adapter;
    private FirebaseRecyclerOptions<InfoNotes> options;
    private SharedPreferences preferences;
    private AlertDialog dialogDelete, dialogAddLink, dialogAddVoice, dialogShare;
    private String URl, NameMidea, Type, nameRandomimage, nameRandomvioce;
    private String pathfileVoice = null;
    private boolean isRecording = false;
    private boolean isRunning = false ;
    private MediaRecorder mediaRecorder;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();


        Login();
        setMainToolbar();
        setNavigationView();
        AdditionNewNotes();
        ShowNotesRecycle();
        Search();
        ClicksMedia();
        goToNotifications();


    }

    private void Login() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            finish();
            startActivity(new Intent(getBaseContext(), LogInUser.class));

        }

    }

    private void ads(View view){
     //   String TS = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f" ;
        String ID = "44833b2540ae7fa360dea5184926abed8d25c09b2cd47fea" ;

        Appodeal.initialize((Activity)view.getContext(),ID,Appodeal.INTERSTITIAL);
        Appodeal.show((Activity)view.getContext(), Appodeal.INTERSTITIAL);

        if(isRunning){

            Appodeal.isLoaded(Appodeal.INTERSTITIAL);
        }

    }

    private void setMainToolbar() {

        setSupportActionBar(mainBinding.mainToolbar.getRoot());
        getSupportActionBar().setTitle("My Notes");

    }

    private void goToNotifications() {


        databaseRef.child("Notifications").child(mAuth.getCurrentUser().getUid()).child("Note").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    long NumNotes = dataSnapshot.getChildrenCount();

                    mainBinding.numNotifications.setText(NumNotes + "");
                } else {

                    mainBinding.numNotifications.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        mainBinding.imagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getBaseContext(), NotifactionsNotes.class), REQUEST_CODE_ADDITIONNOTESACTIVITY);
                ads(v);

            }
        });
    }

    private void setNavigationView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainBinding.layoutDrawer, mainBinding.mainToolbar.getRoot(), R.string.open, R.string.Close);
        mainBinding.layoutDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorWhite));

        } else {

            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        }

        mainBinding.layoutNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.logout:

                        mAuth.signOut();
                        startActivity(new Intent(getBaseContext(), LogInUser.class));
                        Toast.makeText(getBaseContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();

                        break;


                }

                mainBinding.layoutDrawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        actionHeader();
    }

    private void actionHeader() {

        View view = mainBinding.layoutNavigation.getHeaderView(0);

        final CircleImageView usreImage = view.findViewById(R.id.UserImage);
        final TextView userName = view.findViewById(R.id.UserName);
        final TextView userEmail = view.findViewById(R.id.UserEmail);
        final Spinner OptionSearch = view.findViewById(R.id.OptionSearch);

        databaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String name = Character.toUpperCase(dataSnapshot.child("name").getValue().toString().charAt(0)) + dataSnapshot.child("name").getValue().toString().toLowerCase().trim().substring(1);
                    String email = Character.toUpperCase(dataSnapshot.child("email").getValue().toString().trim().charAt(0)) + dataSnapshot.child("email").getValue().toString().toLowerCase().trim().substring(1);

                    Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(usreImage);
                    userName.setText(name);
                    userEmail.setText(email);

                    Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(mainBinding.imagecard);
                    mainBinding.textWelcome.setText("Hello " + name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChooseSearch(OptionSearch);

        view.findViewById(R.id.AllNotes_Header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNotesRecycle();
                mainBinding.layoutDrawer.closeDrawer(GravityCompat.START);
            }
        });

        layoutTabs(view);

    }

    private void ClicksMedia() {

        mainBinding.Camira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermoissionCamera();

            }
        });

        mainBinding.Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PermoissionGallery();
                ads(v);

            }
        });

        mainBinding.Voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermoissionVoice();
                ads(v);

            }
        });

        mainBinding.Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogAddLink();
            }
        });
    }

    private void Camera() {

        Intent CAM = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(CAM, REQUEST_CODE_CAMERA);

    }

    private void Gallery() {

        Intent Gallery = new Intent(Intent.ACTION_GET_CONTENT);
        Gallery.setType("image/*");
        startActivityForResult(Gallery, REQUEST_CODE_GALLLERY);
    }

    private void Voice() {

        DialogVoice();
    }

    private void PermoissionCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
            } else {

                Camera();
            }

        }

    }

    private void PermoissionGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_GALLERY);
            } else {

                Gallery();
            }

        }

    }

    private void PermoissionVoice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION_VOICE);

            } else {

                Voice();
            }

        }
    }

    private void AdditionNewNotes() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5486612716888257/5295916916");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mainBinding.OpenNewBututton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getBaseContext(), AdditionNotes.class), REQUEST_CODE_ADDITIONNOTESACTIVITY);

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

    }

    private void ShowNotesRecycle() {

        Query query = databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note");

        options = new FirebaseRecyclerOptions.Builder<InfoNotes>()
                .setQuery(query, InfoNotes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<InfoNotes, HolderNotes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderNotes holderNotes, final int position, @NonNull final InfoNotes infoNotes) {

                final String NoteREF = getRef(position).getKey();
                final String UID = mAuth.getCurrentUser().getUid();

                databaseRef.child("AllNotes").child(UID).child("Note").child(NoteREF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            holderNotes.setAllNote(dataSnapshot, UID, NoteREF);
                            holderNotes.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    ChooseAction(dataSnapshot, infoNotes);
                                    return false;
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public HolderNotes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HolderNotes(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_allnotes, parent, false));
            }
        };

        mainBinding.NotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mainBinding.swipLayoutRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mainBinding.swipLayoutRecycler.setRefreshing(false);
                mainBinding.NotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                mainBinding.NotesRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.startListening();
            }
        });

        mainBinding.NotesRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    class HolderNotes extends RecyclerView.ViewHolder {

        private CircleImageView SenderImage;
        private RoundedImageView NoteImage;
        private TextView SenderName, Title, Subtitle, DateNote;
        private LinearLayout ColorNote;

        public HolderNotes(@NonNull View itemView) {
            super(itemView);

            SenderImage = itemView.findViewById(R.id.SenderImage);
            NoteImage = itemView.findViewById(R.id.imageNotes);
            SenderName = itemView.findViewById(R.id.NameSender);
            Title = itemView.findViewById(R.id.MainTitle);
            Subtitle = itemView.findViewById(R.id.Subtitle);
            DateNote = itemView.findViewById(R.id.DateNote);
            ColorNote = itemView.findViewById(R.id.layoutNote);

        }

        private void setAllNote(final DataSnapshot snapshot, String UID, String NoteREF) {

            databaseRef.child("Users").child(snapshot.child("sender").getValue().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(SenderImage);
                        SenderName.setText(dataSnapshot.child("name").getValue().toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (snapshot.hasChild("pathimage")) {

                Picasso.get().load(snapshot.child("pathimage").getValue().toString()).into(NoteImage);
                NoteImage.setVisibility(View.VISIBLE);

            } else {

                NoteImage.setVisibility(View.GONE);
            }


            databaseRef.child("AllNotes").child(UID).child("Note").child(NoteREF).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.child("f").getValue().toString().equals("255") && dataSnapshot.child("s").getValue().toString().equals("255") && dataSnapshot.child("t").getValue().toString().equals("255") || dataSnapshot.child("f").getValue().toString().equals("255") && dataSnapshot.child("s").getValue().toString().equals("214") && dataSnapshot.child("t").getValue().toString().equals("0")) {

                            String title = Character.toUpperCase(snapshot.child("title").getValue().toString().charAt(0)) + snapshot.child("title").getValue().toString().toString().substring(1);
                            Title.setText(title);
                            Title.setTextColor(Color.rgb(0, 0, 0));
                            Subtitle.setText(snapshot.child("subtitle").getValue().toString());
                            Subtitle.setTextColor(Color.rgb(0, 0, 0));
                            DateNote.setText(snapshot.child("notedate").getValue().toString());
                            DateNote.setTextColor(Color.rgb(0, 0, 0));
                            GradientDrawable gradientDrawable = (GradientDrawable) ColorNote.getBackground();
                            gradientDrawable.setColor(Color.rgb(Integer.parseInt(snapshot.child("f").getValue().toString()), Integer.parseInt(snapshot.child("s").getValue().toString()), Integer.parseInt(snapshot.child("t").getValue().toString())));
                            SenderName.setTextColor(Color.rgb(0, 0, 0));
                            ColorNote.setVisibility(View.VISIBLE);

                        } else {

                            String title = Character.toUpperCase(snapshot.child("title").getValue().toString().charAt(0)) + snapshot.child("title").getValue().toString().substring(1);
                            Title.setText(title);
                            Title.setTextColor(Color.rgb(255, 255, 255));
                            Subtitle.setText(snapshot.child("subtitle").getValue().toString());
                            Subtitle.setTextColor(Color.rgb(255, 255, 255));
                            DateNote.setText(snapshot.child("notedate").getValue().toString());
                            DateNote.setTextColor(Color.rgb(255, 255, 255));
                            GradientDrawable gradientDrawable = (GradientDrawable) ColorNote.getBackground();
                            gradientDrawable.setColor(Color.rgb(Integer.parseInt(snapshot.child("f").getValue().toString()), Integer.parseInt(snapshot.child("s").getValue().toString()), Integer.parseInt(snapshot.child("t").getValue().toString())));
                            SenderName.setTextColor(Color.rgb(255, 255, 255));
                            ColorNote.setVisibility(View.VISIBLE);
                        }

                    } else {


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CODE_ADDITIONNOTESACTIVITY:

                if (resultCode == RESULT_OK && data != null) {

                    ShowNotesRecycle();
                }

                break;

            case REQUEST_CODE_UPDATENOTE:

                if (resultCode == RESULT_OK && data != null) {

                    ShowNotesRecycle();

                    Toast.makeText(getBaseContext(), "Note have been updated", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_CAMERA:

                if (resultCode == RESULT_OK && data != null) {

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    final byte[] IMG = stream.toByteArray();

                    nameRandomimage = UUID.randomUUID().toString() + ".jpg";
                    final ProgressDialog dialogCam = new ProgressDialog(this);

                    StorageReference storageReference = storageRef.child("Images").child(mAuth.getCurrentUser().getUid()).child(nameRandomimage);
                    uploadTask = storageReference.putBytes(IMG);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (uri.isAbsolute()) {

                                        String imageUri = uri.toString();

                                        HashMap<String, String> imageMap = new HashMap<>();
                                        imageMap.put("pathimage", imageUri);
                                        imageMap.put("nameimage", nameRandomimage);

                                        databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("image")
                                                .setValue(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isComplete()) {

                                                    dialogCam.dismiss();
                                                    Intent NoteCam = new Intent(getBaseContext(), AdditionNotes.class);
                                                    NoteCam.putExtra("tools", 101);
                                                    NoteCam.putExtra("changeCamera_OR_Gallery", 1);
                                                    NoteCam.putExtra("image", IMG);
                                                    startActivityForResult(NoteCam, REQUEST_CODE_ADDITIONNOTESACTIVITY);

                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / (int) taskSnapshot.getTotalByteCount());
                            dialogCam.setMessage("Uplaoding " + ((int) progress + " %..."));
                            dialogCam.show();
                        }
                    });

                }

                break;

            case REQUEST_CODE_GALLLERY:

                if (resultCode == RESULT_OK && data != null) {

                    final String IMGUri = String.valueOf(data.getData());

                    nameRandomimage = UUID.randomUUID().toString() + ".jpg";
                    final ProgressDialog dialogGellary = new ProgressDialog(this);

                    StorageReference storageReference = storageRef.child("Images").child(mAuth.getCurrentUser().getUid()).child(nameRandomimage);
                    uploadTask = storageReference.putFile(Uri.parse(IMGUri));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (uri.isAbsolute()) {

                                        final String IMGPath = uri.toString();

                                        HashMap<String, String> imageMap = new HashMap<>();
                                        imageMap.put("pathimage", IMGPath);
                                        imageMap.put("nameimage", nameRandomimage);

                                        databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("image")
                                                .setValue(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isComplete()) {

                                                    dialogGellary.dismiss();
                                                    Intent NoteGallery = new Intent(getBaseContext(), AdditionNotes.class);
                                                    NoteGallery.putExtra("tools", 102);
                                                    NoteGallery.putExtra("changeCamera_OR_Gallery", 2);
                                                    NoteGallery.putExtra("image", IMGUri);
                                                    startActivityForResult(NoteGallery, REQUEST_CODE_ADDITIONNOTESACTIVITY);

                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / (int) taskSnapshot.getTotalByteCount());
                            dialogGellary.setMessage("Uplaoding " + ((int) progress + " %..."));
                            dialogGellary.show();
                        }
                    });

                }

                break;
        }
    }

    private void SearchNoteRecycle(String Chlid, String Value) {

        Query query = databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").orderByChild(Chlid).startAt(Value.trim()).endAt(Value.trim() + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<InfoNotes>()
                .setQuery(query, InfoNotes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<InfoNotes, HolderNotes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderNotes holderNotes, int position, @NonNull final InfoNotes infoNotes) {

                final String NoteREF = getRef(position).getKey();
                final String UID = mAuth.getCurrentUser().getUid();

                databaseRef.child("AllNotes").child(UID).child("Note").child(NoteREF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            holderNotes.setAllNote(dataSnapshot, UID, NoteREF);

                            holderNotes.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    ChooseAction(dataSnapshot, infoNotes);

                                    return false;
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public HolderNotes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HolderNotes(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_allnotes, parent, false));
            }
        };

        mainBinding.NotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mainBinding.NotesRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    private void Search() {

        preferences = getSharedPreferences("Chlid", MODE_PRIVATE);
        mainBinding.SearchNote.setSubmitButtonEnabled(true);
        mainBinding.SearchNote.setQueryHint("Search Note");
        mainBinding.SearchNote.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                SearchNoteRecycle(preferences.getString("chlid", "Title").toLowerCase(), newText.toLowerCase());

                return false;
            }
        });
        mainBinding.SearchNote.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                ShowNotesRecycle();

                return false;
            }
        });

    }

    private void ChooseSearch(Spinner spinner) {

        String option[] = new String[]{"Option Search", "Title", "Tag", "Subtitle", "Description"};

        final ArrayAdapter<String> adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, option);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals("Option Search")) {

                    preferences = getSharedPreferences("Chlid", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("chlid", "title");
                    editor.commit();

                } else {

                    preferences = getSharedPreferences("Chlid", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("chlid", (String) adapterView.getItemAtPosition(i));
                    editor.commit();

                    mainBinding.layoutDrawer.closeDrawer(GravityCompat.START);

                    Snackbar.make(mainBinding.linearBottom, "Option Search is " + preferences.getString("chlid", ""), Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void ChooseAction(final DataSnapshot snapshot, final InfoNotes notes) {

        final BottomSheetDialog sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyleTheme);
        final View bottomsheetview = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_bottom_sheet_dialog, (ViewGroup) findViewById(R.id.layout_dialog_Action));

        final TextView titleNote = bottomsheetview.findViewById(R.id.title_note);
        final ImageView imageNote = bottomsheetview.findViewById(R.id.image_note);
        final TextView textTitle = bottomsheetview.findViewById(R.id.title_note_D);
        final TextView subNote = bottomsheetview.findViewById(R.id.Subtitle_note);


        String title = Character.toUpperCase(snapshot.child("title").getValue().toString().charAt(0)) + snapshot.child("title").getValue().toString().substring(1);
        titleNote.setText(title);

        if (snapshot.hasChild("pathimage")) {
            Picasso.get().load(snapshot.child("pathimage").getValue().toString()).into(imageNote);
        } else {
            imageNote.setImageResource(R.drawable.no_image);
        }
        getDataSender(snapshot, bottomsheetview);
        textTitle.setText(title);
        subNote.setText(snapshot.child("subtitle").getValue().toString());

        if (snapshot.hasChild("pathimage")) {
            bottomsheetview.findViewById(R.id.Linear_downloading_image).setVisibility(View.VISIBLE);
        } else {
            bottomsheetview.findViewById(R.id.Linear_downloading_image).setVisibility(View.GONE);
        }
        if (snapshot.hasChild("pathvoice")) {
            bottomsheetview.findViewById(R.id.Linear_downloading_voice).setVisibility(View.VISIBLE);
        } else {
            bottomsheetview.findViewById(R.id.Linear_downloading_voice).setVisibility(View.GONE);
        }

        titleNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });

        bottomsheetview.findViewById(R.id.linear_Update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Update = new Intent(getBaseContext(), AdditionNotes.class);
                Update.putExtra("GOUPDATE", true);
                Update.putExtra("IDNOTES", snapshot.child("refnote").getValue().toString());
                Update.putExtra("sender", snapshot.child("sender").getValue().toString());

                if (snapshot.hasChild("pathimage")) {

                    Update.putExtra("P-IMAGE", snapshot.child("pathimage").getValue().toString());
                    Update.putExtra("N-IMAGE", snapshot.child("nameimage").getValue().toString());

                }
                if (snapshot.hasChild("pathvoice")) {

                    Update.putExtra("P-VOICE", snapshot.child("pathvoice").getValue().toString());
                    Update.putExtra("N-VOICE", snapshot.child("namevoice").getValue().toString());
                }


                startActivityForResult(Update, REQUEST_CODE_UPDATENOTE);
                sheetDialog.dismiss();
            }
        });

        bottomsheetview.findViewById(R.id.linear_Share_Online).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogShare(notes);
                sheetDialog.dismiss();
            }
        });

        bottomsheetview.findViewById(R.id.linear_Share_Whats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Share(notes);
                sheetDialog.dismiss();
            }
        });

        bottomsheetview.findViewById(R.id.Linear_downloading_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URl = snapshot.child("pathimage").getValue().toString();
                NameMidea = snapshot.child("title").getValue().toString();
                Type = "jpg";

                PermoissionDownloaingFiles();
                sheetDialog.dismiss();
            }
        });
        bottomsheetview.findViewById(R.id.Linear_downloading_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URl = snapshot.child("pathvoice").getValue().toString();
                NameMidea = snapshot.child("title").getValue().toString();
                Type = "3gp";

                PermoissionDownloaingFiles();
                sheetDialog.dismiss();
            }
        });

        bottomsheetview.findViewById(R.id.Linear_Note_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteNote(snapshot);
                sheetDialog.dismiss();
            }
        });


        sheetDialog.setContentView(bottomsheetview);
        sheetDialog.create();
        sheetDialog.show();


    }

    private void getDataSender(DataSnapshot snapshot, View view) {

        final CircleImageView imageSender = view.findViewById(R.id.image_sender_note);
        final TextView nameSender = view.findViewById(R.id.name_Sender_note);

        databaseRef.child("Users").child(snapshot.child("sender").getValue().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(imageSender);
                    String name = Character.toUpperCase(dataSnapshot.child("name").getValue().toString().charAt(0)) + dataSnapshot.child("name").getValue().toString().substring(1);
                    nameSender.setText(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DeleteNote(final DataSnapshot snapshot) {

        if (dialogDelete == null || dialogDelete != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.delete_note_dialog, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));

            builder.setView(view);
            dialogDelete = builder.create();

            if (dialogDelete.getWindow() != null) {

                dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView Delete = view.findViewById(R.id.textDeleteNote);
            TextView Cancel = view.findViewById(R.id.textCancel);

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").child(snapshot.child("refnote").getValue().toString())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Snackbar.make(mainBinding.OpenNewBututton, "Note successfully deleted", Snackbar.LENGTH_SHORT).show();
                                dialogDelete.dismiss();
                            }
                        }
                    });
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogDelete.dismiss();
                }
            });
        }

        dialogDelete.show();

    }

    private void PermoissionDownloaingFiles() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_DOWNLOAD);
            } else {

                file_download();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_PERMISSION_DOWNLOAD:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    PermoissionDownloaingFiles();

                } else {

                    Toast.makeText(getBaseContext(), "Validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_PERMISSION_CAMERA:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Camera();

                } else {

                    Toast.makeText(getBaseContext(), "Validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_PERMISSION_GALLERY:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Gallery();

                } else {

                    Toast.makeText(getBaseContext(), "Validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_PERMISSION_VOICE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Voice();

                } else {

                    Toast.makeText(getBaseContext(), "Validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    private void file_download() {

        String timeStamp = new SimpleDateFormat("YYYYMMDD_HHMMSS", Locale.getDefault()).format(System.currentTimeMillis());

        File path = Environment.getExternalStorageDirectory();
        File direct = new File(path + "/OnlineNotes/");

        if (!direct.exists()) {

            direct.mkdirs();
        }

        DownloadManager downloadManager = (DownloadManager) getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(URl);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                .setAllowedOverRoaming(false).setTitle(NameMidea)
                .setDescription("")
                .setDestinationInExternalPublicDir("/OnlineNotes/", timeStamp + "." + Type);

        downloadManager.enqueue(request);
    }

    private void Share(InfoNotes note) {

        try {

            Intent ShareWhats = new Intent(Intent.ACTION_SEND);
            ShareWhats.setType("text/plain");
            ShareWhats.setPackage("com.whatsapp");
            String Description = note.getTitle() + "\n" + "\n" + note.getSubtitle() + "\n" + note.getDescription() + "\n" + "\n" + "\n";

            String Link = "";
            String Photo = "";
            String Voice = "";

            if (!TextUtils.isEmpty(note.getWeburl())) {

                Link = "\n" + "\n" + "Link:-https://" + note.getWeburl();

            }
            if (!TextUtils.isEmpty(note.getPathimage())) {

                Photo = "\n" + "\n" + "Photo:-" + note.getPathimage();

            }
            if (!TextUtils.isEmpty(note.getPathimage())) {

                Voice = "\n" + "\n" + "Voice:-" + note.getPathvoice();
            }

            ShareWhats.putExtra(Intent.EXTRA_TEXT, Description + Link + Photo + Voice);
            startActivity(ShareWhats);

        } catch (Exception e) {


        }
    }

    private void DialogAddLink() {

        if (dialogAddLink == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_add_link, (ViewGroup) findViewById(R.id.layoutAddUriContainer));

            builder.setView(view);
            dialogAddLink = builder.create();


            if (dialogAddLink.getWindow() != null) {

                dialogAddLink.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText link = view.findViewById(R.id.inputURL);
            TextView Add = view.findViewById(R.id.add_link);
            TextView Cancel = view.findViewById(R.id.cancel_link);


            Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(link.getText().toString())) {

                        Toast.makeText(getBaseContext(), "Enter URL", Toast.LENGTH_SHORT).show();

                        return;

                    }
                    if (!Patterns.WEB_URL.matcher(link.getText().toString().trim()).matches()) {

                        Toast.makeText(getBaseContext(), "Enter URL", Toast.LENGTH_SHORT).show();

                        return;

                    } else {

                        Intent NoteLink = new Intent(getBaseContext(), AdditionNotes.class);
                        NoteLink.putExtra("tools", 104);
                        NoteLink.putExtra("link", link.getText().toString().trim());
                        startActivity(NoteLink);

                        dialogAddLink.dismiss();
                    }
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogAddLink.dismiss();
                }
            });
        }

        dialogAddLink.show();
    }

    private void layoutTabs(final View v) {

        TabLayout tabLayout = v.findViewById(R.id.LayoutTab);

        tabLayout.addTab(tabLayout.newTab().setText("Tags"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        TagsRecycle(v);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:

                        TagsRecycle(v);
                        break;

                    case 1:

                        SenderRecycleSerch(v);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void TagsRecycle(View v) {

        RecyclerView R_Sender = v.findViewById(R.id.Recycle_Hedar_Tags_Sender);
        RecyclerView R_Tags = v.findViewById(R.id.Recycle_Hedar_Tags_Sender2);
        android.widget.SearchView SearchSender = v.findViewById(R.id.SenderSearch);
        R_Tags.setVisibility(View.VISIBLE);
        SearchSender.setVisibility(View.GONE);
        R_Sender.setVisibility(View.GONE);

        Query query = databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags");

        FirebaseRecyclerOptions<InfoTags> options = new FirebaseRecyclerOptions.Builder<InfoTags>()
                .setQuery(query, InfoTags.class)
                .build();

        FirebaseRecyclerAdapter<InfoTags, HeaderTags> adapter = new FirebaseRecyclerAdapter<InfoTags, HeaderTags>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HeaderTags headerTags, int position, @NonNull final InfoTags infoTags) {

                final String REF = getRef(position).getKey();

                databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    headerTags.setTags(dataSnapshot);

                                    headerTags.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            SearchNoteRecycle("tag", infoTags.getNAMETAGS().toLowerCase());
                                            mainBinding.layoutDrawer.closeDrawer(GravityCompat.START);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @NonNull
            @Override
            public HeaderTags onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HeaderTags(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tags_main_activity, parent, false));
            }
        };

        DividerItemDecoration decoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        R_Tags.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        R_Tags.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        R_Tags.addItemDecoration(decoration);
        adapter.notifyDataSetChanged();
        adapter.startListening();
        R_Tags.setAdapter(adapter);


    }

    public void SenderRecycleSerch(View v) {

        final RecyclerView R_Sender = v.findViewById(R.id.Recycle_Hedar_Tags_Sender);
        RecyclerView R_Tags = v.findViewById(R.id.Recycle_Hedar_Tags_Sender2);
        android.widget.SearchView SearchSender = v.findViewById(R.id.SenderSearch);
        SearchSender.setVisibility(View.VISIBLE);
        SearchSender.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        R_Tags.setVisibility(View.GONE);
        R_Sender.setVisibility(View.GONE);

        SearchSender.setQueryHint("Name Sender");
        SearchSender.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                R_Sender.setVisibility(View.VISIBLE);
                Users(R_Sender, newText.toLowerCase());
                return false;
            }
        });
        SearchSender.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                R_Sender.setVisibility(View.GONE);
                return false;
            }
        });


    }

    private void Users(RecyclerView R_Sender, String type) {

        Query query = databaseRef.child("Users").orderByChild("name").startAt(type.trim().toLowerCase()).endAt(type.trim().toLowerCase() + "\uf8ff");
        FirebaseRecyclerOptions<AccountInformation> options = new FirebaseRecyclerOptions.Builder<AccountInformation>()
                .setQuery(query, AccountInformation.class)
                .build();

        FirebaseRecyclerAdapter<AccountInformation, HeaderTags> adapter = new FirebaseRecyclerAdapter<AccountInformation, HeaderTags>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HeaderTags headerTags, int position, @NonNull final AccountInformation accountInformation) {

                String REF = getRef(position).getKey();

                databaseRef.child("Users").child(REF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            headerTags.setSender(dataSnapshot);
                            headerTags.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    SearchNoteRecycle("sender", accountInformation.getUID());
                                    mainBinding.layoutDrawer.closeDrawer(GravityCompat.START);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public HeaderTags onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new HeaderTags(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tags_main_activity, parent, false));
            }
        };

        DividerItemDecoration decoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        R_Sender.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        R_Sender.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        R_Sender.addItemDecoration(decoration);
        adapter.notifyDataSetChanged();
        adapter.startListening();
        R_Sender.setAdapter(adapter);


    }

    public class HeaderTags extends RecyclerView.ViewHolder {

        private CircleImageView colorTag;
        private TextView nameTag;

        public HeaderTags(@NonNull View itemView) {
            super(itemView);

            colorTag = itemView.findViewById(R.id.colorTag);
            nameTag = itemView.findViewById(R.id.textTag);
        }

        private void setTags(final DataSnapshot snapshot) {

            colorTag.setColorFilter(Color.rgb(Integer.parseInt(snapshot.child("f").getValue().toString()), Integer.parseInt(snapshot.child("s").getValue().toString()), Integer.parseInt(snapshot.child("t").getValue().toString())));
            String nametags = Character.toUpperCase(snapshot.child("nametags").getValue().toString().charAt(0)) + snapshot.child("nametags").getValue().toString().toLowerCase().substring(1);
            nameTag.setText(nametags);

        }

        private void setSender(final DataSnapshot snapshot) {

            Picasso.get().load(snapshot.child("photo").getValue().toString()).into(colorTag);
            String name = Character.toUpperCase(snapshot.child("name").getValue().toString().charAt(0)) + snapshot.child("name").getValue().toString().toLowerCase().substring(1);
            nameTag.setText(name);

        }
    }

    private void DialogVoice() {

        if (dialogAddVoice == null || dialogAddVoice != null) {

            final ProgressDialog dialogVoice = new ProgressDialog(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_add_voice, (ViewGroup) findViewById(R.id.layoutDialog_voice));

            builder.setView(view);
            dialogAddVoice = builder.create();

            if (dialogAddVoice.getWindow() != null) {

                dialogAddVoice.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final CircleImageView colorTextRecord = view.findViewById(R.id.colorImageRecording);
            final TextView textRecord = view.findViewById(R.id.textRecording);
            final Chronometer chronoTime = view.findViewById(R.id.chronoTime);
            final ImageView actionVoice = view.findViewById(R.id.recordVoiceBtn);
            TextView addVoice = view.findViewById(R.id.add_Voice);
            TextView cancelVoice = view.findViewById(R.id.cancel_Voice);

            mediaRecorder = new MediaRecorder();

            actionVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isRecording) {

                        chronoTime.stop();

                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        actionVoice.setImageResource(R.drawable.record_btn_stopped);
                        textRecord.setText("Recording saved...");
                        textRecord.setTextColor(Color.parseColor("#FFFFFF"));
                        colorTextRecord.setColorFilter(Color.parseColor("#3D4051"));
                        colorTextRecord.setBorderColor(Color.parseColor("#3D4051"));
                        isRecording = false;

                    } else {

                        mediaRecorder = new MediaRecorder();
                        chronoTime.setBase(SystemClock.elapsedRealtime());
                        chronoTime.start();

                        String recordPath = getExternalFilesDir("/").getAbsolutePath();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
                        Date now = new Date();
                        pathfileVoice = recordPath + "/" + "Recording_" + dateFormat.format(now) + ".3gp";

                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                        mediaRecorder.setOutputFile(pathfileVoice);

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        actionVoice.setImageResource(R.drawable.record_btn_recording);
                        textRecord.setText("Recording...");
                        textRecord.setTextColor(Color.GREEN);
                        colorTextRecord.setColorFilter(Color.GREEN);
                        colorTextRecord.setBorderColor(Color.parseColor("#FFFFFF"));
                        isRecording = true;
                    }

                }

            });

            addVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {


                    if (isRecording) {

                        Toast.makeText(getBaseContext(), "Please close the recorder first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(pathfileVoice)) {

                        Toast.makeText(getBaseContext(), "Audio file is empty. You can press Cancel", Toast.LENGTH_SHORT).show();
                        return;

                    } else {

                        nameRandomvioce = UUID.randomUUID().toString() + ".3gp";
                        Uri voiceUri = Uri.fromFile(new File(pathfileVoice));

                        StorageReference storageReference = storageRef.child("Voices").child(mAuth.getCurrentUser().getUid()).child(nameRandomvioce);
                        uploadTask = storageReference.putFile(voiceUri);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String voice = uri.toString();

                                        HashMap<String, String> getvoice = new HashMap<>();
                                        getvoice.put("pathvoice", voice);
                                        getvoice.put("namevoice", nameRandomvioce);

                                        databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("voice")
                                                .setValue(getvoice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isComplete()) {

                                                    dialogVoice.dismiss();
                                                    dialogAddVoice.dismiss();
                                                    Intent intentVoice = new Intent(getBaseContext(), AdditionNotes.class);
                                                    intentVoice.putExtra("tools", 103);
                                                    intentVoice.putExtra("recordingVoice", 1);
                                                    intentVoice.putExtra("voicePlay", voice);
                                                    startActivityForResult(intentVoice, REQUEST_CODE_ADDITIONNOTESACTIVITY);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                double progress = (100 * taskSnapshot.getBytesTransferred() / (int) taskSnapshot.getTotalByteCount());

                                dialogVoice.setMessage("Uplaoding " + ((int) progress + " %..."));
                                dialogVoice.show();
                            }
                        });
                    }
                }
            });

            cancelVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isRecording) {

                        Toast.makeText(getBaseContext(), "Please close the recorder first", Toast.LENGTH_SHORT).show();

                    } else {
                        pathfileVoice = null;
                        dialogAddVoice.dismiss();
                    }

                }
            });

            dialogAddVoice.show();
        }
        dialogAddVoice.setCancelable(false);
    }

    private void DialogShare(final InfoNotes notes) {

        if (dialogShare == null || dialogShare != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View viewGroup = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_recyclereview_users_share, (ViewGroup) findViewById(R.id.layout_Users_Share));

            builder.setView(viewGroup);

            dialogShare = builder.create();

            if (dialogShare.getWindow() != null) {

                dialogShare.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            RecyclerView UsersShare = viewGroup.findViewById(R.id.shareUsers);
            android.widget.SearchView searchUser = viewGroup.findViewById(R.id.SearchUser);

            adapterDialgShare(UsersShare, notes);
            SearchShareUser(searchUser, UsersShare, notes);


        }

        dialogShare.show();
    }

    private void adapterDialgShare(RecyclerView recyclerShare, final InfoNotes notes) {

        Query query = databaseRef.child("Users");

        FirebaseRecyclerOptions<AccountInformation> options = new FirebaseRecyclerOptions.Builder<AccountInformation>()
                .setQuery(query, AccountInformation.class)
                .build();

        FirebaseRecyclerAdapter<AccountInformation, shareHolder> adapter = new FirebaseRecyclerAdapter<AccountInformation, shareHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final shareHolder shareHolder, final int position, @NonNull AccountInformation accountInformation) {

                final String REF = getRef(position).getKey();

                databaseRef.child("Users").child(REF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            shareHolder.Users(dataSnapshot, notes, REF);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public shareHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new shareHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_share_users, parent, false));
            }
        };

        DividerItemDecoration decoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        recyclerShare.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerShare.setHasFixedSize(true);
        recyclerShare.addItemDecoration(decoration);
        recyclerShare.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        recyclerShare.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    private void SearchShareUser(android.widget.SearchView searchView, final RecyclerView recyclerShare, final InfoNotes notes) {


        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                UsersShareSearch(recyclerShare, notes, newText.toLowerCase().trim());

                return false;
            }
        });

        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                adapterDialgShare(recyclerShare, notes);

                return false;
            }
        });

    }

    private void UsersShareSearch(RecyclerView recyclerShare, final InfoNotes notes, String nameUser) {

        Query query = databaseRef.child("Users").orderByChild("name").startAt(nameUser.trim().toLowerCase()).endAt(nameUser.trim().toLowerCase() + "\uf8ff");

        FirebaseRecyclerOptions<AccountInformation> options = new FirebaseRecyclerOptions.Builder<AccountInformation>()
                .setQuery(query, AccountInformation.class)
                .build();

        FirebaseRecyclerAdapter<AccountInformation, shareHolder> adapter = new FirebaseRecyclerAdapter<AccountInformation, shareHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final shareHolder shareHolder, int position, @NonNull AccountInformation accountInformation) {

                final String REF = getRef(position).getKey();

                databaseRef.child("Users").child(REF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        shareHolder.Users(dataSnapshot, notes, REF);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public shareHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new shareHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_share_users, parent, false));
            }
        };

        recyclerShare.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerShare.setHasFixedSize(true);
        recyclerShare.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
        recyclerShare.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public class shareHolder extends RecyclerView.ViewHolder {

        private CircleImageView UserShareImage;
        private TextView NameUserShare, ShareNoteBtn;
        private ConstraintLayout customUserLayout;

        public shareHolder(@NonNull View itemView) {
            super(itemView);

            UserShareImage = itemView.findViewById(R.id.UserShareImage);
            NameUserShare = itemView.findViewById(R.id.nameUserShare);
            ShareNoteBtn = itemView.findViewById(R.id.ShareNoteBtn);
            customUserLayout = itemView.findViewById(R.id.customUser);

        }

        private void Users(final DataSnapshot snapshot, final InfoNotes notes, final String RFU) {

            if (!RFU.equals(notes.getFromsender())) {

                Picasso.get().load(snapshot.child("photo").getValue().toString()).into(UserShareImage);
                final String NameUser = Character.toUpperCase(snapshot.child("name").getValue().toString().charAt(0)) + snapshot.child("name").getValue().toString().substring(1).toLowerCase();
                NameUserShare.setText(NameUser);

                ShareNoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String dateSentNote = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss a", Locale.getDefault()).format(new Date());

                        final InfoNotes ShareNote = new InfoNotes(notes.getTitle(), notes.getSubtitle(), notes.getDescription(), dateSentNote, notes.getNameimage(), notes.getPathvoice(), notes.getNamevoice(), notes.getTag(), notes.getRefnote(), mAuth.getCurrentUser().getUid(), notes.getFromsender(), notes.getPathimage(), notes.getWeburl(), notes.getF(), notes.getS(), notes.getT());

                        databaseRef.child("Notifications").child(RFU).child("Note").child(notes.getRefnote()).setValue(ShareNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(getBaseContext(), "Note was successfully shared to " + NameUser, Toast.LENGTH_SHORT).show();
                                    dialogShare.dismiss();

                                }
                            }
                        });
                    }
                });

            } else {

                ShareNoteBtn.setVisibility(View.GONE);
                NameUserShare.setVisibility(View.GONE);
                UserShareImage.setVisibility(View.GONE);
                customUserLayout.setVisibility(View.GONE);
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        isRunning = false ;
    }
}



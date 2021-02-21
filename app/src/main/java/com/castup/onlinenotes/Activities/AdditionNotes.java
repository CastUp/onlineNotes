package com.castup.onlinenotes.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.castup.onlinenotes.Models.InfoTags;
import com.castup.onlinenotes.R;
import com.castup.onlinenotes.databinding.ActivityAdditionNotesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
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
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdditionNotes extends AppCompatActivity {

    private LinearLayout layoutWebURL, layoutMiscellaneous;
    private View viewPartNote;
    private ImageView Back, SaveNote, EditeNote, imageNote;
    private EditText titleNote, subTitle, typeNote;
    private TextView DateNote, WebURL, nameTag;
    private RecyclerView TagsRecycler;
    private AlertDialog dialogAddTag, dialogDeleted, dialogAddLink, dialogAddVoice, dialogDeletedVoice;
    private ImageView deletelink, deleteImage;
    private ActivityAdditionNotesBinding additionNotesBinding;


    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private UploadTask uploadTask;

    private AD_NOTE NotesHadler;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer = null;
    private int ChangeStatGridLayout = 0;
    private int SetColor = 0;
    private int changeCamera_OR_Gallery = 0;
    private int RecordingVoice = 0;
    private boolean G_INTENT;
    private boolean isRecording = false;
    private boolean isplaying = false;
    private boolean isRunning = true;
    private Runnable updateSeekbar;
    private Handler seekbarHandler;
    private Uri photoGallery;
    private byte[] photoCamera;
    private String G_Sender;
    private String RF;
    private String pathfileVoice;
    private String pathfileVoiceUri;
    private String namefileimage;
    private String namefilevioce;
    private Bundle DataStorage;
    private Message i_Message;
    private Message v_Message;
    private Message n_Message;
    private InterstitialAd mInterstitialAd;


    private static final int REQUEST_CODE_PERMISSION_CAMERA = 10;
    private static final int REQUEST_CODE_PERMISSION_IMAGE = 20;
    private static final int REQUEST_CODE_PERMISSION_VOICE = 30;

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_IMAGE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        additionNotesBinding = ActivityAdditionNotesBinding.inflate(getLayoutInflater());
        setContentView(additionNotesBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        view_InflatFirebase();
        update_Or_insert();
        setBack();
        intiMiscellaneous();
        Save_Note();
        DeletedImage();
        Deleted_link();
        DeletedVoice();

    }

    private void view_InflatFirebase() {


        Back = findViewById(R.id.btnBack);
        SaveNote = findViewById(R.id.saveNote);
        imageNote = findViewById(R.id.imageNote);
        titleNote = findViewById(R.id.text_NoteTitle);
        subTitle = findViewById(R.id.text_NoteSubTitle);
        typeNote = findViewById(R.id.text_TypeNote);
        DateNote = findViewById(R.id.text_DateTime);
        WebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);
        viewPartNote = findViewById(R.id.viewPartNote);
        nameTag = findViewById(R.id.nameTag);
        layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        deleteImage = findViewById(R.id.deleteImage);
        deletelink = findViewById(R.id.deletelink);
        DataStorage = new Bundle();
        i_Message = new Message();
        v_Message = new Message();
        n_Message = new Message();


    }

    private void ads(View view) {

       // String TS = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";
        String ID = "44833b2540ae7fa360dea5184926abed8d25c09b2cd47fea";

        Appodeal.initialize((Activity) view.getContext(), ID, Appodeal.INTERSTITIAL);
        Appodeal.show((Activity) view.getContext(), Appodeal.INTERSTITIAL);

        if (isRunning) {

            Appodeal.isLoaded(Appodeal.INTERSTITIAL);
        }

    }

    private void update_Or_insert() {

        G_INTENT = getIntent().getBooleanExtra("GOUPDATE", false);

        if (G_INTENT) {

            RF = getIntent().getStringExtra("IDNOTES");
            G_Sender = getIntent().getStringExtra("sender");
            ReadNote();

        } else {

            int tools = getIntent().getIntExtra("tools", 0);
            G_Sender = mAuth.getCurrentUser().getUid();

            if (tools == 101) {

                changeCamera_OR_Gallery = getIntent().getIntExtra("changeCamera_OR_Gallery", 0);
                byte[] images = getIntent().getByteArrayExtra("image");
                Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);


                imageNote.setImageBitmap(bitmap);
                imageNote.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                RF = databaseRef.push().getKey();
                return;

            }
            if (tools == 102) {

                changeCamera_OR_Gallery = getIntent().getIntExtra("changeCamera_OR_Gallery", 0);
                Uri photo = Uri.parse(getIntent().getStringExtra("image"));

                imageNote.setImageURI(photo);
                imageNote.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                RF = databaseRef.push().getKey();
                return;

            }
            if (tools == 103) {

                RecordingVoice = getIntent().getIntExtra("recordingVoice", 0);
                pathfileVoiceUri = getIntent().getStringExtra("voicePlay");
                pleyerVoice(pathfileVoiceUri);
                RF = databaseRef.push().getKey();
                return;

            }
            if (tools == 104) {

                String link = getIntent().getStringExtra("link");
                WebURL.setText(link);
                layoutWebURL.setVisibility(View.VISIBLE);
                deletelink.setVisibility(View.VISIBLE);
                RF = databaseRef.push().getKey();

                return;

            } else {

                RF = databaseRef.push().getKey();
            }


        }

    }

    private void setBack() {

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), MainActivity.class));
                GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
                gradientDrawable.setColor(Color.rgb(0, 0, 0));
                nameTag.setText("");
                nameTag.setVisibility(View.GONE);
            }
        });
    }

    private void set_permission_Camera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
            } else {

                GetCamera();
            }
        }
    }

    private void set_permission_Image() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_IMAGE);
            } else {

                GetImage();
            }
        }
    }

    private void set_permission_Voice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION_VOICE);
            } else {

                DialogVoice();
            }
        }

    }

    private void GetCamera() {

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_CODE_CAMERA);
    }

    private void GetImage() {

        Intent image = new Intent(Intent.ACTION_GET_CONTENT);
        image.setType("image/*");
        startActivityForResult(image, REQUEST_CODE_IMAGE);
    }

    private void intiMiscellaneous() {

        final BottomSheetBehavior<LinearLayout> sheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        layoutMiscellaneous.findViewById(R.id.text_newTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogAddTag();
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                set_permission_Camera();

                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                set_permission_Image();
                ads(layoutMiscellaneous);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogAddLink();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutDelete_Note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteNote();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddVaice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isplaying) {

                    Toast.makeText(getBaseContext(), "Please close the player first", Toast.LENGTH_SHORT).show();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {

                    set_permission_Voice();
                    ads(layoutMiscellaneous);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

            }
        });

        if (!G_INTENT) {

            String Date = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss a", Locale.getDefault()).format(new Date());
            DateNote.setText(Date);
        }


        TagsRecycle(layoutMiscellaneous);

    }

    private void TagsRecycle(LinearLayout layoutMiscellaneou) {

        TagsRecycler = layoutMiscellaneou.findViewById(R.id.tag_Recycle);
        Query query = databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags");

        FirebaseRecyclerOptions<InfoTags> options = new FirebaseRecyclerOptions.Builder<InfoTags>()
                .setQuery(query, InfoTags.class)
                .build();


        FirebaseRecyclerAdapter<InfoTags, HolderTags> adapter = new FirebaseRecyclerAdapter<InfoTags, HolderTags>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HolderTags holderTags, int position, @NonNull final InfoTags infoTags) {

                final String RF = getRef(position).getKey();

                databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(RF).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            holderTags.setDateTags(infoTags, RF);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holderTags.colorTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(infoTags.getREF()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
                                    gradientDrawable.setColor(Color.rgb(Integer.parseInt(infoTags.getF()), Integer.parseInt(infoTags.getS()), Integer.parseInt(infoTags.getT())));
                                    nameTag.setText(infoTags.getNAMETAGS());
                                    nameTag.setTextColor(Color.WHITE);
                                    nameTag.setVisibility(View.VISIBLE);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holderTags.colorTag.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(infoTags.getREF()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    if (nameTag.getText().toString() == infoTags.getNAMETAGS()) {

                                        GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
                                        gradientDrawable.setColor(Color.rgb(0, 0, 0));
                                        nameTag.setText("");
                                        nameTag.setTextColor(0);
                                        nameTag.setVisibility(View.GONE);

                                    }

                                }

                            }
                        });


                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public HolderTags onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new HolderTags(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_new_tag, parent, false));
            }
        };

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        TagsRecycler.setLayoutManager(manager);
        TagsRecycler.setAdapter(adapter);


    }

    class HolderTags extends RecyclerView.ViewHolder {

        private CardView colorTag;
        private TextView nameTag;

        public HolderTags(@NonNull View itemView) {
            super(itemView);

            colorTag = itemView.findViewById(R.id.colorTag);
            nameTag = itemView.findViewById(R.id.textTag);
        }

        private void setDateTags(final InfoTags infoTags, String RF) {

            colorTag.setCardBackgroundColor(Color.rgb(Integer.parseInt(infoTags.getF()), Integer.parseInt(infoTags.getS()), Integer.parseInt(infoTags.getT())));
            String nametags = Character.toUpperCase(infoTags.getNAMETAGS().charAt(0)) + infoTags.getNAMETAGS().substring(1);
            nameTag.setText(nametags);

            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(RF).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.child("f").getValue().toString().equals("255") && dataSnapshot.child("s").getValue().toString().equals("255") && dataSnapshot.child("t").getValue().toString().equals("255") || dataSnapshot.child("f").getValue().toString().equals("255") && dataSnapshot.child("s").getValue().toString().equals("214") && dataSnapshot.child("t").getValue().toString().equals("0")) {

                            nameTag.setTextColor(Color.BLACK);
                        } else {

                            nameTag.setTextColor(Color.WHITE);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private void DialogAddTag() {

        if (dialogAddTag == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AdditionNotes.this);

            final View Groups = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_add_tag, (ViewGroup) findViewById(R.id.layoutDialogAddTags));

            builder.setView(Groups);
            dialogAddTag = builder.create();

            if (dialogAddTag.getWindow() != null) {

                dialogAddTag.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText addNemeTag = Groups.findViewById(R.id.insertNameTag);
            final CardView showColorTag = Groups.findViewById(R.id.showColorTag);
            final ImageView chooseColorTag = Groups.findViewById(R.id.chooseColorTag);
            final TableLayout TableLayout = Groups.findViewById(R.id.tableLayout);
            final TextView AddTags = Groups.findViewById(R.id.addTag);
            final TextView cancel = Groups.findViewById(R.id.cancelTag);

            chooseColorTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ChangeStatGridLayout == 0) {

                        TableLayout.setVisibility(View.VISIBLE);
                        TableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_lift_to_right));
                        ChangeStatGridLayout = 1;

                    } else {

                        TableLayout.setVisibility(View.GONE);
                        TableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                        ChangeStatGridLayout = 0;
                    }

                }
            });

            AddTags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(addNemeTag.getText().toString())) {

                        Toast.makeText(getBaseContext(), "Enter Name Tag", Toast.LENGTH_SHORT).show();

                    } else {

                        String REF = databaseRef.push().getKey();
                        if (SetColor == 0) {

                            InfoTags infoTags = new InfoTags("0", "0", "0", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 1) {

                            InfoTags infoTags = new InfoTags("0", "0", "0", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 2) {

                            InfoTags infoTags = new InfoTags("145", "159", "247", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 3) {

                            InfoTags infoTags = new InfoTags("0", "184", "218", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 4) {

                            InfoTags infoTags = new InfoTags("51", "51", "51", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 5) {

                            InfoTags infoTags = new InfoTags("170", "0", "255", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 6) {

                            InfoTags infoTags = new InfoTags("100", "221", "23", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 7) {

                            InfoTags infoTags = new InfoTags("255", "109", "0", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 8) {

                            InfoTags infoTags = new InfoTags("255", "255", "255", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 9) {

                            InfoTags infoTags = new InfoTags("238", "44", "44", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 10) {

                            InfoTags infoTags = new InfoTags("255", "214", "0", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 11) {

                            InfoTags infoTags = new InfoTags("8", "170", "76", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }
                        if (SetColor == 12) {

                            InfoTags infoTags = new InfoTags("11", "42", "214", addNemeTag.getText().toString().toLowerCase().trim(), REF);

                            databaseRef.child("ColorTags").child(mAuth.getCurrentUser().getUid()).child("Tags").child(REF).setValue(infoTags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getBaseContext(), "Logo tag added successfully", Toast.LENGTH_SHORT).show();
                                    dialogAddTag.dismiss();
                                    TableLayout.setVisibility(View.GONE);
                                    ChangeStatGridLayout = 0;
                                    addNemeTag.setText("");

                                }
                            });

                        }


                    }

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogAddTag.dismiss();
                    TableLayout.setVisibility(View.GONE);
                    showColorTag.setCardBackgroundColor(Color.rgb(0, 0, 0));
                    ChangeStatGridLayout = 0;
                    addNemeTag.setText("");

                }
            });


            setColorTag(showColorTag, Groups, TableLayout);
        }


        dialogAddTag.show();

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

                        WebURL.setText(link.getText().toString().trim());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        deletelink.setVisibility(View.VISIBLE);
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

    private void DialogVoice() {

        if (dialogAddVoice == null || dialogAddVoice != null) {

            final ProgressDialog dialogVoice = new ProgressDialog(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(AdditionNotes.this);

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

                        namefilevioce = UUID.randomUUID().toString() + ".3gp";
                        Uri voiceUri = Uri.fromFile(new File(pathfileVoice));

                        StorageReference storageReference = storageRef.child("Voices").child(mAuth.getCurrentUser().getUid()).child(namefilevioce);
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
                                        getvoice.put("namevoice", namefilevioce);

                                        databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("voice")
                                                .setValue(getvoice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isComplete()) {

                                                    dialogVoice.dismiss();
                                                    RecordingVoice = 1;
                                                    pathfileVoice = null;
                                                    pleyerVoice(voice);
                                                    additionNotesBinding.seekPlayer.setMax(0);
                                                    dialogAddVoice.dismiss();

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

    private void setColorTag(final CardView showColor, final View groups, final TableLayout tableLayout) {

        CircleImageView A, B, C, D, E, F, A1, B1, C1, D1, E1, F1;

        A = groups.findViewById(R.id.A);
        A1 = groups.findViewById(R.id.A1);
        B = groups.findViewById(R.id.B);
        B1 = groups.findViewById(R.id.B1);
        C = groups.findViewById(R.id.C);
        C1 = groups.findViewById(R.id.C1);
        D = groups.findViewById(R.id.D);
        D1 = groups.findViewById(R.id.D1);
        E = groups.findViewById(R.id.E);
        E1 = groups.findViewById(R.id.E1);
        F = groups.findViewById(R.id.F);
        F1 = groups.findViewById(R.id.F1);


        A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(0, 0, 0));
                SetColor = 1;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });

        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(145, 159, 247));
                SetColor = 2;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(0, 184, 218));
                SetColor = 3;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(51, 51, 51));
                SetColor = 4;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        E.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(170, 0, 255));
                SetColor = 5;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(100, 221, 23));
                SetColor = 6;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        A1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(255, 109, 0));
                SetColor = 7;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(255, 255, 255));
                SetColor = 8;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        C1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(238, 44, 44));
                SetColor = 9;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        D1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(255, 214, 0));
                SetColor = 10;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        E1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(8, 170, 76));
                SetColor = 11;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });
        F1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showColor.setCardBackgroundColor(Color.rgb(11, 42, 214));
                SetColor = 12;
                tableLayout.setVisibility(View.GONE);
                tableLayout.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_right_to_lift));
                ChangeStatGridLayout = 0;

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_PERMISSION_CAMERA:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GetCamera();

                } else {

                    Toast.makeText(getBaseContext(), "Entry validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_PERMISSION_IMAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GetImage();

                } else {

                    Toast.makeText(getBaseContext(), "Entry validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_CODE_PERMISSION_VOICE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    DialogVoice();

                } else {

                    Toast.makeText(getBaseContext(), "Entry validity must be obtained", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CODE_CAMERA:

                if (resultCode == RESULT_OK) {

                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    imageNote.setImageBitmap(image);
                    imageNote.setVisibility(View.VISIBLE);
                    deleteImage.setVisibility(View.VISIBLE);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    photoCamera = stream.toByteArray();

                    namefileimage = UUID.randomUUID().toString() + ".jpg";
                    final ProgressDialog dialogCam = new ProgressDialog(this);

                    StorageReference storageReference = storageRef.child("Images").child(mAuth.getCurrentUser().getUid()).child(namefileimage);
                    uploadTask = storageReference.putBytes(photoCamera);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imgPath = uri.toString();

                                    HashMap<String, String> imageMap = new HashMap<>();
                                    imageMap.put("pathimage", imgPath);
                                    imageMap.put("nameimage", namefileimage);

                                    databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("image")
                                            .setValue(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isComplete()) {

                                                dialogCam.dismiss();
                                                changeCamera_OR_Gallery = 1;
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
                            dialogCam.setMessage("Uplaoding " + ((int) progress + " %..."));
                            dialogCam.show();
                        }
                    });

                }

                break;

            case REQUEST_CODE_IMAGE:

                if (resultCode == RESULT_OK && data != null) {

                    photoGallery = data.getData();
                    imageNote.setImageURI(photoGallery);
                    imageNote.setVisibility(View.VISIBLE);
                    deleteImage.setVisibility(View.VISIBLE);

                    namefileimage = UUID.randomUUID().toString() + ".jpg";
                    final ProgressDialog dialogGallery = new ProgressDialog(this);

                    StorageReference storageReference = storageRef.child("Images").child(mAuth.getCurrentUser().getUid()).child(namefileimage);
                    uploadTask = storageReference.putFile(photoGallery);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imgPath = uri.toString();

                                    HashMap<String, String> imageMap = new HashMap<>();
                                    imageMap.put("pathimage", imgPath);
                                    imageMap.put("nameimage", namefileimage);

                                    databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("image")
                                            .setValue(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isComplete()) {

                                                dialogGallery.dismiss();
                                                changeCamera_OR_Gallery = 2;
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
                            dialogGallery.setMessage("Uplaoding " + ((int) progress + " %..."));
                            dialogGallery.show();
                        }
                    });

                }

                break;
        }

    }

    private void Save_Note() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5486612716888257/5295916916");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        SaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Pattern.matches("\\s+", titleNote.getText().toString()) || TextUtils.isEmpty(titleNote.getText().toString())) {

                    Toast.makeText(getBaseContext(), "Title cannot be left blank", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (Pattern.matches("\\s+", subTitle.getText().toString()) || TextUtils.isEmpty(subTitle.getText().toString())) {

                    Toast.makeText(getBaseContext(), "Sub title cannot be left blank", Toast.LENGTH_SHORT).show();

                    return;

                } else {

                    Uplaoding_newImagesNotes();
                    NotesHadler = new AD_NOTE();
                    ads(view);

                }

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

            }

        });
    }

    private void Uplaoding_newImagesNotes() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (changeCamera_OR_Gallery == 1 || changeCamera_OR_Gallery == 2) {

                    databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("image")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        try {

                                            DataStorage.putString("pathPhoto", dataSnapshot.child("pathimage").getValue().toString());
                                            DataStorage.putString("imageRandam", dataSnapshot.child("nameimage").getValue().toString());
                                            i_Message.setData(DataStorage);
                                            NotesHadler.sendMessage(i_Message);

                                        } catch (Exception e) {


                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
                if (RecordingVoice == 1) {

                    databaseRef.child("GETNOTE").child(mAuth.getCurrentUser().getUid()).child("voice")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        try {

                                            DataStorage.putString("pathVoice", dataSnapshot.child("pathvoice").getValue().toString());
                                            DataStorage.putString("voiceRandom", dataSnapshot.child("namevoice").getValue().toString());
                                            v_Message.setData(DataStorage);
                                            NotesHadler.sendMessage(v_Message);

                                        } catch (Exception e) {


                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                } else {

                    try {

                        DataStorage.putString("Empity", " ");
                        n_Message.setData(DataStorage);
                        NotesHadler.sendMessage(n_Message);

                    } catch (Exception e) {

                    }

                }

            }
        }).start();

    }

    class AD_NOTE extends Handler {

        @Override
        public void handleMessage(@NonNull final Message msg) {
            super.handleMessage(msg);

            final String pathvoice = msg.getData().getString("pathVoice");
            final String namevoice = msg.getData().getString("voiceRandom");
            final String pathimage = msg.getData().getString("pathPhoto");
            final String nameimage = msg.getData().getString("imageRandam");
            final String title = titleNote.getText().toString().toLowerCase().trim();
            final String s_Title = subTitle.getText().toString().toLowerCase().trim();
            final String description = typeNote.getText().toString().toLowerCase().trim();
            final String Date_note = DateNote.getText().toString().trim();
            final String name_Tag = nameTag.getText().toString().toLowerCase().trim();
            final String web_URL = WebURL.getText().toString().toLowerCase().trim();

            final HashMap<String, String> map = new HashMap<>();

            map.put("title", title);
            map.put("subtitle", s_Title);
            map.put("description", description);
            map.put("notedate", Date_note);
            map.put("refnote", RF);
            map.put("fromsender", mAuth.getCurrentUser().getUid());
            if (G_INTENT) {
                map.put("sender", G_Sender);
            } else {
                map.put("sender", mAuth.getCurrentUser().getUid());
            }
            UpdateImage(map, pathimage, nameimage);
            UpdateVoice(map, pathvoice, namevoice);
            uplaodingColor(map);

            if (TextUtils.isEmpty(name_Tag)) {
                map.put("tag", "public".toLowerCase());
            } else {
                map.put("tag", name_Tag);
            }
            if (!TextUtils.isEmpty(web_URL)) {
                map.put("weburl", web_URL);
            }

            databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").child(RF).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
                        gradientDrawable.setColor(Color.rgb(0, 0, 0));
                        changeCamera_OR_Gallery = 0;
                        RecordingVoice = 0;
                        Intent goMain = new Intent();
                        setResult(RESULT_OK, goMain);
                        finish();

                    }

                }
            });
        }

    }

    private void uplaodingColor(HashMap<String, String> hashMap) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
            ColorStateList drawable = gradientDrawable.getColor();

            int red = Color.red(drawable.getDefaultColor());
            int green = Color.green(drawable.getDefaultColor());
            int blue = Color.blue(drawable.getDefaultColor());

            hashMap.put("f", String.valueOf(red));
            hashMap.put("s", String.valueOf(green));
            hashMap.put("t", String.valueOf(blue));

        } else {

            hashMap.put("f", "0");
            hashMap.put("s", "0");
            hashMap.put("t", "0");

        }

    }

    private void DeletedImage() {

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageNote.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);

                changeCamera_OR_Gallery = 0;
            }
        });
    }

    private void Deleted_link() {

        deletelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutWebURL.setVisibility(View.GONE);
                deletelink.setVisibility(View.GONE);
                WebURL.setText("");
            }
        });
    }

    private void DeletedVoice() {

        additionNotesBinding.imageVoiceSender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                DialgDeletVoice();
                return false;
            }
        });
    }

    private void ReadNote() {

        databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").child(RF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    layoutMiscellaneous.findViewById(R.id.layoutDelete_Note).setVisibility(View.VISIBLE);

                    String titleUp = Character.toUpperCase(dataSnapshot.child("title").getValue().toString().charAt(0)) + dataSnapshot.child("title").getValue().toString().substring(1);
                    titleNote.setText(titleUp);
                    DateNote.setText(dataSnapshot.child("notedate").getValue().toString());
                    subTitle.setText(dataSnapshot.child("subtitle").getValue().toString());
                    nameTag.setText(dataSnapshot.child("tag").getValue().toString());
                    nameTag.setVisibility(View.VISIBLE);
                    GradientDrawable drawable = (GradientDrawable) viewPartNote.getBackground();
                    drawable.setColor(Color.rgb(Integer.parseInt(dataSnapshot.child("f").getValue().toString()), Integer.parseInt(dataSnapshot.child("s").getValue().toString()), Integer.parseInt(dataSnapshot.child("t").getValue().toString())));

                    if (dataSnapshot.hasChild("description")) {

                        typeNote.setText(dataSnapshot.child("description").getValue().toString());

                    }
                    if (dataSnapshot.hasChild("pathimage")) {

                        Picasso.get().load(dataSnapshot.child("pathimage").getValue().toString()).into(imageNote);
                        imageNote.setVisibility(View.VISIBLE);
                        deleteImage.setVisibility(View.VISIBLE);

                    }
                    if (dataSnapshot.hasChild("weburl")) {

                        WebURL.setText(dataSnapshot.child("weburl").getValue().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        deletelink.setVisibility(View.VISIBLE);

                    }
                    if (dataSnapshot.hasChild("pathvoice")) {

                        pleyerVoice(dataSnapshot.child("pathvoice").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateImage(final HashMap<String, String> hashMap, String P_image, String N_image) {

        if (changeCamera_OR_Gallery == 0 && imageNote.getVisibility() == View.VISIBLE && G_INTENT) {

            String image_P = getIntent().getStringExtra("P-IMAGE");
            String image_N = getIntent().getStringExtra("N-IMAGE");
            hashMap.put("nameimage", image_N);
            hashMap.put("pathimage", image_P);

        } else {

            hashMap.put("pathimage", P_image);
            hashMap.put("nameimage", N_image);
        }

    }

    private void UpdateVoice(final HashMap<String, String> hashMap, String P_voice, String N_voice) {

        if (RecordingVoice == 0 && additionNotesBinding.layoutVoice.getVisibility() == View.VISIBLE && G_INTENT) {

            String voice_P = getIntent().getStringExtra("P-VOICE");
            String voice_N = getIntent().getStringExtra("N-VOICE");
            hashMap.put("namevoice", voice_N);
            hashMap.put("pathvoice", voice_P);

        } else {

            hashMap.put("namevoice", N_voice);
            hashMap.put("pathvoice", P_voice);
        }
    }

    private void DeleteNote() {

        if (dialogDeleted == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AdditionNotes.this);
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.delete_note_dialog, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));

            builder.setView(view);
            dialogDeleted = builder.create();

            if (dialogDeleted.getWindow() != null) {

                dialogDeleted.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView Delete = view.findViewById(R.id.textDeleteNote);
            TextView Cancel = view.findViewById(R.id.textCancel);

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseRef.child("AllNotes").child(mAuth.getCurrentUser().getUid()).child("Note").child(RF)
                            .removeValue().addOnCompleteListener(AdditionNotes.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isComplete()) {

                                GradientDrawable gradientDrawable = (GradientDrawable) viewPartNote.getBackground();
                                gradientDrawable.setColor(Color.rgb(0, 0, 0));
                                changeCamera_OR_Gallery = 0;
                                RecordingVoice = 0;
                                Intent goMain = new Intent();
                                setResult(RESULT_OK, goMain);
                                finish();

                            }
                        }
                    });

                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogDeleted.dismiss();
                }
            });
        }

        dialogDeleted.show();

    }

    private void DialgDeletVoice() {

        if (dialogDeletedVoice == null || dialogAddVoice != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AdditionNotes.this);
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.delete_voice, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));

            builder.setView(view);
            dialogDeletedVoice = builder.create();

            if (dialogDeletedVoice.getWindow() != null) {

                dialogDeletedVoice.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView Delete = view.findViewById(R.id.textDeleteNoteV);
            TextView Cancel = view.findViewById(R.id.textCancelV);

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isplaying) {

                        Toast.makeText(getBaseContext(), "Please close audio clip first", Toast.LENGTH_SHORT).show();

                    } else {

                        RecordingVoice = 0;
                        additionNotesBinding.layoutVoice.setVisibility(View.GONE);
                        pathfileVoice = null;
                        pathfileVoiceUri = null;
                        additionNotesBinding.seekPlayer.setMax(0);
                        dialogDeletedVoice.dismiss();
                    }

                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogDeletedVoice.dismiss();
                }
            });
        }

        dialogDeletedVoice.show();

    }

    private void pleyerVoice(final String pathVoice) {

        additionNotesBinding.layoutVoice.setVisibility(View.VISIBLE);

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(pathVoice);
            mediaPlayer.prepare();
            additionNotesBinding.meterVoice.setText(milliSecondsTotime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        databaseRef.child("Users").child(G_Sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Picasso.get().load(dataSnapshot.child("photo").getValue().toString()).into(additionNotesBinding.imageVoiceSender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //===========================================player==================================================================

        additionNotesBinding.imagePlayVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isplaying) {

                    pauseAudio();

                } else {

                    playAudio(pathVoice);

                }
            }
        });

        //========================================================seekbar========================================

        additionNotesBinding.seekPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (seekbarHandler != null)
                    pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekbarHandler != null) {
                    final int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });

    }

    private void pauseAudio() {

        mediaPlayer.pause();
        additionNotesBinding.imagePlayVoice.setImageResource(R.drawable.ic_play_voice);
        additionNotesBinding.statPlayer.setText("Pause");
        additionNotesBinding.statPlayer.setTextColor(Color.WHITE);
        isplaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);

    }

    private void resumeAudio() {

        mediaPlayer.start();
        additionNotesBinding.imagePlayVoice.setImageResource(R.drawable.ic_pause_voice);
        additionNotesBinding.statPlayer.setText("Playing");
        additionNotesBinding.statPlayer.setTextColor(Color.GREEN);
        isplaying = true;
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopAudio() {

        additionNotesBinding.imagePlayVoice.setImageResource(R.drawable.ic_play_voice);
        additionNotesBinding.statPlayer.setText("Stopedd");
        additionNotesBinding.statPlayer.setTextColor(Color.WHITE);
        mediaPlayer.stop();
        isplaying = false;

    }

    private void playAudio(final String pathVoice) {

        additionNotesBinding.imagePlayVoice.setImageResource(R.drawable.ic_pause_voice);
        additionNotesBinding.statPlayer.setText("Playing");
        additionNotesBinding.statPlayer.setTextColor(Color.GREEN);
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(pathVoice);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                additionNotesBinding.statPlayer.setText("Finish");
                additionNotesBinding.statPlayer.setTextColor(Color.WHITE);
                additionNotesBinding.imagePlayVoice.setImageResource(R.drawable.ic_play_voice);
                isplaying = false;

            }
        });

        isplaying = true;
        additionNotesBinding.seekPlayer.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler(getMainLooper());
        updateReunable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void updateReunable() {

        updateSeekbar = new Runnable() {
            @Override
            public void run() {

                additionNotesBinding.seekPlayer.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 100);
                additionNotesBinding.meterVoice.setText(milliSecondsTotime(mediaPlayer.getCurrentPosition()));
            }
        };
    }

    private String milliSecondsTotime(long milliSeconds) {

        String timeString = "";
        String secondesString = "";

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {

            timeString = hours + ":";
        }
        if (seconds < 10) {

            secondesString = "0" + seconds;
        } else {

            secondesString = "" + seconds;
        }


        timeString = timeString + minutes + ":" + secondesString;

        return timeString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isplaying) {

            stopAudio();
            isRunning = false;
        }
    }

}

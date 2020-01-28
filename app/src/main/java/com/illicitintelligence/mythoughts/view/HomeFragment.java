package com.illicitintelligence.mythoughts.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.illicitintelligence.mythoughts.Adapter.ThoughtAdapter;
import com.illicitintelligence.mythoughts.BuildConfig;
import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.Thought;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    private final int CAMERA_REQUEST_CODE = 777;

    private UploadThoughtFragment uploadThoughtFragment = new UploadThoughtFragment();

    @BindView(R.id.username_textview)
    public TextView userNameTextView;

    @BindView(R.id.profile_picture_imageview)
    public ImageView profilePictureImageView;

    private DatabaseReference thoughtDatabase;
    private List<Thought> thoughts;

    @BindView(R.id.thoughts_recyclerview)
    public RecyclerView thoughtRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            loadNewImage(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        }
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName().trim().length() > 0) {
            userNameTextView.setText("'" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "'!");
        } else {
            userNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        thoughtDatabase = FirebaseDatabase.getInstance().getReference();
        thoughtDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thoughts = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Thought temp = snap.getValue(Thought.class);
                    thoughts.add(temp);
                }
                setUpThoughts(thoughts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpThoughts(List<Thought> thoughts) {
        thoughtRecyclerView.setAdapter(new ThoughtAdapter(thoughts));
        thoughtRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
    }

    @OnClick(R.id.profile_picture_imageview)
    public void onUploadNewPic(View view) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }


    @OnClick(R.id.username_textview)
    public void onClickText(View view) {
        FirebaseCrashlytics.getInstance().setCustomKey("LoggedIn", true);
        FirebaseCrashlytics.getInstance().setCustomKey("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        throw new RuntimeException("Testing 1, 2, 1, 2, 1, 2");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {

            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");


            if (capturedImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference().child("profile_pics/" + FirebaseAuth.getInstance()
                                .getCurrentUser().getUid());

                UploadTask uploadTask = storageReference.putBytes(imageBytes);

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful() && task.isComplete()) {

                            storageReference.getDownloadUrl().addOnCompleteListener(uriTask ->
                            {
                                if (uriTask.isSuccessful()) {

                                    setNewImage(uriTask.getResult());
                                    loadNewImage(uriTask.getResult());
                                }
                            });

                        } else {
                            Log.d("TAG_X", storageReference.getDownloadUrl().getResult().toString());
                        }
                    }
                });


            }
        }
    }

    @OnClick(R.id.add_thought_button)
    public void uploadThought(View view) {
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.scale_up,
                        R.anim.scale_down,
                        R.anim.scale_up,
                        R.anim.scale_down)
                .replace(R.id.main_frame, uploadThoughtFragment)
                .addToBackStack(uploadThoughtFragment.getTag())
                .commit();
    }

    private void loadNewImage(Uri imageUri) {
        Glide.with(getContext())
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                .load(imageUri)
                .into(profilePictureImageView);
    }


    private void setNewImage(Uri imageUri) {

        UserProfileChangeRequest userChange = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();
        try {
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userChange).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Toast.makeText(getContext(), "Profile picture changed ", Toast.LENGTH_LONG).show();
                    else {
                        Log.d("TAG_X", task.getException().getMessage());
                        Toast.makeText(getContext(), "Profile picture NOT changed ", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_LONG).show();
        }
    }
}

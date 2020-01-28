package com.illicitintelligence.mythoughts.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.Thought;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadThoughtFragment extends Fragment {


    FirebaseStorage fbStorage;

    private final int REQUEST_CODE = 7070;

    private Uri thoughtImageUri = null;

    private String storedImageDirectory;

    @BindView(R.id.thought_et_frag)
    public EditText thoughtEditText;

    @BindView(R.id.profile_iv_frag)
    public ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upload_thought_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.ic_close)
    public void closeFragment(View view) {
        getParentFragment()
                .getChildFragmentManager()
                .popBackStack();
    }

    @OnClick(R.id.upload_bt_frag)
    public void onUpload(View view) {

        if (checkFields()) {
            String thoughts = thoughtEditText.getText().toString().trim();
            String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            try {
                Thought newThought = new Thought();
                newThought.setSharedImage(thoughtImageUri.toString());
                newThought.setSharedBy(user);
                newThought.setSharedThought(thoughts);

                DatabaseReference dbReference = FirebaseDatabase.getInstance()
                        .getReference();
                String key = dbReference.push().getKey();
                dbReference.child(key).setValue(newThought);
                getParentFragment().getChildFragmentManager().popBackStack();

            } catch (Exception e) {


            }


        }
    }

    private boolean checkFields() {

        if (thoughtEditText.getText().toString().length() == 0) {
            Toast.makeText(getContext().getApplicationContext(), "Thoughts cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (thoughtImageUri == null) {
            Toast.makeText(getContext().getApplicationContext(), "Please take a photo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.profile_iv_frag)
    public void onTakePicture(View view) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){


            try {

                File temporaryFile = createTemporaryFile();
                if(temporaryFile != null){

                    Uri imageUri = FileProvider.getUriForFile(
                            getContext(),
                            "com.example.android.fileproviderz",
                            temporaryFile
                    );

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, REQUEST_CODE);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private File createTemporaryFile() throws IOException {

        String date = new SimpleDateFormat("mm_dd_yyyy_hh_mm_ss", Locale.US).format(new Date());
        String imageName = FirebaseAuth.getInstance().getCurrentUser().getUid()+date;

        File fileDirectory = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageName,
                ".jpg",
                fileDirectory);

        storedImageDirectory = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {


            Bitmap capturedImage = BitmapFactory.decodeFile(storedImageDirectory);

            if (capturedImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                byte[] imageBytes = byteArrayOutputStream.toByteArray();

               String date = new SimpleDateFormat("mm_dd_yyyy_hh_mm_ss", Locale.US).format(new Date());

                StorageReference reference = FirebaseStorage.getInstance().getReference()
                        .child("thought_pics/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+date);

                UploadTask uploadTask = reference.putBytes(imageBytes);

                uploadTask.addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful())
                                    reference
                                            .getDownloadUrl()
                                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful())
                                                        setUri(task.getResult());
                                                }
                                            });
                            }
                        }
                );

            }

        }
    }

    private void setUri(Uri result) {
        this.thoughtImageUri = result;

        Glide.with(getContext())
                .applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .load(result)
                .into(imageView);
    }
}

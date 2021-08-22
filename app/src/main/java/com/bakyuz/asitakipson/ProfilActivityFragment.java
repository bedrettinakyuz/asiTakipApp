package com.bakyuz.asitakipson;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilActivityFragment extends Fragment {

    private static final int PERMISSION_REQUEST = 0;

    private static  final   int CHOOSE_IMAGE =101;
    ImageView imageView;
    EditText editText;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;
    FirebaseAuth firebaseAuth;
    String uID;
    FirebaseUser user;
    Button resimKaydet;
    private FirebaseAuth mAuth;

    public ProfilActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil_activity, container, false);
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();

        firebaseAuth =FirebaseAuth.getInstance();
        editText =view.findViewById(R.id.editTextDisplayName);
        imageView=view.findViewById(R.id.imageView);
        resimKaydet = view.findViewById(R.id.buttonKaydet);
        progressBar = view.findViewById(R.id.progresbar) ;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showImageChooser();
            }
        });
        resimKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimKaydet();
            }
        });
        kullaniciBilgiGetir();
        return view;
    }


    private void kullaniciBilgiGetir() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() != null) {

                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getDisplayName() != null) {

                editText.setText(user.getDisplayName());
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode== RESULT_OK && data!= null&& data.getData()!=null){


            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void showImageChooser(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"), CHOOSE_IMAGE  );
    }

    private void uploadImageToFirebaseStorage(final Bitmap bitmap){

        final StorageReference profilFileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+uID+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask  = profilFileImageRef.putBytes(data);

        progressBar.setVisibility(View.VISIBLE);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return profilFileImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    profileImageUrl = downloadUri.toString();
                    //  Toast.makeText(getBaseContext(), "Upload success! URL - " + downloadUri.toString() , Toast.LENGTH_SHORT).show();

                    imageView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }

    private void resimKaydet() {

        String displayName = editText.getText().toString();
        if(displayName.isEmpty()){

            editText.setError("İsim Gerekli");
            editText.requestFocus();
            return;
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null&&profileImageUrl!=null){

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Profil Güncellendi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            new Thread(new Runnable() {
                @Override
                public void run() { Glide.get(getActivity()).clearDiskCache();

                }
            }).start();

        }
    }

}

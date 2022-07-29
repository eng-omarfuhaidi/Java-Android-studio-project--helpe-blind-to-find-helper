package com.tu.blinvoi11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
private Button savebtn;
private EditText usernameET,userBioET;
private ImageView profileImageView;
private static int galleryPick=1;
private Uri ImageUri;
private StorageReference userProfileImgRef;
private String downloadUri;
private DatabaseReference userRef;
private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userProfileImgRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        savebtn=findViewById(R.id.save_settings_btn);
        usernameET=findViewById(R.id.username_settings);
        userBioET=findViewById(R.id.bio_settings);
        profileImageView=findViewById(R.id.settings_profile_image);
        progressDialog= new ProgressDialog(this);
        profileImageView.setOnClickListener(view -> {
            Intent gallaeryIntent =new Intent();
            gallaeryIntent.setAction(Intent.ACTION_GET_CONTENT);
            gallaeryIntent.setType("image/*");
            startActivityForResult(gallaeryIntent,galleryPick);

        });
savebtn.setOnClickListener((V) ->{ saveUserData();});
        /*savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                saveUserData();

            }
        });*/
       retrieveUserInfo();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data !=null)
        {
            ImageUri=data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }

    private void saveUserData()
    {
        final String getUserName=usernameET.getText().toString();
        final String getUserStatus=userBioET.getText().toString();
        if(ImageUri==null)
        {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("Image"))
                    {
                        saveInfoOnlyWithoutImage();
                    }
                    else
                        {
                            Toast.makeText(SettingsActivity.this, "فضلاً اختر صورة اولاً", Toast.LENGTH_SHORT).show();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(getUserName.equals(""))
        {
            Toast.makeText(this, "اسم المستخدم مطوب", Toast.LENGTH_SHORT).show();
        }
        else if(getUserStatus.equals(""))
        {
            Toast.makeText(this, "اسم الحالة مطلوب", Toast.LENGTH_SHORT).show();
        }
        else
            {
                progressDialog.setTitle("اعدادات الحساب");
                progressDialog.setMessage("يرجى الانتظار...");
                progressDialog.show();
                final StorageReference filePath=userProfileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                final UploadTask uploadTask=filePath.putFile(ImageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();
                       }
                       downloadUri=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                    if(task.isSuccessful())
                    {
                        downloadUri=task.getResult().toString();
                        HashMap<String,Object>ProfileMap=new HashMap<>();
                        ProfileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ProfileMap.put("name",getUserName);
                        ProfileMap.put("status",getUserStatus);
                        ProfileMap.put("Image",downloadUri);
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                           if(task.isSuccessful())
                           {
                               Intent intent= new Intent(SettingsActivity.this,BottomActivity.class);
                               startActivity(intent);
                               finish();
                               progressDialog.dismiss();
                               Toast.makeText(SettingsActivity.this, "تم تحديث البروفايل", Toast.LENGTH_SHORT).show();
                           }

                            }
                        });
                    }
                    }
                });
            }

    }

    private void saveInfoOnlyWithoutImage()
    {

     final String getUserName=usernameET.getText().toString();
        final String getUserStatus=userBioET.getText().toString();


         if(getUserName.equals(""))
    {
        Toast.makeText(this, "اسم المستخدم مطوب", Toast.LENGTH_SHORT).show();
    }
    else if(getUserStatus.equals(""))
    {
        Toast.makeText(this, "اسم الحالة مطلوب", Toast.LENGTH_SHORT).show();
    }
    else
    {
        progressDialog.setTitle("اعدادات الحساب");
        progressDialog.setMessage("يرجى الانتظار...");
        progressDialog.show();
        HashMap<String,Object>ProfileMap=new HashMap<>();
        ProfileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        ProfileMap.put("name",getUserName);
        ProfileMap.put("status",getUserStatus);


        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Intent intent= new Intent(SettingsActivity.this,BottomActivity.class);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "تم تحديث البروفايل", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    }
    private void retrieveUserInfo()
    {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String imageDB=snapshot.child("Image").getValue().toString();
                    String nameDB=snapshot.child("name").getValue().toString();
                    String bioDB=snapshot.child("status").getValue().toString();
                    usernameET.setText(nameDB);
                    userBioET.setText(bioDB);
                    Picasso.get().load(imageDB).placeholder(R.drawable.profile_image).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

package com.tu.blinvoi11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {
    private EditText phoneText, codeText;
    private Button continueButton;
    private String checker="",phoneNumber="";
    private RelativeLayout relativeLayout;
    private ProgressDialog progressBar;
private CountryCodePicker ccp;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String nVarificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();
        progressBar=new ProgressDialog(this);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueButton=findViewById(R.id.continueNextButton);
        relativeLayout=findViewById(R.id.phoneAuth);
        ccp=(CountryCodePicker)findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);
        mAuth = FirebaseAuth.getInstance();
continueButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
if(continueButton.getText().equals("ارسال")||checker.equals("تم ارسال الكود"))
{
String vervicationcode= codeText.getText().toString();
if(vervicationcode.equals(""))
{
    Toast.makeText(SignupActivity.this, "فضلاً ادخل رمز التحقق", Toast.LENGTH_SHORT).show();
}
else
    {
        progressBar.setTitle("التحقق من الكود");
        progressBar.setMessage("يرجى الانتظار حتى يتم التحقق من رقم الكود.");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(nVarificationId,vervicationcode);
        signInWithPhoneAuthCredential(credential);
    }
}
else
{
    phoneNumber=ccp.getFullNumberWithPlus();
    if(!phoneNumber.equals(""))
    {
        progressBar.setTitle("التحقق من رقم الهاتف");
        progressBar.setMessage("يرجى الانتظار حتى يتم التحقق من رقم هاتفك.");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,SignupActivity.this,callbacks);
    }
    else
        {
            Toast.makeText(SignupActivity.this, "فضلاً ادخل رقم هاتف صالح", Toast.LENGTH_SHORT).show();
        }
}
    }
});
callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(@NonNull FirebaseException e) {
        Toast.makeText(SignupActivity.this, "رقم هاتف غير معرف", Toast.LENGTH_SHORT).show();
        progressBar.dismiss();
        relativeLayout.setVisibility(View.VISIBLE);
        continueButton.setText("متابعة");
        codeText.setVisibility(View.GONE);
    }

    @Override
    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(s, forceResendingToken);
       nVarificationId=s;
       mResendToken=forceResendingToken;
        relativeLayout.setVisibility(View.GONE);
        checker="تم ارسال الكود";
        continueButton.setText("ارسال");
        codeText.setVisibility(View.VISIBLE);
        progressBar.dismiss();
        Toast.makeText(SignupActivity.this, "تم ارسال الكود , يرجى التحقق", Toast.LENGTH_SHORT).show();
    }
};
      //  initializeUI();

       /* regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            Intent homeintent =new Intent(SignupActivity.this,BottomActivity.class);
            startActivity(homeintent);
            finish();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          progressBar.dismiss();
                            Toast.makeText(SignupActivity.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                            sendusertoMainactivity();
                        } else {
                            progressBar.dismiss();
                            String e=task.getException().toString();
                            Toast.makeText(SignupActivity.this, "فشل التسجيل"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendusertoMainactivity()
    {
        Intent intent = new Intent(SignupActivity.this,BottomActivity.class);
        startActivity(intent);
        finish();
    }


    /*private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
       // regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);

    }*/

  /*  private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "فضلا ادخل البريد الالكتروني...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "فضلا ادخل كلمة المرور!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "تم التسجيل بنجاح !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "فشل التسجيل ! حاول مرة اخرى", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }*/
}

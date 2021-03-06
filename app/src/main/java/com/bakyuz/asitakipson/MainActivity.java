package com.bakyuz.asitakipson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    String uID;
    EditText editTextEMail;
    EditText editTextSifre;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEMail=(EditText)findViewById(R.id.text_email);
        editTextSifre=(EditText)findViewById(R.id.edit_text_password);
        mAuth = FirebaseAuth.getInstance();


        //ONE SİGNAL

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

    //ÜYE OL BUTTON
    public void btnUyeOl(View button) {
        startActivity(new Intent(MainActivity.this,UyeOlActivity.class));


    }

    public void btnGiris(View view){

        Giris();
    }

    public void btnSifremiUnuttum(View view)
    {
        startActivity(new Intent(MainActivity.this,SifremiUnuttum.class));
    }


    private void Giris(){

        String eMail = editTextEMail.getText().toString().trim();
        String sifre = editTextSifre.getText().toString().trim();
        if (eMail.isEmpty()) {
            editTextEMail.setError("E-Mail Alanı Zorunludur");
            editTextEMail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {

            editTextEMail.setError("Geçerli Bir E-Mail Girin");
            editTextEMail.requestFocus();
            return;
        }
        if (sifre.isEmpty()) {
            editTextSifre.setError("Şifre Alanı Zorunludur");
            editTextSifre.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(eMail,sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    finish();

                    Intent intent = new Intent(MainActivity.this,tab.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                }else{
                    Toast.makeText(MainActivity.this, "Kullanıcı Adı veya Şifre Hatalı", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}

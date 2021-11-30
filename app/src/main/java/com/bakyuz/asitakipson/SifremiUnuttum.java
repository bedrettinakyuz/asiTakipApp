package com.bakyuz.asitakipson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SifremiUnuttum extends AppCompatActivity {

    EditText editTextEMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifremi_unuttum);

        editTextEMail=findViewById(R.id.EditTextEMail);
    }

    public void btnSifremiUnuttum(View v){

        String eMail = editTextEMail.getText().toString().trim();
        if (eMail.isEmpty()) {
            editTextEMail.setError("E-Mail Alanı Zorunludur");
            editTextEMail.requestFocus();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches())
        {

            editTextEMail.setError("Geçerli Bir E-Mail Girin");
            editTextEMail.requestFocus();

        }
        else
        {
            FirebaseAuth.getInstance().sendPasswordResetEmail(eMail).
             addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     if(task.isSuccessful()){
                         Toast.makeText(SifremiUnuttum.this, "Şifre Sıfırlama Bağlantısı Email Adresinize Gönderildi", Toast.LENGTH_SHORT).show();
                         finish();

                     } else {
                         Toast.makeText(SifremiUnuttum.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                     }
                 }
             });



        }





    }
}
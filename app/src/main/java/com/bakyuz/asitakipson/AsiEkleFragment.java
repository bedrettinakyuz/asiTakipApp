package com.bakyuz.asitakipson;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AsiEkleFragment extends Fragment {


    DatabaseReference db;
    EditText editTextAsiAdi;
    EditText editTextHastahaneAdi;
    EditText editTextAsiTarihi;
    CalendarView calendarViewTarih;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    Button btnAsiKaydet;
    String uID;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_asi_ekle, container, false);
        mAuth = FirebaseAuth.getInstance();
        uID= mAuth.getUid();
        editTextAsiAdi = view.findViewById(R.id.EditTextAsiAdi);
        editTextHastahaneAdi = view.findViewById(R.id.EditTextHastahane);
        editTextAsiTarihi=view.findViewById(R.id.EditTextAsiTarihi);
        btnAsiKaydet = view.findViewById(R.id.ButtonAsiKaydet);
        String eMail;

        btnAsiKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsiKaydet();

            }
        });

        editTextAsiTarihi.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public  void onClick(View v){tarihAc();}

            }
         );


        if(uID!=null){
            db = FirebaseDatabase.getInstance().getReference(uID).child("Asilar");

        }



        return view;



    }



    public void AsiKaydet() {

        if(!editTextAsiAdi.getText().toString().isEmpty() &&
                !editTextAsiTarihi.getText().toString().isEmpty()&&
                !editTextHastahaneAdi.getText().toString().isEmpty()
        ){
            Asi asi = new Asi();
            asi.setAsiAdi(editTextAsiAdi.getText().toString());
            asi.setHastahaneAdi(editTextHastahaneAdi.getText().toString());
            asi.setAsiTarih(editTextAsiTarihi.getText().toString());
            asi.setAsiDurum(false);
            String asiID =db.push().getKey();
            asi.setAsiId(asiID);

            db.child(asiID).setValue(asi).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Aşı Eklendi");

                    alertDialog
                            .setMessage("Geri dönmek istiyor musunuz?")
                            .setCancelable(false)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    editTextAsiAdi.setText("");
                                    editTextHastahaneAdi.setText("");

                                }
                            });

                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Aşı Ekleme Esnasında Hata Oluştu !!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else Toast.makeText(getContext(), "Gerekli Alanları Doldurunuz !!", Toast.LENGTH_SHORT).show();


    }


    public void tarihAc(){

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editTextAsiTarihi.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        }, day,month,year);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}



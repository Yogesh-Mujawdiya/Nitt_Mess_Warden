package com.my.nitt_mess_warden;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.my.nitt_mess_warden.ui.FeedbackFragment;

import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FeedbackActivity extends AppCompatActivity {

    TextView FeedBack;
    LinearLayout layoutFeedback;
    RatingBar Food, Quantity, Service, Cleanliness, FoodOnTime, Overall;
    TextInputLayout Remark;
    Button Reset, Submit;
    LinearLayout FeedBackLayout;
    FirebaseUser User;
    String MessId;
    String Key,key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getSupportActionBar().setTitle("FeedBack");
        MessId = getIntent().getStringExtra("Id");

        GregorianCalendar calendar = new GregorianCalendar();
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        Key = Year + "/" + Month;
        key = Month+"-"+Year;
        FeedBack = findViewById(R.id.UserFeedBack);
        layoutFeedback = findViewById(R.id.layoutFeedback);
        Food = findViewById(R.id.ratingBarFood);
        Quantity = findViewById(R.id.ratingBarQuantity);
        FoodOnTime = findViewById(R.id.ratingBarFoodOnTime);
        Service = findViewById(R.id.ratingBarService);
        Cleanliness = findViewById(R.id.ratingBarCleanliness);
        Reset = findViewById(R.id.ResetButton);
        Submit = findViewById(R.id.SubmitButton);
        FeedBackLayout = findViewById(R.id.layout);
        User = FirebaseAuth.getInstance().getCurrentUser();
        getFeedback();
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food.setRating((float) 2.5);
                Quantity.setRating((float) 2.5);
                FoodOnTime.setRating((float) 2.5);
                Service.setRating((float) 2.5);
                Cleanliness.setRating((float) 2.5);
                Overall.setRating((float) 2.5);
                Remark.getEditText().setText("");
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final String RollNo = firebaseUser.getEmail().split("@")[0];
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/MessRating").child(MessId).child(Key);
                databaseReference.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Float FoodData = currentData.child("Food").getValue(Float.class);
                        Float QuantityData = currentData.child("Quantity").getValue(Float.class);
                        Float FoodOnTimeData = currentData.child("FoodOnTime").getValue(Float.class);
                        Float ServiceData = currentData.child("Service").getValue(Float.class);
                        Float CleanlinessData = currentData.child("Cleanliness").getValue(Float.class);
                        int Count = currentData.child("Count").getValue(Integer.class);
                        Count+=1;
                        FoodData = (FoodData+Food.getRating())/Count;
                        QuantityData = (QuantityData+Quantity.getRating())/Count;
                        FoodOnTimeData = (FoodOnTimeData+FoodOnTime.getRating())/Count;
                        ServiceData = (ServiceData+Service.getRating())/Count;
                        CleanlinessData = (CleanlinessData+Cleanliness.getRating())/Count;
                        currentData.child("Food").setValue(FoodData);
                        currentData.child("Quantity").setValue(QuantityData);
                        currentData.child("FoodOnTime").setValue(FoodOnTimeData);
                        currentData.child("Service").setValue(ServiceData);
                        currentData.child("Cleanliness").setValue(CleanlinessData);
                        currentData.child("Count").setValue(Count);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        FirebaseDatabase.getInstance().getReference("Data/Warden").child(RollNo).child("LastFeedback").setValue(key);
                    }
                });
            }
        });
    }

    private void getFeedback() {
        final ProgressDialog progressDialog = new ProgressDialog(FeedbackActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String RollNo = firebaseUser.getEmail().split("@")[0];
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/Warden").child(RollNo).child("LastFeedback");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue().equals(key)) {
                        FeedBack.setText("Feedback Submitted");
                        layoutFeedback.setVisibility(View.GONE);
                }else {
                    layoutFeedback.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                FeedBackLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
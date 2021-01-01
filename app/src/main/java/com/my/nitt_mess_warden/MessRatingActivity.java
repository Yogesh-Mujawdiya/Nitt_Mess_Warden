package com.my.nitt_mess_warden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MessRatingActivity extends AppCompatActivity {

    TextView textView;
    RatingBar Food, Quantity, FoodOnTime, Service, Cleanliness, OverAll;
    private String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_rating);

        getSupportActionBar().setTitle("Mess Rating");
        String messId = getIntent().getStringExtra("Id");
        String messName = getIntent().getStringExtra("Name");
        GregorianCalendar calendar = new GregorianCalendar();
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        Key = Year + "/" + Month;

        textView = findViewById(R.id.MessName);
        Food = findViewById(R.id.Food);
        Quantity = findViewById(R.id.Quantity);
        FoodOnTime = findViewById(R.id.FoodOnTime);
        Service = findViewById(R.id.Service);
        Cleanliness = findViewById(R.id.Cleanliness);
        OverAll = findViewById(R.id.OverAll);
        textView.setText(messName);

        getMessRating(messId);

    }

    private void getMessRating(String MessId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data/MessRating").child(MessId).child(Key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot currentData) {
                if(currentData.exists()) {
                    Float FoodData = currentData.child("Food").getValue(Float.class);
                    Float QuantityData = currentData.child("Quantity").getValue(Float.class);
                    Float FoodOnTimeData = currentData.child("FoodOnTime").getValue(Float.class);
                    Float ServiceData = currentData.child("Service").getValue(Float.class);
                    Float CleanlinessData = currentData.child("Cleanliness").getValue(Float.class);
                    Float OverAllData = (FoodData + QuantityData + FoodOnTimeData + ServiceData + CleanlinessData) / 5;

                    Food.setRating(FoodData);
                    Quantity.setRating(QuantityData);
                    FoodOnTime.setRating(FoodOnTimeData);
                    Service.setRating(ServiceData);
                    Cleanliness.setRating(CleanlinessData);
                    OverAll.setRating(OverAllData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
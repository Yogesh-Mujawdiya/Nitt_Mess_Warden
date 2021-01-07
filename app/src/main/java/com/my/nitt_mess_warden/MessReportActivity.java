package com.my.nitt_mess_warden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MessReportActivity extends AppCompatActivity {

    EditText MonthData, YearData;
    int Month, Year;
    private File filePath;
    FirebaseUser User;
    AppCompatButton button;
    String MessId;
    float Price, Breakfast, Lunch, Snack, Dinner;
    List<String> Students;
    Map<String, String> StudentName;
    Map<String, Integer> StudentOnLeave;
    Map<String, Map<String, Integer>> StudentGuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_report);
        MessId = getIntent().getStringExtra("MessId");
        filePath = new File(Environment.getExternalStorageDirectory() + "/Report.xls");
        MonthData = findViewById(R.id.picker_month);
        Price = Breakfast = Lunch = Snack = Dinner = 0;
        YearData = findViewById(R.id.picker_year);
        User = FirebaseAuth.getInstance().getCurrentUser();
        button = findViewById(R.id.GenerateReport);
        button.setOnClickListener(view -> {
            Students = new ArrayList<>();
            StudentName = new HashMap<>();
            StudentOnLeave = new HashMap<>();
            StudentGuest = new HashMap<>();
            button.setVisibility(View.GONE);
            if (!hasPermissions(MessReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Permission ask
                ActivityCompat.requestPermissions(MessReportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            } else {
                getMessPrice();
            }

        });
    }

    private void getMessPrice() {
        Month = Integer.parseInt(MonthData.getText().toString()) - 1;
        Year = Integer.parseInt(YearData.getText().toString());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Mess").child(MessId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if (data.exists()) {
                    if(data.hasChild("Price")) {
                        Price = data.child("Price").getValue(Integer.class);
                    }
                    if(data.hasChild("GuestPrice")){
                        Breakfast = data.child("GuestPrice").child("Breakfast").getValue(Integer.class);
                        Lunch = data.child("GuestPrice").child("Lunch").getValue(Integer.class);
                        Snack = data.child("GuestPrice").child("Snack").getValue(Integer.class);
                        Dinner = data.child("GuestPrice").child("Dinner").getValue(Integer.class);
                    }
                    getAllStudent();
                } else
                    button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getAllStudent() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/AllocatedMessHistory").child(MessId).child(Year + "/" + Month);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if (data.exists()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Students.add(d.getKey());
                        StudentName.put(d.getKey(), d.getValue(String.class));
                        StudentOnLeave.put(d.getKey(), 0);
                        Map<String, Integer> details = new HashMap<>();
                        details.put("Breakfast", 0);
                        details.put("Lunch", 0);
                        details.put("Snack", 0);
                        details.put("Dinner", 0);
                        StudentGuest.put(d.getKey(), details);
                    }
                    getStudentOnLeave();
                    Log.d("DDDDDDD", "DDDDDDd");
                } else
                    button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getStudentOnLeave() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/StudentOnLeave").child(MessId).child(Year + "/" + Month);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if (data.exists()) {
                    for (DataSnapshot d : data.getChildren()) {
                        for (DataSnapshot roll : d.getChildren()) {
                            StudentOnLeave.put(roll.getKey(), StudentOnLeave.get(roll.getKey()) + 1);
                        }
                    }
                }
                Log.d("DDDDDDD", "DDDDDDd");
                getStudentGuestCount();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getStudentGuestCount() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Data/Guest").child(MessId).child(Year + "/" + Month);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot data) {
                if (data.exists()) {
                    for (DataSnapshot d : data.getChildren()) {
                        for (DataSnapshot roll : d.getChildren()) {
                            Map<String, Integer> details = StudentGuest.get(roll.getKey());
                            if (roll.hasChild("Breakfast"))
                                details.put("Breakfast", details.get("Breakfast") + roll.child("Breakfast").getValue(Integer.class));
                            if (roll.hasChild("Lunch"))
                                details.put("Lunch", details.get("Lunch") + roll.child("Lunch").getValue(Integer.class));
                            if (roll.hasChild("Snack"))
                                details.put("Snack", details.get("Snack") + roll.child("Snack").getValue(Integer.class));
                            if (roll.hasChild("Dinner"))
                                details.put("Dinner", details.get("Dinner") + roll.child("Dinner").getValue(Integer.class));
                        }
                    }
                }
                buttonCreateExcel();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    public void buttonCreateExcel() {
        button.setVisibility(View.VISIBLE);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Mess Report");
        HSSFRow hssfRow = hssfSheet.createRow(0);
        hssfRow.createCell(0).setCellValue("Roll No");
        hssfRow.createCell(1).setCellValue("Name");
        hssfRow.createCell(2).setCellValue("Leaves Taken");
        hssfRow.createCell(3).setCellValue("Guest Breakfast");
        hssfRow.createCell(3).setCellValue("Guest Lunch");
        hssfRow.createCell(3).setCellValue("Guest Snack");
        hssfRow.createCell(3).setCellValue("Guest Dinner");
        hssfRow.createCell(4).setCellValue("Amount");
        GregorianCalendar calendar = new GregorianCalendar(Year, Month, 1);

        int i = 1;
        for (String R : Students) {
            HSSFRow row = hssfSheet.createRow(i);
            row.createCell(0).setCellValue(R);
            row.createCell(1).setCellValue(StudentName.get(R));
            row.createCell(2).setCellValue(StudentOnLeave.get(R));
            row.createCell(3).setCellValue(StudentGuest.get(R).get("Breakfast"));
            row.createCell(3).setCellValue(StudentGuest.get(R).get("Lunch"));
            row.createCell(3).setCellValue(StudentGuest.get(R).get("Snack"));
            row.createCell(3).setCellValue(StudentGuest.get(R).get("Dinner"));
            float G =  StudentGuest.get(R).get("Breakfast")*Breakfast +
                    StudentGuest.get(R).get("Lunch")*Lunch +
                    StudentGuest.get(R).get("Snack")*Snack + StudentGuest.get(R).get("Dinner")*Dinner;
                    row.createCell(4).setCellValue((calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - StudentOnLeave.get(R)) * Price + G);
            i += 1;
        }
        Environment.getExternalStorageState();
        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }


            // Get URI and MIME type of file
//                        Uri uri = FileProvider.getUriForFile(getContext(), "com.my.nitt_mess" + ".provider", filePath);
            Uri uri = FileProvider.getUriForFile(MessReportActivity.this, getPackageName() + ".provider", filePath);
            String mime = getContentResolver().getType(uri);

            // Open file with user selected app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMessPrice();
            }
        }
    }

}
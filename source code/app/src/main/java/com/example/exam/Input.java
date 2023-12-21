package com.example.exam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Input extends AppCompatActivity {
    DataBaseHelper dbhelper;
    EditText FoodEditText;
    EditText KcalEditText;
    EditText ProteinEditText;
    MyApplication myApplication; // MyApplication 클래스 객체



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        dbhelper = new DataBaseHelper(getApplicationContext());

        // MyApplication 클래스 객체 초기화
        MyApplication myApplication = (MyApplication) getApplication();


        FoodEditText = findViewById(R.id.FoodInput);
        KcalEditText = findViewById(R.id.KcalInput);
        ProteinEditText = findViewById(R.id.ProteinInput);

        Button cancelButton = findViewById(R.id.button10);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 확인 버튼
        Button saveButton = findViewById(R.id.button2);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFoodData();

                // MyApplication 클래스를 통해 날짜 정보 가져오기
                int year = myApplication.getSelectedYear();
                int month = myApplication.getSelectedMonth();
                int day = myApplication.getSelectedDay();

                Intent intent = new Intent(Input.this, Select.class);
                intent.putExtra("selectedYear", year);
                intent.putExtra("selectedMonth", month);
                intent.putExtra("selectedDay", day);
                startActivity(intent);
                finish();
            }
        });

    }

    private void saveFoodData() {
        String food = FoodEditText.getText().toString().trim();
        String kcalString = KcalEditText.getText().toString().trim();
        String proteinString = ProteinEditText.getText().toString().trim();
        // 중복 체크
        if (isFoodAlreadyExists(food)) {
            // 중복된 음식이 이미 존재할 경우
            Toast.makeText(this, "이미 등록된 음식입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int kcal = Integer.parseInt(kcalString);
        int protein = Integer.parseInt(proteinString);


        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("food", food);
        values.put("kcal", kcal);
        values.put("protein", protein);

        values.put("resid", R.drawable.rice);

        long newRowId = db.insert(DataBaseHelper.FoodTableName, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "음식을 저장하였습니다", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "음식 데이터 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isFoodAlreadyExists(String food) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String query = "SELECT * FROM " + DataBaseHelper.FoodTableName + " WHERE food=?";
        String[] selectionArgs = {food};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }
}

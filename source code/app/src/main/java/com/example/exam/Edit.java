package com.example.exam;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class Edit extends AppCompatActivity {
    DataBaseHelper dbhelper;
    EditText FoodEditText;
    EditText KcalEditText;
    EditText ProteinEditText;

    MyApplication myApplication; // MyApplication 클래스 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        dbhelper = new DataBaseHelper(getApplicationContext());
        // MyApplication 클래스 객체 초기화
        MyApplication myApplication = (MyApplication) getApplication();

        int kcal = 0;
        int protein = 0;
        String foodName = null;

        Intent intent = getIntent();
        if (intent != null) {
            foodName = intent.getStringExtra("foodName");
            kcal = intent.getIntExtra("kcal", 0);
            protein = intent.getIntExtra("protein", 0);
        }

        FoodEditText = findViewById(R.id.NameInput);
        KcalEditText = findViewById(R.id.KcalInput);
        ProteinEditText = findViewById(R.id.ProteinInput);

        if (foodName != null)
            FoodEditText.setHint(foodName);
        KcalEditText.setHint(String.valueOf(kcal));
        ProteinEditText.setHint(String.valueOf(protein));


        
        Button editfood = (Button)findViewById(R.id.button2);//수정확인버튼

        String finalFoodName = foodName;
        int finalKcal = kcal;
        int finalProtein = protein;

        editfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDataBase(finalFoodName, finalKcal, finalProtein);

                Intent intent = new Intent(Edit.this, Select.class);


                int year = myApplication.getSelectedYear();
                int month = myApplication.getSelectedMonth();
                int day = myApplication.getSelectedDay();

                intent.putExtra("selectedYear", year);
                intent.putExtra("selectedMonth", month);
                intent.putExtra("selectedDay", day);


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



        Button cancel = (Button)findViewById(R.id.button10);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit.this, Select.class);
                // 이전에 열려있던 액티비티들을 모두 종료하고 새로운 액티비티를 시작

                // MyApplication 클래스를 통해 날짜 정보 가져와서 Intent에 추가
                int year = myApplication.getSelectedYear();
                int month = myApplication.getSelectedMonth();
                int day = myApplication.getSelectedDay();

                intent.putExtra("selectedYear", year);
                intent.putExtra("selectedMonth", month);
                intent.putExtra("selectedDay", day);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }//oncreate

    private  void DeleteDataBase(String foodName){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String selection = "food=?";
        String[] selectionArgs = {foodName};
        db.delete(DataBaseHelper.FoodTableName, selection, selectionArgs);
        db.close();
    }


    private void updateDataBase(String foodName, int kcal, int protein){


        String newFoodName = FoodEditText.getText().toString().trim();
        if(newFoodName == null || newFoodName.isEmpty())
            newFoodName = foodName;
        else{
            if (isFoodAlreadyExists(newFoodName)) {
                Toast.makeText(this, "같은 이름의 음식이 이미 존재합니다", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String newKcal = KcalEditText.getText().toString().trim();
       if(newKcal == null || newKcal.isEmpty())
          newKcal = String.valueOf(kcal);

        String newProtein = ProteinEditText.getText().toString().trim();
        if(newProtein == null || newProtein.isEmpty())
            newProtein = String.valueOf(protein);




            SQLiteDatabase db = dbhelper.getWritableDatabase();
            String whereClause = "food=?";
            String[] whereArgs = {foodName};

            // 업데이트할 컬럼과 값
            ContentValues values = new ContentValues();
            values.put("food", newFoodName);
            values.put("kcal", newKcal);
            values.put("protein", newProtein);

            // 데이터베이스 업데이트
            db.update(DataBaseHelper.FoodTableName, values, whereClause, whereArgs);
            db.close();

    }
    private boolean isFoodAlreadyExists(String foodName) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String query = "SELECT food FROM " + DataBaseHelper.FoodTableName + " WHERE food=?";
        Cursor cursor = db.rawQuery(query, new String[]{foodName});


        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }
}//AppCompatActivity상속

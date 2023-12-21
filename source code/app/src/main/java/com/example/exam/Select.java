package com.example.exam;

import static com.example.exam.DataBaseHelper.FoodDataTableName;
import static com.example.exam.DataBaseHelper.FoodTableName;
import static java.security.AccessController.getContext;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Select extends AppCompatActivity {
    DataBaseHelper dbhelper;
    ListView listView;
    FoodItemAdapter adapter; // 리스트뷰에 연결할 어댑터
    MyApplication myApplication; // MyApplication 클래스 객체


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        dbhelper = new DataBaseHelper(getApplicationContext());

        // MyApplication 클래스 객체 초기화
        MyApplication myApplication = (MyApplication) getApplication();


        listView = findViewById(R.id.listView);
        adapter = new FoodItemAdapter();
        listView.setAdapter(adapter);

        updateListView();

        Button cancelbutton = (Button)findViewById(R.id.cancel);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this, MainActivity.class);
                // 이전에 열려있던 액티비티들을 모두 종료하고 새로운 액티비티를 시작
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button addfood = (Button)findViewById(R.id.addfood);
        addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this, Input.class);

                // MyApplication 클래스를 통해 날짜 정보 가져와서 Intent에 추가
                int year = myApplication.getSelectedYear();
                int month = myApplication.getSelectedMonth();
                int day = myApplication.getSelectedDay();

                intent.putExtra("selectedYear", year);
                intent.putExtra("selectedMonth", month);
                intent.putExtra("selectedDay", day);

                startActivity(intent);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 데이터 가져오기
                FoodItem clickedItem = (FoodItem) adapter.getItem(position);
                showDialog(clickedItem);
            }
        });
    }


    private void showDialog(FoodItem clickedItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        AlertDialog alertDialog = builder.create();
        builder.setTitle("음식");
        builder.setIcon(R.drawable.edit);
        builder.setMessage("현재 음식 : " + clickedItem.getName() + "\n"
                + "Kcal : " + clickedItem.getKcal() + "\n"
                + "Protein : " + clickedItem.getProtein());

        // 추가 버튼 추가 (선택적)
        builder.setNegativeButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 다이얼로그에서 확인 버튼을 눌렀을 때의 동작 추가 (선택적)
                Intent secondintent = getIntent();

                int Year = 0;
                int Month = 0;
                int Day = 0;

                if (secondintent != null) {
                    Year = secondintent.getIntExtra("selectedYear", 0);
                    Month = secondintent.getIntExtra("selectedMonth", 0);
                    Day = secondintent.getIntExtra("selectedDay", 0);
                }
                updateDateTable(clickedItem, Year, Month, Day);
            }
        });
        builder.setNeutralButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 다이얼로그에서 확인 버튼을 눌렀을 때의 동작 추가 (선택적)
                Intent intent = new Intent(Select.this, Edit.class);


                intent.putExtra("foodName", clickedItem.getName());
                intent.putExtra("kcal", clickedItem.getKcal());
                intent.putExtra("protein", clickedItem.getProtein());

                Intent secondintent = getIntent();

                int Year = 0;
                int Month = 0;
                int Day = 0;

                if (secondintent != null) {
                    Year = secondintent.getIntExtra("selectedYear", 0);
                    Month = secondintent.getIntExtra("selectedMonth", 0);
                    Day = secondintent.getIntExtra("selectedDay", 0);
                }

                startActivity(intent);
            }
        });
        builder.setPositiveButton("취소",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        // 다이얼로그 표시
        builder.show();
    }


    private void updateDateTable(FoodItem clickeditem, int selectedYear, int selectedMonth, int selectedDay) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String desiredFoodName = clickeditem.getName();

        String query = "SELECT _id FROM " + FoodTableName + " WHERE food = ?";
        Cursor cursor = db.rawQuery(query, new String[]{desiredFoodName});

        long desiredFoodId = -1;

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("_id");
            if (columnIndex != -1) {
                desiredFoodId = cursor.getLong(columnIndex);
            }
            cursor.close();
        }


        if (desiredFoodId != -1) {
            int Year = selectedYear;
            int Month = selectedMonth + 1;
            int Day = selectedDay;

            SQLiteDatabase database = dbhelper.getWritableDatabase();

            String dateTableQuery = "SELECT fid FROM " + FoodDataTableName +
                    " WHERE year = ? AND month = ? AND day = ? AND fid = ?";
            String[] dateTableArgs = {String.valueOf(Year), String.valueOf(Month), String.valueOf(Day),
                    String.valueOf(desiredFoodId)};

            Cursor dateTableCursor = database.rawQuery(dateTableQuery, dateTableArgs);

            if (dateTableCursor.getCount() > 0) {
                Toast.makeText(this, "해당 날짜에 이미 존재하는 데이터입니다", Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put("year", selectedYear);
                values.put("month", selectedMonth + 1);
                values.put("day", selectedDay);
                values.put("fid", desiredFoodId);

                long newRowId = database.insert(DataBaseHelper.FoodDataTableName, null, values);

                if (newRowId != -1) {
                    Toast.makeText(this, "음식을 저장하였습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "음식 데이터 저장 실패", Toast.LENGTH_SHORT).show();
                }
            }

            dateTableCursor.close();
            database.close();
        }
    }

    private void updateListView() {
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        if(database != null) {
            try {
                String sql = "SELECT food, kcal, protein, resid FROM Food";
                Cursor cursor = database.rawQuery(sql, null);

                adapter.clear();



                for(int i =0; i<cursor.getCount(); i++){
                    cursor.moveToNext();
                    String food = cursor.getString(0);
                    int kcal = cursor.getInt(1);
                    int protein = cursor.getInt(2);
                    int resId = cursor.getInt(3);
                    if(resId == 0)
                        resId = R.drawable.rice;
                    adapter.addItem(new FoodItem(food, kcal, protein, resId));
                }



                cursor.close();
                adapter.notifyDataSetChanged();
                database.close();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "오류가 발생하였습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



    class FoodItemAdapter extends BaseAdapter {


        ArrayList<FoodItem> items = new ArrayList<FoodItem>();

        public void addAll(List<FoodItem> foodItemList) {
            items.addAll(foodItemList);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(FoodItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            FoodItemView view = new FoodItemView(getApplicationContext());

            FoodItem item = items.get(position);
            view.setName(item.getName());
            view.setKcal(item.getKcal());
            view.setProtein(item.getProtein());
            view.setImage(item.getResId());

            return view;
        }

        public void clear() {
            items.clear();
        }
    }
}
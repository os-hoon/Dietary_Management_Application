package com.example.exam;


import static com.example.exam.DataBaseHelper.FoodDataTableName;
import static com.example.exam.DataBaseHelper.FoodTableName;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class DateTimePicker extends LinearLayout {
    DataBaseHelper dbhelper;
    private DatePicker datePicker;
    FoodItemAdapter adapter;
    TextView kcaltext;
    TextView proteintext;

    public DateTimePicker(Context context) {
        super(context);
        init();
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.datetimepicker, this);
        datePicker = findViewById(R.id.datePicker);

        ListView listview = findViewById(R.id.listview);
        adapter = new FoodItemAdapter();

        loadDate();
        updateListView();
        updateTotal();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템의 데이터 가져오기
                FoodItem clickedItem = (FoodItem) adapter.getItem(position);
                showDialog(clickedItem);
            }
        });


        // DatePicker의 날짜가 변경될 때마다 updateListView 호출
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        updateListView();
                        updateTotal();
                    }

                });

        Button addbutton = (Button) findViewById(R.id.add);
        addbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication myApplication = (MyApplication) getContext().getApplicationContext();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                // MyApplication에 날짜 정보 저장
                myApplication.setSelectedYear(year);
                myApplication.setSelectedMonth(month);
                myApplication.setSelectedDay(day);

                Context context = getContext();
                Intent secondintent = new Intent(context, Select.class);
                secondintent.putExtra("selectedYear", year);
                secondintent.putExtra("selectedMonth", month);
                secondintent.putExtra("selectedDay", day);


                context.startActivity(secondintent);
            }
        });
    }

    public void loadDate(){
        MyApplication myApplication = (MyApplication) getContext().getApplicationContext();
        if(myApplication.getSelectedYear()==-1){
            int T_Year = datePicker.getYear();
            int T_Month = datePicker.getMonth();
            int T_Day = datePicker.getDayOfMonth();
            datePicker.updateDate(T_Year, T_Month, T_Day);
        }
        else {
            datePicker.updateDate(myApplication.getSelectedYear(),
                    myApplication.getSelectedMonth(),
                    myApplication.getSelectedDay());
        }
    }

    private void showDialog(FoodItem clickedItem){
        int C_kcal = FindInfo(clickedItem, 0);
        int C_protein = FindInfo(clickedItem, 1);

        final EditText edttext1 = new EditText(getContext());

        AlertDialog.Builder dlg = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme));
        AlertDialog alertDialog = dlg.create();
        dlg.setTitle("음식정보 수정");

        dlg.setMessage("현재 음식 : " + clickedItem.getName() + "\n"
                + "총 섭취 Kcal : " + C_kcal+ "\n"
                + "총 섭취 Protein : " + C_protein+ "\n"
                +"\n"
                +  "섭취한 g수를 입력해주세요");

        dlg.setView(edttext1);
        edttext1.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        dlg.setIcon(R.drawable.edit);
        dlg.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = edttext1.getText().toString();
                if(!inputText.isEmpty())
                {
                    int intValue = Integer.parseInt(inputText);
                    countUpdate(clickedItem, intValue);
                    updateTotal();
                }
                else {
                    Toast.makeText(getContext(), "값이 입력되지 않음", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dlg.setNeutralButton("삭제",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                DeleteFood(clickedItem);
            }
        });
        
        
        dlg.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dlg.show();
    }


    public int FindInfo(FoodItem clickedItem, int index){
        int S_kcal = clickedItem.getKcal();
        int S_protein = clickedItem.getProtein();
        int S_count = -1;
        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        String foodName = clickedItem.getName();
        int foodId = -1;

        if (database != null) {
            String query = "SELECT _id FROM " + FoodTableName + " WHERE food = ?";
            String[] selectionArgs = {foodName};
            Cursor cursor = database.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("_id");
                foodId = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        database.close();


        int Year = datePicker.getYear();
        int Month = datePicker.getMonth()+1;
        int Day = datePicker.getDayOfMonth();

        database = dbhelper.getWritableDatabase();

        String countQuery = "SELECT count FROM " + FoodDataTableName +
                " WHERE fid = ? AND year = ? AND month = ? AND day = ?";
        String[] countArgs = {String.valueOf(foodId), String.valueOf(Year), String.valueOf(Month), String.valueOf(Day)};

        Cursor countCursor = database.rawQuery(countQuery, countArgs);

        if (countCursor.moveToFirst()) {
            S_count = countCursor.getInt(0);
            S_count/=100;
        }

        countCursor.close();
        S_kcal *= S_count;
        S_protein *= S_count;

        if(index==0)
            return S_kcal;
        else
            return S_protein;
    }

    public void DeleteFood(FoodItem clickedItem){
        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        String foodName = clickedItem.getName();

        int foodId = -1;

        if (database != null) {
            String query = "SELECT _id FROM " + FoodTableName + " WHERE food = ?";
            String[] selectionArgs = {foodName};
            Cursor cursor = database.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("_id");
                foodId = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        database.close();


        int Year = datePicker.getYear();
        int Month = datePicker.getMonth()+1;
        int Day = datePicker.getDayOfMonth();

        database = dbhelper.getWritableDatabase();
        String whereClause = "fid = ? AND year = ? AND month = ? AND day = ?";
        String[] whereArgs = {String.valueOf(foodId), String.valueOf(Year), String.valueOf(Month), String.valueOf(Day)};

        // Execute the DELETE query
        database.delete(FoodDataTableName, whereClause, whereArgs);

        database.close();
        updateListView();
        updateTotal();
    }


    public void updateTotal(){

        int Year = datePicker.getYear();
        int Month = datePicker.getMonth()+1;
        int Day = datePicker.getDayOfMonth();

        kcaltext = findViewById(R.id.kcal);
        proteintext = findViewById(R.id.protein);

        int Count = 0;
        int Fid = 0;

        int TotalKcal = 0;
        int TotalProtein = 0;

        List<Pair<Integer, Integer>> fidCountList = new ArrayList<>();

        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        if(database != null){
            String sql = "SELECT fid, count FROM FoodData WHERE year = ? AND month = ? AND day = ?";
            String[] selectionArgs = {String.valueOf(Year), String.valueOf(Month), String.valueOf(Day)};
            Cursor cursor = database.rawQuery(sql, selectionArgs);

            if (cursor.getCount() > 0) {
                // 해당 날짜에 대한 데이터가 존재하는 경우에만 fidCountList에 추가
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    Fid = cursor.getInt(0);
                    Count = cursor.getInt(1);
                    fidCountList.add(new Pair<>(Fid, Count));
                }
            }
            else {
                kcaltext.setText(String.valueOf(TotalKcal));
                proteintext.setText(String.valueOf(TotalProtein));
            }
            cursor.close();
        }
        database.close();


        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase db = dbhelper.getReadableDatabase();


        for(Pair<Integer, Integer>pair : fidCountList){
            int fid = pair.first;
            int count = pair.second;

            int kcal = 0;
            int protein = 0;

            Cursor cursor = db.rawQuery("SELECT protein, kcal FROM " +
                    FoodTableName +
                    " WHERE _id = ?", new String[]{String.valueOf(fid)});

            if (cursor.moveToFirst()) {
                // 커서에서 프로틴과 칼로리 정보 가져오기
                protein = cursor.getInt(0);
                kcal = cursor.getInt(1);
            }

            cursor.close();

            TotalKcal += (count/100)*kcal;
            TotalProtein += (count/100)*protein;
        }
        db.close();



        kcaltext.setText(String.valueOf(TotalKcal));
        proteintext.setText(String.valueOf(TotalProtein));
    }


    public void countUpdate(FoodItem clickeditem, int intValue){
        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        String foodName = clickeditem.getName();

        int foodId = -1;

        if (database != null) {
            String query = "SELECT _id FROM " + FoodTableName + " WHERE food = ?";
            String[] selectionArgs = {foodName};
            Cursor cursor = database.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("_id");
                foodId = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        database.close();


        int Year = datePicker.getYear();
        int Month = datePicker.getMonth()+1;
        int Day = datePicker.getDayOfMonth();

        database = dbhelper.getWritableDatabase();

        if(database != null) {
            ContentValues values = new ContentValues();
            values.put("count", intValue);

            // 해당 조건에 맞는 레코드의 count 값을 업데이트
            String whereClause = "fid = ? AND year = ? AND month = ? AND day = ?";
            String[] whereArgs = {String.valueOf(foodId), String.valueOf(Year), String.valueOf(Month), String.valueOf(Day)};

            database.update(FoodDataTableName, values, whereClause, whereArgs);
        }
        database.close();
    }


    public void updateListView() {
        dbhelper = new DataBaseHelper(getContext());
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        int selected_year = datePicker.getYear();
        int selected_month = datePicker.getMonth()+1;
        int selected_day = datePicker.getDayOfMonth();

        List<Long> fidList = new ArrayList<>();

        if (database != null) {
            try {
                // fooddatatable에서 현재 선택한 날짜와 동일한 year, month, day를 가진 레코드들의 fid를 찾음
                String query = "SELECT fid FROM " + FoodDataTableName + " WHERE year = ? AND month = ? AND day = ?";
                String[] selectionArgs = {String.valueOf(selected_year), String.valueOf(selected_month), String.valueOf(selected_day)};
                Cursor cursor = database.rawQuery(query, selectionArgs);

                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("fid");
                    long currentFid = cursor.getLong(columnIndex);
                    fidList.add(currentFid);
                }
                cursor.close();


                adapter = new FoodItemAdapter();
                for (long fid : fidList) {
                    String foodQuery = "SELECT food, kcal, protein, resid FROM " + FoodTableName + " WHERE _id = ?";
                    String[] foodSelectionArgs = {String.valueOf(fid)};
                    Cursor foodCursor = database.rawQuery(foodQuery, foodSelectionArgs);

                    if (foodCursor.moveToFirst()) {
                        String foodName = foodCursor.getString(0);
                        int kcal = foodCursor.getInt(1);
                        int protein = foodCursor.getInt(2);
                        int resid = foodCursor.getInt(3);
                        // 가져온 정보를 FoodItemAdapter에 추가
                        adapter.addItem(new FoodItem(foodName, kcal, protein, resid));
                        foodCursor.close();
                    }
                }

                ListView listView = findViewById(R.id.listview);
                listView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.close();
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
            FoodItemView view = new FoodItemView(getContext());

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
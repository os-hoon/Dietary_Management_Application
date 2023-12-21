package com.example.exam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

class DataBaseHelper extends SQLiteOpenHelper {

    public static String DataBaseName = "DB";
    public static String FoodTableName = "Food";
    public static String FoodDataTableName = "FoodData";

    // 생성자
    public DataBaseHelper(Context context) {
        super(context, DataBaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createFoodTable = "CREATE TABLE " + FoodTableName + "(_id integer PRIMARY KEY autoincrement, " +
                "food text, kcal integer, protein integer, resid integer)";

        String createFoodDataTable = "CREATE TABLE " + FoodDataTableName + "(_id integer PRIMARY KEY autoincrement, " +
                "year integer, month integer, day integer, fid integer, count integer DEFAULT 100," +
                "foreign key (fid) references " + FoodTableName + "(_id))";

        db.execSQL(createFoodTable);
        db.execSQL(createFoodDataTable);

        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FoodTableName);
        db.execSQL("DROP TABLE IF EXISTS " + FoodDataTableName);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Food 테이블 초기 데이터, execSQL을 통해서 INSERT sql문을 실행합니다. (과제용 임시 데이터)
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('사과', 52, 0, " + R.drawable.apple + ")");
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('바나나', 89, 1, " + R.drawable.banana + ")");
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('닭가슴살', 165, 30, " + R.drawable.chicken + ")");
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('보충제', 410, 80, " + R.drawable.supplement + ")");
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('샐러드', 80, 8, " + R.drawable.salad + ")");
        db.execSQL("INSERT INTO " + FoodTableName + " (food, kcal, protein, resid) VALUES ('소고기', 286, 26, " + R.drawable.beef + ")");




// FoodData 테이블 초기 데이터, execSQL을 통해서 INSERT sql문을 실행합니다. (과제용 임시 데
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','1', 1, 50)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','1', 2, 50)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','1', 4, 100)");

        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','13', 3, 300)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','13', 5, 100)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','13', 2, 100)");

        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','14', 3, 200)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','14', 5, 100)");

        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','15', 3, 300)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','15', 5, 100)");

        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','16', 6, 300)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','16', 5, 100)");
        db.execSQL("INSERT INTO FoodData (year, month, day, fid,count) VALUES ('2023', '12','16', 4, 100)");
    }

}
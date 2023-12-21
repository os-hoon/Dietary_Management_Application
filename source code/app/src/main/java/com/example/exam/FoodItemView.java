package com.example.exam;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodItemView extends LinearLayout{
    TextView textView;
    TextView textView2;
    TextView textView3;
    ImageView imageView;

    public FoodItemView(Context context) {
        super(context);
        init(context);
    }

    public FoodItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.food_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public void setKcal(int Kcal) {
        textView2.setText(String.valueOf(Kcal));
    }

    public void setProtein(int protein) {
        textView3.setText(String.valueOf(protein));
    }

    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }
}

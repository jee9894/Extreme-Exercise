package com.example.hyun.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter_dips extends PagerAdapter {

    private int[] images = {R.drawable.one, R.drawable.two, R.drawable.dips_guide, R.drawable.both, R.drawable.green, R.drawable.red};
    private String[] explain = {"센서박스를 실행합니다.", "우측 상단의 설정버튼을 눌러\n횟수와 운동간격시간을 설정합니다.", "왼쪽 : 흉근 상부        오른쪽 : 삼두근","두 LED가 5번 깜빡인 후 측정을 시작합니다.", "수행 신호", "원위치 신호"};
    private LayoutInflater inflater;
    private Context context;

    public Adapter_dips(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((View) o);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.slider, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        imageView.setImageResource(images[position]);
        textView.setText(explain[position]);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}

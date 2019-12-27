package com.example.hyun.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Db4Activity extends AppCompatActivity {
    private LineChart lineChart;

    TextView tvTotalTime_db;
    TextView tvValidTimes_db;
    TextView tvScore_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db4);

        tvTotalTime_db = (TextView) findViewById(R.id.totalTime_db);
        tvValidTimes_db = (TextView) findViewById(R.id.validTimes_db);
        tvScore_db = (TextView) findViewById(R.id.score_db);

        Intent intent6 = getIntent();
        String id = intent6.getExtras().getString("id");
        String kind = intent6.getExtras().getString("kind");
        String detail = intent6.getExtras().getString("detail");
        String set = intent6.getExtras().getString("set");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child(kind).child(detail).child(set);

        myRef.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String exData = dataSnapshot.getValue().toString();

                lineChart = (LineChart)findViewById(R.id.chart);

                StringTokenizer tokens = new StringTokenizer(exData, ",");
                List<Entry> entries = new ArrayList<>();
                for(int x = 1; tokens.hasMoreElements(); x++){
                    entries.add(new Entry(x, Integer.parseInt(tokens.nextToken())));
                }

                LineDataSet lineDataSet = new LineDataSet(entries, "측정값");
                lineDataSet.setLineWidth(2);
                lineDataSet.setCircleRadius(6);
                lineDataSet.setCircleColor(Color.parseColor("#FFFFFF"));
                lineDataSet.setCircleColorHole(Color.WHITE);
                lineDataSet.setColor(Color.parseColor("#FFFFFF"));
                lineDataSet.setDrawCircleHole(false);
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawHighlightIndicators(false);
                lineDataSet.setDrawValues(false);

                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);

                XAxis xAxis;
                xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.WHITE);

                YAxis yLAxis = lineChart.getAxisLeft();
                yLAxis.setTextColor(Color.WHITE);

                YAxis yRAxis = lineChart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
                yRAxis.setDrawGridLines(false);

                Description description = new Description();
                description.setText("");

                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.setBackgroundColor(Color.BLACK);
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDescription(description);
                lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("total time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();

                tvTotalTime_db.setText("■총 운동 시간 : " + value + "초");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("valid times").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();

                tvValidTimes_db.setText("■유효 횟수 : " + value + "회");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();

                tvScore_db.setText("■점수 : " + value + "점");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_result:
                Intent intent6 = getIntent();
                String id = intent6.getExtras().getString("id");

                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("id", id);

                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
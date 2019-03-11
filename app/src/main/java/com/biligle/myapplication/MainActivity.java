package com.biligle.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckedTextView;

import com.bs.shocklibrary.ShockLisener;
import com.bs.shocklibrary.ShockUtil;

public class MainActivity extends AppCompatActivity implements ShockLisener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ShockUtil shockUtil;
    private CheckedTextView checkedTextView1,checkedTextView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startShock();
    }

    private void initView() {
        checkedTextView1 = (CheckedTextView) findViewById(R.id.checkbox1);
        checkedTextView2 = (CheckedTextView) findViewById(R.id.checkbox2);
        checkedTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextView1.toggle();
                if (checkedTextView1.isChecked() && checkedTextView2.isChecked()) {
                    checkedTextView2.toggle();
                    shockUtil.setShockStyle(ShockUtil.SHAKE_SHOCK).shock(MainActivity.this);
                }
            }
        });
        checkedTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextView2.toggle();
                if (checkedTextView2.isChecked() && checkedTextView1.isChecked()) {
                    checkedTextView1.toggle();
                    shockUtil.setShockStyle(ShockUtil.VERSE_SHOCK).shock(MainActivity.this);
                }
            }
        });
    }

    /**
     * 搖一搖（翻一翻）
     */
    private void startShock() {
        shockUtil = ShockUtil.getInstance();
        shockUtil.setLisener(this);
//        shockUtil.setEffectTime(800).setShockStyle(ShockUtil.VERSE_SHOCK).shock(this);
        shockUtil.setShockStyle(ShockUtil.SHAKE_SHOCK).shock(this);

    }

    @Override
    public void onShockResult() {
//        ShockUtil.getInstance().openShock(true);
        startActivity(new Intent(this, LoginActivity.class));
    }
}

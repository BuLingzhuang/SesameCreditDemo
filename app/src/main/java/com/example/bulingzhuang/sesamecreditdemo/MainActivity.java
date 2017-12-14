package com.example.bulingzhuang.sesamecreditdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.bulingzhuang.sesamecreditdemo.custom.CreditView;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private CreditView mCreditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.et_num);
        mCreditView = (CreditView) findViewById(R.id.scv_content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCreditView.setCurrentNumAnim(300);
    }

    public void btnSubmit(View view) {
        if (!TextUtils.isEmpty(mEditText.getText().toString())) {
            Integer num = Integer.valueOf(mEditText.getText().toString());
            mCreditView.setCurrentNumAnim(num);
        }
    }
}

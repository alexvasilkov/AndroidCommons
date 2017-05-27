package com.alexvasilkov.android.commons.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alexvasilkov.android.commons.nav.Navigate;
import com.alexvasilkov.android.commons.ui.Views;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Views.find(this, R.id.test).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigate.from(MainActivity.this).external().email()
                                .toEmails("alexvasilkov@gmail.com")
                                .subject("Subject")
                                .body("Google")
                                .start();
                    }
                }
        );
    }

}

package com.rizwan.sigcrime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class StartActivity extends AppCompatActivity {

	AppCompatButton btnRegister, btnLogin, btnSkip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		btnRegister = findViewById(R.id.button);
		btnLogin =findViewById(R.id.button2);
		btnSkip =findViewById(R.id.buttonskip);

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, LoginActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});
	}
}
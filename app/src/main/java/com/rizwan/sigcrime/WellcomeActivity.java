package com.rizwan.sigcrime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class WellcomeActivity extends AppCompatActivity {

	AppCompatButton button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wellcome);

		PermissionListener permissionlistener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
				Toast.makeText(WellcomeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {
				Toast.makeText(WellcomeActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
			}
		};

		TedPermission.create()
				.setPermissionListener(permissionlistener)
				.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
				.check();

		button = findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(WellcomeActivity.this, StartActivity.class);
				startActivity(startIntent);
			}
		});
	}
}
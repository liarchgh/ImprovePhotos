package com.example.improvephotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}
	public void jump2Photos(View v) {
		Intent it = new Intent();
		it.setClass(Home.this, Photos.class);
		Home.this.startActivity(it);
	}
}

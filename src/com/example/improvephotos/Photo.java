package com.example.improvephotos;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Photo extends Activity {
	private static int ApplyPhotoPermission = 0;
	public final static int TakePhoto = 1;
	public final static int ChoosePhoto = 2;
	private ImageView photo;
	private EditText et2p;
	private LinearLayout llAll;
	private LinearLayout controlBar;
	private Bitmap photoBm;
	private Bitmap newPhotoBm;
	private Uri cameraUri;
	
	private float painPositionX = 0;
	private float painPositionY = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		photo = (ImageView)findViewById(R.id.photo);
		et2p = (EditText)findViewById(R.id.et2p);
		llAll = (LinearLayout)findViewById(R.id.llAll);
		controlBar = (LinearLayout)findViewById(R.id.controlBar);
		
		checkAndApplyPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

		photo.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                float[] photoMatrix = new float[10];
                photo.getImageMatrix().getValues(photoMatrix);

				float photoWidth = photo.getDrawable().getBounds().width();
                photoWidth = (int)(photoWidth * photoMatrix[0]);  
				float photoHeight = photo.getDrawable().getBounds().height();
                photoHeight = (int)(photoHeight * photoMatrix[4]);  

				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					painPositionX = (event.getX()-(photo.getWidth() - photoWidth)/2)/photoWidth;
					painPositionY = (event.getY()-(photo.getHeight() - photoHeight)/2)/photoHeight;
					pain2Photo(v);
				}

//				Dialog dl = new Dialog(Photo.this);
//				dl.setTitle(photoHeight+" "+photo.getScaleY()+" "+event.getY());
//				dl.show();
				
				return true;
			}
		});
		
		Intent recIt = getIntent();
		switch(recIt.getIntExtra("way", 5)) {
		case Photo.TakePhoto:
			takePhoto();
			break;
		case Photo.ChoosePhoto:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");//相片类型
			startActivityForResult(intent, Photo.ChoosePhoto);
			break;
		}
		controlBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case Photo.TakePhoto:
			showPhotoUri(cameraUri);
			break;
		case Photo.ChoosePhoto:
			showPhotoUri(data.getData());
			break;
		}
	}
	
	public void pain2Photo(View v) {
		newPhotoBm = Bitmap.createBitmap(photoBm.getWidth(), photoBm.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas tempCv = new Canvas(newPhotoBm);
		Paint tempPt = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		Rect src = new Rect(0, 0, newPhotoBm.getWidth(), newPhotoBm.getHeight());
		Rect dst = new Rect(0, 0, newPhotoBm.getWidth(), newPhotoBm.getHeight());
		tempCv.drawBitmap(photoBm, src, dst, tempPt);
		
		Paint textPt = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		textPt.setTextSize(20.0f);
		textPt.setTypeface(Typeface.DEFAULT_BOLD);
		textPt.setColor(Color.GREEN);
		
//		tempCv.drawText(et2p.getText().toString(), newPhotoBm.getWidth()/2-5, tempBm.getHeight()/2+5, textPt);
		tempCv.drawText(et2p.getText().toString(), painPositionX*newPhotoBm.getWidth(), painPositionY*newPhotoBm.getHeight(), textPt);
//		tempCv.drawText(et2p.getText().toString(), 0, 0, textPt);
		tempCv.save();
//		tempCv.restore();
		
		photo.setImageBitmap(newPhotoBm);
	}
	
	@SuppressLint("NewApi")
	private void checkAndApplyPermission(String pm) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			
			if (Photo.this.checkSelfPermission(pm)
					!= PackageManager.PERMISSION_GRANTED){
			   //权限还没有授予，需要在这里写申请权限的代码
				Photo.this.requestPermissions(
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
	                    ApplyPhotoPermission);
			}	
		}

	}
	
	private void showPhotoUri(Uri photoUri) {
		try {
			photoBm = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
			photo.setImageBitmap(photoBm);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("photo", "notFound");
			e.printStackTrace();
		}
	}
	
	private void takePhoto() {
		File photoFile = new File(Environment.getExternalStorageDirectory(), "123.jpg");
		if(photoFile.exists()) {
			photoFile.delete();
		}
		cameraUri = Uri.fromFile(photoFile);
		Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
		it.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		Photo.this.startActivityForResult(it, Photo.TakePhoto);
	}
	
	public void saveNewPhoto(View v) {
		Log.i("save photo", "begin");
		File newPhotoFile = new File(Environment.getExternalStorageDirectory(), "333.jpg");
		if(newPhotoFile.exists()) {
			newPhotoFile.delete();
		}
		try {
			newPhotoFile.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newPhotoFile));
			newPhotoBm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i("save photo", "over");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("save photo", "eee");
			e.printStackTrace();
		}
	}
}

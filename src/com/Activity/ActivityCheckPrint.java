package com.Activity;

import java.util.ArrayList;

import org.json.JSONObject;

import com.Adapter.GridAdapter;
import com.Bll.MinaSocket;
import com.Tool.Bimp;
import com.Tool.ImageItem;
import com.Tool.SPUtils;
import com.Tool.ToastUtil;
import com.muli_image_selector.onee.MultiImageSelector;
import com.muli_image_selector.onee.MultiImageSelectorActivity;

import Entity.CheckInfoEntity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityCheckPrint extends Activity{
	
	private static final int REQUEST_IMAGE = 2;
	private static final int PREVIEW=1;
	
	private ImageView back;
	private TextView report,nowpoint;
	private EditText checkinfo;
	private GridView gridview;
	private GridAdapter adapter;
	
	public static Handler mhander;
	
	
	private int foucsid;
	private String foucsname;
	
	private ArrayList<String> path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_checkprint);
		Intent intent=getIntent();
		foucsid=intent.getIntExtra("foucsid",-1);
		foucsname=intent.getStringExtra("foucsname");
		initView();
		setlisten();
		updateUI();
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		gridview.setAdapter(adapter);
		
	}
	private void updateUI() {
		// TODO Auto-generated method stub
		mhander=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					if((Boolean) msg.obj){
						ToastUtil.show(ActivityCheckPrint.this,"���ͳɹ�");
						finish();
					}else{
						ToastUtil.show(ActivityCheckPrint.this,"����ʧ��");
					}
				}
			}
		};
	}
	private void setlisten() {

		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		report.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkinfo.length()>0){
					CheckInfoEntity checkinfoentity=new CheckInfoEntity();
					checkinfoentity.setCheckInfo(checkinfo.getText().toString());
					checkinfoentity.setUserId((Integer) SPUtils.get(ActivityCheckPrint.this,"userId", -1));
					checkinfoentity.setFoucsId(foucsid);
					
					
					JSONObject json=new JSONObject();
					try {
						json=new CheckInfoEntity().ToJSON(30, checkinfoentity);
						MinaSocket.SendMessage(json);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					ToastUtil.show(ActivityCheckPrint.this,"�ȵ㲻��Ϊ��");
				}
				
				
				
			}
		});
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					MultiImageSelector.create(ActivityCheckPrint.this).origin(path)
			        .start(ActivityCheckPrint.this, REQUEST_IMAGE);
					
				} else {
					Intent intent = new Intent(ActivityCheckPrint.this,
							ActivityPreview.class);
					intent.putExtra("position",arg2);
					intent.putStringArrayListExtra("path", path);
					startActivityForResult(intent,PREVIEW);
				}
			}
		});
	}
	private void initView() {
		back=(ImageView)findViewById(R.id.back_image);
		report=(TextView)findViewById(R.id.report_text);
		nowpoint=(TextView) findViewById(R.id.nowpoint);
		checkinfo=(EditText) findViewById(R.id.checkinfo);
		gridview=(GridView) findViewById(R.id.noScrollgridview);
		
		nowpoint.setText(foucsname);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_IMAGE){
	        if(Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK){
	            // ��ȡ���ص�ͼƬ�б�
	        	Bimp.tempSelectBitmap.clear();
	            path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
	           for(int i=0;i<path.size();i++){
	        	   ImageItem takePhoto = new ImageItem();
  					takePhoto.setImagePath(path.get(i));
  					Bimp.tempSelectBitmap.add(takePhoto);
	           }
	           adapter.notifyDataSetChanged();
	        }
	    }
		if(requestCode == PREVIEW){
			if(resultCode==RESULT_OK){
				path=data.getStringArrayListExtra("path");
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
		
	}

}

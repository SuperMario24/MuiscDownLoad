package my.supermario.muiscdownload;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnDownload;
	private List<SongUrl> songUrls;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnDownload = (Button) findViewById(R.id.button1);
		songUrls = new ArrayList<SongUrl>();

		
		
		btnDownload.setOnClickListener(new OnClickListener() {
			//点击按钮后开始下载
			@Override
			public void onClick(View v) {
				LoadModel model = new LoadModel();
				model.loadMusic(new ICallback() {
					@Override
					public void onMusicLoaded(final List<SongUrl> songUrls) {
						/**
						 * 执行下载业务
						 */
						//点击时弹出对话框
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						
						//对话框的栏数，是个String数组
						String[] items = new String[songUrls.size()];
						for (int i = 0; i < items.length; i++) {
							//给各个条目栏加上每种版本的文件大小
							int size = songUrls.get(i).getFile_size();
							items[i] = Math.floor(size*100.0/1024/1024)/100.0+"M";
						}
						
						//没个条目的点击事件，必须为     DialogInterface.OnClickListener
						builder.setItems(items, new DialogInterface.OnClickListener() {
							
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SongUrl songUrl = songUrls.get(which);
								
								//启动Service下载，提高线程优先级
								Intent intent = new Intent(MainActivity.this,MyService.class);
								intent.putExtra("file_link", songUrl.getFile_link());
								startService(intent);
								
							}

						});
						
						//创建对话框(create)
						AlertDialog dialog = builder.create();
						dialog.show();
						
					}
				});
				
			}
		});
		






	}

	class LoadModel{

		public void loadMusic(final ICallback callback){
			AsyncTask<String, Void, List<SongUrl>> task = new AsyncTask<String, Void, List<SongUrl>>(){

				@Override
				protected List<SongUrl> doInBackground(String... params) {
					List<SongUrl> songUrls = new ArrayList<SongUrl>();
					try {
						//发送http get请求 返回接收到的响应输入流
						String str = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid=271832017&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
						URL url = new URL(str);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						InputStream is = conn.getInputStream();

						// 把输入流转化成字符串
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						StringBuffer sb = new StringBuffer();
						String line = null;
						while((line = br.readLine())!= null){
							sb.append(line);
						}
						String json = sb.toString();

						//解析JSON
						JSONObject obj = new JSONObject(json);// 创建JSON对象
						JSONArray urlAry = obj.getJSONObject("songurl").getJSONArray("url");
						for (int i = 0; i < urlAry.length(); i++) {
							JSONObject jsonObj = urlAry.getJSONObject(i);
							String file_link = jsonObj.getString("file_link");
							int file_size = jsonObj.getInt("file_size"); 
							SongUrl songUrl = new SongUrl(file_link, file_size);
							songUrls.add(songUrl);
						}
						Log.d("info", ""+songUrls.size());

					} catch (Exception e) {
						e.printStackTrace();
					}
					return songUrls;
				}

				@Override
				protected void onPostExecute(List<SongUrl> songUrls) {
					//解析完成后得到了封装了file_link,file_size的songUrl对象，执行回调
					callback.onMusicLoaded(songUrls);
				
				}

			};

			task.execute();
		}
	}


}

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
			//�����ť��ʼ����
			@Override
			public void onClick(View v) {
				LoadModel model = new LoadModel();
				model.loadMusic(new ICallback() {
					@Override
					public void onMusicLoaded(final List<SongUrl> songUrls) {
						/**
						 * ִ������ҵ��
						 */
						//���ʱ�����Ի���
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						
						//�Ի�����������Ǹ�String����
						String[] items = new String[songUrls.size()];
						for (int i = 0; i < items.length; i++) {
							//��������Ŀ������ÿ�ְ汾���ļ���С
							int size = songUrls.get(i).getFile_size();
							items[i] = Math.floor(size*100.0/1024/1024)/100.0+"M";
						}
						
						//û����Ŀ�ĵ���¼�������Ϊ     DialogInterface.OnClickListener
						builder.setItems(items, new DialogInterface.OnClickListener() {
							
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SongUrl songUrl = songUrls.get(which);
								
								//����Service���أ�����߳����ȼ�
								Intent intent = new Intent(MainActivity.this,MyService.class);
								intent.putExtra("file_link", songUrl.getFile_link());
								startService(intent);
								
							}

						});
						
						//�����Ի���(create)
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
						//����http get���� ���ؽ��յ�����Ӧ������
						String str = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid=271832017&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
						URL url = new URL(str);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						InputStream is = conn.getInputStream();

						// ��������ת�����ַ���
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						StringBuffer sb = new StringBuffer();
						String line = null;
						while((line = br.readLine())!= null){
							sb.append(line);
						}
						String json = sb.toString();

						//����JSON
						JSONObject obj = new JSONObject(json);// ����JSON����
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
					//������ɺ�õ��˷�װ��file_link,file_size��songUrl����ִ�лص�
					callback.onMusicLoaded(songUrls);
				
				}

			};

			task.execute();
		}
	}


}

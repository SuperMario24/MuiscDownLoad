package my.supermario.muiscdownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class MyService extends IntentService{

	private NotificationManager manager;
	private Builder builder;
	private static final int NOTIFICATION_ID = 100;

	public MyService() {
		super("download");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//����NotificationManager
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		builder = new Builder(this);

	}

	/**
	 * �ڹ����߳���ִ��
	 * ������startService����ʱ�������
	 * onHandleIntent�е�ҵ���߼���ӵ�
	 * ������еȴ�ִ�С����ֵ�������ʱ
	 * ��ִ�и÷����еĴ��롣
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			//��ȡActivity��������file_link
			String url = intent.getStringExtra("file_link");
			Log.i("info", "service--url:"+url);

			/**
			 * ִ������ҵ��
			 */
			//������Ҫд���Ŀ��File����
			//targetFile:   /mnt/sdcard/Music/xxx.mp3
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),url+".mp3");
			
			//����ļ�������
			if(file.exists()){
				return;
			}
			//�����Ŀ¼�����ڣ��򴴽�
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}

			//д���ļ�
			FileOutputStream fos = new FileOutputStream(file);
			//��ȡ�������ӵ�������
			URL url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			InputStream is = conn.getInputStream();

			//���ƣ������ļ������ֽ���
			byte[] buf = new byte[1024*200];
			int length = 0;
			while((length = is.read(buf)) != -1){
				fos.write(buf,0,length);
				fos.flush();
			}


			fos.close();

			clearNotification();
			//������ϣ�֪ͨ�û�
			setNotification();



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void clearNotification(){
		manager.cancel(NOTIFICATION_ID);
	}

	//�����������֪ͨ�û�����Ҫsdk��Ͱ汾Ϊ11
	private void setNotification() {
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("��������");
		builder.setContentText("�����������");
		builder.setTicker("�������");

		//֪ͨ(build)
		Notification  n = builder.build();
		manager.notify(NOTIFICATION_ID , n);

	}

}

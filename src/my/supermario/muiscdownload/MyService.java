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

		//创建NotificationManager
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		builder = new Builder(this);

	}

	/**
	 * 在工作线程中执行
	 * 当调用startService方法时，将会把
	 * onHandleIntent中的业务逻辑添加到
	 * 任务队列等待执行。当轮到该任务时
	 * 才执行该方法中的代码。
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			//获取Activity传过来的file_link
			String url = intent.getStringExtra("file_link");
			Log.i("info", "service--url:"+url);

			/**
			 * 执行下载业务
			 */
			//声明需要写入的目标File对象
			//targetFile:   /mnt/sdcard/Music/xxx.mp3
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),url+".mp3");
			
			//如果文件已下载
			if(file.exists()){
				return;
			}
			//如果父目录不存在，则创建
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}

			//写入文件
			FileOutputStream fos = new FileOutputStream(file);
			//获取下载链接的输入流
			URL url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			InputStream is = conn.getInputStream();

			//复制，下载文件，用字节流
			byte[] buf = new byte[1024*200];
			int length = 0;
			while((length = is.read(buf)) != -1){
				fos.write(buf,0,length);
				fos.flush();
			}


			fos.close();

			clearNotification();
			//下载完毕，通知用户
			setNotification();



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void clearNotification(){
		manager.cancel(NOTIFICATION_ID);
	}

	//设置下载完毕通知用户，需要sdk最低版本为11
	private void setNotification() {
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("歌曲下载");
		builder.setContentText("歌曲下载完成");
		builder.setTicker("下载完毕");

		//通知(build)
		Notification  n = builder.build();
		manager.notify(NOTIFICATION_ID , n);

	}

}

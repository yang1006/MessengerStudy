package yll.com.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by yll
 * 绑定远程service,发送消息并接收server返回的消息
 * */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Messenger clientMessenger;

    private Messenger replyMessenger = new Messenger(new ClientHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(), MessengerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private static class ClientHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstant.MSG_REPLY_FROM_SERVER:
                    String reply = msg.getData().getString("reply");
                    Log.e(TAG, reply);
                    break;
            }
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");
            clientMessenger = new Messenger(service);
            Message msg = Message.obtain();
            msg.what = MyConstant.MSG_FROM_CLIENT;
            Bundle bundle = new Bundle();
            bundle.putString("data", "Hi, I'm message from client.");
            msg.setData(bundle);
            msg.replyTo = replyMessenger;
            try {
                clientMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            clientMessenger = null;
        }
    };


    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}

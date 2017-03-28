package yll.com.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by yll on 17/3/28.
 * 服务端 接收client消息并返回处理消息
 */

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    private final Messenger messenger = new Messenger(new ServiceHandler());

    private static class ServiceHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstant.MSG_FROM_CLIENT:
                    String data = msg.getData().getString("data");
                    Log.e(TAG, data);
                    Messenger replyMessenger = msg.replyTo;
                    Message message = Message.obtain();
                    message.what = MyConstant.MSG_REPLY_FROM_SERVER;
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "I'm Server, I have received you message! ");
                    message.setData(bundle);
                    try {
                        replyMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}

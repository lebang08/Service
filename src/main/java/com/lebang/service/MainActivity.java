package com.lebang.service;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import com.lebang.*;

/**
 * 服务端，提供热点
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LeBangDebug";

    private String serverIPAddress = "";

    private static final String SERVER_URL = "192.168.43.228";
    private static final int SERVER_SOCKET_PORT = 7666;

    private DataInputStream mInput;
    private DataOutputStream mOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "class begin");

        receiveConnect();
    }


    /**
     * 提供热点
     */
    private void receiveConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "receive broadcast begin ");
                    DatagramSocket mSocket = new DatagramSocket(7996);
                    byte[] receiveBuf = new byte[1024];
                    DatagramPacket mPacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                    mSocket.receive(mPacket);
//                    Log.e(TAG, "receive broadcast finish , " + Arrays.toString(receiveBuf));
                    serverIPAddress = mPacket.getAddress().getHostAddress();
//                    Log.e(TAG, "receive broadcast packet = ip " + serverIPAddress);

                    startAllConnectSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startAllConnectSocket() {
        InetAddress serveraddr = null;
        Socket mSocketCommand = null;

        try {
            serveraddr = InetAddress.getByName(SERVER_URL);
            mSocketCommand = new Socket(serveraddr, SERVER_SOCKET_PORT);
//            if (null != mSocketMain) {
//                Log.d(TAG, "Connected to: " + mSocketMain.toString());
//                ConnectSocket connectSocket = new ConnectSocket(CommonParams.SERVER_SOCKET_NAME, mSocketMain);
//                connectSocket.startConmunication();
//            }

            mOutput = new DataOutputStream(mSocketCommand.getOutputStream());
            mInput = new DataInputStream(mSocketCommand.getInputStream());

            mOutput.writeUTF("Phone : hello Client , I am Server");
            Log.e(TAG, "Phone : " + mInput.readUTF());

        } catch (Exception ex) {
            Log.d(TAG, "start ConnectSocket fail");
            ex.printStackTrace();
        }

    }

}

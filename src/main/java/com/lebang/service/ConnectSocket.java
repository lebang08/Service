/******************************************************************************
 * Copyright 2017 The Baidu Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package com.lebang.service;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectSocket {

    private static final String TAG = "ConnectSocket";

    private static final String READ_THREAD_NAME = "ReadThread";
    private static final String WRITE_THREAD_NAME = "WriteThread";
    private static final String CONNECT_THREAD_NAME = "ConnectThread";

    private static final int MAX_BUFFER_BYTES = 4096;
    private static final int SEND_BUFFER_SIZE = 32 * 1024;
    private static final int RECEIVE_BUFFER_SIZE = 32 * 1024;
    private static final String BYTES_FORMAT_TYPE = "utf-8";

    private String connectSocketName = "ConnectSocket";

    private ReadThread mReadThread = null;
    private WriteThread mWriteThread = null;

    private Socket mSocket = null;
    private BufferedInputStream mInputStream = null;
    private BufferedOutputStream mOutputStream = null;
    private boolean isConnected = false;

    private static int TEST_MSG_NUM = 0;
    private static int TOTAL_TEST_MSG_NUM = 1;

    public static final int SLEEP_TIME_MS = 100;

    public ConnectSocket(String socketName, Socket socket) {
        connectSocketName = socketName;
        mSocket = socket;
    }

    public String getConnectSocketName() {
        return connectSocketName;
    }

    public void startConmunication() {
        Log.d(TAG, "Start Conmunication");
        if (!isConnected) {
            try {

                mSocket.setTcpNoDelay(true);
                mSocket.setSendBufferSize(SEND_BUFFER_SIZE);
                mSocket.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);

                mInputStream = new BufferedInputStream(mSocket.getInputStream());
                mOutputStream = new BufferedOutputStream(mSocket.getOutputStream());

                doShakeHands();
                afterShakeHands();
                isConnected = true;

            } catch (Exception e) {
                Log.e(TAG, "Start Conmunication Fail");
                e.printStackTrace();
            }
        }
    }

    public void stopConnunication() {
        Log.d(TAG, "Stop Conmunication");
        if (isConnected) {
            try {

                if (null != mSocket) {
                    mSocket.close();
                    mSocket = null;
                }
                if (null != mInputStream) {
                    mInputStream.close();
                    mInputStream = null;
                }
                if (null != mOutputStream) {
                    mOutputStream.close();
                    mOutputStream = null;
                }

                isConnected = false;

            } catch (Exception e) {
                Log.e(TAG, "Stop Conmunication Fail");
            }
        }
    }

    private void doShakeHands() {
        Log.d(TAG, "ConnectSocket do shake hands");
//        ConnectManager.getInstance().addConnectSocket(this);
    }

    private void afterShakeHands() {
        Log.d(TAG, "ConnectSocket after shake hands");
        if (connectSocketName.equals(CommonParams.SERVER_SOCKET_NAME)) {
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

//    public int writeData(CarlifeCmdMessage msg) {
//        try {
//            if (null != mOutputStream) {
//                dumpData("SEND CarlifeMsg CMD", msg);
//
//                if (EncryptSetupManager.getInstance().isEncryptEnable() && msg.getLength() > 0) {
//                    byte[] encryptData = mWriteAESManager.encrypt(msg.getData(), msg.getData().length);
//                    if (encryptData == null) {
//                        Log.e(TAG, "encrypt failed!");
//                        return -1;
//                    }
//                    msg.setLength(encryptData.length);
//                    mOutputStream.write(msg.toByteArray());
//                    mOutputStream.flush();
//                    if (msg.getLength() > 0) {
//                        mOutputStream.write(encryptData);
//                        mOutputStream.flush();
//                    }
//                } else {
//                    mOutputStream.write(msg.toByteArray());
//                    mOutputStream.flush();
//                    if (msg.getLength() > 0) {
//                        mOutputStream.write(msg.getData());
//                        mOutputStream.flush();
//                    }
//                }
//                return CommonParams.MSG_CMD_HEAD_SIZE_BYTE + msg.getLength();
//            } else {
//                Log.e(TAG, connectSocketName + " Send Data Fail, mOutputStream is null");
//                throw new IOException();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, connectSocketName + " IOException, Send Data Fail");
//            ConnectClient.getInstance().setIsConnected(false);
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    public static int writeData(CarlifeCmdMessage msg, BufferedOutputStream tmpOutputStream) {
//        try {
//            if (null != tmpOutputStream) {
//                dumpData("SEND CarlifeMsg CMD", msg);
//                tmpOutputStream.write(msg.toByteArray());
//                tmpOutputStream.flush();
//                Log.d(TAG, "After SEND CarlifeMsg");
//                if (msg.getLength() > 0) {
//                    tmpOutputStream.write(msg.getData());
//                    tmpOutputStream.flush();
//                }
//                return CommonParams.MSG_CMD_HEAD_SIZE_BYTE + msg.getLength();
//            } else {
//                Log.e(TAG, "Send Data Fail, mOutputStream is null");
//                throw new IOException();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "IOException, Send Data Fail");
//            ConnectClient.getInstance().setIsConnected(false);
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    public int writeData(byte[] buffer, int len) {
//        try {
//            if (null != mOutputStream) {
//                mOutputStream.write(buffer, 0, len);
//                mOutputStream.flush();
//                return len;
//            } else {
//                Log.e(TAG, connectSocketName + " Send Data Fail, mOutputStream is null");
//                throw new IOException();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, connectSocketName + " IOException, Send Data Fail");
//            ConnectClient.getInstance().setIsConnected(false);
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    private CarlifeCmdMessage readData() {
//        CarlifeCmdMessage carlifeMsg = new CarlifeCmdMessage(false);
//        try {
//            if (null != mInputStream) {
//                int cnt;
//                int r;
//                cnt = CommonParams.MSG_CMD_HEAD_SIZE_BYTE;
//                byte[] headBuf = new byte[CommonParams.MSG_CMD_HEAD_SIZE_BYTE];
//                int headLen = 0;
//                while (cnt > 0) {
//                    r = mInputStream.read(headBuf, headLen, cnt);
//                    if (r > 0) {
//                        cnt -= r;
//                        headLen += r;
//                    } else {
//                        Log.e(TAG, connectSocketName + " Receive Carlife Msg Head Error: ret = " + r);
//                        throw new IOException();
//                        // return null;
//                    }
//                }
//                if (headLen == CommonParams.MSG_CMD_HEAD_SIZE_BYTE) {
//                    carlifeMsg.fromByteArray(headBuf);
//                } else {
//                    Log.e(TAG, connectSocketName + " Receive Carlife Msg Head Error: headLen = " + headLen);
//                    throw new IOException();
//                }
//                int len = carlifeMsg.getLength();
//                cnt = len;
//                byte[] dataBuf = new byte[len];
//                int dataLen = 0;
//                while (cnt > 0) {
//                    r = mInputStream.read(dataBuf, dataLen, cnt);
//                    if (r > 0) {
//                        cnt -= r;
//                        dataLen += r;
//                    } else {
//                        Log.e(TAG, connectSocketName + " Receive Carlife Msg Data Error: ret = " + r);
//                        throw new IOException();
//                        // return null;
//                    }
//                }
//                if (dataLen == len) {
//                    if (EncryptSetupManager.getInstance().isEncryptEnable() && dataLen > 0) {
//                        byte[] decryptData = mReadAESManager.decrypt(dataBuf, dataLen);
//                        if (decryptData == null) {
//                            Log.e(TAG, "decrypt failed!");
//                            return null;
//                        }
//                        carlifeMsg.setLength(decryptData.length);
//                        carlifeMsg.setData(decryptData);
//                    } else {
//                        carlifeMsg.setData(dataBuf);
//                    }
//                } else {
//                    Log.e(TAG, connectSocketName + " Receive Carlife Msg Data Error: dataLen = " + dataLen);
//                    throw new IOException();
//                }
//
//                dumpData("RECV CarlifeMsg CMD", carlifeMsg);
//
//            } else {
//                Log.e(TAG, connectSocketName + " Receive Data Fail, mInputStream is null");
//                throw new IOException();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, connectSocketName + " IOException, Receive Data Fail");
//            e.printStackTrace();
//            return null;
//        }
//        return carlifeMsg;
//    }

    public int readData(byte[] buffer, int len) {
        int r = -1;
        try {
            if (null != mInputStream) {
                int cnt;

                cnt = len;
                int dataLen = 0;
                while (cnt > 0) {
                    r = mInputStream.read(buffer, dataLen, cnt);
                    if (r > 0) {
                        cnt -= r;
                        dataLen += r;
                    } else {
                        Log.e(TAG, connectSocketName + " Receive Data Error: ret = " + r);
                        throw new IOException();
                        // return r;
                    }
                }
                if (dataLen == len) {
                    Log.v(TAG, "dataLen == len");
                } else {
                    Log.e(TAG, connectSocketName + " Receive Data Error: dataLen = " + dataLen);
                    throw new IOException();
                }
                return dataLen;
            } else {
                Log.e(TAG, connectSocketName + " Receive Data Fail, mInputStream is null");
                throw new IOException();
            }
        } catch (Exception e) {
            Log.e(TAG, connectSocketName + " IOException, Receive Data Fail");
//            ConnectClient.getInstance().setIsConnected(false);
            e.printStackTrace();
            return r;
        }
    }

//    private static void dumpData(String tag, CarlifeCmdMessage carlifeMsg) {
//        if (!CarlifeUtil.isDebug()) {
//            return;
//        }
//        String msg = "";
//        try {
//            msg += "index = " + Integer.toString(carlifeMsg.getIndex());
//            msg += ", length = " + Integer.toString(carlifeMsg.getLength());
//            msg += ", service_type = 0x" + DigitalTrans.algorismToHEXString(carlifeMsg.getServiceType(), 8);
//            msg += ", name = " + CommonParams.getMsgName(carlifeMsg.getServiceType());
//            Log.d(TAG, "[" + tag + "]" + msg);
//        } catch (Exception e) {
//            Log.e("TAG", "dumpData get Exception");
//            e.printStackTrace();
//        }
//    }

//    private static void dumpData(String tag, byte[] data, int len) {
//        if (!CarlifeUtil.isDebug()) {
//            return;
//        }
//        if (len < 4) {
//            return;
//        }
//        String msg = "";
//        int length = 0;
//        int serviceType = 0;
//        String name = null;
//        try {
//            length = (int) ByteConvert.bytesToInt(new byte[] {data[0], data[1], data[2], data[3]});
//            msg += "length = " + Integer.toString(length);
//
//            if (len >= 12) {
//                serviceType = (int) ByteConvert.bytesToInt(new byte[] {data[8], data[9], data[10], data[11]});
//                name = CommonParams.getMsgName(serviceType);
//                if (name == null) {
//                    return;
//                }
//                msg += ", service_type = 0x" + DigitalTrans.algorismToHEXString(serviceType, 8);
//                msg += ", name = " + name;
//            }
//            Log.d(TAG, "[" + tag + "]" + msg);
//        } catch (Exception e) {
//            Log.e("TAG", "dumpData get Exception");
//            e.printStackTrace();
//        }
//    }

    public BufferedInputStream getInputStream() {
        return mInputStream;
    }

    public BufferedOutputStream getOutputStream() {
        return mOutputStream;
    }

//    private CarlifeCmdMessage getTestCarlifeCmdMessage() {
//        if (TEST_MSG_NUM >= TOTAL_TEST_MSG_NUM) {
//            return null;
//        }
//        char c = (char) ('a' + (TEST_MSG_NUM % 26));
//
//        CarlifeCmdMessage tcarlifeMsg = new CarlifeCmdMessage(true);
//        String ts = "Msg Num:" + Integer.toString(TEST_MSG_NUM);
//        int len = MAX_BUFFER_BYTES - ts.length();
//        StringBuffer sb = new StringBuffer(ts);
//        for (int j = 0; j < len; ++j) {
//            sb.append(c);
//        }
//        try {
//            tcarlifeMsg.setData(sb.toString().getBytes(BYTES_FORMAT_TYPE));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return null;
//        }
//        tcarlifeMsg.setLength(MAX_BUFFER_BYTES);
//
//        ++TEST_MSG_NUM;
//        return tcarlifeMsg;
//    }

    private class ReadThread extends Thread {
        public ReadThread() {
            setName(READ_THREAD_NAME);
        }

        @Override
        public void run() {
            try {
                sleep(SLEEP_TIME_MS);

                while (isConnected) {
                    if (!mSocket.isConnected()) {
                        Log.e(TAG, "socket is disconnected when read data");
                        break;
                    }
//                    CarlifeCmdMessage carlifeMsg = readData();
//                    if (null != carlifeMsg) {
//                        MsgHandlerCenter.dispatchMessage(carlifeMsg.getServiceType(), 0, 0, carlifeMsg);
//                    } else {
//                        Log.e(TAG, "read carlife msg fail");
//                        break;
//                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "get InterruptedException in ReadThread");
                e.printStackTrace();
            } catch (Exception ex) {
                Log.e(TAG, "get Exception in ReadThread");
                ex.printStackTrace();
            }
        }
    }

    private class WriteThread extends Thread {
        public WriteThread() {
            setName(WRITE_THREAD_NAME);
        }

        @Override
        public void run() {
            try {
                while (isConnected) {
                    if (!mSocket.isConnected()) {
                        Log.e(TAG, "socket is disconnected when write data");
                        break;
                    }
//                    CarlifeCmdMessage carlifeMsg = getTestCarlifeCmdMessage();
//                    if (null != carlifeMsg) {
//                        writeData(carlifeMsg);
//                    } else {
//                        Log.e(TAG, "write carlife msg fail");
//                        break;
//                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "get Exception in WriteThread");
                ex.printStackTrace();
            }
        }
    }
}

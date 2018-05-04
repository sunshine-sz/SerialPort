package com.dfxh.wang.serialport_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import android_serialport_api.SerialPort;
import utils.CRC16;
import utils.FormatTransfer;
import utils.HexStringUtils;

public class UpdateDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvContent;
    private Button btQuery;
    private Button btReset;

    private SerialPort mSerialPort;
    public String m_strBaud = "";
    public String m_strPort = "";
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private boolean needRead = false;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private byte[] sendCommand = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device);
        initWidget();
        initSerialPort();
    }

    private void initWidget() {
        tvContent = (TextView) findViewById(R.id.tv_content);
        btQuery = (Button) findViewById(R.id.bt_query);
        btReset = (Button) findViewById(R.id.bt_reset);
        btQuery.setOnClickListener(this);
        btReset.setOnClickListener(this);
    }

    private void initSerialPort() {
        if (mSerialPort != null) {
            if (mReadThread != null) {
                mReadThread.interrupt();
            }
            if (mSendThread != null) {
                mSendThread.interrupt();
            }
            closeSerialPort();
        }

        try {

            mSerialPort = getSerialPor();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
//            mSendThread = new SendThread();
//            mSendThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_query:
                needRead = true;
                mReadThread = new ReadThread();
                mReadThread.start();
                sendCommand = new byte[]{0x02, 0x01, 0x00};
                new SendThread().start();
                break;

            case R.id.bt_reset:
                needRead = true;
                mReadThread = new ReadThread();
                mReadThread.start();
                sendCommand = new byte[]{0x05, 0x02, 0x00};
                new SendThread().start();
                break;
        }
    }


    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (needRead) {
                    if (mInputStream == null)
                        return;
//                    int count = 4;
//                    byte[] buffer = new byte[count];//4位帧控制,第4位是帧长度，
//                    Log.e("buffer", "侦控制" + HexStringUtils.toHexString(buffer));
//                    int availableCount = 0;
//                    int offset = 0;
                    byte[] bufferContent = new byte[16];//4位帧控制,第4位是帧长度，3位帧校验，其他为帧数据长度
                    if (mInputStream.available() >= 1) {
                        mInputStream.read(bufferContent, 0, 16);
                        onDataReceived(bufferContent, 16);
//                        needRead = false;
                    }
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * @param buffer
     * @param size
     */
    void onDataReceived(final byte[] buffer, final int size) {
        final String str = FormatTransfer.BytesToString(buffer, size);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.append(str + "\n");
            }
        });

        Log.e("#################", str);
    }


    //----------------------------------------------------
    private class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (sendCommand == null) {
                Log.e("UpdateDeviceActivity", "发送指令不能为空");
                return;
            }
            sendFilterData(sendCommand);
        }
    }

    public void sendFilterData(byte[] bCommand) {
        try {
            byte[] bOutArray = encodeBytes(bCommand);
            Log.e("sendData", "byte:" + HexStringUtils.toHexString(bOutArray));
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add by TOM for new chip
     * 编码要发送的字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] encodeBytes(byte[] bytes) {
        //计算校验码
        byte[] resCRC = CRC16.calcCRC(bytes);
        ArrayList<Byte> convertedBytes = new ArrayList<Byte>();
        //添加数据头
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0x00);
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0x00);
        convertedBytes.add((byte) 0xAA);
        //转义
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            if (b == 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == 0xA9) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x99);
            } else {
                convertedBytes.add(bytes[index]);
            }
        }

        for (int index = resCRC.length - 1; index >= 0; index--) {
            byte b = resCRC[index];
            if (b == 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == 0xA9) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x99);
            } else {
                convertedBytes.add(resCRC[index]);
            }
        }

        convertedBytes.add((byte) 0xAA);
        byte[] packetBytes = new byte[convertedBytes.size()];
        for (int i = 0; i < convertedBytes.size(); i++) {
            packetBytes[i] = convertedBytes.get(i);
        }
        return packetBytes;
    }


    public void closeSerialPort() {
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
    }

    private SerialPort getSerialPor() {
        if (this.mSerialPort == null) {
            String str = "/dev/ttyMT1";
//            int i = 115200;
            int i = 57600;
            this.m_strPort = str;
            this.m_strBaud = i + "";
            if ((str.length() == 0) || (i == -1))
                throw new InvalidParameterException();
            try {
                this.mSerialPort = new SerialPort(new File(str), i, 0);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return this.mSerialPort;
    }
}

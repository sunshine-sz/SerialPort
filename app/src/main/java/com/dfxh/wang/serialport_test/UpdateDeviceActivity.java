package com.dfxh.wang.serialport_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import android_serialport_api.SerialPort;
import utils.CRC16;
import utils.HexStringUtils;
import utils.SerialPortUtils;

public class UpdateDeviceActivity extends MPermissionsActivity implements View.OnClickListener, SerialPortUtils.OnDataReceiveListener {

    private TextView tvContent;
    private Button btQuery;
    private Button btReset;

    private SerialPort mSerialPort;
    public String m_strBaud = "";
    public String m_strPort = "";

    private byte[] sendCommand = null;
    private Button btRead;

    private byte progressCount = 0;
    boolean isFrist = true;
    private int sendCount;
    byte[] mFileBuffer = null;
    /**
     * 波特率
     */
    private int i = 115200;
    private ProgressBar progressBar;
    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_device);
        initWidget();
    }

//    private void initSerialPort() {
//        if (mSerialPort != null) {
//            if (mReadThread != null) {
//                mReadThread.interrupt();
//            }
//            if (mSendThread != null) {
//                mSendThread.interrupt();
//            }
//            closeSerialPort();
//        }
//
//        try {
//            mSerialPort = getSerialPor();
//            mOutputStream = mSerialPort.getOutputStream();
//            mInputStream = mSerialPort.getInputStream();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
    }

    @Override
    public void permissionFail(int requestCode) {
        super.permissionFail(requestCode);
        Toast.makeText(getApplicationContext(),"拒绝了权限，功能不完整",Toast.LENGTH_LONG).show();
    }

    private void initWidget() {
        serialPortUtils.setOnDataReceiveListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }
        tvContent = (TextView) findViewById(R.id.tv_content);
        btQuery = (Button) findViewById(R.id.bt_query);
        btReset = (Button) findViewById(R.id.bt_reset);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        btQuery.setOnClickListener(this);
        btReset.setOnClickListener(this);
        findViewById(R.id.bt_set).setOnClickListener(this);
        findViewById(R.id.bt_read).setOnClickListener(this);
        Spinner spinner = (Spinner) findViewById(R.id.sp_nner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.languages);
                tvContent.append("\n选择了波特率为:" + languages[pos]);
                i = Integer.parseInt(languages[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(6);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_query:
                if (serialPortUtils.serialPort==null){
                    SerialPort serialPort = serialPortUtils.openSerialPort(i);
                }
                progressCount++;
                sendCommand = new byte[]{0x16, progressCount, 0x00};
                byte[] bOutArray = encodeBytes(sendCommand);
                serialPortUtils.sendSerialPort(bOutArray);
                break;
            case R.id.bt_reset:
                if (serialPortUtils.serialPort==null){
                    SerialPort serialPort = serialPortUtils.openSerialPort(i);
                }
                startUpdate();
                break;
            case R.id.bt_read:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
                break;
            case R.id.bt_set:
                Intent intent2 = new Intent(this, SettingActivity.class);
                startActivityForResult(intent2, 3638);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3638) {
                if (data == null) return;
                int id = data.getIntExtra("id", 0);
                progressCount++;
                sendCommand = new byte[]{0x03, progressCount, 0x01,(byte) id};
                if (serialPortUtils.serialPort==null){
                    SerialPort serialPort = serialPortUtils.openSerialPort(i);
                }
                byte[] bOutArray = encodeBytes(sendCommand);
                serialPortUtils.sendSerialPort(bOutArray);
            }else {
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                    path = uri.getPath();
                    tvContent.append("\n文件路径："+path);
                    readFile(path);
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = getPath(this, uri);
                    tvContent.append("\n文件路径："+path);
                    readFile(path);
                } else {//4.4以下下系统调用方法
                    path = getRealPathFromURI(uri);
                    tvContent.append("\n文件路径："+path);
                    readFile(path);
                    //Toast.makeText(MainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 下发18指令
     */
    private void startUpdate() {
        if (serialPortUtils.serialPort==null){
            SerialPort serialPort = serialPortUtils.openSerialPort(i);
        }
        byte[] command = {0x18, progressCount, 0x03, 0x01};
        byte[] reResCRC = CRC16.calcReCRC(command, 0, command.length - 1);
        ArrayList<Byte> convertedBytes = new ArrayList<Byte>();
        for (int i = 0; i < command.length; i++) {
            convertedBytes.add(command[i]);
        }
        for (int index = 0; index < reResCRC.length; index++) {
            convertedBytes.add(reResCRC[index]);
        }
        sendCommand = new byte[convertedBytes.size()];
        for (int i = 0; i < convertedBytes.size(); i++) {
            sendCommand[i] = convertedBytes.get(i);
        }
        byte[] bOutArray = encodeBytes(sendCommand);
        serialPortUtils.sendSerialPort(bOutArray);
    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        String str = HexStringUtils.toHexString(buffer);
        Log.e("#################", str);
        Message message = new Message();
        message.what = 0;
        message.obj = str;
        handler.sendMessage(message);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        serialPortUtils.closeSerialPort();
    }


    int count = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (serialPortUtils.serialPort==null){
                serialPortUtils.openSerialPort(i);
            }
            switch (msg.what) {
                case 0:
                    String data = (String) msg.obj;
                    if (TextUtils.isEmpty(data)) return;
                    byte[] bytes = HexStringUtils.hexString2Bytes(data);
                    if (data.startsWith("AA16")) {
                        byte[] version = new byte[2];
                        System.arraycopy(bytes, 10, version, 0, version.length);
                        tvContent.append("\n当前软件版本:" + HexStringUtils.toHexString(version));
                    } else if (data.startsWith("AA18")) {
                        if (bytes[5] == 1) {
                            tvContent.append("\n启动应用层成功，下发重启指令");
                            progressCount++;
                            sendCommand = new byte[]{0x05, progressCount, 0x00};
                            byte[] bOutArray = encodeBytes(sendCommand);
                            serialPortUtils.sendSerialPort(bOutArray);

                        } else if (bytes[5] == 0) {
                            if (mFileBuffer == null) {
                                Toast.makeText(getApplicationContext(),"请先读取升级文件",Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (progressCount >= 255) {
                                progressCount = 0;
                            } else {
                                progressCount++;
                            }
                            sendCount = 0;
                            tvContent.append("\n启动boot成功，下发升级指令");
                            sendCommand = new byte[1031];
                            sendCommand[0] = (byte) 0xEE;
                            sendCommand[1] = progressCount;
                            sendCommand[2] = (byte) 0xFF;
                            sendCommand[3] = 0x00;
                            sendCommand[4] = 0x00;
                            sendCommand[5] = 0x00;
                            sendCommand[6] = (byte) sendCount;
                            System.arraycopy(mFileBuffer, count, sendCommand, 7, 1024);
                            count += 1024;
                            byte[] bOutArray = encodeBytes(sendCommand);
                            serialPortUtils.sendSerialPort(bOutArray);
                        }
                    } else if (data.startsWith("AA05")) {
                        if (bytes[4] == 0) {
                            tvContent.append("\n重启成功");
                        }
                        if (data.contains("AAFE")) {
                            isFrist = false;
                            progressCount++;
                            tvContent.append("\n执行下发boot启动");
                            startUpdate();
                        }
                    } else if (data.startsWith("AAEE")) {
                        progressBar.setProgress((int) ((count * 1f) / mFileBuffer.length * 100));
                        if (count >= mFileBuffer.length) {
                            Log.e("####", "更新完毕");
                            tvContent.append("\n更新完毕，下发结束指令");
                            progressCount++;
                            sendCommand = new byte[]{(byte) 0xE4, progressCount, 0x00};
                            byte[] bOutArray = encodeBytes(sendCommand);
                            serialPortUtils.sendSerialPort(bOutArray);
                        } else {
                            sendCount++;
                            sendCommand = new byte[1031];
                            sendCommand[0] = (byte) 0xEE;
                            sendCommand[1] = progressCount;
                            sendCommand[2] = (byte) 0xFF;
                            sendCommand[3] = 0x00;
                            sendCommand[4] = 0x00;
                            sendCommand[5] = 0x00;
                            sendCommand[6] = (byte) sendCount;
                            System.arraycopy(mFileBuffer, count, sendCommand, 7, 1024);
                            count += 1024;
                            byte[] bOutArray = encodeBytes(sendCommand);
                            serialPortUtils.sendSerialPort(bOutArray);
                        }

                    } else if (data.startsWith("AAFE")) {
                        if (isFrist) {
                            isFrist = false;
                            progressCount++;
                            tvContent.append("\n执行下发boot启动");
                            startUpdate();
                        }

                    } else if (data.startsWith("AA03")) {
                        tvContent.append("\n设置波特率成功");
                    }
                    break;
            }
        }
    };






    /**
     * add by TOM for new chip
     * 编码要发送的字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] encodeBytes(final byte[] bytes) {
        //计算校验码
        byte[] resCRC = CRC16.calcCRC(bytes);
        ArrayList<Byte> convertedBytes = new ArrayList<Byte>();
        //添加数据头
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0x00);
        convertedBytes.add((byte) 0x0F);
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0xAA);
        //转义
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            if (b == (byte) 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == (byte) 0xA9) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x99);
            } else {
                convertedBytes.add(bytes[index]);
            }
        }

        for (int index = resCRC.length - 1; index >= 0; index--) {
            byte b = resCRC[index];
            if (b == (byte) 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == (byte) 0xA9) {
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
//            int i = 57600;
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

    /**
     * 读取bin文件数据
     */
    private void readFile(String path) {
        // File mDir = Environment.getExternalStorageDirectory();
        //String filepath = mDir.getAbsolutePath() + File.separator + "upFile.bin";
        // Load binary file
        try {
            File f = new File(path);
            InputStream stream = new FileInputStream(f);
            mFileBuffer = new byte[(int) f.length()];
            stream.close();
        } catch (IOException e) {
            tvContent.append("\n File open failed: " + path + "\n");
        }

        try {
            // Read the file raw into a buffer
            InputStream stream;
            File f = new File(path);
            stream = new FileInputStream(f);
            stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
//            String bytesToString = FormatTransfer.BytesToString(mFileBuffer, mFileBuffer.length);
            tvContent.append("\n 升级文件大小:" + mFileBuffer.length + "字节");
        } catch (IOException e) {
            // Handle exceptions here
            tvContent.append("\n File open failed: " + path + "\n");
        }
    }



    String path;


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}

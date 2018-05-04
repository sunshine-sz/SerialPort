//package com.dfxh.wang.serialport_test;
//
//import android.annotation.SuppressLint;
//import android.content.ContentUris;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.FileDescriptor;
//import java.io.IOException;
//
//import android_serialport_api.SerialPort;
//import utils.SerialPortUtils;
//
//public class MainActivity extends AppCompatActivity {
//    private final String TAG = "MainActivity";
//
//    private Button button_open;
//    private Button button_close;
//    private EditText editText_send;
//    private Button button_send;
//    private TextView textView_status;
//    private Button button_status;
//    private Spinner spinner_one;
//
//    private SerialPortUtils serialPortUtils = new SerialPortUtils();
//    private SerialPort serialPort;
//
//    private Handler handler;
//    private byte[] mBuffer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        handler = new Handler(); //创建主线程的handler  用于更新UI
//
//        button_open = (Button) findViewById(R.id.button_open);
//        button_close = (Button) findViewById(R.id.button_close);
//        button_send = (Button) findViewById(R.id.button_send);
//        editText_send = (EditText) findViewById(R.id.editText_send);
//        textView_status = (TextView) findViewById(R.id.textView_status);
//        button_status = (Button) findViewById(R.id.button_status);
//        spinner_one = (Spinner) findViewById(R.id.spinner_one);
//        Button select = (Button) findViewById(R.id.button_select);
//
//        select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//无类型限制
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, 1);
//            }
//        });
//
//        editText_send.setText("S3");
//
//        button_open.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //serialPortUtils = new SerialPortUtils();
//                serialPort = serialPortUtils.openSerialPort();
//                if (serialPort == null) {
//                    Log.e(TAG, "串口打开失败");
//                    Toast.makeText(MainActivity.this, "串口打开失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                textView_status.setText("串口已打开");
//                Toast.makeText(MainActivity.this, "串口已打开", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        button_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                serialPortUtils.closeSerialPort();
//                textView_status.setText("串口已关闭");
//                Toast.makeText(MainActivity.this, "串口关闭成功", Toast.LENGTH_SHORT).show();
//            }
//        });
//        button_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                serialPortUtils.sendSerialPort(editText_send.getText().toString());
//                textView_status.setText("串口发送指令：" + serialPortUtils.data_);
//                Toast.makeText(MainActivity.this, "发送指令：" + editText_send.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        button_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //boolean status = serialPortUtils.serialPortStatus;
//                //textView_status.setText(String.valueOf(status));
//                FileDescriptor fileDescriptor = serialPort.mFd;
//                String result = fileDescriptor.toString();
//                textView_status.setText(result);
//            }
//        });
//        //串口数据监听事件
//        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
//            @Override
//            public void onDataReceive(byte[] buffer, int size) {
//                Log.d(TAG, "进入数据监听事件中。。。" + new String(buffer));
//                //
//                //在线程中直接操作UI会报异常：ViewRootImpl$CalledFromWrongThreadException
//                //解决方法：handler
//                //
//                mBuffer = buffer;
//                handler.post(runnable);
//            }
//
//            //开线程更新UI
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    textView_status.setText("size：" + String.valueOf(mBuffer.length) + "数据监听：" + new String(mBuffer));
//                }
//            };
//        });
//
//
////        //定义一个下拉列表适配器
////        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this,R.array.data,R.layout.support_simple_spinner_dropdown_item);
////        spinner_one.setAdapter(arrayAdapter); //将适配器传入spinner
////        //设置选中事件
////        spinner_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                //获取选中数据
////                String a = spinner_one.getSelectedItem().toString();
////                Toast.makeText(MainActivity.this,"选中了"+a,Toast.LENGTH_SHORT).show();
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> adapterView) {
////
////            }
////        });
//
//    }
//
//    String path;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
//                path = uri.getPath();
//                textView_status.setText(path);
//                Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
//                path = getPath(MainActivity.this, uri);
//                textView_status.setText(path);
//                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
//            } else {//4.4以下下系统调用方法
//                path = getRealPathFromURI(uri);
//                textView_status.setText(path);
//                Toast.makeText(MainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public String getRealPathFromURI(Uri contentUri) {
//        String res = null;
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        if(null!=cursor&&cursor.moveToFirst()){;
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            res = cursor.getString(column_index);
//            cursor.close();
//        }
//        return res;
//    }
//
//    /**
//     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
//     */
//    @SuppressLint("NewApi")
//    public String getPath(final Context context, final Uri uri) {
//
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[]{split[1]};
//
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    /**
//     * Get the value of the data column for this Uri. This is useful for
//     * MediaStore Uris, and other file-based ContentProviders.
//     *
//     * @param context       The context.
//     * @param uri           The Uri to query.
//     * @param selection     (Optional) Filter used in the query.
//     * @param selectionArgs (Optional) Selection arguments used in the query.
//     * @return The value of the _data column, which is typically a file path.
//     */
//    public String getDataColumn(Context context, Uri uri, String selection,
//                                String[] selectionArgs) {
//
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
//                    null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is ExternalStorageProvider.
//     */
//    public boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     */
//    public boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    public boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//}

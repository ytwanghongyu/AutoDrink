package com.example.drink;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.util.Log;
import android.text.Html;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import com.friendlyarm.FriendlyThings.HardwareControler;
import com.friendlyarm.FriendlyThings.BoardType;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    private Button ModeButton;
    private Button KeleButton;
    private Button XuebiButton;
    private Button FendaButton;
    private TextView ChoseText;

    // NanoPC-T4 UART4
    private String devName = "/dev/ttyAMA3";//ttyAMA3 UART3
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private TextView test_text;

    String  kele_str  = "1" ;//可乐发送的字符串
    String  xuebi_str = "2" ;//雪碧发送的字符串
    String  fenda_str = "3" ;//芬达发送的字符串
    String  init_str  = "b" ;//初始化发送的字符串
    String  success_init_str = "c";//初始化成功响应字符串



    private final int BUFSIZE = 512;
    private byte[] buf = new byte[BUFSIZE];
    private Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //串口开启
        devfd = HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
        //按钮Id | ButtonId
        ModeButton  =findViewById(R.id.mode_btn );
        XuebiButton =findViewById(R.id.xuebi_btn);
        KeleButton  =findViewById(R.id.kele_btn );
        FendaButton =findViewById(R.id.fenda_btn);
        ChoseText   =findViewById(R.id.chose_txt);


        
        
        //向MCU发送初始化请求
        //串口开启
        devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
        //串口写b 详见编码.pdf
        HardwareControler.write(devfd, init_str.getBytes());
        //关闭串口
        com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
/*      
        加了个显示时间的 看情况吧 之后加不加再说 咩咩！
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //获取系统时间
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);
        time2.setText("Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);
 */




        //死循环，进入10cm才能工作
        boolean unWorkPermission = true;
        //串口开启
        devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
        while(unWorkPermission){
            
            String WarningText = "请靠近至10cm内";
            ChoseText.setText(WarningText);
            if (HardwareControler.select(devfd, 0, 0) == 1) {       //判断是否有数据可读
                int retSize = HardwareControler.read(devfd, buf, BUFSIZE);    //读取数据（设置大，主要是保证读取完毕吧）；要读取的数据都是返回值，一般返回值都是函数运行结果的状态
                if (retSize > 0) {
                    String str = new String(buf, 0, retSize);
                    //在这写收到的判断
                    if( str.equals("5")){
                        unWorkPermission = false;
                        Toast.makeText( MainActivity.this,"已进入工作范围，请操作", Toast.LENGTH_SHORT).show();
                        //向MCU发送初始化成功响应
                        //串口写c 详见编码.pdf
                        HardwareControler.write(devfd, success_init_str.getBytes());
                        //关闭串口
                        com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
                    }
                }
            }
        }



        //维护模式btn
        ModeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
            }
        });
        //可乐btn
        KeleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //串口写1
                HardwareControler.write(devfd, kele_str.getBytes());
                Intent intent = new Intent(MainActivity.this, color.class);
                startActivity(intent);
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });
        //雪碧btn
        XuebiButton .setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //串口写2
                HardwareControler.write(devfd, xuebi_str.getBytes());
                Intent intent = new Intent(MainActivity.this, color.class);
                startActivity(intent);
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });
        //芬达btn
        FendaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //串口写3
                HardwareControler.write(devfd, fenda_str.getBytes());
                Intent intent = new Intent(MainActivity.this, color.class);
                startActivity(intent);
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });

    }
}
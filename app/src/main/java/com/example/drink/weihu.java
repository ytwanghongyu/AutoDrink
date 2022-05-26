package com.example.drink;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class weihu extends AppCompatActivity {
    private Button  exit;
    private Button  DistanceButton;
    private Button  ColorButton;
    private Button  ServoButton1;
    private Button  ServoButton2;
    private Button  ServoButton3;

    private TextView DistanceText;
    private TextView ColorText;
    // NanoPC-T4 UART4
    private String devName = "/dev/ttyAMA3";//ttyAMA3 UART3
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private  int state = 0;

    private final int BUFSIZE = 512;
    private byte[] buf = new byte[BUFSIZE];

    private Timer timer = new Timer();

    private TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (com.friendlyarm.FriendlyThings.HardwareControler.select(devfd, 0, 0) == 1) {       //判断是否有数据可读
                        int retSize = com.friendlyarm.FriendlyThings.HardwareControler.read(devfd, buf, BUFSIZE);    //读取数据；要读取的数据都是返回值，一般返回值都是函数运行结果的状态

                        if (retSize > 0) {
                            String str1 = new String(buf, 0, retSize);
                            //对传来的值进行判断
                            if(state == 0){
                                break;

                            }
                            else if (state == 1){
                                //获取字符串
                                String str_get = new String(buf, 0, retSize);
                                //str_decode:解码后的str
                                char[] str_dcd = new char[3];
                                //解码
                                str_get.getChars(2,5,str_dcd,0);
                                //char 转 string
                                String str_dis = String.valueOf(str_dcd);
                                str_dis = str_dis + "mm";
                                //显示
                                DistanceText.setText(str_dis);
                                state = 0;
                            }
                            else if (state == 2){
                                //获取字符串
                                String str_get = new String(buf, 0, retSize);
                                //str_decode:解码后的str
                                char[] str_dcdR = new char[3];
                                char[] str_dcdG = new char[3];
                                char[] str_dcdB = new char[3];
                                //解码
                                str_get.getChars(2,5,str_dcdR,0);
                                str_get.getChars(5,8,str_dcdG,0);
                                str_get.getChars(8,11,str_dcdB,0);
                                //char 转 string
                                String str_disR = String.valueOf(str_dcdR);
                                String str_disG = String.valueOf(str_dcdG);
                                String str_disB = String.valueOf(str_dcdB);
                                String str_disRGB = "R:" + str_disR + " | G:" + str_disG + " | B:" + str_disB;
                                //显示
                                ColorText.setText(str_disRGB);
                                state = 0;
                            }
                        }
                    }
                    break;
            }
            super.handleMessage(msg); // 帮助处理信息的一个类
        }
    };


    String  DistanceSendStr  = "6" ;//距离传感器测试发送的字符串
    String  ColorSendStr     = "7";//颜色传感器测试发送的字符串
    String  ServoSendStr1    = "8";//舵机1测试发送的字符串
    String  ServoSendStr2    = "9";//舵机2测试发送的字符串
    String  ServoSendStr3    = "a";//舵机3测试发送的字符串
    public static weihu instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weihu);
        //关闭其他页面
        if(MainActivity.instance!=null){
            MainActivity.instance.finish();
        }
        if(color.instance!=null){
            color.instance.finish();
        }
        if(login.instance!=null){
            login.instance.finish();
        }
        if(warning.instance!=null){
            warning.instance.finish();
        }

        exit=findViewById(R.id.exit);
        DistanceButton  =   findViewById(R.id.  distance_btn  );
        ColorButton     =   findViewById(R.id.  color_btn     );
        ServoButton1    =   findViewById(R.id.  servo_btn_1   );
        ServoButton2    =   findViewById(R.id.  servo_btn_2   );
        ServoButton3    =   findViewById(R.id.  servo_btn_3   );

        DistanceText    =   findViewById(R.id.  distance_txt  );
        ColorText       =   findViewById(R.id.  color_txt     );

        //串口开启
        devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(weihu.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 设备是否开启判别
        if (devfd >= 0) {
            timer.schedule(task, 0, 500);
        } else {
            devfd = -1;
            Toast.makeText(weihu.this,"Failed  to  open....",Toast.LENGTH_LONG).show();
        }


        //距离传感器测试btn
        DistanceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = DistanceSendStr;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                int ret= com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                //串口写
                //com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                if(ret>0){
                    DistanceText.setText("等待数据传输...");
                    state = 1;
                }
                else {
                    DistanceText.setText("串口发送失败");
                }
            }
        });

        //颜色传感器测试btn
        ColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ColorSendStr;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                int ret= com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                //串口写
                //com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                if(ret>0){
                    ColorText.setText("等待数据传输...");
                    state = 2;
                }
                else {
                    ColorText.setText("串口发送失败");
                }
            }
        });



        //舵机1测试btn
        ServoButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //关闭其他测试
                state   =   0;
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr1;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //串口写
                //com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
            }
        });

        //舵机2测试btn
        ServoButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //关闭其他测试
                state   =   0;
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr2;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //串口写
                //com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
            }
        });

        //舵机3测试btn
        ServoButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //关闭其他测试
                state   =   0;
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr3;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //串口写
                //com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
            }
        });
    }
}
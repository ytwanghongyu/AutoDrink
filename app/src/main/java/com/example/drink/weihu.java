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

    private Timer timer = new Timer();
    private final int BUFSIZE = 512;
    private byte[] buf = new byte[BUFSIZE];

    

    String  DistanceSendStr  = "6" ;//距离传感器测试发送的字符串
    String  ColorSendStr     = "7";//颜色传感器测试发送的字符串
    String  ServoSendStr1    = "8";//舵机1测试发送的字符串
    String  ServoSendStr2    = "9";//舵机2测试发送的字符串
    String  ServoSendStr3    = "a";//舵机3测试发送的字符串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weihu);
        exit=findViewById(R.id.exit);
        DistanceButton  =   findViewById(R.id.  distance_btn  );
        ColorButton     =   findViewById(R.id.  color_btn     );
        ServoButton1    =   findViewById(R.id.  servo_btn_1   );
        ServoButton2    =   findViewById(R.id.  servo_btn_2   );
        ServoButton3    =   findViewById(R.id.  servo_btn_3   );

        DistanceText    =   findViewById(R.id.  distance_txt  );
        ColorText       =   findViewById(R.id.  color_txt     );


        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(weihu.this, MainActivity.class);
                startActivity(intent);

            }
        });

        //距离传感器测试btn
        DistanceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //补换行符\n
                String str = DistanceSendStr;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                int ret= com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                if(ret>0){
                    DistanceText.setText("waiting for data...");
                    while(true){
                            //判断是否有数据可读
                        if (com.friendlyarm.FriendlyThings.HardwareControler.select(devfd, 0, 0) == 1) {
                            //串口开启
                            devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                            //串口接收
                            int retSize = com.friendlyarm.FriendlyThings.HardwareControler.read(devfd, buf, BUFSIZE);
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
                            //关闭串口
                            com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
                            break;
                        }
                    }
                }
            }
        });

        //颜色传感器测试btn
        ColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //补换行符\n
                String str = ColorSendStr;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                int ret= com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str.getBytes());
                if(ret>0){
                    DistanceText.setText("waiting for data...");
                    while(true){
                        //判断是否有数据可读
                        if (com.friendlyarm.FriendlyThings.HardwareControler.select(devfd, 0, 0) == 1) {
                            //串口开启
                            devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                            //串口接收
                            int retSize = com.friendlyarm.FriendlyThings.HardwareControler.read(devfd, buf, BUFSIZE);
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
                            String str_disRGB = "R:" + str_disR + "|G:" + str_disG + "|B:" + str_disB;
                            //显示
                            DistanceText.setText(str_disRGB);
                            //关闭串口
                            com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
                            break;
                        }
                    }
                }
            }
        });

        //舵机1测试btn
        ServoButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr1;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });

        //舵机2测试btn
        ServoButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr2;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });

        //舵机3测试btn
        ServoButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //串口开启
                devfd = com.friendlyarm.FriendlyThings.HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );
                //补换行符\n
                String str = ServoSendStr3;
                if (str.charAt(str.length()-1) != '\n') {
                    str = str + "\n";
                }
                //串口写
                com.friendlyarm.FriendlyThings.HardwareControler.write(devfd, str .getBytes());
                //关闭串口
                com.friendlyarm.FriendlyThings.HardwareControler.close(devfd);
            }
        });
    }
}
package com.example.drink;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Object;
import java.util.Timer;
import java.util.TimerTask;

import com.friendlyarm.FriendlyThings.HardwareControler;

public class color extends AppCompatActivity {

    private Button exit1;

    private int devfd = -1;
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


    // NanoPC-T4 UART4
    private String devName = "/dev/ttyAMA3";//ttyAMA3 UART3
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;

    private Handler handler = new Handler() {       
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (HardwareControler.select(devfd, 0, 0) == 1) {       //判断是否有数据可读
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);    //读取数据；要读取的数据都是返回值，一般返回值都是函数运行结果的状态

                        if (retSize > 0) {
                            String str = new String(buf, 0, retSize);
                            //对传来的值进行判断
                            

                            //可乐
                            if( str.equals("1")){
                                //提示成功
                                Toast.makeText( color.this,"成功识别，正在推出可乐，请稍候...", Toast.LENGTH_SHORT).show();
                                //延时1秒
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //返回主界面
                                Intent intent = new Intent(color.this, MainActivity.class);
                            }
                        }
                    }
                    break;
            }
            super.handleMessage(msg); // 帮助处理信息的一个类
        }
    };
    public static color instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        if(MainActivity.instance!=null){
            MainActivity.instance.finish();
        }
        if(login.instance!=null){
            login.instance.finish();
        }
        if(warning.instance!=null){
            warning.instance.finish();
        }
        if(weihu.instance!=null){
            weihu.instance.finish();
        }


        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);

        //串口开启
        devfd = HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );

        if (devfd >= 0) {
            timer.schedule(task, 0, 100);
        } else {
            devfd = -1;
            Toast.makeText(color.this,"Failed to open uart....",Toast.LENGTH_LONG).show();
        }

        //返回键
        exit1=findViewById(R.id.exit1);

        exit1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(color.this, MainActivity.class);
                startActivity(intent);

            }





        });
    }
}
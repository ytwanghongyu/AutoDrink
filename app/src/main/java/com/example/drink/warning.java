package com.example.drink;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.friendlyarm.FriendlyThings.HardwareControler;


public class warning extends AppCompatActivity {
    public static warning instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        if(MainActivity.instance!=null){
            MainActivity.instance.finish();
        }
        if(color.instance!=null){
            color.instance.finish();
        }
        if(login.instance!=null){
            login.instance.finish();
        }
        if(weihu.instance!=null){
            weihu.instance.finish();
        }

    }
}
package com.aayaz.aidlclientexample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aayaz.aidlexample.IAdditionService;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button buttonBind, buttonCalculate;
    private TextView textViewResult;

    private IAdditionService additionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonBind = findViewById(R.id.buttonBind);
        buttonCalculate = findViewById(R.id.buttonCalculate);

        textViewResult = findViewById(R.id.textViewResult);

        buttonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.aayaz.aidlexample.servie.AIDL");
                bindService(convertImplicitIntentToExplicitIntent(intent), serviceConnection, BIND_AUTO_CREATE);
            }
        });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int result = additionService.add(10, 200);
                    textViewResult.setText("" + result);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    textViewResult.setText(e.getMessage());
                }
            }
        });
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            additionService = IAdditionService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            additionService = null;
        }
    };

    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent) {

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(implicitIntent, 0);

        if (resolveInfos == null || resolveInfos.size() != 1) {
            return null;
        }

        ResolveInfo resolveInfo = resolveInfos.get(0);

        ComponentName component = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;

    }

}
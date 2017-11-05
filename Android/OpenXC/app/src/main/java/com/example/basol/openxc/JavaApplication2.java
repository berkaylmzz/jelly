package com.example.basol.openxc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class JavaApplication2 extends AsyncTask<Void, Void, TcpClient> {

    public String roadKM = null;
    String ACC_ave = null;
    String ENGI_ave = null;
    String odo = null;
    String fuel = null;
    public boolean check = false;
    TcpClient aClient;
    public String resp;
    Activity a;

    public JavaApplication2(Activity a) {
        this.a = a;
    }

    private static File getLatestFilefromDir(String dirPath)
    {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    public void appitself() throws FileNotFoundException,
            IOException
    {
        float acce_total = 0;
        float acce_count = 0;

        float engi_total = 0;
        float engi_count = 0;

        float odo_first = 0;
        float odo_last = 0;

        float fuel_first = 0;
        float fuel_last = 0;

        int flag_odo = 0;
        int flag_fuel = 0;

        int reverse_flag = 2;
        int reverse_odo_seen = 0;
        int reverse_fuel_seen = 0;

        try (BufferedReader br = new BufferedReader(
                new FileReader(("/storage/emulated/0/openxc-traces/driving.json"))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] stringValidTokens = line.split(",");

                //System.out.println(stringValidTokens[0]);

                if(stringValidTokens[0]
                        .equals("{\"name\":\"accelerator_pedal_position\""))
                {
                    String[] acc_split = stringValidTokens[1].split(":");

                    //System.out.println(acc_split[1]);

                    float result = Float.parseFloat(acc_split[1]);
                    acce_total = acce_total + result;
                    acce_count++;
                }
                else if(stringValidTokens[0]
                        .equals("{\"name\":\"engine_speed\""))
                {
                    String[] acc_split = stringValidTokens[1].split(":");

                    //System.out.println(acc_split[1]);

                    float result = Float.parseFloat(acc_split[1]);
                    engi_total = engi_total + result;
                    engi_count++;
                }
                else if(stringValidTokens[0]
                        .equals("{\"name\":\"odometer\"") && flag_odo == 0)
                {
                    flag_odo = 1;

                    String[] acc_split = stringValidTokens[1].split(":");

                    //System.out.println(acc_split[1]);

                    float result = Float.parseFloat(acc_split[1]);
                    odo_first = result;
                }
                else if(stringValidTokens[0]
                        .equals("{\"name\":\"fuel_consumed_since_restart\"")
                        && flag_fuel == 0)
                {
                    flag_fuel = 1;

                    String[] acc_split = stringValidTokens[1].split(":");

                    //System.out.println(acc_split[1]);

                    float result = Float.parseFloat(acc_split[1]);
                    fuel_first = result;
                }
            }
        }

        File file_reverse = new File("/storage/emulated/0/openxc-traces/driving.json");

        BufferedReader in = new BufferedReader(new InputStreamReader
                (new ReverseLineInputStream(file_reverse)));

        while(true)
        {
            String line = in.readLine();
            String[] stringValidTokens = line.split(",");

            if(stringValidTokens[0]
                    .equals("{\"name\":\"odometer\"") && reverse_flag != 0
                    && reverse_odo_seen == 0)
            {
                reverse_flag--;
                reverse_odo_seen = 1;

                String[] acc_split = stringValidTokens[1].split(":");

                //System.out.println(acc_split[1]);

                float result = Float.parseFloat(acc_split[1]);
                odo_last = result;
            }
            else if(stringValidTokens[0]
                    .equals("{\"name\":\"fuel_consumed_since_restart\"")
                    && reverse_flag != 0
                    && reverse_fuel_seen == 0)
            {
                reverse_flag--;
                reverse_fuel_seen = 1;

                String[] acc_split = stringValidTokens[1].split(":");

                //System.out.println(acc_split[1]);

                float result = Float.parseFloat(acc_split[1]);
                fuel_last = result;
            }
            else if (reverse_flag == 0) break;
        }

        Log.e("ACC_ave: " , String.valueOf(acce_total / acce_count));
        /*System.out.println("ENGI_ave: " + engi_total / engi_count);
        System.out.println("odo_first: " + odo_first);
        System.out.println("fuel_first: " + fuel_first);
        System.out.println("odo_last: " + odo_last);
        System.out.println("fuel_last: " + fuel_last);
        System.out.println("odo: " + (odo_last - odo_first));*/

        Log.e("fuel: " , String.valueOf(fuel_last - fuel_first));

        ACC_ave = Float.toString(acce_total / acce_count);
        ENGI_ave = Float.toString(engi_total / engi_count);
        odo = Float.toString(odo_last - odo_first);
        fuel = Float.toString(fuel_last - fuel_first);
        Log.e("odo: " ,odo);
        Log.e("ENGI_ave: " ,ENGI_ave);
        Log.e("roadKM: " ,roadKM);
        Log.e("fuel: " ,fuel);
        check = true;
        aClient.dataCheck = true;
        aClient.ACC = ACC_ave;
        aClient.ENGI = ENGI_ave;
        aClient.roadWent = roadKM;
        aClient.f = fuel;
        aClient.sendData();
    }

    @Override
    protected TcpClient doInBackground(Void... voids)
    {
        System.out.println();
        try {
            aClient = new TcpClient(roadKM,a);
            Log.e("road at a:", String.valueOf(roadKM));
            if(check)
            {
                aClient.dataCheck = true;
                aClient.ACC = ACC_ave;
                aClient.ENGI = ENGI_ave;
                aClient.roadWent = roadKM;
                aClient.f = fuel;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}
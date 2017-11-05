package com.example.basol.openxc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Basol on 11/4/2017.
 */

public class TcpClient
{
    public String response;
    String receivedSentence = null;
    boolean dataCheck = false;
    public String r = null;
    public String ACC = null;
    public String ENGI = null;
    public String roadWent = null;
    public String f = null;
    DataOutputStream outToServer;
    Socket clientSocket;
    Context context;

    //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    public TcpClient(String r, final Activity a) throws IOException
    {
        Log.e("inside of tcp", "const");
        this.r = r;
        clientSocket = new Socket("10.46.0.125", 2347);

        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader
                (clientSocket.getInputStream()));
        InputStream input = clientSocket.getInputStream();

        outToServer.writeBytes("1" + '\n');
        outToServer.flush();

        Log.e("FROM SERVER: 1", String.valueOf(r));
        outToServer.writeBytes(r + '\n');

        Log.e("Receive","Now");

        byte[] buffer = new byte[4];
        input.read(buffer,0,4);
        response = new String(buffer);

        a.runOnUiThread(new Runnable() {
            public void run() {
                    Toast.makeText(a, "Estimated consumption:"  + response, Toast.LENGTH_LONG).show();
            }
        });

        Log.e("FROM SERVER: 1", String.valueOf(response));


        /*receivedSentence = inFromServer.readLine();

        Log.e("FROM SERVER: 1", String.valueOf(receivedSentence));*/

    }

    public void sendData() {
        try {
            System.out.println("asd");
            outToServer.writeBytes(ACC + '\n');
            outToServer.flush();
            System.out.println("asd4");
            outToServer.writeBytes(ENGI + '\n');
            outToServer.flush();
            outToServer.writeBytes(f + '\n');
            outToServer.flush();

            System.out.println("asd2");
        } catch (IOException e) {
            System.out.println("asd3");
            e.printStackTrace();
        }

    }
}

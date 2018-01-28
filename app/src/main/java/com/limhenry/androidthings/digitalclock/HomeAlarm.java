package com.limhenry.androidthings.digitalclock;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HomeAlarm {

    private static final int NB_THREADS = 255;
    Context context;
    private ArrayList<NetworkItem> devicesAddress;
    private DatabaseReference mDatabase;
    private Handler scanNetworkHandler;
    private Runnable scanNetworkRunnable;

    public HomeAlarm(Context context) {
        this.context = context;
        setScanNetworkHandler();
    }

    private static ArrayList<String> getLinesInARPCache() {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public void setScanNetworkHandler() {
        if (scanNetworkRunnable != null) {
            scanNetworkHandler.removeCallbacks(scanNetworkRunnable);
        }
        scanNetworkHandler = new Handler();
        scanNetworkRunnable = new Runnable() {
            public void run() {
                doScan();
            }
        };
        scanNetworkHandler.postDelayed(scanNetworkRunnable, 30000);
    }

    public void doScan() {
        Log.i("Scan Network", "Start Scanning");
        devicesAddress = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
        for (int dest = 1; dest < 255; dest++) {
            String host = "10.42.0." + dest;
            Runnable worker = new pingRunnable(host);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
        }

        Log.i("Scan Network", "Finish Scan: " + devicesAddress.size());
        updateFirebase();
    }

    private void updateFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("connected");
        Long timestamp = System.currentTimeMillis() / 1000;
        ref.child("last_update").setValue(timestamp);
        for (NetworkItem item : devicesAddress) {
            ref.child("devices").child(item.getMac()).child("ip_address").setValue(item.getIp());
            ref.child("devices").child(item.getMac()).child("mac_address").setValue(item.getMac());
            ref.child("devices").child(item.getMac()).child("timestamp").setValue(timestamp);
        }
        setScanNetworkHandler();
    }

    public class NetworkItem {
        private String ip;
        private String mac;

        public NetworkItem(String ip, String mac) {
            this.ip = ip;
            this.mac = mac;
        }

        public String getIp() {
            return ip;
        }

        public String getMac() {
            return mac;
        }
    }

    public class pingRunnable implements Runnable {
        private final String address;

        pingRunnable(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            try {
                InetAddress inet = InetAddress.getByName(address);
                boolean reachable = inet.isReachable(3000);
                if (reachable) {
                    String ip = inet.getHostAddress();
                    for (String line : getLinesInARPCache()) {
                        String[] splitted = line.split(" +");
                        if (splitted.length >= 4 && ip.equals(splitted[0])) {
                            String mac = splitted[3];
                            if (mac.matches("..:..:..:..:..:..")) {
                                devicesAddress.add(new NetworkItem(address, mac));
                            }
                        }
                    }
                }
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            }
        }
    }
}
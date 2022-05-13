package vendetta.androidbenchmark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import benchmark.gpubenchmark.BenchmarkNcnn;

public class GPUActivity extends AppCompatActivity {

    private BenchmarkNcnn benchmarkncnn = new BenchmarkNcnn();

    private Spinner spinnerThreads;
    private Spinner spinnerPowersave;
    private Spinner spinnerMempool;
    private Spinner spinnerWinograd;
    private Spinner spinnerSGEMM;
    private Spinner spinnerPack4;
    private Spinner spinnerBF16s;
    private Spinner spinnerGPU;
    private Spinner spinnerGPUFP16p;
    private Spinner spinnerGPUFP16s;
    private Spinner spinnerGPUFP16a;
    private Spinner spinnerGPUPack8;
    private Spinner spinnerModel;
    private Spinner spinnerLoops;

    private TextView textviewMin;
    private TextView textviewMax;
    private TextView textviewAvg;

    private BenchmarkNcnn.Obj result;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpuactivity);

        boolean ret_init = benchmarkncnn.Init();
        if (!ret_init) {
            Log.e("MainActivity", "benchmarkncnn Init failed");
        }

        String platform = benchmarkncnn.GetPlatform();
        String ncnnversion = benchmarkncnn.GetNcnnVersion();
        setTitle("ncnn-" + ncnnversion + " " + platform);

        spinnerThreads = (Spinner) findViewById(R.id.spinnerThreads);
        spinnerPowersave = (Spinner) findViewById(R.id.spinnerPowersave);
        spinnerMempool = (Spinner) findViewById(R.id.spinnerMempool);
        spinnerWinograd = (Spinner) findViewById(R.id.spinnerWinograd);
        spinnerSGEMM = (Spinner) findViewById(R.id.spinnerSGEMM);
        spinnerPack4 = (Spinner) findViewById(R.id.spinnerPack4);
        spinnerBF16s = (Spinner) findViewById(R.id.spinnerBF16s);
        spinnerGPU = (Spinner) findViewById(R.id.spinnerGPU);
        spinnerGPUFP16p = (Spinner) findViewById(R.id.spinnerGPUFP16p);
        spinnerGPUFP16s = (Spinner) findViewById(R.id.spinnerGPUFP16s);
        spinnerGPUFP16a = (Spinner) findViewById(R.id.spinnerGPUFP16a);
        spinnerGPUPack8 = (Spinner) findViewById(R.id.spinnerGPUPack8);
        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerLoops = (Spinner) findViewById(R.id.spinnerLoops);

        textviewMin = (TextView) findViewById(R.id.textviewMin);
        textviewMax = (TextView) findViewById(R.id.textviewMax);
        textviewAvg = (TextView) findViewById(R.id.textviewAvg);

        // apply default settings
        spinnerThreads.setSelection(3);
        spinnerBF16s.setSelection(1);
        spinnerGPUPack8.setSelection(1);

        Button buttonRun = (Button) findViewById(R.id.buttonRun);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                new Thread(new Runnable() {
                    public void run() {

                        int threads = Integer.parseInt(spinnerThreads.getSelectedItem().toString());
                        int powersave = spinnerPowersave.getSelectedItem().toString().equals("All") ? 0 : spinnerPowersave.getSelectedItem().toString().equals("Little") ? 1 : 2;
                        boolean mempool = spinnerMempool.getSelectedItem().toString().equals("Yes");
                        boolean winograd = spinnerWinograd.getSelectedItem().toString().equals("Yes");
                        boolean sgemm = spinnerSGEMM.getSelectedItem().toString().equals("Yes");
                        boolean pack4 = spinnerPack4.getSelectedItem().toString().equals("Yes");
                        boolean bf16s = spinnerBF16s.getSelectedItem().toString().equals("Yes");
                        boolean gpu = spinnerGPU.getSelectedItem().toString().equals("Yes");
                        boolean gpufp16p = spinnerGPUFP16p.getSelectedItem().toString().equals("Yes");
                        boolean gpufp16s = spinnerGPUFP16s.getSelectedItem().toString().equals("Yes");
                        boolean gpufp16a = spinnerGPUFP16a.getSelectedItem().toString().equals("Yes");
                        boolean gpupack8 = spinnerGPUPack8.getSelectedItem().toString().equals("Yes");
                        int model = spinnerModel.getSelectedItemPosition();
                        int loops = Integer.parseInt(spinnerLoops.getSelectedItem().toString());

                        result = benchmarkncnn.Run(getAssets(), threads, powersave, mempool, winograd, sgemm, pack4, bf16s, gpu, gpufp16p, gpufp16s, gpufp16a, gpupack8, model, loops);

                        textviewMin.post(new Runnable() {
                            public void run() {

                                String errortext = "";
                                if (result.retcode == 1)
                                    errortext = "  no gpu";
                                if (result.retcode == 2)
                                    errortext = "  unknown model";
                                if (result.retcode == 3)
                                    errortext = "  unknown shape";

                                textviewMin.setText(result.retcode != 0 ? errortext : String.format("  %.2f", result.min));
                                textviewMax.setText(result.retcode != 0 ? errortext : String.format("  %.2f", result.max));
                                textviewAvg.setText(result.retcode != 0 ? errortext : String.format("  %.2f", result.avg));
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });
                    }
                }).start();
            }
        });

    }
}
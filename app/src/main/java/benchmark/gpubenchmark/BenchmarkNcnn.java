package benchmark.gpubenchmark;

import android.content.res.AssetManager;

public class BenchmarkNcnn {
    public native boolean Init();

    public native String GetPlatform();

    public native String GetNcnnVersion();

    public class Obj
    {
        // 0 = success
        // 1 = no gpu
        public int retcode;
        public float min;
        public float max;
        public float avg;
    }

    public native Obj Run(AssetManager mgr, int threads, int powersave,
                          boolean mempool, boolean winograd, boolean sgemm, boolean pack4, boolean bf16s,
                          boolean gpu, boolean gpufp16p, boolean gpufp16s, boolean gpufp16a, boolean gpupack8,
                          int model, int loops);

    static {
        System.loadLibrary("benchmarkncnn");
    }
}

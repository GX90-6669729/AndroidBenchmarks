package benchmark.cpubenchmark;

import android.os.Debug;

import benchmark.IBenchmark;
import database.Score;
import log.myTimeUnit;
import stopwatch.Timer;

/**
 * Created by Vendetta on 07-Mar-17.
 */

public class IntegerMathCPUBenchmark implements IBenchmark {
    private Long size = Long.MAX_VALUE;
    private volatile boolean shouldTestRun;
    private long result;
    private String extra;
    private long res;

    @Override
    public void initialize(){
        this.size = 20000000L;
        this.result = 0;
    }

    @Override
    public void initialize(Long size) {
        this.size = size;
        this.result = 0;
    }

    @Override
    public void warmup(){
        Long prevSize = this.size;
        this.size = 1000L;
        for (int i=1; i<=3; i*=10){
            compute();
            this.size *= 10;
        }
        this.size = prevSize;
    }

    @Override
    public void run(Object... param) {
        this.run();
    }

    @Override
    public void run() {
        final Timer timer = new Timer();
        this.warmup();
        timer.start();
        this.compute();
        this.result = timer.stop();
        this.memInfo();
    }

    @Override
    public long memInfo() {
        Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        res = memInfo.getTotalPrivateDirty();
        res += memInfo.getTotalPrivateClean();
        return res;
    }

    /**
     * Does a lot of fixed point operations.
     */
    @Override
    public void compute() {
        this.shouldTestRun = true;
        long result = 1L;
        for (long i = 0L; i < this.size && this.shouldTestRun; i++) {
            result += i / 256L;
            result *= i % 3L + 1L;
            result /= i % 2L + 1L;
        }
        this.shouldTestRun = false;
    }

    @Override
    public void stop() {
        this.shouldTestRun = false;
    }

    @Override
    public void clean() {}

    @Override
    public String getInfo(){
        return "IntegerBenchmark: Performs various arithmetic operations on Integers";
    }

    @Override
    public Score getScore() {
        return new Score(
                "IntegerBenchmark",
                Long.valueOf(myTimeUnit.convertTime(this.result, myTimeUnit.MilliSecond)).toString(),
                "Done 60 million integer arithmetic ops in "+myTimeUnit.convertTime(this.result, myTimeUnit.MilliSecond)+" ms "+ this.res + "kB. \n");
    }

}

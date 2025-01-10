package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for(int i = 0; i < 8; i += 1){
            SLList<Integer> slList = new SLList<>();
            Integer N = 1000*(1<<i);
            Integer ops = 10000;
            for(int j = 0; j < N; j += 1) slList.addLast(j);
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < ops; j += 1) slList.getLast();
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(N);times.addLast(timeInSeconds);opCounts.addLast(ops);
        }
        printTimingTable(Ns,times,opCounts);
    }

}

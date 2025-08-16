package src;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TerminateHandler implements Runnable {
    private int interval;
    private PeerAdmin peerAdmin;
    private Random random = new Random();
    private ScheduledFuture<?> job = null;
    private ScheduledExecutorService scheduler = null;

    public TerminateHandler(PeerAdmin peerAdmin) {
        this.peerAdmin = peerAdmin;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startJob(int timeInterval) {
        this.interval = timeInterval * 2;
        this.job = scheduler.scheduleAtFixedRate(this, 30, this.interval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            if (this.peerAdmin.checkIfDone()) {
                this.peerAdmin.closeHandlers();
                this.cancelJob();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelJob() {
        this.scheduler.shutdownNow();
    }
}

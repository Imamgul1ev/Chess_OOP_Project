package main;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessTimer extends AbstractTimer {
    private Timer timer;
    private Runnable onTimeUp;
    private Runnable onTick;

    public ChessTimer(int startSeconds, Runnable onTimeUp, Runnable onTick) {
        super(startSeconds); // initializes timeLeft and isRunning in AbstractTimer
        this.onTimeUp = onTimeUp;
        this.onTick = onTick;
        this.timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                }
                if (ChessTimer.this.onTick != null) ChessTimer.this.onTick.run();
                if (timeLeft == 0) {
                    timer.stop();
                    isRunning = false;
                    if (ChessTimer.this.onTimeUp != null) ChessTimer.this.onTimeUp.run();
                }
            }
        });
    }

    @Override
    public void start() {
        if (!isRunning && timeLeft > 0) {
            timer.start();
            isRunning = true;
        }
    }

    @Override
    public void stop() {
        timer.stop();
        isRunning = false;
    }

    public Runnable getOnTimeUp() {
        return onTimeUp;
    }
    public void setOnTimeUp(Runnable onTimeUp) {
        this.onTimeUp = onTimeUp;
    }

    public Runnable getOnTick() {
        return onTick;
    }
    public void setOnTick(Runnable onTick) {
        this.onTick = onTick;
    }
}

package main;

public abstract class AbstractTimer implements GameTimer {
    protected int timeLeft;
    protected boolean isRunning;

    public AbstractTimer(int startSeconds) {
        this.timeLeft = startSeconds;
        this.isRunning = false;
    }

    @Override
    public abstract void start();

    @Override
    public abstract void stop();

    @Override
    public int getTimeLeft() {
        return timeLeft;
    }

    @Override
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    @Override
    public String getTimeText() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

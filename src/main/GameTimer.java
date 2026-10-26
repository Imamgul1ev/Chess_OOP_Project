package main;

public interface GameTimer {

    void start();
    void stop();
    int getTimeLeft();
    void setTimeLeft(int timeLeft);
    boolean isRunning();
    void setRunning(boolean running);
    String getTimeText();

}

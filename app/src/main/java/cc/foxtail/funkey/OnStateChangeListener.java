package cc.foxtail.funkey;


public interface OnStateChangeListener {
    void onChanged(int state);
    void onReceived(byte[] protocol);
}

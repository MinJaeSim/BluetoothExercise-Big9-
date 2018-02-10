package cc.foxtail.funkey.util;


public class KalManFilter {
    private double Q = 0.00001;
    private double R = 0.001;
    private double X = 0, P = 1, K;

    public KalManFilter(double initValue) {
        X = initValue;
    }

    private void measurementUpdate(){
        K = (P + Q) / (P + Q + R);
        P = R * (P + Q) / (R + P + Q);

    }

    public double update(double measurement) {
        measurementUpdate();
        X = X + (measurement - X) * K;
        return X;
    }

}

package cc.foxtail.funkey.bluetooth;

import cc.foxtail.funkey.exercise.ExerciseModel;

public interface BluetoothContract {

    interface View {
        void printLog(String message);

        void showDialog(String message);

        void showResultWindow(int star, int score, int maxCount);

    }


    interface Presenter {
        void setView(View view);

        void connectBlueToothDevice(String address);

        void setModel(ExerciseModel exerciseModel);

        void sendProtocol(byte[] protocol);

        void disconnect();

        boolean isBluetoothServiceReady();
    }
}

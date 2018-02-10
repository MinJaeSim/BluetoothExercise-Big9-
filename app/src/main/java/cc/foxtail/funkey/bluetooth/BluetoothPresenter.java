package cc.foxtail.funkey.bluetooth;


import java.util.Objects;

import cc.foxtail.funkey.OnStateChangeListener;
import cc.foxtail.funkey.exercise.ExerciseFinishListener;
import cc.foxtail.funkey.exercise.ExerciseModel;
import cc.foxtail.funkey.util.ExerciseCountHelper;

import static cc.foxtail.funkey.data.ProtocolConstants.CONNECTION_TEST;

public class BluetoothPresenter implements BluetoothContract.Presenter {

    private BluetoothContract.View view;
    private BluetoothService bluetoothService;
    private ExerciseCountHelper exerciseCountHelper = new ExerciseCountHelper();
    private boolean readyToExercise = false;
    private ExerciseModel exerciseModel;


    @Override
    public void setView(BluetoothContract.View view) {
        this.view = view;
    }


    @Override
    public void connectBlueToothDevice(String address) {
        if (bluetoothService == null)
            bluetoothService = new BluetoothService(new OnStateChangeListener() {
                @Override
                public void onChanged(final int state) {
                    if (state == 3)
                        prepareToCommunication();
                    if (state == 4)
                        view.showDialog("기기에 연결이 되지 않았습니다.\n뒤로 돌아갑니다.");
                }

                @Override
                public void onReceived(byte[] protocol) {

                    if (Objects.equals(byteToHexCode(protocol), "6f 4f ")) {
                        readyToExercise = true;
                    }
                    if (exerciseModel != null) {
                        exerciseModel.countExercise(byteToHexCode(protocol));
                        exerciseCountHelper.checkProtocol(protocol);
                        setSensorData(protocol);
                    }


                    if (view != null) {
                        view.printLog(byteToHexCode(protocol));
                        exerciseCountHelper.checkProtocol(protocol);
                        setSensorData(protocol);
                    }
                }
            });
        bluetoothService.getDeviceInfo(address);

    }

    public String byteToHexCode(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);

        for (byte b : byteArray)
            sb.append(String.format("%02x ", b));

        return sb.toString();
    }

    @Override
    public void setModel(ExerciseModel exerciseModel) {
        this.exerciseModel = exerciseModel;
        this.exerciseModel.setFinishListener(new ExerciseFinishListener() {
            @Override
            public void finishExercise(int star, int score, int maxCount) {
                view.showResultWindow(star, score, maxCount);
            }
        });
        this.exerciseModel.setExerciseCountHelper(exerciseCountHelper);
    }

    public void startExercise() {
        exerciseModel.startExercise();
    }

    private void setSensorData(byte[] protocol) {
        int sensorData = (int) protocol[1];
        String header = String.format("%02x",protocol[0]);
        if (Objects.equals(header, "5a")) {
            exerciseCountHelper.setAngleZ(sensorData - 1);
        }
        if (Objects.equals(header, "59")) {
            exerciseCountHelper.setAngleX(sensorData);
        }
        if (Objects.equals(header, "58")) {
            exerciseCountHelper.setAngleY(-(sensorData - 2));
        }
        if (Objects.equals(header, "55")) {
            exerciseCountHelper.calculateVelocity(sensorData);
        }
    }

    @Override
    public void sendProtocol(byte[] protocol) {
        bluetoothService.write(protocol);
    }


    @Override
    public void disconnect() {
        bluetoothService.disconnect();
    }

    @Override
    public boolean isBluetoothServiceReady() {
        return bluetoothService != null;
    }

    public ExerciseCountHelper getExerciseCountHelper() {
        return this.exerciseCountHelper;
    }

    private void prepareToCommunication() {


        for (int i = 0; i < 1000; i++) {
            sendProtocol(CONNECTION_TEST);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;

            }
            if (readyToExercise)
                break;
        }
    }
}
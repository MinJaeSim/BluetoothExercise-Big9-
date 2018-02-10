package cc.foxtail.funkey.deviceConnect;

public interface DeviceConnectContract {

    interface View {
        void enableBlueTooth();

        void showProgressDialog(String text);

        void dismissProgressDialog();
    }

    interface Presenter {
        void setView(View view);

        void setAdapter(DeviceListAdapter pairedAdapter, DeviceListAdapter searchedAdapter);

        void findPairedDevice();

        void enableBlueTooth();

        void doDiscovery();

        void stopDiscovery();
    }
}

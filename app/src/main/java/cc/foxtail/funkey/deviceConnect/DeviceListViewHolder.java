package cc.foxtail.funkey.deviceConnect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import cc.foxtail.funkey.R;


public class DeviceListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView deviceNameTextView;
    private TextView deviceAddressTextView;

    public DeviceListViewHolder(View itemView) {
        super(itemView);

        deviceNameTextView = itemView.findViewById(R.id.device_name_text_view);
        deviceAddressTextView = itemView.findViewById(R.id.device_address_text_view);
    }

    @Override
    public void onClick(View v) {

    }

    public void bindDeviceInfo(String deviceInfo) {
        String[] deviceInfos = deviceInfo.split("\n");
        if(Objects.equals(deviceInfo, "검색된 기기가 없습니다.") || Objects.equals(deviceInfo, "패어링된 기기가 없습니다.")){
            deviceNameTextView.setText(deviceInfos[0]);
            deviceAddressTextView.setText("");
        }

        deviceNameTextView.setText(deviceInfos[0]);
        if (deviceInfos.length >= 2)
            deviceAddressTextView.setText(deviceInfos[1]);
    }
}

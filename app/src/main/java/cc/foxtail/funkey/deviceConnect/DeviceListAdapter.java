package cc.foxtail.funkey.deviceConnect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.OnDeviceClickListener;
import cc.foxtail.funkey.R;


public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListViewHolder> {
    private List<String> deviceList;

    private OnDeviceClickListener onDeviceClickListener;

    public DeviceListAdapter(OnDeviceClickListener onDeviceClickListener) {
        this.deviceList = new ArrayList<>();
        this.onDeviceClickListener = onDeviceClickListener;
    }

    @Override
    public DeviceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.list_item_new_device, parent, false);
        view.setOnClickListener(deviceClickListener);

        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceListViewHolder holder, int position) {
        String deviceInfo = deviceList.get(position);
        holder.bindDeviceInfo(deviceInfo);
    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void addDevice(String deviceInfo) {
        this.deviceList.add(deviceInfo);
    }

    public void clearDeviceList() {
        deviceList.clear();
        notifyDataSetChanged();
    }

    private View.OnClickListener deviceClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            TextView nameTextview = v.findViewById(R.id.device_name_text_view);
            TextView addressTextview = v.findViewById(R.id.device_address_text_view);

            String address = addressTextview.getText().toString();
//            String info = ((TextView) v).getText().toString();
//            String address = info.substring(info.length() - 17);

            if (onDeviceClickListener != null) {
                onDeviceClickListener.onClick(address);
            }
        }
    };

}

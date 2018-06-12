package com.github.ghostbusters.ghosthouse.home.fragments;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.db.Device;
import com.github.ghostbusters.ghosthouse.services.ServiceProvider;
import com.github.ghostbusters.ghosthouse.services.iotClient.IotClient;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> data;

    public void setDeviceList(final List<Device> devices) {
        if (data == null) {
            data = devices;
            notifyItemRangeInserted(0, data.size());
        } else {
            final List<Device> oldData = data;
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldData.size();
                }

                @Override
                public int getNewListSize() {
                    return devices.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldData.get(oldItemPosition).getDeviceId() ==
                            devices.get(newItemPosition).getDeviceId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldData.get(oldItemPosition).equals(devices.get(newItemPosition));
                }
            });
            data = devices;
            result.dispatchUpdatesTo(this);
        }
    }


    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_type1_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.ViewHolder holder, int position) {
        final Device device = data.get(position);
        holder.deviceNameTv.setText(device.getName());
        holder.aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            ServiceProvider.getIotClient()
                    .switchOn(holder.itemView.getContext(), device.getIp(), msg -> {},b?"ON":"OFF");
            Log.d(DeviceListAdapter.class.getSimpleName(), "checked changed: " + b);
            Log.d(DeviceListAdapter.class.getSimpleName(), device.toString());
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView deviceNameTv;
        final Switch aSwitch;

        ViewHolder(View v) {
            super(v);
            deviceNameTv = v.findViewById(R.id.device_name_tv);
            aSwitch = v.findViewById(R.id.device_sw);
        }
    }
}

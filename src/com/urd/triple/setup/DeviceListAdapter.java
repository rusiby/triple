package com.urd.triple.setup;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.R;

public class DeviceListAdapter extends BaseAdapter {

    private final List<BluetoothDevice> mDevices;

    public DeviceListAdapter() {
        mDevices = new ArrayList<BluetoothDevice>(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }
        ((TextView) convertView).setText(mDevices.get(position).getName());

        return convertView;
    }
}

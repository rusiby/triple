package com.urd.triple.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

public class BluetoothGameSocket extends GameSocket {
    private static final Logger LOG = LoggerFactory.getLogger(BluetoothGameSocket.class);

    private UUID mUUID;
    private BluetoothSocket mSocket;
    private ConnectTask mConnectTask;

    public BluetoothGameSocket(GameSocketListener listener, UUID uuid) {
        super(listener);

        mUUID = uuid;
    }

    public BluetoothGameSocket(GameSocketListener listener, BluetoothSocket socket) {
        super(listener);

        mSocket = socket;
        notifyConnected();
        start();
    }

    @Override
    public void close() {
        super.close();

        if (mConnectTask != null) {
            LOG.debug("cancel connect task.");

            mConnectTask.cancel(true);
            mConnectTask = null;
        }
        if (mSocket != null) {
            LOG.debug("close socket.");

            closeSocket(mSocket);
            mSocket = null;
        }
    }

    @Override
    public String getID() {
        String id = null;

        if (mSocket != null) {
            BluetoothDevice device = mSocket.getRemoteDevice();
            if (device != null) {
                id = device.getAddress();
            }
        }

        return id != null ? id : super.getID();
    }

    @Override
    public String getName() {
        String name = null;

        if (mSocket != null) {
            BluetoothDevice device = mSocket.getRemoteDevice();
            if (device != null) {
                name = device.getName();
            }
        }

        return name != null ? name : super.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mSocket == null) {
            throw new IOException();
        }

        return mSocket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mSocket == null) {
            throw new IOException();
        }

        return mSocket.getOutputStream();
    }

    public void connect(BluetoothDevice device) {
        assert (mSocket == null);
        assert (mConnectTask == null);

        LOG.info("connect {}({}) ...", device.getName(), device.getAddress());

        mConnectTask = new ConnectTask();
        mConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device);
    }

    public boolean isConnected() {
        return mSocket != null;
    }

    public boolean isConnecting() {
        return mConnectTask != null;
    }

    private static void closeSocket(BluetoothSocket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.warn("close failed.", e);
        }
    }

    private class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... params) {
            BluetoothSocket socket = null;

            try {
                socket = params[0].createInsecureRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                LOG.warn("create failed.", e);
            }

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                LOG.warn("connect failed. what={}", e.getMessage());

                closeSocket(socket);
                socket = null;
            }

            return socket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket result) {
            if (result != null) {
                if (!isCancelled()) {
                    LOG.info("connected.");

                    mSocket = result;
                    notifyConnected();
                    start();
                } else {
                    closeSocket(result);
                }
            } else {
                notifyConnectFailed();
            }
        }
    }
}

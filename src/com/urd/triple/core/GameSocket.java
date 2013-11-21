package com.urd.triple.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.gson.Gson;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.CommandFactory;

public abstract class GameSocket {
    private static final Logger LOG = LoggerFactory.getLogger(GameSocket.class);

    private GameSocketListener mListener;
    private Gson mGson;
    private SendTask mSendTask;
    private RecvThread mRecvThread;
    private Queue<Command> mCommands;

    public interface GameSocketListener {

        void onSocketConnect(GameSocket socket);

        void onSocketCommand(GameSocket socket, Command command);

        void onSocketError(GameSocket socket);
    }

    public GameSocket(GameSocketListener listener) {
        mListener = listener;

        mGson = new Gson();
        mCommands = new ConcurrentLinkedQueue<Command>();
    }

    public void send(Command command) {
        LOG.debug("send command. command={}", command);

        mCommands.add(command);
        if (mSendTask == null) {
            send();
        }
    }

    public void close() {
        LOG.info("close.");

        stop();
    }

    public boolean isConnected() {
        return mRecvThread != null;
    }

    public String getID() {
        return "UNKNOWN";
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    protected void start() {
        assert (mRecvThread == null);

        LOG.info("start.");

        mRecvThread = new RecvThread();
        mRecvThread.start();
    }

    protected void stop() {
        LOG.info("stop.");

        if (mSendTask != null) {
            mSendTask.cancel(true);
            mSendTask = null;
        }
        if (mRecvThread != null) {
            mRecvThread.cancel();
        }
    }

    protected void notifyConnected() {
        mListener.onSocketConnect(this);
    }

    protected void notifyConnectFailed() {
        mListener.onSocketError(this);
    }

    private void send() {
        LOG.debug("send. list.size={}", mCommands.size());

        mSendTask = new SendTask();
        mSendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SendTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;

            try {
                DataOutputStream output = new DataOutputStream(getOutputStream());
                Command command = null;
                while ((command = mCommands.poll()) != null) {
                    if (isCancelled()) {
                        break;
                    }

                    output.writeInt(command.getID());
                    output.writeUTF(mGson.toJson(command));
                }
                success = true;
            } catch (IOException e) {
                LOG.warn("write failed. what={}", e.getMessage());
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSendTask = null;

            if (!isCancelled()) {
                if (result) {
                    LOG.debug("write complete.");

                    if (!(mCommands.isEmpty())) {
                        send();
                    }
                } else {
                    mListener.onSocketError(GameSocket.this);
                }
            }
        }
    }

    private class RecvThread extends Thread {

        private final Handler mHandler;

        public RecvThread() {
            mHandler = new Handler();
        }

        @Override
        public void run() {
            LOG.info("recv begin.");

            try {
                DataInputStream input = new DataInputStream(getInputStream());
                while (true) {
                    final Command command = CommandFactory.getCommand(input.readInt(), input.readUTF());
                    synchronized (GameSocket.this) {
                        if (!isInterrupted()) {
                            LOG.debug("recv command. command={}", command);

                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    mListener.onSocketCommand(GameSocket.this, command);
                                }
                            });
                        } else {
                            LOG.debug("interrupted.");

                            break;
                        }
                    }
                }
            } catch (IOException e) {
                LOG.warn("read failed. what={}", e.getMessage());

                synchronized (GameSocket.this) {
                    if (!isInterrupted()) {
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                mListener.onSocketError(GameSocket.this);
                            }
                        });
                    }
                }
            }

            LOG.info("recv end.");
        }

        public void cancel() {
            LOG.info("cancel recv thread.");

            synchronized (GameSocket.this) {
                interrupt();
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    }
}

package com.urd.triple;

import java.util.Collection;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Player;

public class PlayerListActivity extends ListActivity {

    private PlayerListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PlayerListAdapter();
        setListAdapter(mAdapter);

        GameCore.getInstance().registerListener(mGameListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GameCore.getInstance().unregisterListener(mGameListener);
        GameCore.getInstance().close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (GameCore.getInstance().isMaster()) {
            getMenuInflater().inflate(R.menu.player_list, menu);
        }

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.start:
            if (isAllReady()) {
                Intent intent = new Intent();
                GameActivity.launch(this, intent);
            }
            break;

        default:
            return super.onMenuItemSelected(featureId, item);
        }

        return true;
    }

    private boolean isAllReady() {
        // TODO 是否满足开桌条件
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(PlayerListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private final GameListener mGameListener = new GameListener() {

        @Override
        public void onPlayerLogout(Player player) {
            mAdapter.update();
        }

        @Override
        public void onPlayerLogin(Player player) {
            mAdapter.update();
        }

        @Override
        public void onLoginSuccess(Collection<Player> players) {
        }

        @Override
        public void onLoginFailed(int errorCode) {
        }

        @Override
        public void onNetworkError() {
            showToast("网络错误.");
        }
    };
}

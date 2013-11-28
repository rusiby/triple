package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMananger {

    private static final int MAX_PLAYER = 8;

    private final List<Player> mPlayers;
    private final Map<String, Player> mPlayerMap;

    public PlayerMananger() {
        mPlayers = new ArrayList<Player>(MAX_PLAYER);
        mPlayerMap = new HashMap<String, Player>(MAX_PLAYER);
    }

    public Player add(Player player) {
        mPlayers.add(player);
        mPlayerMap.put(player.id, player);

        return player;
    }

    public void addAll(Collection<Player> players) {
        for (Player player : players) {
            add(player);
        }
    }

    public Player add(String id, String name) {
        return add(new Player(id, name));
    }

    public Player add(GameSocket socket, String id, String name) {
        Player player = add(id, name);
        player.socket = socket;

        return player;
    }

    public Player remove(Player player) {
        return remove(player.id);
    }

    public Player remove(String id) {
        Player player = mPlayerMap.get(id);
        if (player != null) {
            mPlayers.remove(player);
            mPlayerMap.remove(id);
        }

        return player;
    }

    public Collection<Player> remove(GameSocket socket) {
        Collection<Player> players = get(socket);
        for (Player player : players) {
            remove(player);
        }

        return players;
    }

    public void clear() {
        mPlayers.clear();
        mPlayerMap.clear();
    }

    public Player get(String id) {
        return mPlayerMap.get(id);
    }

    public Collection<Player> get(GameSocket socket) {
        List<Player> players = new ArrayList<Player>();
        for (Player player : mPlayers) {
            if (player.socket == socket) {
                players.add(player);
            }
        }

        return players;
    }

    public boolean contains(String id) {
        return mPlayerMap.containsKey(id);
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

    public boolean isAllPlayerHeroSelected() {
        int heroSelectedCount = 0;
        for (Player p : mPlayers) {
            if (p.hero != Hero.UNKNOWN) {
                heroSelectedCount++;
            }
        }

        return heroSelectedCount > 0 && heroSelectedCount == mPlayers.size();
    }

    public int size() {
        return mPlayers.size();
    }
}

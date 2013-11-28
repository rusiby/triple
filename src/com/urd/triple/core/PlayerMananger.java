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

    public Player add(GameSocket socket, String name) {
        Player player = add(socket.getID(), name);
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

    public Player remove(GameSocket socket) {
        return remove(socket.getID());
    }

    public void clear() {
        mPlayers.clear();
        mPlayerMap.clear();
    }

    public Player get(String id) {
        return mPlayerMap.get(id);
    }

    public Player get(GameSocket socket) {
        return get(socket.getID());
    }

    public boolean contains(String id) {
        return mPlayerMap.containsKey(id);
    }

    public boolean contains(GameSocket socket) {
        return contains(socket.getID());
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

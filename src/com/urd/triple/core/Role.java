package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.SparseArray;

public class Role {

    public static final int UNKNOWN = 0;
    public static final int LORD = 1; // 主公
    public static final int LOYALIST = 2; // 忠臣
    public static final int REBEL = 3; // 反贼
    public static final int TRAITOR = 4; // 内奸

    public static class Generator {

        LinkedList<Integer> mRoles;

        public Generator(int count) {
            mRoles = new LinkedList<Integer>(ROLE_MAP.get(count));
            Collections.shuffle(mRoles);
        }

        public int generate() {
            int role = -1;

            if (!(mRoles.isEmpty())) {
                role = mRoles.pop();
            }

            return role;
        }
    }

    public static String getName(int role) {
        String name = "";

        switch (role) {
        case Role.LORD:
            name = "主公";
            break;

        case Role.LOYALIST:
            name = "忠臣";
            break;

        case Role.REBEL:
            name = "反贼";
            break;

        case Role.TRAITOR:
            name = "内奸";
            break;

        default:
            break;
        }

        return name;
    }

    private static void add(List<Integer> list, int role, int count) {
        for (int i = 0; i < count; i++) {
            list.add(role);
        }
    }

    private static void add(SparseArray<List<Integer>> map, int loyalistCount, int rebelCount, int traitorCount) {
        List<Integer> list = new ArrayList<Integer>();
        add(list, LORD, 1);
        add(list, LOYALIST, loyalistCount);
        add(list, REBEL, rebelCount);
        add(list, TRAITOR, traitorCount);
        map.append(list.size(), list);
    }

    private static SparseArray<List<Integer>> ROLE_MAP;

    static {
        ROLE_MAP = new SparseArray<List<Integer>>();
        // TODO 测试完成之后删除 2个或3个玩家的情况
        add(ROLE_MAP, 0, 0, 1); // 2
        add(ROLE_MAP, 0, 1, 1); // 3
        add(ROLE_MAP, 1, 1, 1); // 4
        add(ROLE_MAP, 1, 2, 1); // 5
        add(ROLE_MAP, 1, 3, 1); // 6
        add(ROLE_MAP, 2, 3, 1); // 7
        add(ROLE_MAP, 2, 4, 1); // 8
    }
}

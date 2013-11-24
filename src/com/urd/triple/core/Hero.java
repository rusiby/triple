package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Hero {

    private static final int COUNT = 25;

    public static final int UNKNOWN = 0;

    public static class Generator {

        LinkedList<Integer> mHeroes;

        // 主公分配 3主公 + 2英雄
        public Generator() {
            // TODO: 更新实际的规则
            mHeroes = new LinkedList<Integer>();
            for (int i = 3; i < COUNT; i++) {
                mHeroes.add(i + 1);
            }
            Collections.shuffle(mHeroes);
            mHeroes.addFirst(2);
            mHeroes.addFirst(1);
            mHeroes.addFirst(0);
        }

        // 普通角色分配
        public Generator(int except) {
            this();

            Collections.shuffle(mHeroes);
            mHeroes.remove(Integer.valueOf(except));
        }

        public List<Integer> generate(int count) {
            List<Integer> heroes = new ArrayList<Integer>(count);
            for (int i = 0; i < count; i++) {
                heroes.add(mHeroes.poll());
            }

            return heroes;
        }
    }
}

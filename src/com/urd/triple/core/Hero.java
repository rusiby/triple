package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.SparseArray;

public class Hero {

    public static final int UNKNOWN = 0;

    public int id;
    public String name;
    public List<String> skills;

    public static class Generator {

        LinkedList<Integer> mHeroes;

        // 主公分配 3主公 + 2英雄
        public Generator() {
            mHeroes = new LinkedList<Integer>();
            for (Hero hero : getHeroes()) {
                mHeroes.add(hero.id);
            }
            Collections.shuffle(mHeroes.subList(3, mHeroes.size() - 1));
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

    public static Hero valueOf(int hero) {
        return HERO_MAP.get(hero);
    }

    public static List<Hero> getHeroes() {
        List<Hero> heroes = new ArrayList<Hero>();
        for (int i = 0; i < HERO_MAP.size(); i++) {
            heroes.add(HERO_MAP.valueAt(i));
        }

        return heroes;
    }

    private static void map(int hero, String name, String skill1, String skill2) {
        Hero h = new Hero();
        h.id = hero;
        h.name = name;
        h.skills = new ArrayList<String>();
        h.skills.add(skill1);
        if (skill2 != null) {
            h.skills.add(skill2);
        }
        HERO_MAP.append(hero, h);
    }

    private static final SparseArray<Hero> HERO_MAP;

    static {
        HERO_MAP = new SparseArray<Hero>();
        // TODO: 主公需放在前三个
        map(4000037, "曹操", "奸雄", "护驾");
        // TODO 张聪测试添加，需要裘伟定义后删除
        map(4000038, "刘备", "激将", "仁德");
        map(4000039, "孙权", "制衡", "好舒服啊");
        map(4000040, "郭嘉", "天妒", null);
        map(4000041, "香香", "结婚", null);
    }
}

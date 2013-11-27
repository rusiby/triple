package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

public class Hero {

    public static final int UNKNOWN = 0;

    public int id;
    public String name;
    public String pic;
    public String shortName;
    public String portraitPic;
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
        h.pic = String.format(Locale.US, "2%02d.png", hero % 100);
        int endIndex = name.length() > 2 ? 2 : name.length();
        h.shortName = name.substring(0, endIndex);
        h.portraitPic = String.format(Locale.US, "3%02d.jpg", hero % 100);
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
        map(201, "曹操", "奸雄", "护驾");
        map(210, "刘备", "仁德", "激将");
        map(216, "孙权", "制衡", "救援");

        map(202, "大乔", "国色", "流离");
        map(203, "貂蝉", "离间", "闭月");
        map(204, "甘宁", "奇袭", "null");
        map(205, "关羽", "武圣", "null");
        map(206, "郭嘉", "天妒", "遗计");
        map(207, "华佗", "急救", "青囊");
        map(208, "黄盖", "苦肉", "null");
        map(209, "黄月英", "集智", "奇才");
        map(211, "陆逊", "谦逊", "连营");
        map(212, "吕布", "无双", "null");
        map(213, "吕蒙", "克己", "null");
        map(214, "马超", "马术", "铁骑");
        map(215, "司马懿", "反馈", "鬼才");
        map(217, "孙尚香", "结姻", "萧姬");
        map(218, "夏侯惇", "刚烈", "null");
        map(219, "许诸", "裸衣", "null");
        map(220, "张飞", "咆哮", "null");
        map(221, "张辽", "突袭", "null");
        map(222, "赵云", "龙胆", "null");
        map(223, "甄姬", "倾国", "洛神");
        map(224, "周瑜", "英姿", "反间");
        map(225, "诸葛亮", "观星", "空城");
    }
}

package com.urd.triple.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

public class Card {

    public static final int UNKNOWN = 0;

    // 区域
    public static final int AREA_DECK = 0; // 牌堆
    public static final int AREA_DESK = 1; // 牌桌
    public static final int AREA_EQUIP = 2; // 装备区
    public static final int AREA_JUDGE = 3; // 判定区
    public static final int AREA_HAND = 4; // 手牌

    public static final int AREA_DECK_TOP = 5; // 牌堆顶
    public static final int AREA_DECK_BOTTOM = 6; // 牌堆底

    public static class Detail {

        public int id;
        public String name;
        public String fullname;

    }

    public int id;
    public int area;
    public transient Detail detail;

    public static Card find(List<Card> cards, int card) {
        Card result = null;

        for (Card c : cards) {
            if (c.id == card) {
                result = c;
                break;
            }
        }

        return result;
    }

    public static Card remove(List<Card> cards, int card) {
        Card result = find(cards, card);
        if (result != null) {
            cards.remove(result);
        }

        return result;
    }

    public static List<Card> getCards() {
        List<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < DETAIL_MAP.size(); i++) {
            cards.add(new Card(DETAIL_MAP.valueAt(i)));
        }

        return cards;
    }

    public static String calcSuit(int id) {
        int suit = (id / 100000) % 10;

        String strSuit = null;
        switch (suit) {
        case 1:
            strSuit = "♠";
            break;
        case 2:
            strSuit = "♣";
            break;
        case 3:
            strSuit = "♦";
            break;
        case 4:
            strSuit = "♥";
            break;
        default:
            break;
        }

        return strSuit;
    }

    public static String calcPoint(int id) {
        int point = (id / 1000) % 100;
        String strPoint = null;
        switch (point) {
        case 1:
            strPoint = "A";
            break;
        case 11:
            strPoint = "J";
            break;
        case 12:
            strPoint = "Q";
            break;
        case 13:
            strPoint = "K";
            break;
        default:
            strPoint = String.valueOf(point);
            break;
        }
        return strPoint;
    }

    public Card(int id) {
        this.id = id;
        this.area = AREA_DECK;
        this.detail = DETAIL_MAP.get(id);
    }

    public Card(int id, int area) {
        this.id = id;
        this.area = area;
        this.detail = DETAIL_MAP.get(id);
    }

    public Card(Detail detail) {
        this.id = detail.id;
        this.area = AREA_DECK;
        this.detail = detail;
    }

    private static void map(int card, String name) {
        Detail detail = new Detail();
        detail.id = card;
        detail.name = name;
        // TODO: 名称加入花色及点数

        detail.fullname = String.format(Locale.US, "%s %s", name, createSuit(card));

        DETAIL_MAP.append(card, detail);
    }

    private static String createSuit(int id) {
        String strSuit = calcSuit(id);

        String strPoint = calcPoint(id);

        return String.format(Locale.US, "%s %s", strSuit, strPoint);
    }

    private static final SparseArray<Detail> DETAIL_MAP;

    static {
        DETAIL_MAP = new SparseArray<Card.Detail>();

        map(1403001, "桃");
        map(1404001, "桃");
        map(1406001, "桃");
        map(1407001, "桃");
        map(1408001, "桃");
        map(1409001, "桃");
        map(1412001, "桃");
        map(1312001, "桃");
        map(1306002, "杀");
        map(1307002, "杀");
        map(1308002, "杀");
        map(1309002, "杀");
        map(1310002, "杀");
        map(1313002, "杀");
        map(1107002, "杀");
        map(1108002, "杀");
        map(1108102, "杀");
        map(1109002, "杀");
        map(1109102, "杀");
        map(1110002, "杀");
        map(1110102, "杀");
        map(1410002, "杀");
        map(1410102, "杀");
        map(1411002, "杀");
        map(1202002, "杀");
        map(1203002, "杀");
        map(1204002, "杀");
        map(1205002, "杀");
        map(1206002, "杀");
        map(1207002, "杀");
        map(1208002, "杀");
        map(1208102, "杀");
        map(1209002, "杀");
        map(1209102, "杀");
        map(1210002, "杀");
        map(1210102, "杀");
        map(1211002, "杀");
        map(1211102, "杀");
        map(1302003, "闪");
        map(1302103, "闪");
        map(1303003, "闪");
        map(1304003, "闪");
        map(1305003, "闪");
        map(1306003, "闪");
        map(1307003, "闪");
        map(1308003, "闪");
        map(1309003, "闪");
        map(1310003, "闪");
        map(1311003, "闪");
        map(1311103, "闪");
        map(1402003, "闪");
        map(1402103, "闪");
        map(1413003, "闪");
        map(1101004, "决斗");
        map(1201004, "决斗");
        map(1301004, "决斗");
        map(1103005, "过河拆桥");
        map(1104005, "过河拆桥");
        map(1112005, "过河拆桥");
        map(1412005, "过河拆桥");
        map(1203005, "过河拆桥");
        map(1204005, "过河拆桥");
        map(1303006, "顺手牵羊");
        map(1304006, "顺手牵羊");
        map(1103006, "顺手牵羊");
        map(1104006, "顺手牵羊");
        map(1111006, "顺手牵羊");
        map(1401007, "万箭齐发");
        map(1107008, "南蛮入侵");
        map(1113008, "南蛮入侵");
        map(1207008, "南蛮入侵");
        map(1401009, "桃园结义");
        map(1407010, "无中生有");
        map(1408010, "无中生有");
        map(1409010, "无中生有");
        map(1411010, "无中生有");
        map(1403011, "五谷丰登");
        map(1404011, "五谷丰登");
        map(1212012, "借刀杀人");
        map(1213012, "借刀杀人");
        map(1206013, "乐不思蜀");
        map(1406013, "乐不思蜀");
        map(1106013, "乐不思蜀");
        map(1101014, "闪电");
        map(1412014, "闪电");
        map(1312015, "无懈可击");
        map(1111015, "无懈可击");
        map(1113015, "无懈可击");
        map(1212015, "无懈可击");
        map(1213015, "无懈可击");
        map(1401015, "无懈可击");
        map(1413015, "无懈可击");
        map(1301016, "诸葛连弩");
        map(1201016, "诸葛连弩");
        map(1102017, "雌雄双股");
        map(1102018, "寒冰剑");
        map(1106019, "青钢剑");
        map(1105020, "青龙偃月刀");
        map(1112021, "丈八蛇矛");
        map(1305022, "贯石斧");
        map(1312023, "方天画戟");
        map(1405024, "麒麟弓");
        map(1202025, "八卦阵");
        map(1102025, "八卦阵");
        map(1202026, "仁王盾");
        map(1413027, "的卢+1");
        map(1205028, "绝影+1");
        map(1105029, "爪黄飞电+1");
        map(1405030, "赤兔-1");
        map(1313031, "大宛-1");
        map(1113032, "紫骍-1");
    }
}

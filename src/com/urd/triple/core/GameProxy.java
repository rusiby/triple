package com.urd.triple.core;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.urd.triple.core.commands.CardAction;
import com.urd.triple.core.commands.CardActionNotify;
import com.urd.triple.core.commands.ChangeHPNotify;
import com.urd.triple.core.commands.CleanDeskNotify;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.HeroListNotify;
import com.urd.triple.core.commands.RoleNotify;
import com.urd.triple.core.commands.SelectHeroNotify;
import com.urd.triple.core.commands.StartGameNotify;

public class GameProxy {
    private static final Logger LOG = LoggerFactory.getLogger(GameProxy.class);

    private PlayerMananger mPlayerMananger;
    private LinkedList<Card> mDeskCards;

    public GameProxy(PlayerMananger manager) {
        mPlayerMananger = manager;
        mDeskCards = new LinkedList<Card>();
    }

    public void execute(Command command) {
        switch (command.getID()) {
        case StartGameNotify.ID:
            onStartGameNotify((StartGameNotify) command);
            break;

        case HeroListNotify.ID:
            onHeroListNotify((HeroListNotify) command);
            break;

        case SelectHeroNotify.ID:
            onSelectHeroNotify((SelectHeroNotify) command);
            break;

        case CardActionNotify.ID:
            onCardActionNotify((CardActionNotify) command);
            break;

        case CleanDeskNotify.ID:
            onCleanDeskNotify();
            break;

        case ChangeHPNotify.ID:
            onChangeHPNotify((ChangeHPNotify) command);
            break;

        case RoleNotify.ID:
            onRoleNotify((RoleNotify) command);
            break;

        default:
            break;
        }
    }

    public void clear() {
        mDeskCards.clear();
    }

    public List<Card> getDeskCards() {
        return mDeskCards;
    }

    public Card getCard(int card, int area, Player player) {
        Card result = null;

        switch (area) {
        case Card.AREA_DECK:
        case Card.AREA_DECK_TOP:
        case Card.AREA_DECK_BOTTOM:
            result = new Card(card, Card.AREA_DECK);
            break;

        case Card.AREA_DESK:
            result = Card.find(mDeskCards, card);
            break;

        case Card.AREA_EQUIP:
        case Card.AREA_JUDGE:
        case Card.AREA_HAND:
            result = Card.find(player.cards, card);
            break;

        default:
            break;
        }

        return result;
    }

    private void onStartGameNotify(StartGameNotify notify) {
        mPlayerMananger.get(notify.dst__).role = notify.role;
        mPlayerMananger.get(notify.lordID).role = Role.LORD;
    }

    private void onHeroListNotify(HeroListNotify notify) {
        mPlayerMananger.get(notify.dst__).heroes = notify.heroes;
    }

    private void onSelectHeroNotify(SelectHeroNotify notify) {
        mPlayerMananger.get(notify.src).hero = notify.hero;
    }

    private void onCardActionNotify(CardActionNotify notify) {
        Player player = mPlayerMananger.get(notify.src);

        Card card = null;
        CardAction action = notify.action;
        switch (action.srcArea) {
        case Card.AREA_DECK:
            card = new Card(action.card);
            card.area = action.dstArea;
            switch (action.dstArea) {
            case Card.AREA_DESK:
                mDeskCards.add(card);
                break;

            case Card.AREA_HAND:
                player.cards.add(card);
                break;

            default:
                break;
            }
            break;

        case Card.AREA_DESK:
            card = Card.remove(mDeskCards, action.card);
            if (card != null) {
                card.area = Card.AREA_HAND;
                player.cards.add(card);
            } else {
                LOG.warn("card not exist. card={} area={}", action.card, action.srcArea);
            }
            break;

        case Card.AREA_EQUIP:
        case Card.AREA_JUDGE:
        case Card.AREA_HAND:
            Player target = null;
            if (action.target != null) {
                target = mPlayerMananger.get(action.target);
            }
            if (action.mode == CardAction.MODE_GET) {
                doCardAction(target, player, action);
            } else {
                doCardAction(player, target, action);
            }
            break;

        default:
            break;
        }
    }

    private void onCleanDeskNotify() {
        mDeskCards.clear();
    }

    private void onChangeHPNotify(ChangeHPNotify notify) {
        mPlayerMananger.get(notify.src).hp = notify.hp;
    }

    private void onRoleNotify(RoleNotify notify) {
        mPlayerMananger.get(notify.src).role = notify.role;
    }

    private void doCardAction(Player src, Player dst, CardAction action) {
        Card card = Card.find(src.cards, action.card);
        if (card != null) {
            if (card.area == action.srcArea) {
                src.cards.remove(card);
                card.area = action.dstArea;
                switch (action.dstArea) {
                case Card.AREA_DESK:
                    mDeskCards.add(card);
                    break;

                case Card.AREA_EQUIP:
                case Card.AREA_JUDGE:
                case Card.AREA_HAND:
                    dst.cards.add(card);
                    break;

                default:
                    break;
                }
            }
        } else {
            LOG.warn("card not exist. card={} area={}", action.card, action.srcArea);
        }
    }
}

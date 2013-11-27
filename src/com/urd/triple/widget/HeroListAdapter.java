package com.urd.triple.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.core.Hero;

public class HeroListAdapter extends BaseAdapter {

    private final List<Hero> mHeroes;

    public HeroListAdapter(Collection<Integer> heroes) {
        mHeroes = new ArrayList<Hero>();
        for (Integer hero : heroes) {
            mHeroes.add(Hero.valueOf(hero));
        }
    }

    @Override
    public int getCount() {
        return mHeroes.size();
    }

    @Override
    public Hero getItem(int position) {
        return mHeroes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mHeroes.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        }
        Hero hero = mHeroes.get(position);
        String text = String.format(Locale.US, "%s (%s)", hero.name, StringUtils.join(hero.skills, ","));
        ((TextView) convertView).setText(text);

        return convertView;
    }
}

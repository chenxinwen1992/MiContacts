package org.chenxinwen.micontacts.adapter;

/**
 * Created by chenxinwen on 16/8/9.11:00.
 * Email:191205292@qq.com
 */

import android.support.v7.widget.RecyclerView;

import org.chenxinwen.micontacts.bean.Contacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class ContactsListAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<Contacts> items = new ArrayList<Contacts>();

    public ContactsListAdapter() {
        setHasStableIds(true);
    }

    public void add(Contacts object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Contacts object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Contacts> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(Contacts... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public Contacts getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}


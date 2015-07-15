/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2.listbox;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicListBoxModel<T> extends AbstractListBoxModel<T> {
    private final List<T> dataList = new CopyOnWriteArrayList<T>();

    @Override
    public int size() {
        return dataList.size();
    }

    @Override
    public int indexOf(T item) {
        return dataList.indexOf(item);
    }

    @Override
    public T getItemAt(int index) {
        return dataList.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return dataList.iterator();
    }

    public void addItem(T item) {
        dataList.add(item);
        notifyDataChanged();
    }

    public void clear() {
        dataList.clear();
        notifyDataChanged();
    }
}

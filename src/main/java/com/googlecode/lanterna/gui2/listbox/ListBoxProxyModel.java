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

public class ListBoxProxyModel<T> extends AbstractListBoxModel<T> {
    private ListBoxModel<T> delegated;
    private final ListBoxModel.Observer delegatedObserver = new ListBoxModel.Observer() {
        @Override
        public void onDataChanged() {
            delegatedDataChanged();
            notifyDataChanged();
        }
    };

    protected void delegatedDataChanged() {}

    @Override
    public int size() {
        return delegated == null ? 0 : delegated.size();
    }

    @Override
    public int indexOf(T item) {
        return delegated == null ? -1 : delegated.indexOf(item);
    }

    @Override
    public T getItemAt(int index) {
        return delegated == null ? null : delegated.getItemAt(index);
    }

    @Override
    public Iterator<T> iterator() {
        return delegated == null ? ListBoxProxyModel.<T>emptyIterator() : delegated.iterator();
    }

    @SuppressWarnings("unchecked") // covariant cast is always safe
    public void setDelegated(ListBoxModel<? extends T> delegated) {
        if (this.delegated != null) {
            this.delegated.removeObserver(this.delegatedObserver);
        }
        this.delegated = (ListBoxModel<T>) delegated;
        if (this.delegated != null) {
            this.delegated.addObserver(this.delegatedObserver);
        }
        notifyDataChanged();
    }

    public ListBoxModel<T> getDelegated() {
        return delegated;
    }

    private static <T> Iterator<T> emptyIterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }
}

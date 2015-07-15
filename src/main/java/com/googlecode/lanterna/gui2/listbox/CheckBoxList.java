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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by martin on 29/09/14.
 */
public class CheckBoxList<V> extends AbstractListBox<V, CheckBoxList<V>> {
    private final Model<V> model = new Model<V>();

    public CheckBoxList() {
        this(null);
    }

    public CheckBoxList(TerminalSize preferredSize) {
        super(preferredSize);
        super.setDataModel(model);
    }

    @Override
    public synchronized void setDataModel(ListBoxModel<? extends V> model) {
        this.model.setDelegated(model);
    }

    @Override
    protected ListItemRenderer<V,CheckBoxList<V>> createDefaultListItemRenderer() {
        return new CheckBoxListItemRenderer<V>();
    }

    public synchronized Boolean isChecked(V object) {
        int itemIndex = model.indexOf(object);
        if (itemIndex == -1) {
            return null;
        } else {
            return model.isItemChecked(itemIndex);
        }
    }

    public synchronized Boolean isChecked(int index) {
        if(index < 0 || index >= model.size())
            return null;

        return model.isItemChecked(index);
    }

    public synchronized void setChecked(V object, boolean checked) {
        int itemIndex = indexOf(object);
        if (itemIndex != -1) {
            model.setItemChecked(itemIndex, checked);
        }
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ')) {
            if (model.isItemChecked(getSelectedIndex())) {
                model.setItemChecked(getSelectedIndex(), Boolean.FALSE);
            } else {
                model.setItemChecked(getSelectedIndex(), Boolean.TRUE);
            }
            return Result.HANDLED;
        } else {
            return super.handleKeyStroke(keyStroke);
        }
    }

    public static class CheckBoxListItemRenderer<V> extends ListItemRenderer<V,CheckBoxList<V>> {
        @Override
        protected int getHotSpotPositionOnLine(int selectedIndex) {
            return 1;
        }

        @Override
        protected String getLabel(CheckBoxList<V> listBox, int index, V item) {
            boolean itemChecked = listBox.model.isItemChecked(index);
            String checkMark = itemChecked ? "x" : " ";

            String text = item.toString();
            return "[" + checkMark + "] " + text;
        }
    }


    private static class Model<V> extends ListBoxProxyModel<V> {
        private final List<Boolean> itemStatus = new CopyOnWriteArrayList<Boolean>();

        public Boolean isItemChecked(V item) {
            return isItemChecked(indexOf(item));
        }

        @Override
        protected void delegatedDataChanged() {
            int newSize = size();
            for (int i = itemStatus.size(); i > newSize; --i) {
                itemStatus.remove(i);
            }
            super.delegatedDataChanged();
        }

        public Boolean isItemChecked(int index) {
            if (index < 0 || index >= itemStatus.size()) {
                return Boolean.FALSE;
            } else {
                return itemStatus.get(index);
            }
        }

        public void setItemChecked(int index, Boolean checked) {
            int oldStatusItemsSize = itemStatus.size();
            for (int i = oldStatusItemsSize; i <= index; ++i) {
                itemStatus.add(Boolean.FALSE);
            }
            itemStatus.set(index, checked);
            notifyDataChanged();
        }
    }
}

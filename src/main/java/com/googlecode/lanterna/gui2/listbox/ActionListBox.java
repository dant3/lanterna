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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Created by martin on 04/10/14.
 */
public class ActionListBox extends AbstractListBox<Runnable, ActionListBox> {
    private final BasicListBoxModel<Runnable> model = new BasicListBoxModel<Runnable>();

    public ActionListBox() {
        this(null);
    }

    public ActionListBox(TerminalSize preferredSize) {
        super(preferredSize);
        super.setDataModel(model);
    }

    @Override
    protected final synchronized void setDataModel(ListBoxModel<? extends Runnable> model) {
        throw new UnsupportedOperationException();
    }

    public void addItem(final String label, final Runnable action) {
        model.addItem(labeledAction(label, action));
    }

    public void clear() {
        model.clear();
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return null;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        Runnable selectedItem = getSelectedItem();
        if(selectedItem != null &&
                (keyStroke.getKeyType() == KeyType.Enter ||
                (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' '))) {

            selectedItem.run();
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }


    private static Runnable labeledAction(final String label, final Runnable action) {
        return new Runnable() {
            @Override
            public void run() {
                action.run();
            }

            @Override
            public String toString() {
                return label;
            }
        };
    }
}

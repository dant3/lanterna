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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author martin
 */
class VirtualTerminalImplementation extends AbstractTerminal implements IOSafeTerminal {
    
    interface DeviceEmulator {

        public KeyStroke readInput();

        public void enterPrivateMode();

        public void exitPrivateMode();

        public TextBuffer getBuffer();

        public void setCursorVisible(boolean visible);
        
        public void flush();

        public byte[] equireTerminal();
        
    }
    
    private final DeviceEmulator deviceEmulator;
    private TerminalSize terminalSize;
    private TerminalPosition currentPosition;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final EnumSet<SGR> activeSGRs;

    public VirtualTerminalImplementation(DeviceEmulator deviceEmulator, TerminalSize initialSize) {
        this.deviceEmulator = deviceEmulator;
        this.terminalSize = initialSize;
        this.currentPosition = TerminalPosition.TOP_LEFT_CORNER;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.activeSGRs = EnumSet.noneOf(SGR.class);
        
        //Initialize lastKnownSize in AbstractTerminal
        onResized(terminalSize.getColumns(), terminalSize.getRows());
    }

    ///////////
    // Now implement all Terminal-related methods
    ///////////
    @Override
    public KeyStroke readInput() throws IOException {
        return deviceEmulator.readInput();
    }

    @Override
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
    }

    @Override
    public void enterPrivateMode() {
        deviceEmulator.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        deviceEmulator.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        deviceEmulator.getBuffer().fillScreen(getTerminalSize(), TerminalCharacter.DEFAULT_CHARACTER);
    }
    
    private void advanceCursor() {
        int x = currentPosition.getColumn();
        int y = currentPosition.getRow();
        TerminalSize size = getTerminalSize();
        if(x < 0) {
            x = 0;
        }
        x++;
        if(x >= size.getColumns()) {
            x = 0;
            y++;
        }
        if(y < 0) {
            y = 0;
        }
        else if(y >= size.getRows()) {
            y = size.getRows() - 1;
        }
        currentPosition = currentPosition.withColumn(x).withRow(y);
    }

    @Override
    public void moveCursor(int x, int y) {
        TerminalSize size = getTerminalSize();
        if(x < 0) {
            x = 0;
        }
        else if(x >= size.getColumns()) {
            x = size.getColumns() - 1;
        }
        if(y < 0) {
            y = 0;
        }
        else if(y >= size.getRows()) {
            y = size.getRows() - 1;
        }
        currentPosition = currentPosition.withColumn(x).withRow(y);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        deviceEmulator.setCursorVisible(visible);
    }

    @Override
    public void putCharacter(char c) {
        deviceEmulator.getBuffer().setCharacter(getTerminalSize(), currentPosition, new TerminalCharacter(c, foregroundColor, backgroundColor, activeSGRs));
        advanceCursor();
    }

    @Override
    public void enableSGR(SGR sgr) {
        activeSGRs.add(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        activeSGRs.remove(sgr);
    }

    @Override
    public void resetAllSGR() {
        activeSGRs.clear();
    }

    @Override
    public void applyForegroundColor(ANSIColor color) {
        foregroundColor = TextColor.ANSI.fromTerminalANSIColor(color);
    }

    @Override
    public void applyForegroundColor(int index) {
        foregroundColor = new TextColor.Indexed(index);
    }

    @Override
    public void applyForegroundColor(int r, int g, int b) {
        foregroundColor = new TextColor.RGB(r, g, b);
    }

    @Override
    public void applyBackgroundColor(ANSIColor color) {
        backgroundColor = TextColor.ANSI.fromTerminalANSIColor(color);
    }

    @Override
    public void applyBackgroundColor(int index) {
        backgroundColor = new TextColor.Indexed(index);
    }

    @Override
    public void applyBackgroundColor(int r, int g, int b) {
        backgroundColor = new TextColor.RGB(r, g, b);
    }

    void setTerminalSize(TerminalSize terminalSize) {
        onResized(terminalSize.getColumns(), terminalSize.getRows());
        this.terminalSize = terminalSize;
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return deviceEmulator.equireTerminal();
    }

    @Override
    public void flush() {
        deviceEmulator.flush();
    }
}
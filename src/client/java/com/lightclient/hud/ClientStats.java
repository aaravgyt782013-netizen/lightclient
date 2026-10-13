package com.lightclient.hud;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClientStats {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean wasAttackDown;
    private static boolean wasUseDown;
    private static final long SESSION_START = System.currentTimeMillis();
    private ClientStats() {}

    public static void tick(Minecraft client) {
        if (client.player == null) {
            wasAttackDown = false;
            wasUseDown = false;
            LEFT_CLICKS.clear();
            RIGHT_CLICKS.clear();
            return;
        }
        boolean down = client.options.keyAttack.isDown();
        boolean useDown = client.options.keyUse.isDown();
        long now = System.currentTimeMillis();
        if (down && !wasAttackDown) LEFT_CLICKS.addLast(now);
        if (useDown && !wasUseDown) RIGHT_CLICKS.addLast(now);
        wasAttackDown = down;
        wasUseDown = useDown;
        while (!LEFT_CLICKS.isEmpty() && now - LEFT_CLICKS.peekFirst() > 1000L) LEFT_CLICKS.removeFirst();
        while (!RIGHT_CLICKS.isEmpty() && now - RIGHT_CLICKS.peekFirst() > 1000L) RIGHT_CLICKS.removeFirst();
    }

    public static long sessionSeconds() {
        return Math.max(0L, (System.currentTimeMillis() - SESSION_START) / 1000L);
    }

    public static int cps() {
        long now = System.currentTimeMillis();
        while (!LEFT_CLICKS.isEmpty() && now - LEFT_CLICKS.peekFirst() > 1000L) LEFT_CLICKS.removeFirst();
        return LEFT_CLICKS.size();
    }

    public static int rightCps() {
        long now = System.currentTimeMillis();
        while (!RIGHT_CLICKS.isEmpty() && now - RIGHT_CLICKS.peekFirst() > 1000L) RIGHT_CLICKS.removeFirst();
        return RIGHT_CLICKS.size();
    }
}
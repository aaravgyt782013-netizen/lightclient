package com.lightclient.hud;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClientStats {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static boolean wasAttackDown;
    private ClientStats() {}

    public static void tick(Minecraft client) {
        if (client.player == null) {
            wasAttackDown = false;
            LEFT_CLICKS.clear();
            return;
        }
        boolean down = client.options.keyAttack.isDown();
        long now = System.currentTimeMillis();
        if (down && !wasAttackDown) LEFT_CLICKS.addLast(now);
        wasAttackDown = down;
        while (!LEFT_CLICKS.isEmpty() && now - LEFT_CLICKS.peekFirst() > 1000L) {
            LEFT_CLICKS.removeFirst();
        }
    }

    public static int cps() {
        long now = System.currentTimeMillis();
        while (!LEFT_CLICKS.isEmpty() && now - LEFT_CLICKS.peekFirst() > 1000L) LEFT_CLICKS.removeFirst();
        return LEFT_CLICKS.size();
    }
}
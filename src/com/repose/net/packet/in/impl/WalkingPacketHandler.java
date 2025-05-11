package com.repose.net.packet.in.impl;

import java.nio.ByteBuffer;

import com.repose.game.Account;
import com.repose.game.entity.actor.WalkingQueue;
import com.repose.game.world.map.TilePosition;
import com.repose.net.packet.Packet;
import com.repose.net.packet.in.IncomingPacketHandler;

public class WalkingPacketHandler implements IncomingPacketHandler {

	@Override
	public void handlePacket(Account account, Packet packet, ByteBuffer packetBuffer) {

		final int stepCount = (packet.getPayload().length - 5) / 2;
		final int[][] path = new int[stepCount][2];

		int x = (int) packetBuffer.getShort();
		boolean run = packetBuffer.get() == 1;
		int y = (int) packetBuffer.getShort();

		for (int i = 0; i < stepCount; i++) {
			path[i][0] = (int) packetBuffer.get();
			path[i][1] = (int) packetBuffer.get();
		}

		TilePosition[] steps = new TilePosition[stepCount + 1];
		steps[0] = new TilePosition(x, y);
		for (int i = 0; i < stepCount; i++) {
			steps[i + 1] = new TilePosition(path[i][0] + x, path[i][1] + y);
		}

		WalkingQueue queue = account.getPlayer().getWalkingQueue();
		for (int index = 0; index < steps.length; index++) {
			TilePosition step = steps[index];
			if (index == 0) {
				queue.addFirstStep(step);
			} else {
				queue.addStep(step);
			}
		}
		queue.setRunning(run);
	}

}

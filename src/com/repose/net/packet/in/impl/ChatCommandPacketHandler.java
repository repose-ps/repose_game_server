package com.repose.net.packet.in.impl;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.repose.game.Account;
import com.repose.game.entity.GameObject;
import com.repose.game.entity.actor.player.Player;
import com.repose.io.cache.map.MapTile;
import com.repose.io.cache.map.WorldMap;
import com.repose.net.packet.Packet;
import com.repose.net.packet.in.IncomingPacketHandler;

public class ChatCommandPacketHandler implements IncomingPacketHandler {

	@Override
	public void handlePacket(Account account, Packet packet, ByteBuffer packetBuffer) {
		final String commandLine = IncomingPacketHandler.readString(packetBuffer);
		final String[] args = commandLine.split(" ");
		final Player player = account.getPlayer();

		final String command = args[0];

		if (command.equalsIgnoreCase("clip")) {
			if (!WorldMap.tileIsCached(player.getX(), player.getY())) {
				account.sendChatMessage("tile is not cached.");
			} else {
				final MapTile tile = WorldMap.getTile(player.getX(), player.getY(), player.getPlane());
				account.sendChatMessage(Integer.toBinaryString(tile.clippingData));

				final Collection<GameObject> objects = tile.tileObjects.values();
				for (GameObject obj : objects) {
					account.sendChatMessage("obj[id=" + obj.getObjectID() + ", type=" + obj.getObjectType()
							+ ", orientation=" + obj.getOrientation() + "]");
				}
			}
			return;
		}

		if (command.equalsIgnoreCase("tele")) {
			final int teleX;
			final int teleY;
			final int telePlane;

			try {
				if (args.length == 3 || args.length == 4) {
					teleX = Integer.parseInt(args[1]);
					teleY = Integer.parseInt(args[2]);
					if (args.length == 4) {
						telePlane = Integer.parseInt(args[3]);
					} else {
						telePlane = player.getPlane();
					}
				} else {
					sendInvalidCommand(account, command, "absX", "absY", "[plane]");
					return;
				}
			} catch (NumberFormatException e) {
				sendInvalidCommand(account, command, "absX", "absY", "[plane]");
				return;
			}

			player.teleport(teleX, teleY, telePlane);
			return;
		}

		account.sendChatMessage("Unhandled command: " + command);
	}

	private static void sendInvalidCommand(Account account, String command, String... appropriateSyntax) {
		account.sendChatMessage("Invalid command syntax.");

		String syntax = "";

		for (int i = 0; i < appropriateSyntax.length; i++) {
			syntax += (i == 0 ? " " : ", ");
			syntax += appropriateSyntax[i];
		}
		account.sendChatMessage("expected: " + command + syntax);
	}

	private static void sendInvalidCommand(Account account, String command, String[]... appropriateSyntaxes) {
		account.sendChatMessage("Invalid command syntax.");

		for (int a = 0; a < appropriateSyntaxes.length; a++) {
			final String[] appropriateSyntax = appropriateSyntaxes[a];
			String syntax = "";
			for (int i = 0; i < appropriateSyntax.length; i++) {
				syntax += (i == 0 ? " " : ", ");
				syntax += appropriateSyntax[i];
			}
			account.sendChatMessage(
					"expected (" + (a + 1) + "/" + (appropriateSyntaxes.length) + ": " + command + syntax);
		}
	}
}

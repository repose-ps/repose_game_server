// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache.def;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.repose.GameServer;
import com.repose.io.cache.Archive;
import com.repose.io.cache.Buffer;

public class GameObjectDefinition {

	public static GameObjectDefinition gameObjectCache[];
	public static int objCount;

	public int[] modelIds;
	public int[] modelTypes;
	public String name;
	public byte[] description;
	public int sizeX;
	public int sizeY;
	public boolean solid;
	public boolean walkable;
	public boolean hasActions;
	public boolean adjustToTerrain;
	public boolean nonFlatShading;
	public boolean wall;
	public int animationId;
	public int offsetAmplifier;
	public byte modelLightFalloff;
	public byte modelLightAmbient;
	public String[] options;
	public int[] replacedColorArray;
	public int[] replacingColorArray;
	public int icon;
	public boolean unknown3;
	public boolean castsShadow;
	public int modelSizeX;
	public int modelSizeY;
	public int modelSizeZ;
	public int mapScene;
	public int face;
	public int translateX;
	public int translateY;
	public int translateZ;
	public boolean unknown;
	public boolean unwalkableSolid;
	public int _solid;
	public int varbitId;
	public int configIds;
	public int[] childIds;

	public int objId;

	/**
	 * Dumps the cached definitions to a text file.
	 * 
	 * @param filePath the file's directory on the file system
	 * @throws IOException if an I/O error occurs
	 */
	public static void dump(String filePath) throws IOException {
		DataWriter writer = new DataWriter(new FileWriter(new File(filePath)));

		for (int i = 0; i < objCount; i++) {
			final GameObjectDefinition def = gameObjectCache[i];
			writer.writeCategory(Integer.toString(i));

			writer.writeVar("name", def.name);
			writer.writeVar("description", def.description == null ? "null" : new String(def.description));

			if (def.modelIds != null) {
				writer.writeVar("modelIds", def.modelIds);
			}
			if (def.modelTypes != null) {
				writer.writeVar("modelTypes", def.modelTypes);
			}
			if (def.sizeX != 1) {
				writer.writeVar("sizeX", def.sizeX);
			}
			if (def.sizeY != 1) {
				writer.writeVar("sizeY", def.sizeY);
			}
			if (!def.solid) {
				writer.writeVar("solid", def.solid);
			}
			if (!def.walkable) {
				writer.writeVar("walkable", def.walkable);
			}
			if (def.hasActions) {
				writer.writeVar("hasActions", def.hasActions);
			}
			if (def.adjustToTerrain) {
				writer.writeVar("adjustToTerrain", def.adjustToTerrain);
			}
			if (def.nonFlatShading) {
				writer.writeVar("nonFlatShading", def.nonFlatShading);
			}
			if (def.wall) {
				writer.writeVar("wall", def.wall);
			}
			if (def.animationId != -1) {
				writer.writeVar("animationId", def.animationId);
			}
			if (def.offsetAmplifier != 16) {
				writer.writeVar("offsetAmplifier", def.offsetAmplifier);
			}
			if (def.modelLightFalloff != 0) {
				writer.writeVar("modelLightFalloff", def.modelLightFalloff);
			}
			if (def.modelLightAmbient != 0) {
				writer.writeVar("modelLightAmbient", def.modelLightAmbient);
			}
			if (def.options != null) {
				writer.writeVar("options", def.options);
			}
			if (def.replacedColorArray != null) {
				writer.writeVar("replacedColorArray", def.replacedColorArray);
				writer.writeVar("replacingColorArray", def.replacingColorArray);
			}
			if (def.icon != -1) {
				writer.writeVar("icon", def.icon);
			}
			if (def.unknown3) {
				writer.writeVar("unknown3", def.unknown3);
			}
			if (!def.castsShadow) {
				writer.writeVar("castsShadow", def.castsShadow);
			}
			if (def.modelSizeX != 128) {
				writer.writeVar("modelSizeX", def.modelSizeX);
			}
			if (def.modelSizeY != 128) {
				writer.writeVar("modelSizeY", def.modelSizeY);
			}
			if (def.modelSizeZ != 128) {
				writer.writeVar("modelSizeZ", def.modelSizeZ);
			}
			if (def.mapScene != -1) {
				writer.writeVar("mapScene", def.mapScene);
			}
			if (def.face != 0) {
				writer.writeVar("face", def.face);
			}
			if (def.translateX != 0) {
				writer.writeVar("translateX", def.translateX);
			}
			if (def.translateY != 0) {
				writer.writeVar("translateY", def.translateY);
			}
			if (def.translateZ != 0) {
				writer.writeVar("translateZ", def.translateZ);
			}
			if (def.unknown) {
				writer.writeVar("unknown", def.unknown);
			}
			if (def.unwalkableSolid) {
				writer.writeVar("unwalkableSolid", def.unwalkableSolid);
			}
			if (def._solid != -1) {
				writer.writeVar("_solid", def._solid);
			}
			if (def.varbitId != -1) {
				writer.writeVar("varbitId", def.varbitId);
			}
			if (def.configIds != -1) {
				writer.writeVar("configIds", def.configIds);
			}
			if (def.childIds != null) {
				writer.writeVar("childIds", def.childIds);
			}
		}
		writer.close();
	}

	/**
	 * Loads all of the object definitions into memory.
	 * 
	 * @param archive the archive that the data is stored in
	 */
	public static void load(Archive archive) {
		Buffer dataBuffer = new Buffer(archive.decompressFile("loc.dat"));
		Buffer indexBuffer = new Buffer(archive.decompressFile("loc.idx"));
		objCount = indexBuffer.getUnsignedShortBE();
		gameObjectCache = new GameObjectDefinition[objCount];

		int indexBufferIndex = 2;
		for (int objId = 0; objId < objCount; objId++) {
			dataBuffer.position = indexBufferIndex;
			indexBufferIndex += indexBuffer.getUnsignedShortBE();

			final GameObjectDefinition definition = gameObjectCache[objId] = new GameObjectDefinition();
			definition.loadDefinitions(dataBuffer);
			definition.objId = objId;
		}

		GameServer.getLogger().info("Loaded " + objCount + " object definitions.");
	}

	public GameObjectDefinition() {
		this.objId = -1;
		this.modelIds = null;
		this.modelTypes = null;
		this.name = "null";
		this.description = null;
		this.replacedColorArray = null;
		this.replacingColorArray = null;
		this.sizeX = 1;
		this.sizeY = 1;
		this.solid = true;
		this.walkable = true;
		this.hasActions = false;
		this.adjustToTerrain = false;
		this.nonFlatShading = false;
		this.wall = false;
		this.animationId = -1;
		this.offsetAmplifier = 16;
		this.modelLightFalloff = 0;
		this.modelLightAmbient = 0;
		this.options = null;
		this.icon = -1;
		this.mapScene = -1;
		this.unknown3 = false;
		this.castsShadow = true;
		this.modelSizeX = 128;
		this.modelSizeY = 128;
		this.modelSizeZ = 128;
		this.face = 0;
		this.translateX = 0;
		this.translateY = 0;
		this.translateZ = 0;
		this.unknown = false;
		this.unwalkableSolid = false;
		this._solid = -1;
		this.varbitId = -1;
		this.configIds = -1;
		this.childIds = null;
	}

	public void loadDefinitions(Buffer buffer) {
		int _actions = -1;
		label0: do {
			int attribute;
			do {
				attribute = buffer.getUnsignedByte();
				if (attribute == 0)
					break label0;
				if (attribute == 1) {
					int modelCount = buffer.getUnsignedByte();
					if (modelCount > 0)
						if (this.modelIds == null) {
							this.modelTypes = new int[modelCount];
							this.modelIds = new int[modelCount];
							for (int m = 0; m < modelCount; m++) {
								this.modelIds[m] = buffer.getUnsignedShortBE();
								this.modelTypes[m] = buffer.getUnsignedByte();
							}

						} else {
							buffer.position += modelCount * 3;
						}
				} else if (attribute == 2)
					this.name = buffer.getString();
				else if (attribute == 3)
					this.description = buffer.getStringBytes();
				else if (attribute == 5) {
					int modelCount = buffer.getUnsignedByte();
					if (modelCount > 0)
						if (this.modelIds == null) {
							this.modelTypes = null;
							this.modelIds = new int[modelCount];
							for (int m = 0; m < modelCount; m++)
								this.modelIds[m] = buffer.getUnsignedShortBE();

						} else {
							buffer.position += modelCount * 2;
						}
				} else if (attribute == 14)
					this.sizeX = buffer.getUnsignedByte();
				else if (attribute == 15)
					this.sizeY = buffer.getUnsignedByte();
				else if (attribute == 17)
					this.solid = false;
				else if (attribute == 18)
					this.walkable = false;
				else if (attribute == 19) {
					_actions = buffer.getUnsignedByte();
					if (_actions == 1)
						this.hasActions = true;
				} else if (attribute == 21)
					this.adjustToTerrain = true;
				else if (attribute == 22)
					this.nonFlatShading = true;
				else if (attribute == 23)
					this.wall = true;
				else if (attribute == 24) {
					this.animationId = buffer.getUnsignedShortBE();
					if (this.animationId == 65535)
						this.animationId = -1;
				} else if (attribute == 28)
					this.offsetAmplifier = buffer.getUnsignedByte();
				else if (attribute == 29)
					this.modelLightFalloff = buffer.getSignedByte();
				else if (attribute == 39)
					this.modelLightAmbient = buffer.getSignedByte();
				else if (attribute >= 30 && attribute < 39) {
					if (this.options == null)
						this.options = new String[5];
					this.options[attribute - 30] = buffer.getString();
					if (this.options[attribute - 30].equalsIgnoreCase("hidden"))
						this.options[attribute - 30] = null;
				} else if (attribute == 40) {
					int replacedColorCount = buffer.getUnsignedByte();
					this.replacedColorArray = new int[replacedColorCount];
					this.replacingColorArray = new int[replacedColorCount];
					for (int i2 = 0; i2 < replacedColorCount; i2++) {
						this.replacedColorArray[i2] = buffer.getUnsignedShortBE();
						this.replacingColorArray[i2] = buffer.getUnsignedShortBE();
					}

				} else if (attribute == 60)
					this.icon = buffer.getUnsignedShortBE();
				else if (attribute == 62)
					this.unknown3 = true;
				else if (attribute == 64)
					this.castsShadow = false;
				else if (attribute == 65)
					this.modelSizeX = buffer.getUnsignedShortBE();
				else if (attribute == 66)
					this.modelSizeY = buffer.getUnsignedShortBE();
				else if (attribute == 67)
					this.modelSizeZ = buffer.getUnsignedShortBE();
				else if (attribute == 68)
					this.mapScene = buffer.getUnsignedShortBE();
				else if (attribute == 69)
					this.face = buffer.getUnsignedByte();
				else if (attribute == 70)
					this.translateX = buffer.getSignedShortBE();
				else if (attribute == 71)
					this.translateY = buffer.getSignedShortBE();
				else if (attribute == 72)
					this.translateZ = buffer.getSignedShortBE();
				else if (attribute == 73)
					this.unknown = true;
				else if (attribute == 74) {
					this.unwalkableSolid = true;
				} else {
					if (attribute != 75)
						continue;
					this._solid = buffer.getUnsignedByte();
				}
				continue label0;
			} while (attribute != 77);
			this.varbitId = buffer.getUnsignedShortBE();
			if (this.varbitId == 65535)
				this.varbitId = -1;
			this.configIds = buffer.getUnsignedShortBE();
			if (this.configIds == 65535)
				this.configIds = -1;
			int childCount = buffer.getUnsignedByte();
			this.childIds = new int[childCount + 1];
			for (int c = 0; c <= childCount; c++) {
				this.childIds[c] = buffer.getUnsignedShortBE();
				if (this.childIds[c] == 65535)
					this.childIds[c] = -1;
			}

		} while (true);
		if (_actions == -1) {
			this.hasActions = false;
			if (this.modelIds != null && (this.modelTypes == null || this.modelTypes[0] == 10))
				this.hasActions = true;
			if (this.options != null)
				this.hasActions = true;
		}
		if (this.unwalkableSolid) {
			this.solid = false;
			this.walkable = false;
		}
		if (this._solid == -1)
			this._solid = this.solid ? 1 : 0;
	}
}

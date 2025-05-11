// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache.bzip2;

public class BZip2DecompressionState {

	public BZip2DecompressionState() {
		this.unzftab = new int[256];
		this.cftab = new int[257];
		this.inUse = new boolean[256];
		this.inUse16 = new boolean[16];
		this.seqToUnseq = new byte[256];
		this.mtfa = new byte[4096];
		this.mtfbase = new int[16];
		this.selector = new byte[18002];
		this.selectorMtf = new byte[18002];
		this.len = new byte[6][258];
		this.limit = new int[6][258];
		this.base = new int[6][258];
		this.perm = new int[6][258];
		this.minLens = new int[6];
	}

	public byte stream[];
	public int nextIn;
	public int availableIn;
	public int totalInLo32;
	public int totalInHi32;
	public byte buf[];
	public int nextOut;
	public int availOut;
	public int totalOutLo32;
	public int totalOutHigh32;
	public byte stateOutCh;
	public int stateOutLen;
	public boolean blockRandomised;
	public int bsBuff;
	public int bsLive;
	public int blockSize100k;
	public int currBlockNumber;
	public int origPtr;
	public int tPos;
	public int k0;
	public int unzftab[];
	public int nBlockUsed;
	public int cftab[];
	public static int tt[];
	public int nInUse;
	public boolean inUse[];
	public boolean inUse16[];
	public byte seqToUnseq[];
	public byte mtfa[];
	public int mtfbase[];
	public byte selector[];
	public byte selectorMtf[];
	public byte len[][];
	public int limit[][];
	public int base[][];
	public int perm[][];
	public int minLens[];
	public int nBlock;
}

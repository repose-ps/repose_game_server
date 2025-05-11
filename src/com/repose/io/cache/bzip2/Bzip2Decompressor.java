// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache.bzip2;

public class Bzip2Decompressor {

	public static int decompress(byte output[], int retVal, byte bzStream[], int maxLen, int minLen) {
		synchronized (state) {
			state.stream = bzStream;
			state.nextIn = minLen;
			state.buf = output;
			state.nextOut = 0;
			state.availableIn = maxLen;
			state.availOut = retVal;
			state.bsLive = 0;
			state.bsBuff = 0;
			state.totalInLo32 = 0;
			state.totalInHi32 = 0;
			state.totalOutLo32 = 0;
			state.totalOutHigh32 = 0;
			state.currBlockNumber = 0;
			decompress(state);
			retVal -= state.availOut;
			return retVal;
		}
	}

	public static void method313(BZip2DecompressionState block) {
		byte stateOutCh = block.stateOutCh;
		int stateOutLen = block.stateOutLen;
		int nBlockUsed = block.nBlockUsed;
		int k0 = block.k0;
		int tt[] = BZip2DecompressionState.tt;
		int tPos = block.tPos;
		byte buf[] = block.buf;
		int csNextOut = block.nextOut;
		int csAvailOut = block.availOut;
		int availOutInit = csAvailOut;
		int savedNBlockPP = block.nBlock + 1;
		outer: do {
			if (stateOutLen > 0) {
				do {
					if (csAvailOut == 0)
						break outer;
					if (stateOutLen == 1)
						break;
					buf[csNextOut] = stateOutCh;
					stateOutLen--;
					csNextOut++;
					csAvailOut--;
				} while (true);
				if (csAvailOut == 0) {
					stateOutLen = 1;
					break;
				}
				buf[csNextOut] = stateOutCh;
				csNextOut++;
				csAvailOut--;
			}
			boolean flag = true;
			while (flag) {
				flag = false;
				if (nBlockUsed == savedNBlockPP) {
					stateOutLen = 0;
					break outer;
				}
				stateOutCh = (byte) k0;
				tPos = tt[tPos];
				byte k1 = (byte) (tPos & 0xff);
				tPos >>= 8;
				nBlockUsed++;
				if (k1 != k0) {
					k0 = k1;
					if (csAvailOut == 0) {
						stateOutLen = 1;
					} else {
						buf[csNextOut] = stateOutCh;
						csNextOut++;
						csAvailOut--;
						flag = true;
						continue;
					}
					break outer;
				}
				if (nBlockUsed != savedNBlockPP)
					continue;
				if (csAvailOut == 0) {
					stateOutLen = 1;
					break outer;
				}
				buf[csNextOut] = stateOutCh;
				csNextOut++;
				csAvailOut--;
				flag = true;
			}
			stateOutLen = 2;
			tPos = tt[tPos];
			byte k1 = (byte) (tPos & 0xff);
			tPos >>= 8;
			if (++nBlockUsed != savedNBlockPP)
				if (k1 != k0) {
					k0 = k1;
				} else {
					stateOutLen = 3;
					tPos = tt[tPos];
					byte k1_ = (byte) (tPos & 0xff);
					tPos >>= 8;
					if (++nBlockUsed != savedNBlockPP)
						if (k1_ != k0) {
							k0 = k1_;
						} else {
							tPos = tt[tPos];
							byte k1__ = (byte) (tPos & 0xff);
							tPos >>= 8;
							nBlockUsed++;
							stateOutLen = (k1__ & 0xff) + 4;
							tPos = tt[tPos];
							k0 = (byte) (tPos & 0xff);
							tPos >>= 8;
							nBlockUsed++;
						}
				}
		} while (true);
		int oldTotalOutLo32 = block.totalOutLo32;
		block.totalOutLo32 += availOutInit - csAvailOut;
		if (block.totalOutLo32 < oldTotalOutLo32)
			block.totalOutHigh32++;
		block.stateOutCh = stateOutCh;
		block.stateOutLen = stateOutLen;
		block.nBlockUsed = nBlockUsed;
		block.k0 = k0;
		BZip2DecompressionState.tt = tt;
		block.tPos = tPos;
		block.buf = buf;
		block.nextOut = csNextOut;
		block.availOut = csAvailOut;
	}

	public static void decompress(BZip2DecompressionState block) {
		int gMinLen = 0;
		int gLimit[] = null;
		int gBase[] = null;
		int gPerm[] = null;
		block.blockSize100k = 1;
		if (BZip2DecompressionState.tt == null)
			BZip2DecompressionState.tt = new int[block.blockSize100k * 0x186a0];
		boolean flag19 = true;
		while (flag19) {
			byte uc = getUChar(block);
			if (uc == 23)
				return;
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getUChar(block);
			block.currBlockNumber++;
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getUChar(block);
			uc = getBit(block);
			if (uc != 0)
				block.blockRandomised = true;
			else
				block.blockRandomised = false;
			if (block.blockRandomised)
				System.out.println("PANIC! RANDOMISED BLOCK!");
			block.origPtr = 0;
			uc = getUChar(block);
			block.origPtr = block.origPtr << 8 | uc & 0xff;
			uc = getUChar(block);
			block.origPtr = block.origPtr << 8 | uc & 0xff;
			uc = getUChar(block);
			block.origPtr = block.origPtr << 8 | uc & 0xff;
			for (int i = 0; i < 16; i++) {
				byte bit = getBit(block);
				if (bit == 1)
					block.inUse16[i] = true;
				else
					block.inUse16[i] = false;
			}

			for (int k = 0; k < 256; k++)
				block.inUse[k] = false;

			for (int l = 0; l < 16; l++)
				if (block.inUse16[l]) {
					for (int j = 0; j < 16; j++) {
						byte bit = getBit(block);
						if (bit == 1)
							block.inUse[l * 16 + j] = true;
					}

				}

			makeMaps(block);
			int alphaSize = block.nInUse + 2;
			int nGroups = getBits(3, block);
			int nSelectors = getBits(15, block);
			for (int i = 0; i < nSelectors; i++) {
				int count = 0;
				do {
					byte terminator = getBit(block);
					if (terminator == 0)
						break;
					count++;
				} while (true);
				block.selectorMtf[i] = (byte) count;
			}

			byte pos[] = new byte[6];
			for (byte v = 0; v < nGroups; v++)
				pos[v] = v;

			for (int i = 0; i < nSelectors; i++) {
				byte v = block.selectorMtf[i];
				byte tmp = pos[v];
				for (; v > 0; v--)
					pos[v] = pos[v - 1];

				pos[0] = tmp;
				block.selector[i] = tmp;
			}

			for (int t = 0; t < nGroups; t++) {
				int curr = getBits(5, block);
				for (int i = 0; i < alphaSize; i++) {
					do {
						byte bit = getBit(block);
						if (bit == 0)
							break;
						bit = getBit(block);
						if (bit == 0)
							curr++;
						else
							curr--;
					} while (true);
					block.len[t][i] = (byte) curr;
				}

			}

			for (int t = 0; t < nGroups; t++) {
				byte minLen = 32;
				int maxLen = 0;
				for (int i = 0; i < alphaSize; i++) {
					if (block.len[t][i] > maxLen)
						maxLen = block.len[t][i];
					if (block.len[t][i] < minLen)
						minLen = block.len[t][i];
				}

				createDecodeTables(block.limit[t], block.base[t], block.perm[t], block.len[t], minLen, maxLen,
						alphaSize);
				block.minLens[t] = minLen;
			}

			int eob = block.nInUse + 1;
			int groupNo = -1;
			int groupPos = 0;
			for (int i = 0; i <= 255; i++)
				block.unzftab[i] = 0;

			int kk = 4095;
			for (int ii = 15; ii >= 0; ii--) {
				for (int jj = 15; jj >= 0; jj--) {
					block.mtfa[kk] = (byte) (ii * 16 + jj);
					kk--;
				}

				block.mtfbase[ii] = kk + 1;
			}

			int nBlock = 0;
			if (groupPos == 0) {
				groupNo++;
				groupPos = 50;
				byte gSel = block.selector[groupNo];
				gMinLen = block.minLens[gSel];
				gLimit = block.limit[gSel];
				gPerm = block.perm[gSel];
				gBase = block.base[gSel];
			}
			groupPos--;
			int zn = gMinLen;
			int zvec;
			byte zj;
			for (zvec = getBits(zn, block); zvec > gLimit[zn]; zvec = zvec << 1 | zj) {
				zn++;
				zj = getBit(block);
			}

			for (int nextSym = gPerm[zvec - gBase[zn]]; nextSym != eob;)
				if (nextSym == 0 || nextSym == 1) {
					int es = -1;
					int n = 1;
					do {
						if (nextSym == 0)
							es += n;
						else if (nextSym == 1)
							es += 2 * n;
						n *= 2;
						if (groupPos == 0) {
							groupNo++;
							groupPos = 50;
							byte byte13 = block.selector[groupNo];
							gMinLen = block.minLens[byte13];
							gLimit = block.limit[byte13];
							gPerm = block.perm[byte13];
							gBase = block.base[byte13];
						}
						groupPos--;
						int zn_ = gMinLen;
						int zvec_;
						byte byte10;
						for (zvec_ = getBits(zn_, block); zvec_ > gLimit[zn_]; zvec_ = zvec_ << 1 | byte10) {
							zn_++;
							byte10 = getBit(block);
						}

						nextSym = gPerm[zvec_ - gBase[zn_]];
					} while (nextSym == 0 || nextSym == 1);
					es++;
					byte uc_ = block.seqToUnseq[block.mtfa[block.mtfbase[0]] & 0xff];
					block.unzftab[uc_ & 0xff] += es;
					for (; es > 0; es--) {
						BZip2DecompressionState.tt[nBlock] = uc_ & 0xff;
						nBlock++;
					}

				} else {
					int nn = nextSym - 1;
					byte uc_;
					if (nn < 16) {
						int pp = block.mtfbase[0];
						uc_ = block.mtfa[pp + nn];
						for (; nn > 3; nn -= 4) {
							int z = pp + nn;
							block.mtfa[z] = block.mtfa[z - 1];
							block.mtfa[z - 1] = block.mtfa[z - 2];
							block.mtfa[z - 2] = block.mtfa[z - 3];
							block.mtfa[z - 3] = block.mtfa[z - 4];
						}

						for (; nn > 0; nn--)
							block.mtfa[pp + nn] = block.mtfa[(pp + nn) - 1];

						block.mtfa[pp] = uc_;
					} else {
						int lno = nn / 16;
						int off = nn % 16;
						int pp = block.mtfbase[lno] + off;
						uc_ = block.mtfa[pp];
						for (; pp > block.mtfbase[lno]; pp--)
							block.mtfa[pp] = block.mtfa[pp - 1];

						block.mtfbase[lno]++;
						for (; lno > 0; lno--) {
							block.mtfbase[lno]--;
							block.mtfa[block.mtfbase[lno]] = block.mtfa[(block.mtfbase[lno - 1] + 16) - 1];
						}

						block.mtfbase[0]--;
						block.mtfa[block.mtfbase[0]] = uc_;
						if (block.mtfbase[0] == 0) {
							int kk_ = 4095;
							for (int ii = 15; ii >= 0; ii--) {
								for (int jj = 15; jj >= 0; jj--) {
									block.mtfa[kk_] = block.mtfa[block.mtfbase[ii] + jj];
									kk_--;
								}

								block.mtfbase[ii] = kk_ + 1;
							}

						}
					}
					block.unzftab[block.seqToUnseq[uc_ & 0xff] & 0xff]++;
					BZip2DecompressionState.tt[nBlock] = block.seqToUnseq[uc_ & 0xff] & 0xff;
					nBlock++;
					if (groupPos == 0) {
						groupNo++;
						groupPos = 50;
						byte byte14 = block.selector[groupNo];
						gMinLen = block.minLens[byte14];
						gLimit = block.limit[byte14];
						gPerm = block.perm[byte14];
						gBase = block.base[byte14];
					}
					groupPos--;
					int zn_ = gMinLen;
					int zvec_;
					byte byte11;
					for (zvec_ = getBits(zn_, block); zvec_ > gLimit[zn_]; zvec_ = zvec_ << 1 | byte11) {
						zn_++;
						byte11 = getBit(block);
					}

					nextSym = gPerm[zvec_ - gBase[zn_]];
				}

			block.stateOutLen = 0;
			block.stateOutCh = 0;
			block.cftab[0] = 0;
			for (int i = 1; i <= 256; i++)
				block.cftab[i] = block.unzftab[i - 1];

			for (int i = 1; i <= 256; i++)
				block.cftab[i] += block.cftab[i - 1];

			for (int i = 0; i < nBlock; i++) {
				byte uc_ = (byte) (BZip2DecompressionState.tt[i] & 0xff);
				BZip2DecompressionState.tt[block.cftab[uc_ & 0xff]] |= i << 8;
				block.cftab[uc_ & 0xff]++;
			}

			block.tPos = BZip2DecompressionState.tt[block.origPtr] >> 8;
			block.nBlockUsed = 0;
			block.tPos = BZip2DecompressionState.tt[block.tPos];
			block.k0 = (byte) (block.tPos & 0xff);
			block.tPos >>= 8;
			block.nBlockUsed++;
			block.nBlock = nBlock;
			method313(block);
			if (block.nBlockUsed == block.nBlock + 1 && block.stateOutLen == 0)
				flag19 = true;
			else
				flag19 = false;
		}
	}

	public static byte getUChar(BZip2DecompressionState block) {
		return (byte) getBits(8, block);
	}

	public static byte getBit(BZip2DecompressionState block) {
		return (byte) getBits(1, block);
	}

	public static int getBits(int numBits, BZip2DecompressionState block) {
		int bits;
		do {
			if (block.bsLive >= numBits) {
				int v = block.bsBuff >> block.bsLive - numBits & (1 << numBits) - 1;
				block.bsLive -= numBits;
				bits = v;
				break;
			}
			block.bsBuff = block.bsBuff << 8 | block.stream[block.nextIn] & 0xff;
			block.bsLive += 8;
			block.nextIn++;
			block.availableIn--;
			block.totalInLo32++;
			if (block.totalInLo32 == 0)
				block.totalInHi32++;
		} while (true);
		return bits;
	}

	public static void makeMaps(BZip2DecompressionState block) {
		block.nInUse = 0;
		for (int i = 0; i < 256; i++)
			if (block.inUse[i]) {
				block.seqToUnseq[block.nInUse] = (byte) i;
				block.nInUse++;
			}

	}

	public static void createDecodeTables(int limit[], int base[], int perm[], byte len[], int minLen, int maxLen,
			int k) {
		int pp = 0;
		for (int i = minLen; i <= maxLen; i++) {
			for (int j = 0; j < k; j++)
				if (len[j] == i) {
					perm[pp] = j;
					pp++;
				}

		}

		for (int i = 0; i < 23; i++)
			base[i] = 0;

		for (int i = 0; i < k; i++)
			base[len[i] + 1]++;

		for (int i = 1; i < 23; i++)
			base[i] += base[i - 1];

		for (int i = 0; i < 23; i++)
			limit[i] = 0;

		int vec = 0;
		for (int i = minLen; i <= maxLen; i++) {
			vec += base[i + 1] - base[i];
			limit[i] = vec - 1;
			vec <<= 1;
		}

		for (int i = minLen + 1; i <= maxLen; i++)
			base[i] = (limit[i - 1] + 1 << 1) - base[i];

	}

	public static BZip2DecompressionState state = new BZip2DecompressionState();

}

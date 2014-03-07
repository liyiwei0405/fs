package com.funshion.search.utils.systemWatcher.heartBeat;

import static org.junit.Assert.*;

import org.junit.Test;

import com.funshion.search.utils.systemWatcher.heartBeat.ISNHeartBeat;

public class ISNHeartBeatTest {

	@Test
	public void testToStringAndParse() {
		final int ver = 1;
		final long seq = System.currentTimeMillis();
		final String businessType = "videoletTest";
		final int groupId = 0;
		final int groupSize = 1;
		final int groupIdx = 0;
		final long modifyDateInSeconds = System.currentTimeMillis() / 1000;//in seconds
		final int port = 45655;
		ISNHeartBeat hb = new ISNHeartBeat(ver, seq, businessType, groupId, groupSize, groupIdx, modifyDateInSeconds, port);
		String hbStr = hb.toString();
		ISNHeartBeat hbNew = ISNHeartBeat.parse(hbStr);
		assertEquals("ver mismatch!", ver, hbNew.ver);
		assertTrue("businessType mismatch!", businessType.equalsIgnoreCase(hbNew.businessType));
		assertEquals("groupId mismatch!", groupId, hbNew.groupId);
		assertEquals("groupSize mismatch!", groupSize, hbNew.groupSize);
		assertEquals("groupIdx mismatch!", groupIdx, hbNew.groupIdx);
		assertEquals("modifyDateInSeconds mismatch!", modifyDateInSeconds, hbNew.modifyFlagInSeconds);
		assertEquals("port mismatch!", port, hbNew.svcPort);
	}

}

package com.funshion.videoService;

import java.io.IOException;

import com.funshion.search.FlushableRecord;
import com.funshion.search.utils.LineWriter;
import com.funshion.videoService.thrift.VideoletInfo;

public class VideoletRecord extends FlushableRecord{
	public static final String FIELD_VIDEO_ID = "a";
	public static final String FIELD_TITLE = "b";
	public static final String FIELD_TIMELEN = "c";
	public static final String FIELD_SCORE = "d";
	public static final String FIELD_PLAY_NUM = "e";
	public static final String FIELD_TAG_IDS = "f";
	public static final String FIELD_TYPES = "g";
	public static final String FIELD_TAGS = "h";
	public static final String FIELD_CREATE_DATE = "i";
	public static final String FIELD_MODIFY_DATE = "j";
	public static final String FIELD_SOURCE = "k";

	private VideoletInfo videoletInfo;

	public VideoletRecord(VideoletInfo videoletInfo){
		this.videoletInfo = videoletInfo;
	}

	@Override
	public void flushTo(LineWriter lw) throws IOException {
		lw.writeLine(FlushableRecord.RECORD_START_FLAG);

		writeItem(lw, FIELD_VIDEO_ID, videoletInfo.videoId);
		writeItem(lw, FIELD_TITLE, videoletInfo.title);
		writeItem(lw, FIELD_TIMELEN, videoletInfo.timeLen);
		writeItem(lw, FIELD_SCORE, videoletInfo.score);
		writeItem(lw, FIELD_PLAY_NUM, videoletInfo.playNum);
		writeItem(lw, FIELD_TAG_IDS, videoletInfo.tagIds);
		writeItem(lw, FIELD_TYPES, videoletInfo.types);
		writeItem(lw, FIELD_TAGS, videoletInfo.tags);
		writeItem(lw, FIELD_CREATE_DATE, videoletInfo.createDate);
		writeItem(lw, FIELD_MODIFY_DATE, videoletInfo.modifyDate);
		writeItem(lw, FIELD_SOURCE, videoletInfo.source);

		lw.writeLine(RECORD_END_FLAG);
	}

}

package com.funshion.search.videolet.dataCollector;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.mongodb.DBObject;

public class VideoletRecord {
	public static final String UGCClassName = "ugc";
	public static final String MicroClassName = "normal";
	public static final int isFromFs = 1;
	public static final int isNotFromFs = 2;

	public static final int isMicroVideoType = 1;
	public static final int isUGCType = 2;

	public static final String XML_DOC_PREFIX = " <sphinx:document id=\"";
	public static final String XML_DOC_SUFFIX = " </sphinx:document>";
	public static final String XML_DOCSET_TAIL = "</sphinx:docset>";

	public final int videoOrUgc;
	public final int videoId;

	public final String title;
	public final String tags;

	public final long modifydate;

	public final int playTimes;
	public final int docId;
	public final boolean isDel;
	public VideoletRecord( DBObject dbobj){
		this.videoId = this.getVideoId(dbobj);
		this.isDel = !"published".equalsIgnoreCase(this.getString(dbobj.get("publishflag"), ""));
		this.videoOrUgc = this.getIsMicroVideoOrUgc(dbobj);

		this.title = this.getTitle(dbobj);
		this.tags = this.getTags(dbobj);

		this.modifydate = this.getModifyDate(dbobj);

		this.playTimes = this.getPlayNum(dbobj);
		this.docId = videoId + getDocBase();
	}	

	public String info(){
		return "videoid:" + videoId;
	}

	public int getIsMicroVideoOrUgc(DBObject dbobj){
		String str = getString(dbobj.get("class"), null);
		if(str.equalsIgnoreCase(UGCClassName)){
			return isUGCType;
		}else if(str.equalsIgnoreCase(MicroClassName)){
			return isMicroVideoType;
		}else{
			throw new RuntimeException("unknown class:" + dbobj.get("class") + " for " + this.videoId + " ");
		}
	}

	public int getVideoId(DBObject dbobj) {
		return getInt(dbobj.get("videoid"), 0);
	}

	public String getTitle(DBObject dbobj) {
		return getString(dbobj.get("title"), "");
	}

	public String getTags(DBObject dbobj) {
		return rewriteTags(getString(dbobj.get("tags"), ""));
	}

	public long getModifyDate(DBObject dbobj) {
		return getLong(dbobj.get("modifydate"), 0);
	}

	public int getPlayNum(DBObject dbobj) {
		return getInt(dbobj.get("playnum"), 0);
	}

	public int getDocBase() {
		return 2000000000;
	}

	public static final String FIELD_VIDEO_OR_UGC = "videoOrUgc";
	public static final String FIELD_VIDEO_ID = "videoid";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_TAGS = "tags";
	public static final String FIELD_PLAY_NUM = "playnum";
	public static final String FIELD_IS_DEL = "isdel";
	public static final String FIELD_CREATE_DATE = "createdate";
	public static final String FIELD_MODIFY_DATE = "modifydate";
	public static final String FIELD_PIC_PATH = "picturepath";
	public static final String FIELD_IS_FROM_FUNSHION = "fromfs";
	public static final String FIELD_TIEM_LEN = "timelen";
	public static final String FIELD_UPLOADER_ID = "userid";
	public static final String FIELD_UPLOADER_NAME = "username";
	public static final String FIELD_STORED_TITLE = "stitle";
	public static final String FIELD_STORED_TAGS = "stags";
	Logger log = Logger.getLogger("dump");
	public void writeToBuffer(BufferedWriter bufferedWriter, String att, String value) throws IOException{
		bufferedWriter.write("  <");
		bufferedWriter.write(att);
		bufferedWriter.write(">");

		bufferedWriter.write(stripNonCharCodepoints(value));
		bufferedWriter.write("</");
		bufferedWriter.write(att);
		bufferedWriter.write(">");
		bufferedWriter.write('\n');
	}
	public void writeToBuffer(BufferedWriter bufferedWriter) throws IOException{
		bufferedWriter.write(XML_DOC_PREFIX);
		bufferedWriter.write(((Integer)this.docId).toString());
		bufferedWriter.write("\">");


		writeToBuffer(bufferedWriter, FIELD_VIDEO_OR_UGC, ((Integer)this.videoOrUgc).toString());
		writeToBuffer(bufferedWriter, FIELD_VIDEO_ID, ((Integer)this.videoId).toString());

		String title = this.encString(this.title);
		writeToBuffer(bufferedWriter, FIELD_TITLE, title);

		String tags = this.encString(this.tags);
		writeToBuffer(bufferedWriter, FIELD_TAGS, tags);

		writeToBuffer(bufferedWriter, FIELD_IS_DEL, this.isDel ? "1" : "0");

		writeToBuffer(bufferedWriter, FIELD_PLAY_NUM, ((Integer)this.playTimes).toString());

		//		writeToBuffer(bufferedWriter, FIELD_CREATE_DATE, ((Long)this.createdate).toString());
		writeToBuffer(bufferedWriter, FIELD_MODIFY_DATE, ((Long)this.modifydate).toString());

		//			writeToBuffer(bufferedWriter, FIELD_PIC_PATH, this.encString(this.picturePath));
		//
		//			writeToBuffer(bufferedWriter, FIELD_IS_FROM_FUNSHION, ((Integer)this.fromFs).toString());
		//			writeToBuffer(bufferedWriter, FIELD_TIEM_LEN, this.encString(this.timelen));
		//			writeToBuffer(bufferedWriter, FIELD_UPLOADER_ID, ((Integer)this.uploaderId).toString());
		//			writeToBuffer(bufferedWriter, FIELD_UPLOADER_NAME, "");
		//			writeToBuffer(bufferedWriter, FIELD_STORED_TITLE, title);
		//			writeToBuffer(bufferedWriter, FIELD_STORED_TAGS, tags);
		bufferedWriter.write(XML_DOC_SUFFIX);
		bufferedWriter.write('\n');
	}
	public double getDouble(Object o, double dft){
		if(o == null)
			return dft;

		String str = getString(o, "");
		if(str.length() == 0){
			return dft;
		}
		try{
			return Double.parseDouble(str);
		}catch(Exception e){
			log.error("can not covert to double for '" + str + "'");
		}
		return dft;

	}
	public int getInt(Object o, int dft){
		long ret = getLong(o, dft);
		return ret > Integer.MAX_VALUE ? dft :(int) ret;
	}
	public long getLong(Object o, long dft){
		if(o == null)
			return dft;

		String str = getString(o, "");
		if(str.length() == 0){
			return dft;
		}
		try{
			return Long.parseLong(str);
		}catch(Exception e){
			log.error("can not covert to long for '" + str + "'");
		}
		return dft;
	}

	protected String getString(Object object, String dft) {
		if(object == null){
			return dft;
		}
		if(object instanceof String){
			return (String) object;
		}else{
			return object.toString();
		}
	}

	private static void buildBuffer(StringBuilder sb, boolean isFullTextField, String fieldName, String ...typeAndStricts){
		if(isFullTextField){
			sb.append("<sphinx:field name=\"");
		}else{
			sb.append("<sphinx:attr name=\"");
		}
		sb.append(fieldName);
		sb.append("\" ");

		if(typeAndStricts != null && typeAndStricts.length > 0){
			sb.append("type=\"");
			sb.append(typeAndStricts[0]);
			sb.append("\" ");
			if(typeAndStricts.length > 1){
				sb.append(" ");
				sb.append(typeAndStricts[1]);
			}
		}
		sb.append("/>\n");
	}

	public static final String XML_DOCSET_HEAD;
	static{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<sphinx:docset>\n" +
				"<sphinx:schema>\n" );

		buildBuffer(sb, false, FIELD_VIDEO_OR_UGC, "int", "bits=\"8\"");
		buildBuffer(sb, false, FIELD_VIDEO_ID, "int", "bits=\"32\"");

		buildBuffer(sb, true, FIELD_TITLE);
		buildBuffer(sb, true, FIELD_TAGS);

		buildBuffer(sb, false, FIELD_IS_DEL, "int", "bits=\"8\"");
		buildBuffer(sb, false, FIELD_PLAY_NUM, "int", "bits=\"32\"");
		//		buildBuffer(sb, false, FIELD_CREATE_DATE, "timestamp");
		buildBuffer(sb, false, FIELD_MODIFY_DATE, "timestamp");
		//		buildBuffer(sb, false, FIELD_PIC_PATH, "string");
		//		buildBuffer(sb, false, FIELD_IS_FROM_FUNSHION, "int", "bits=\"8\"");
		//		buildBuffer(sb, false, FIELD_TIEM_LEN, "string");
		//		buildBuffer(sb, false, FIELD_UPLOADER_ID, "int", "bits=\"32\"");
		//		buildBuffer(sb, false, FIELD_UPLOADER_NAME, "string");
		//		buildBuffer(sb, false, FIELD_STORED_TITLE, "string");
		//		buildBuffer(sb, false, FIELD_STORED_TAGS, "string");
		sb.append("</sphinx:schema>");

		XML_DOCSET_HEAD = sb.toString();
	}

	public static String rewriteTags(String str){
		if(str == null)
			return "";
		str = str.trim();
		if(str.startsWith("[")){
			str = str.substring(1);
		}
		if(str.endsWith("]")){
			str = str.substring(0, str.length() - 1);
		}
		str = str.replace('\"', ' ').trim();
		return str;
	}
	public String encString(String input) {
		char ch;
		StringBuilder retval = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if(!isGoodUtf8(ch)){//skip non-utf8 characters
				continue;
			}
			if(ch == '<'){
				retval.append("&lt;");
			}else if(ch == '>'){
				retval.append("&gt;");
			}else if(ch == '&'){
				retval.append("&amp;");
			}else if(ch == '"'){
				retval.append("&quot;");
			}else if(ch == '\''){
				retval.append("&apos;");
			}else{
				retval.append(ch);
			}
		}
		return retval.toString();
	}
	/**
	 * check if ch is valid utf8 characters
	 * @param ch
	 * @return
	 */
	public static boolean isGoodUtf8(char ch){
		if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step 0x10000
				ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
				(ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
				(ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd)) {
			return true;
		} 
		return false;
	}
	public static String stripNonCharCodepoints(String input) {
		StringBuilder retval = new StringBuilder();
		char ch;
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (isGoodUtf8(ch)) {
				retval.append(ch);
			}
		}
		return retval.toString();
	}
}

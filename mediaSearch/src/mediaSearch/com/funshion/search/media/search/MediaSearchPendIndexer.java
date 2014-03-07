package com.funshion.search.media.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;

import com.funshion.luc.defines.IRecord;
import com.funshion.luc.defines.PendIndexer;
import com.funshion.search.IndexableRecord;
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixSpecialWordTrimer;


public class MediaSearchPendIndexer extends PendIndexer{

	@Override
	public void index(Document doc, IRecord irec) {
		IndexableRecord rec = irec.getRec();
		String value = rec.valueOf(FieldDefine.FIELD_NAME_CHAR_RELATED_VIDEOLET);
		boolean hasVideolet = value.length() > 0;
		Field fieldHasVideolet = new IntField(FieldDefine.FIELD_NAME_HAS_VIDEOLET, hasVideolet ? 1 : 0, Store.YES);
		doc.add(fieldHasVideolet);
	
		boolean att[] = new boolean[]{
				Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_0)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_1)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_2)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_3)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_4)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_5)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_6)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_7)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_8)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_9)) == 0,
		};
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < att.length; x ++){
			if(att[x]){
				if(sb.length() > 0){
					sb.append('\t');
				}
				sb.append(x);
			}
		}
		String attValues = sb.toString();

		Field fieldTa_0_9 = new TextField(FieldDefine.FIELD_NAME_TA_0_9, attValues, Store.YES);
		doc.add(fieldTa_0_9);

		String ot = rec.valueOf(FieldDefine.FIELD_NAME_NAME_OT);
		String cn = rec.valueOf(FieldDefine.FIELD_NAME_NAME_CN);
		String orgCn = cn;
		final String []trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(cn);
		MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(trimed == null ? orgCn : trimed[0]);

		if(trimed != null){
			String alia = MediaTitleSuffixSpecialWordTrimer.instance.getAlia(trimed[1]);
			ot = ot + "\t" + trimed[0] + " " + alia;
			//			for(String v : alias){
			//				ot = ot + "\t" + trimed[0] + " " + v;
			//			}
			//			ot = ot + "\t" + trimed[0];
		}
		if(mr != null){
			cn = cn + "\t" + CnNumRewrite.cnNumRewrite(mr.norm());
			ot = ot + "\t" + CnNumRewrite.cnNumRewrite(mr.getNewNameCn()); 
		}else{
			String cnNew = CnNumRewrite.cnNumTryRewrite(cn);
			if(cnNew != null){
				cn = cn + "\t" + cnNew;
			}
			String otNew = CnNumRewrite.cnNumTryRewrite(ot);
			if(otNew != null){
				ot = ot + "\t" + otNew;
			}
		}
		StringBuilder sbuf = new StringBuilder();
		appendTitle(cn, sbuf);
		appendTitle(rec.valueOf(FieldDefine.FIELD_NAME_NAME_EN), sbuf);
		appendTitle(ot, sbuf);
		appendTitle(rec.valueOf(FieldDefine.FIELD_NAME_NAME_SN), sbuf);
		String strBuf = sbuf.toString();
		Field field = new TextField(FieldDefine.FIELD_NAME_NAMES, strBuf, Store.NO);
		doc.add(field);
		
	}
	private void appendTitle(String tokens, StringBuilder sb){
		if(sb.length() > 0){
			sb.append('\n');
		}
		tokens = tokens.trim();
		if(tokens.length() == 0){
			tokens = " ";
		}
		sb.append(tokens);
		String []xx = tokens.split("\t");
		for(String v : xx){
			v = v.trim();
			if(v.length() == 0){
				continue;
			}
//			String cvtFj = F2JConvert.instance.conver(v);
//			if(!v.equals(cvtFj)){
//				sb.append('\t');
//				sb.append(cvtFj);
//			}
		}

	}

}

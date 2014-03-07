package com.funshion.search;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;

import com.funshion.luc.defines.EnumTextField;
import com.funshion.luc.defines.IFieldDefine;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.luc.defines.IndexActionDetector.ActionType;
import com.funshion.luc.defines.PendIndexer;


public abstract class IndexableRecord {
	public abstract String valueOf(String key);
	public abstract ActionType getActionType();
	
	protected void floatField(Document doc, IFieldDefine fld, String value){
		Field field = new FloatField(fld.fieldName, Float.parseFloat(value), fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}
	protected void longField(Document doc, IFieldDefine fld, String value){
		Field field = new LongField(fld.fieldName, Long.parseLong(value), fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}
	protected void intField(Document doc, IFieldDefine fld, String value){
		Field field = new IntField(fld.fieldName, Integer.parseInt(value), fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}

	protected void strField(Document doc, IFieldDefine fld, String value){
		Field field = new StringField(fld.fieldName, value, fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}
	private void textField(Document doc, IFieldDefine fld, String value) {
		Field field = new TextField(fld.fieldName, value, fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}
	protected void enumField(Document doc, IFieldDefine fld, String value){
		Field field = new EnumTextField(fld.fieldName, value, fld.store ? Store.YES : Store.NO);
		doc.add(field);
	}

	public void indexRecord(Document doc, PendIndexer[]pendIndexers){
		Collection<IFieldDefine> defs = ITableDefine.instance.getFieldDefines();
		for(IFieldDefine fld : defs){
			if(fld.index || fld.store){
				String value = valueOf(fld.fieldName);
				switch(fld.aType){
				case aInt:
					this.intField(doc, fld, value);
					break;
				case aLong:
					this.longField(doc, fld, value);
					break;
				case aFloat:
					this.floatField(doc, fld, value);
					break;
				case aStr:
					this.strField(doc, fld, value);
					break;
				case eStr:
					this.enumField(doc, fld, value);
					break;
				case aText:
					this.textField(doc, fld, value);
					break;
				default:
					throw new RuntimeException("can not index field " + fld);
				}
			}
		}
		for(PendIndexer pi : pendIndexers){
			pi.index(doc, this);
		}
	}
	
}

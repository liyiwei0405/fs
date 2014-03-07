package com.funshion.luc.defines;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;

public final class EnumTextField extends Field {

  /** Indexed, tokenized, stored. */
  public static final FieldType TYPE_STORED = new FieldType();
  public static final FieldType TYPE_NOT_STORED = new FieldType();
  static {
	  TYPE_STORED.setStored(true);
	  TYPE_STORED.setIndexed(true);
	  TYPE_STORED.setTokenized(true);
	  TYPE_STORED.setOmitNorms(true);
	  TYPE_STORED.setIndexOptions(IndexOptions.DOCS_ONLY);
	  
	  TYPE_STORED.setStoreTermVectorOffsets(false);
	  TYPE_STORED.setStoreTermVectorPayloads(false);
	  TYPE_STORED.setStoreTermVectorPositions(false);
	  TYPE_STORED.setStoreTermVectors(false);
	  TYPE_STORED.freeze();
	  
	  TYPE_NOT_STORED.setStored(false);
	  TYPE_NOT_STORED.setIndexed(true);
	  TYPE_NOT_STORED.setTokenized(true);
	  TYPE_NOT_STORED.setOmitNorms(true);
	  
	  TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_ONLY);
	  TYPE_NOT_STORED.setStoreTermVectorOffsets(false);
	  TYPE_NOT_STORED.setStoreTermVectorPayloads(false);
	  TYPE_NOT_STORED.setStoreTermVectorPositions(false);
	  TYPE_NOT_STORED.setStoreTermVectors(false);
	  TYPE_NOT_STORED.freeze();
  }

  /** Creates a new TextField with String value. 
   * @param name field name
   * @param value string value
   * @param store Store.YES if the content should also be stored
   * @throws IllegalArgumentException if the field name or value is null.
   */
  public EnumTextField(String name, String value, Store store) {
    super(name, value, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
  }
  
 
}

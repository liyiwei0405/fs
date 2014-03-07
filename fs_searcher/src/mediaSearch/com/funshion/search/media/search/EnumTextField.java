package com.funshion.search.media.search;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;

/** A field that is indexed and tokenized, without term
 *  vectors.  For example this would be used on a 'body'
 *  field, that contains the bulk of a document's text. */

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

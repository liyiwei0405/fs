/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.funshion.search.media.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangeQuery implements org.apache.thrift.TBase<RangeQuery, RangeQuery._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RangeQuery");

  private static final org.apache.thrift.protocol.TField FIELD_FIELD_DESC = new org.apache.thrift.protocol.TField("field", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField R_START_FIELD_DESC = new org.apache.thrift.protocol.TField("rStart", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField R_END_FIELD_DESC = new org.apache.thrift.protocol.TField("rEnd", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField EXCLUDE_FIELD_DESC = new org.apache.thrift.protocol.TField("exclude", org.apache.thrift.protocol.TType.BOOL, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RangeQueryStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RangeQueryTupleSchemeFactory());
  }

  public String field; // required
  public int rStart; // required
  public int rEnd; // required
  public boolean exclude; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FIELD((short)1, "field"),
    R_START((short)2, "rStart"),
    R_END((short)3, "rEnd"),
    EXCLUDE((short)4, "exclude");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FIELD
          return FIELD;
        case 2: // R_START
          return R_START;
        case 3: // R_END
          return R_END;
        case 4: // EXCLUDE
          return EXCLUDE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __RSTART_ISSET_ID = 0;
  private static final int __REND_ISSET_ID = 1;
  private static final int __EXCLUDE_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FIELD, new org.apache.thrift.meta_data.FieldMetaData("field", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.R_START, new org.apache.thrift.meta_data.FieldMetaData("rStart", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.R_END, new org.apache.thrift.meta_data.FieldMetaData("rEnd", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.EXCLUDE, new org.apache.thrift.meta_data.FieldMetaData("exclude", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RangeQuery.class, metaDataMap);
  }

  public RangeQuery() {
  }

  public RangeQuery(
    String field,
    int rStart,
    int rEnd,
    boolean exclude)
  {
    this();
    this.field = field;
    this.rStart = rStart;
    setRStartIsSet(true);
    this.rEnd = rEnd;
    setREndIsSet(true);
    this.exclude = exclude;
    setExcludeIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RangeQuery(RangeQuery other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetField()) {
      this.field = other.field;
    }
    this.rStart = other.rStart;
    this.rEnd = other.rEnd;
    this.exclude = other.exclude;
  }

  public RangeQuery deepCopy() {
    return new RangeQuery(this);
  }

  @Override
  public void clear() {
    this.field = null;
    setRStartIsSet(false);
    this.rStart = 0;
    setREndIsSet(false);
    this.rEnd = 0;
    setExcludeIsSet(false);
    this.exclude = false;
  }

  public String getField() {
    return this.field;
  }

  public RangeQuery setField(String field) {
    this.field = field;
    return this;
  }

  public void unsetField() {
    this.field = null;
  }

  /** Returns true if field field is set (has been assigned a value) and false otherwise */
  public boolean isSetField() {
    return this.field != null;
  }

  public void setFieldIsSet(boolean value) {
    if (!value) {
      this.field = null;
    }
  }

  public int getRStart() {
    return this.rStart;
  }

  public RangeQuery setRStart(int rStart) {
    this.rStart = rStart;
    setRStartIsSet(true);
    return this;
  }

  public void unsetRStart() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __RSTART_ISSET_ID);
  }

  /** Returns true if field rStart is set (has been assigned a value) and false otherwise */
  public boolean isSetRStart() {
    return EncodingUtils.testBit(__isset_bitfield, __RSTART_ISSET_ID);
  }

  public void setRStartIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __RSTART_ISSET_ID, value);
  }

  public int getREnd() {
    return this.rEnd;
  }

  public RangeQuery setREnd(int rEnd) {
    this.rEnd = rEnd;
    setREndIsSet(true);
    return this;
  }

  public void unsetREnd() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REND_ISSET_ID);
  }

  /** Returns true if field rEnd is set (has been assigned a value) and false otherwise */
  public boolean isSetREnd() {
    return EncodingUtils.testBit(__isset_bitfield, __REND_ISSET_ID);
  }

  public void setREndIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REND_ISSET_ID, value);
  }

  public boolean isExclude() {
    return this.exclude;
  }

  public RangeQuery setExclude(boolean exclude) {
    this.exclude = exclude;
    setExcludeIsSet(true);
    return this;
  }

  public void unsetExclude() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __EXCLUDE_ISSET_ID);
  }

  /** Returns true if field exclude is set (has been assigned a value) and false otherwise */
  public boolean isSetExclude() {
    return EncodingUtils.testBit(__isset_bitfield, __EXCLUDE_ISSET_ID);
  }

  public void setExcludeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __EXCLUDE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case FIELD:
      if (value == null) {
        unsetField();
      } else {
        setField((String)value);
      }
      break;

    case R_START:
      if (value == null) {
        unsetRStart();
      } else {
        setRStart((Integer)value);
      }
      break;

    case R_END:
      if (value == null) {
        unsetREnd();
      } else {
        setREnd((Integer)value);
      }
      break;

    case EXCLUDE:
      if (value == null) {
        unsetExclude();
      } else {
        setExclude((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case FIELD:
      return getField();

    case R_START:
      return Integer.valueOf(getRStart());

    case R_END:
      return Integer.valueOf(getREnd());

    case EXCLUDE:
      return Boolean.valueOf(isExclude());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case FIELD:
      return isSetField();
    case R_START:
      return isSetRStart();
    case R_END:
      return isSetREnd();
    case EXCLUDE:
      return isSetExclude();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RangeQuery)
      return this.equals((RangeQuery)that);
    return false;
  }

  public boolean equals(RangeQuery that) {
    if (that == null)
      return false;

    boolean this_present_field = true && this.isSetField();
    boolean that_present_field = true && that.isSetField();
    if (this_present_field || that_present_field) {
      if (!(this_present_field && that_present_field))
        return false;
      if (!this.field.equals(that.field))
        return false;
    }

    boolean this_present_rStart = true;
    boolean that_present_rStart = true;
    if (this_present_rStart || that_present_rStart) {
      if (!(this_present_rStart && that_present_rStart))
        return false;
      if (this.rStart != that.rStart)
        return false;
    }

    boolean this_present_rEnd = true;
    boolean that_present_rEnd = true;
    if (this_present_rEnd || that_present_rEnd) {
      if (!(this_present_rEnd && that_present_rEnd))
        return false;
      if (this.rEnd != that.rEnd)
        return false;
    }

    boolean this_present_exclude = true;
    boolean that_present_exclude = true;
    if (this_present_exclude || that_present_exclude) {
      if (!(this_present_exclude && that_present_exclude))
        return false;
      if (this.exclude != that.exclude)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(RangeQuery other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    RangeQuery typedOther = (RangeQuery)other;

    lastComparison = Boolean.valueOf(isSetField()).compareTo(typedOther.isSetField());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetField()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.field, typedOther.field);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRStart()).compareTo(typedOther.isSetRStart());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRStart()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rStart, typedOther.rStart);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetREnd()).compareTo(typedOther.isSetREnd());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetREnd()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rEnd, typedOther.rEnd);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetExclude()).compareTo(typedOther.isSetExclude());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetExclude()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.exclude, typedOther.exclude);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RangeQuery(");
    boolean first = true;

    sb.append("field:");
    if (this.field == null) {
      sb.append("null");
    } else {
      sb.append(this.field);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rStart:");
    sb.append(this.rStart);
    first = false;
    if (!first) sb.append(", ");
    sb.append("rEnd:");
    sb.append(this.rEnd);
    first = false;
    if (!first) sb.append(", ");
    sb.append("exclude:");
    sb.append(this.exclude);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RangeQueryStandardSchemeFactory implements SchemeFactory {
    public RangeQueryStandardScheme getScheme() {
      return new RangeQueryStandardScheme();
    }
  }

  private static class RangeQueryStandardScheme extends StandardScheme<RangeQuery> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RangeQuery struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FIELD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.field = iprot.readString();
              struct.setFieldIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // R_START
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.rStart = iprot.readI32();
              struct.setRStartIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // R_END
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.rEnd = iprot.readI32();
              struct.setREndIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // EXCLUDE
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.exclude = iprot.readBool();
              struct.setExcludeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, RangeQuery struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.field != null) {
        oprot.writeFieldBegin(FIELD_FIELD_DESC);
        oprot.writeString(struct.field);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(R_START_FIELD_DESC);
      oprot.writeI32(struct.rStart);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(R_END_FIELD_DESC);
      oprot.writeI32(struct.rEnd);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(EXCLUDE_FIELD_DESC);
      oprot.writeBool(struct.exclude);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RangeQueryTupleSchemeFactory implements SchemeFactory {
    public RangeQueryTupleScheme getScheme() {
      return new RangeQueryTupleScheme();
    }
  }

  private static class RangeQueryTupleScheme extends TupleScheme<RangeQuery> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RangeQuery struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetField()) {
        optionals.set(0);
      }
      if (struct.isSetRStart()) {
        optionals.set(1);
      }
      if (struct.isSetREnd()) {
        optionals.set(2);
      }
      if (struct.isSetExclude()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetField()) {
        oprot.writeString(struct.field);
      }
      if (struct.isSetRStart()) {
        oprot.writeI32(struct.rStart);
      }
      if (struct.isSetREnd()) {
        oprot.writeI32(struct.rEnd);
      }
      if (struct.isSetExclude()) {
        oprot.writeBool(struct.exclude);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RangeQuery struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.field = iprot.readString();
        struct.setFieldIsSet(true);
      }
      if (incoming.get(1)) {
        struct.rStart = iprot.readI32();
        struct.setRStartIsSet(true);
      }
      if (incoming.get(2)) {
        struct.rEnd = iprot.readI32();
        struct.setREndIsSet(true);
      }
      if (incoming.get(3)) {
        struct.exclude = iprot.readBool();
        struct.setExcludeIsSet(true);
      }
    }
  }

}


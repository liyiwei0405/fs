/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.funshion.searchHint.thrift.v1;

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

public class HintRecord implements org.apache.thrift.TBase<HintRecord, HintRecord._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("HintRecord");

  private static final org.apache.thrift.protocol.TField HINT_FIELD_DESC = new org.apache.thrift.protocol.TField("hint", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new HintRecordStandardSchemeFactory());
    schemes.put(TupleScheme.class, new HintRecordTupleSchemeFactory());
  }

  public String hint; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    HINT((short)1, "hint");

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
        case 1: // HINT
          return HINT;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.HINT, new org.apache.thrift.meta_data.FieldMetaData("hint", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(HintRecord.class, metaDataMap);
  }

  public HintRecord() {
  }

  public HintRecord(
    String hint)
  {
    this();
    this.hint = hint;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public HintRecord(HintRecord other) {
    if (other.isSetHint()) {
      this.hint = other.hint;
    }
  }

  public HintRecord deepCopy() {
    return new HintRecord(this);
  }

  @Override
  public void clear() {
    this.hint = null;
  }

  public String getHint() {
    return this.hint;
  }

  public HintRecord setHint(String hint) {
    this.hint = hint;
    return this;
  }

  public void unsetHint() {
    this.hint = null;
  }

  /** Returns true if field hint is set (has been assigned a value) and false otherwise */
  public boolean isSetHint() {
    return this.hint != null;
  }

  public void setHintIsSet(boolean value) {
    if (!value) {
      this.hint = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case HINT:
      if (value == null) {
        unsetHint();
      } else {
        setHint((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case HINT:
      return getHint();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case HINT:
      return isSetHint();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof HintRecord)
      return this.equals((HintRecord)that);
    return false;
  }

  public boolean equals(HintRecord that) {
    if (that == null)
      return false;

    boolean this_present_hint = true && this.isSetHint();
    boolean that_present_hint = true && that.isSetHint();
    if (this_present_hint || that_present_hint) {
      if (!(this_present_hint && that_present_hint))
        return false;
      if (!this.hint.equals(that.hint))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(HintRecord other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    HintRecord typedOther = (HintRecord)other;

    lastComparison = Boolean.valueOf(isSetHint()).compareTo(typedOther.isSetHint());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHint()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hint, typedOther.hint);
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
    StringBuilder sb = new StringBuilder("HintRecord(");
    boolean first = true;

    sb.append("hint:");
    if (this.hint == null) {
      sb.append("null");
    } else {
      sb.append(this.hint);
    }
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class HintRecordStandardSchemeFactory implements SchemeFactory {
    public HintRecordStandardScheme getScheme() {
      return new HintRecordStandardScheme();
    }
  }

  private static class HintRecordStandardScheme extends StandardScheme<HintRecord> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, HintRecord struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // HINT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.hint = iprot.readString();
              struct.setHintIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, HintRecord struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.hint != null) {
        oprot.writeFieldBegin(HINT_FIELD_DESC);
        oprot.writeString(struct.hint);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class HintRecordTupleSchemeFactory implements SchemeFactory {
    public HintRecordTupleScheme getScheme() {
      return new HintRecordTupleScheme();
    }
  }

  private static class HintRecordTupleScheme extends TupleScheme<HintRecord> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, HintRecord struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetHint()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetHint()) {
        oprot.writeString(struct.hint);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, HintRecord struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.hint = iprot.readString();
        struct.setHintIsSet(true);
      }
    }
  }

}

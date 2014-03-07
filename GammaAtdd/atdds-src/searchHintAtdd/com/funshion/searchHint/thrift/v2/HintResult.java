/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.funshion.searchHint.thrift.v2;

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

public class HintResult implements org.apache.thrift.TBase<HintResult, HintResult._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("HintResult");

  private static final org.apache.thrift.protocol.TField RET_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("retCode", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField RET_MSG_FIELD_DESC = new org.apache.thrift.protocol.TField("retMsg", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField RECORDS_FIELD_DESC = new org.apache.thrift.protocol.TField("records", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new HintResultStandardSchemeFactory());
    schemes.put(TupleScheme.class, new HintResultTupleSchemeFactory());
  }

  public int retCode; // required
  public String retMsg; // required
  public List<HintRecord> records; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RET_CODE((short)1, "retCode"),
    RET_MSG((short)2, "retMsg"),
    RECORDS((short)3, "records");

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
        case 1: // RET_CODE
          return RET_CODE;
        case 2: // RET_MSG
          return RET_MSG;
        case 3: // RECORDS
          return RECORDS;
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
  private static final int __RETCODE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RET_CODE, new org.apache.thrift.meta_data.FieldMetaData("retCode", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.RET_MSG, new org.apache.thrift.meta_data.FieldMetaData("retMsg", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RECORDS, new org.apache.thrift.meta_data.FieldMetaData("records", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, HintRecord.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(HintResult.class, metaDataMap);
  }

  public HintResult() {
  }

  public HintResult(
    int retCode,
    String retMsg,
    List<HintRecord> records)
  {
    this();
    this.retCode = retCode;
    setRetCodeIsSet(true);
    this.retMsg = retMsg;
    this.records = records;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public HintResult(HintResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.retCode = other.retCode;
    if (other.isSetRetMsg()) {
      this.retMsg = other.retMsg;
    }
    if (other.isSetRecords()) {
      List<HintRecord> __this__records = new ArrayList<HintRecord>();
      for (HintRecord other_element : other.records) {
        __this__records.add(new HintRecord(other_element));
      }
      this.records = __this__records;
    }
  }

  public HintResult deepCopy() {
    return new HintResult(this);
  }

  @Override
  public void clear() {
    setRetCodeIsSet(false);
    this.retCode = 0;
    this.retMsg = null;
    this.records = null;
  }

  public int getRetCode() {
    return this.retCode;
  }

  public HintResult setRetCode(int retCode) {
    this.retCode = retCode;
    setRetCodeIsSet(true);
    return this;
  }

  public void unsetRetCode() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __RETCODE_ISSET_ID);
  }

  /** Returns true if field retCode is set (has been assigned a value) and false otherwise */
  public boolean isSetRetCode() {
    return EncodingUtils.testBit(__isset_bitfield, __RETCODE_ISSET_ID);
  }

  public void setRetCodeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __RETCODE_ISSET_ID, value);
  }

  public String getRetMsg() {
    return this.retMsg;
  }

  public HintResult setRetMsg(String retMsg) {
    this.retMsg = retMsg;
    return this;
  }

  public void unsetRetMsg() {
    this.retMsg = null;
  }

  /** Returns true if field retMsg is set (has been assigned a value) and false otherwise */
  public boolean isSetRetMsg() {
    return this.retMsg != null;
  }

  public void setRetMsgIsSet(boolean value) {
    if (!value) {
      this.retMsg = null;
    }
  }

  public int getRecordsSize() {
    return (this.records == null) ? 0 : this.records.size();
  }

  public java.util.Iterator<HintRecord> getRecordsIterator() {
    return (this.records == null) ? null : this.records.iterator();
  }

  public void addToRecords(HintRecord elem) {
    if (this.records == null) {
      this.records = new ArrayList<HintRecord>();
    }
    this.records.add(elem);
  }

  public List<HintRecord> getRecords() {
    return this.records;
  }

  public HintResult setRecords(List<HintRecord> records) {
    this.records = records;
    return this;
  }

  public void unsetRecords() {
    this.records = null;
  }

  /** Returns true if field records is set (has been assigned a value) and false otherwise */
  public boolean isSetRecords() {
    return this.records != null;
  }

  public void setRecordsIsSet(boolean value) {
    if (!value) {
      this.records = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RET_CODE:
      if (value == null) {
        unsetRetCode();
      } else {
        setRetCode((Integer)value);
      }
      break;

    case RET_MSG:
      if (value == null) {
        unsetRetMsg();
      } else {
        setRetMsg((String)value);
      }
      break;

    case RECORDS:
      if (value == null) {
        unsetRecords();
      } else {
        setRecords((List<HintRecord>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RET_CODE:
      return Integer.valueOf(getRetCode());

    case RET_MSG:
      return getRetMsg();

    case RECORDS:
      return getRecords();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RET_CODE:
      return isSetRetCode();
    case RET_MSG:
      return isSetRetMsg();
    case RECORDS:
      return isSetRecords();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof HintResult)
      return this.equals((HintResult)that);
    return false;
  }

  public boolean equals(HintResult that) {
    if (that == null)
      return false;

    boolean this_present_retCode = true;
    boolean that_present_retCode = true;
    if (this_present_retCode || that_present_retCode) {
      if (!(this_present_retCode && that_present_retCode))
        return false;
      if (this.retCode != that.retCode)
        return false;
    }

    boolean this_present_retMsg = true && this.isSetRetMsg();
    boolean that_present_retMsg = true && that.isSetRetMsg();
    if (this_present_retMsg || that_present_retMsg) {
      if (!(this_present_retMsg && that_present_retMsg))
        return false;
      if (!this.retMsg.equals(that.retMsg))
        return false;
    }

    boolean this_present_records = true && this.isSetRecords();
    boolean that_present_records = true && that.isSetRecords();
    if (this_present_records || that_present_records) {
      if (!(this_present_records && that_present_records))
        return false;
      if (!this.records.equals(that.records))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(HintResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    HintResult typedOther = (HintResult)other;

    lastComparison = Boolean.valueOf(isSetRetCode()).compareTo(typedOther.isSetRetCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRetCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.retCode, typedOther.retCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRetMsg()).compareTo(typedOther.isSetRetMsg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRetMsg()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.retMsg, typedOther.retMsg);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRecords()).compareTo(typedOther.isSetRecords());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRecords()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.records, typedOther.records);
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
    StringBuilder sb = new StringBuilder("HintResult(");
    boolean first = true;

    sb.append("retCode:");
    sb.append(this.retCode);
    first = false;
    if (!first) sb.append(", ");
    sb.append("retMsg:");
    if (this.retMsg == null) {
      sb.append("null");
    } else {
      sb.append(this.retMsg);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("records:");
    if (this.records == null) {
      sb.append("null");
    } else {
      sb.append(this.records);
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class HintResultStandardSchemeFactory implements SchemeFactory {
    public HintResultStandardScheme getScheme() {
      return new HintResultStandardScheme();
    }
  }

  private static class HintResultStandardScheme extends StandardScheme<HintResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, HintResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RET_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.retCode = iprot.readI32();
              struct.setRetCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // RET_MSG
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.retMsg = iprot.readString();
              struct.setRetMsgIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RECORDS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.records = new ArrayList<HintRecord>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  HintRecord _elem2; // required
                  _elem2 = new HintRecord();
                  _elem2.read(iprot);
                  struct.records.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setRecordsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, HintResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(RET_CODE_FIELD_DESC);
      oprot.writeI32(struct.retCode);
      oprot.writeFieldEnd();
      if (struct.retMsg != null) {
        oprot.writeFieldBegin(RET_MSG_FIELD_DESC);
        oprot.writeString(struct.retMsg);
        oprot.writeFieldEnd();
      }
      if (struct.records != null) {
        oprot.writeFieldBegin(RECORDS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.records.size()));
          for (HintRecord _iter3 : struct.records)
          {
            _iter3.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class HintResultTupleSchemeFactory implements SchemeFactory {
    public HintResultTupleScheme getScheme() {
      return new HintResultTupleScheme();
    }
  }

  private static class HintResultTupleScheme extends TupleScheme<HintResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, HintResult struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetRetCode()) {
        optionals.set(0);
      }
      if (struct.isSetRetMsg()) {
        optionals.set(1);
      }
      if (struct.isSetRecords()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetRetCode()) {
        oprot.writeI32(struct.retCode);
      }
      if (struct.isSetRetMsg()) {
        oprot.writeString(struct.retMsg);
      }
      if (struct.isSetRecords()) {
        {
          oprot.writeI32(struct.records.size());
          for (HintRecord _iter4 : struct.records)
          {
            _iter4.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, HintResult struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.retCode = iprot.readI32();
        struct.setRetCodeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.retMsg = iprot.readString();
        struct.setRetMsgIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.records = new ArrayList<HintRecord>(_list5.size);
          for (int _i6 = 0; _i6 < _list5.size; ++_i6)
          {
            HintRecord _elem7; // required
            _elem7 = new HintRecord();
            _elem7.read(iprot);
            struct.records.add(_elem7);
          }
        }
        struct.setRecordsIsSet(true);
      }
    }
  }

}

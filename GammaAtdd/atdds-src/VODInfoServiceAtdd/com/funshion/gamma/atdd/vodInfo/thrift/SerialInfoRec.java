/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.funshion.gamma.atdd.vodInfo.thrift;

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

public class SerialInfoRec implements org.apache.thrift.TBase<SerialInfoRec, SerialInfoRec._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SerialInfoRec");

  private static final org.apache.thrift.protocol.TField SERIAL_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("serialID", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SERIAL_NO_FIELD_DESC = new org.apache.thrift.protocol.TField("serialNo", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField SERIAL_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("serialName", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField SERIAL_PIC_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("serialPicID", org.apache.thrift.protocol.TType.I32, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SerialInfoRecStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SerialInfoRecTupleSchemeFactory());
  }

  public int serialID; // required
  public int serialNo; // required
  public String serialName; // required
  public int serialPicID; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SERIAL_ID((short)1, "serialID"),
    SERIAL_NO((short)2, "serialNo"),
    SERIAL_NAME((short)3, "serialName"),
    SERIAL_PIC_ID((short)4, "serialPicID");

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
        case 1: // SERIAL_ID
          return SERIAL_ID;
        case 2: // SERIAL_NO
          return SERIAL_NO;
        case 3: // SERIAL_NAME
          return SERIAL_NAME;
        case 4: // SERIAL_PIC_ID
          return SERIAL_PIC_ID;
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
  private static final int __SERIALID_ISSET_ID = 0;
  private static final int __SERIALNO_ISSET_ID = 1;
  private static final int __SERIALPICID_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SERIAL_ID, new org.apache.thrift.meta_data.FieldMetaData("serialID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SERIAL_NO, new org.apache.thrift.meta_data.FieldMetaData("serialNo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SERIAL_NAME, new org.apache.thrift.meta_data.FieldMetaData("serialName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SERIAL_PIC_ID, new org.apache.thrift.meta_data.FieldMetaData("serialPicID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SerialInfoRec.class, metaDataMap);
  }

  public SerialInfoRec() {
  }

  public SerialInfoRec(
    int serialID,
    int serialNo,
    String serialName,
    int serialPicID)
  {
    this();
    this.serialID = serialID;
    setSerialIDIsSet(true);
    this.serialNo = serialNo;
    setSerialNoIsSet(true);
    this.serialName = serialName;
    this.serialPicID = serialPicID;
    setSerialPicIDIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SerialInfoRec(SerialInfoRec other) {
    __isset_bitfield = other.__isset_bitfield;
    this.serialID = other.serialID;
    this.serialNo = other.serialNo;
    if (other.isSetSerialName()) {
      this.serialName = other.serialName;
    }
    this.serialPicID = other.serialPicID;
  }

  public SerialInfoRec deepCopy() {
    return new SerialInfoRec(this);
  }

  @Override
  public void clear() {
    setSerialIDIsSet(false);
    this.serialID = 0;
    setSerialNoIsSet(false);
    this.serialNo = 0;
    this.serialName = null;
    setSerialPicIDIsSet(false);
    this.serialPicID = 0;
  }

  public int getSerialID() {
    return this.serialID;
  }

  public SerialInfoRec setSerialID(int serialID) {
    this.serialID = serialID;
    setSerialIDIsSet(true);
    return this;
  }

  public void unsetSerialID() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SERIALID_ISSET_ID);
  }

  /** Returns true if field serialID is set (has been assigned a value) and false otherwise */
  public boolean isSetSerialID() {
    return EncodingUtils.testBit(__isset_bitfield, __SERIALID_ISSET_ID);
  }

  public void setSerialIDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SERIALID_ISSET_ID, value);
  }

  public int getSerialNo() {
    return this.serialNo;
  }

  public SerialInfoRec setSerialNo(int serialNo) {
    this.serialNo = serialNo;
    setSerialNoIsSet(true);
    return this;
  }

  public void unsetSerialNo() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SERIALNO_ISSET_ID);
  }

  /** Returns true if field serialNo is set (has been assigned a value) and false otherwise */
  public boolean isSetSerialNo() {
    return EncodingUtils.testBit(__isset_bitfield, __SERIALNO_ISSET_ID);
  }

  public void setSerialNoIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SERIALNO_ISSET_ID, value);
  }

  public String getSerialName() {
    return this.serialName;
  }

  public SerialInfoRec setSerialName(String serialName) {
    this.serialName = serialName;
    return this;
  }

  public void unsetSerialName() {
    this.serialName = null;
  }

  /** Returns true if field serialName is set (has been assigned a value) and false otherwise */
  public boolean isSetSerialName() {
    return this.serialName != null;
  }

  public void setSerialNameIsSet(boolean value) {
    if (!value) {
      this.serialName = null;
    }
  }

  public int getSerialPicID() {
    return this.serialPicID;
  }

  public SerialInfoRec setSerialPicID(int serialPicID) {
    this.serialPicID = serialPicID;
    setSerialPicIDIsSet(true);
    return this;
  }

  public void unsetSerialPicID() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SERIALPICID_ISSET_ID);
  }

  /** Returns true if field serialPicID is set (has been assigned a value) and false otherwise */
  public boolean isSetSerialPicID() {
    return EncodingUtils.testBit(__isset_bitfield, __SERIALPICID_ISSET_ID);
  }

  public void setSerialPicIDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SERIALPICID_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SERIAL_ID:
      if (value == null) {
        unsetSerialID();
      } else {
        setSerialID((Integer)value);
      }
      break;

    case SERIAL_NO:
      if (value == null) {
        unsetSerialNo();
      } else {
        setSerialNo((Integer)value);
      }
      break;

    case SERIAL_NAME:
      if (value == null) {
        unsetSerialName();
      } else {
        setSerialName((String)value);
      }
      break;

    case SERIAL_PIC_ID:
      if (value == null) {
        unsetSerialPicID();
      } else {
        setSerialPicID((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SERIAL_ID:
      return Integer.valueOf(getSerialID());

    case SERIAL_NO:
      return Integer.valueOf(getSerialNo());

    case SERIAL_NAME:
      return getSerialName();

    case SERIAL_PIC_ID:
      return Integer.valueOf(getSerialPicID());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SERIAL_ID:
      return isSetSerialID();
    case SERIAL_NO:
      return isSetSerialNo();
    case SERIAL_NAME:
      return isSetSerialName();
    case SERIAL_PIC_ID:
      return isSetSerialPicID();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SerialInfoRec)
      return this.equals((SerialInfoRec)that);
    return false;
  }

  public boolean equals(SerialInfoRec that) {
    if (that == null)
      return false;

    boolean this_present_serialID = true;
    boolean that_present_serialID = true;
    if (this_present_serialID || that_present_serialID) {
      if (!(this_present_serialID && that_present_serialID))
        return false;
      if (this.serialID != that.serialID)
        return false;
    }

    boolean this_present_serialNo = true;
    boolean that_present_serialNo = true;
    if (this_present_serialNo || that_present_serialNo) {
      if (!(this_present_serialNo && that_present_serialNo))
        return false;
      if (this.serialNo != that.serialNo)
        return false;
    }

    boolean this_present_serialName = true && this.isSetSerialName();
    boolean that_present_serialName = true && that.isSetSerialName();
    if (this_present_serialName || that_present_serialName) {
      if (!(this_present_serialName && that_present_serialName))
        return false;
      if (!this.serialName.equals(that.serialName))
        return false;
    }

    boolean this_present_serialPicID = true;
    boolean that_present_serialPicID = true;
    if (this_present_serialPicID || that_present_serialPicID) {
      if (!(this_present_serialPicID && that_present_serialPicID))
        return false;
      if (this.serialPicID != that.serialPicID)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(SerialInfoRec other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SerialInfoRec typedOther = (SerialInfoRec)other;

    lastComparison = Boolean.valueOf(isSetSerialID()).compareTo(typedOther.isSetSerialID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSerialID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serialID, typedOther.serialID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSerialNo()).compareTo(typedOther.isSetSerialNo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSerialNo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serialNo, typedOther.serialNo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSerialName()).compareTo(typedOther.isSetSerialName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSerialName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serialName, typedOther.serialName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSerialPicID()).compareTo(typedOther.isSetSerialPicID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSerialPicID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serialPicID, typedOther.serialPicID);
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
    StringBuilder sb = new StringBuilder("SerialInfoRec(");
    boolean first = true;

    sb.append("serialID:");
    sb.append(this.serialID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("serialNo:");
    sb.append(this.serialNo);
    first = false;
    if (!first) sb.append(", ");
    sb.append("serialName:");
    if (this.serialName == null) {
      sb.append("null");
    } else {
      sb.append(this.serialName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("serialPicID:");
    sb.append(this.serialPicID);
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

  private static class SerialInfoRecStandardSchemeFactory implements SchemeFactory {
    public SerialInfoRecStandardScheme getScheme() {
      return new SerialInfoRecStandardScheme();
    }
  }

  private static class SerialInfoRecStandardScheme extends StandardScheme<SerialInfoRec> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SerialInfoRec struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SERIAL_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.serialID = iprot.readI32();
              struct.setSerialIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SERIAL_NO
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.serialNo = iprot.readI32();
              struct.setSerialNoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SERIAL_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.serialName = iprot.readString();
              struct.setSerialNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // SERIAL_PIC_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.serialPicID = iprot.readI32();
              struct.setSerialPicIDIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SerialInfoRec struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(SERIAL_ID_FIELD_DESC);
      oprot.writeI32(struct.serialID);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SERIAL_NO_FIELD_DESC);
      oprot.writeI32(struct.serialNo);
      oprot.writeFieldEnd();
      if (struct.serialName != null) {
        oprot.writeFieldBegin(SERIAL_NAME_FIELD_DESC);
        oprot.writeString(struct.serialName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SERIAL_PIC_ID_FIELD_DESC);
      oprot.writeI32(struct.serialPicID);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SerialInfoRecTupleSchemeFactory implements SchemeFactory {
    public SerialInfoRecTupleScheme getScheme() {
      return new SerialInfoRecTupleScheme();
    }
  }

  private static class SerialInfoRecTupleScheme extends TupleScheme<SerialInfoRec> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SerialInfoRec struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetSerialID()) {
        optionals.set(0);
      }
      if (struct.isSetSerialNo()) {
        optionals.set(1);
      }
      if (struct.isSetSerialName()) {
        optionals.set(2);
      }
      if (struct.isSetSerialPicID()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetSerialID()) {
        oprot.writeI32(struct.serialID);
      }
      if (struct.isSetSerialNo()) {
        oprot.writeI32(struct.serialNo);
      }
      if (struct.isSetSerialName()) {
        oprot.writeString(struct.serialName);
      }
      if (struct.isSetSerialPicID()) {
        oprot.writeI32(struct.serialPicID);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SerialInfoRec struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.serialID = iprot.readI32();
        struct.setSerialIDIsSet(true);
      }
      if (incoming.get(1)) {
        struct.serialNo = iprot.readI32();
        struct.setSerialNoIsSet(true);
      }
      if (incoming.get(2)) {
        struct.serialName = iprot.readString();
        struct.setSerialNameIsSet(true);
      }
      if (incoming.get(3)) {
        struct.serialPicID = iprot.readI32();
        struct.setSerialPicIDIsSet(true);
      }
    }
  }

}

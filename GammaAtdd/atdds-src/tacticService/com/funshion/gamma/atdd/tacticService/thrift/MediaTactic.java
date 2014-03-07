/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.funshion.gamma.atdd.tacticService.thrift;

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

public class MediaTactic implements org.apache.thrift.TBase<MediaTactic, MediaTactic._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MediaTactic");

  private static final org.apache.thrift.protocol.TField MEDIAID_FIELD_DESC = new org.apache.thrift.protocol.TField("mediaid", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField HAS_TACTIC_FIELD_DESC = new org.apache.thrift.protocol.TField("hasTactic", org.apache.thrift.protocol.TType.BOOL, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MediaTacticStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MediaTacticTupleSchemeFactory());
  }

  public int mediaid; // required
  public boolean hasTactic; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MEDIAID((short)1, "mediaid"),
    HAS_TACTIC((short)2, "hasTactic");

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
        case 1: // MEDIAID
          return MEDIAID;
        case 2: // HAS_TACTIC
          return HAS_TACTIC;
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
  private static final int __MEDIAID_ISSET_ID = 0;
  private static final int __HASTACTIC_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MEDIAID, new org.apache.thrift.meta_data.FieldMetaData("mediaid", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.HAS_TACTIC, new org.apache.thrift.meta_data.FieldMetaData("hasTactic", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MediaTactic.class, metaDataMap);
  }

  public MediaTactic() {
  }

  public MediaTactic(
    int mediaid,
    boolean hasTactic)
  {
    this();
    this.mediaid = mediaid;
    setMediaidIsSet(true);
    this.hasTactic = hasTactic;
    setHasTacticIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MediaTactic(MediaTactic other) {
    __isset_bitfield = other.__isset_bitfield;
    this.mediaid = other.mediaid;
    this.hasTactic = other.hasTactic;
  }

  public MediaTactic deepCopy() {
    return new MediaTactic(this);
  }

  @Override
  public void clear() {
    setMediaidIsSet(false);
    this.mediaid = 0;
    setHasTacticIsSet(false);
    this.hasTactic = false;
  }

  public int getMediaid() {
    return this.mediaid;
  }

  public MediaTactic setMediaid(int mediaid) {
    this.mediaid = mediaid;
    setMediaidIsSet(true);
    return this;
  }

  public void unsetMediaid() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MEDIAID_ISSET_ID);
  }

  /** Returns true if field mediaid is set (has been assigned a value) and false otherwise */
  public boolean isSetMediaid() {
    return EncodingUtils.testBit(__isset_bitfield, __MEDIAID_ISSET_ID);
  }

  public void setMediaidIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MEDIAID_ISSET_ID, value);
  }

  public boolean isHasTactic() {
    return this.hasTactic;
  }

  public MediaTactic setHasTactic(boolean hasTactic) {
    this.hasTactic = hasTactic;
    setHasTacticIsSet(true);
    return this;
  }

  public void unsetHasTactic() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __HASTACTIC_ISSET_ID);
  }

  /** Returns true if field hasTactic is set (has been assigned a value) and false otherwise */
  public boolean isSetHasTactic() {
    return EncodingUtils.testBit(__isset_bitfield, __HASTACTIC_ISSET_ID);
  }

  public void setHasTacticIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __HASTACTIC_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MEDIAID:
      if (value == null) {
        unsetMediaid();
      } else {
        setMediaid((Integer)value);
      }
      break;

    case HAS_TACTIC:
      if (value == null) {
        unsetHasTactic();
      } else {
        setHasTactic((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MEDIAID:
      return Integer.valueOf(getMediaid());

    case HAS_TACTIC:
      return Boolean.valueOf(isHasTactic());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MEDIAID:
      return isSetMediaid();
    case HAS_TACTIC:
      return isSetHasTactic();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MediaTactic)
      return this.equals((MediaTactic)that);
    return false;
  }

  public boolean equals(MediaTactic that) {
    if (that == null)
      return false;

    boolean this_present_mediaid = true;
    boolean that_present_mediaid = true;
    if (this_present_mediaid || that_present_mediaid) {
      if (!(this_present_mediaid && that_present_mediaid))
        return false;
      if (this.mediaid != that.mediaid)
        return false;
    }

    boolean this_present_hasTactic = true;
    boolean that_present_hasTactic = true;
    if (this_present_hasTactic || that_present_hasTactic) {
      if (!(this_present_hasTactic && that_present_hasTactic))
        return false;
      if (this.hasTactic != that.hasTactic)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(MediaTactic other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    MediaTactic typedOther = (MediaTactic)other;

    lastComparison = Boolean.valueOf(isSetMediaid()).compareTo(typedOther.isSetMediaid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMediaid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mediaid, typedOther.mediaid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHasTactic()).compareTo(typedOther.isSetHasTactic());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasTactic()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasTactic, typedOther.hasTactic);
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
    StringBuilder sb = new StringBuilder("MediaTactic(");
    boolean first = true;

    sb.append("mediaid:");
    sb.append(this.mediaid);
    first = false;
    if (!first) sb.append(", ");
    sb.append("hasTactic:");
    sb.append(this.hasTactic);
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

  private static class MediaTacticStandardSchemeFactory implements SchemeFactory {
    public MediaTacticStandardScheme getScheme() {
      return new MediaTacticStandardScheme();
    }
  }

  private static class MediaTacticStandardScheme extends StandardScheme<MediaTactic> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MediaTactic struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MEDIAID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.mediaid = iprot.readI32();
              struct.setMediaidIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // HAS_TACTIC
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasTactic = iprot.readBool();
              struct.setHasTacticIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MediaTactic struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(MEDIAID_FIELD_DESC);
      oprot.writeI32(struct.mediaid);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(HAS_TACTIC_FIELD_DESC);
      oprot.writeBool(struct.hasTactic);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MediaTacticTupleSchemeFactory implements SchemeFactory {
    public MediaTacticTupleScheme getScheme() {
      return new MediaTacticTupleScheme();
    }
  }

  private static class MediaTacticTupleScheme extends TupleScheme<MediaTactic> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MediaTactic struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMediaid()) {
        optionals.set(0);
      }
      if (struct.isSetHasTactic()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetMediaid()) {
        oprot.writeI32(struct.mediaid);
      }
      if (struct.isSetHasTactic()) {
        oprot.writeBool(struct.hasTactic);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MediaTactic struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.mediaid = iprot.readI32();
        struct.setMediaidIsSet(true);
      }
      if (incoming.get(1)) {
        struct.hasTactic = iprot.readBool();
        struct.setHasTacticIsSet(true);
      }
    }
  }

}

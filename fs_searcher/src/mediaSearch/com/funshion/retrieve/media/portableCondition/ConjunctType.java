package com.funshion.retrieve.media.portableCondition;

import org.apache.lucene.search.BooleanClause.Occur;

public enum ConjunctType {
  OR('?'),
  AND('+'),
  /**
   * 注意，NOT不应当单独使用。
   * 例如：AND(a = 5) NOT(b = 7),不应当写为 AND(a = 5) AND( NOT(b = 7));如果查询条件中只存在一个NOT条件，应当转化为其他形式，避免出现独立NOT条件
   */
  NOT('-');

  private final char value;

  private ConjunctType(char value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public char getValue() {
    return value;
  }

  public static ConjunctType findByValue(String value) { 
	  return findByValue(value.charAt(0));
  }
  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static ConjunctType findByValue(char value) { 
    switch (value) {
      case '?':
        return OR;
      case '+':
        return AND;
      case '-':
        return NOT;
      default:
        throw new RuntimeException("unknown ConnjuctType " + value);
    }
  }
  public Occur toOccurType(){
	  switch (value) {
      case '?':
        return Occur.SHOULD;
      case '+':
        return Occur.MUST;
      case '-':
        return Occur.MUST_NOT;
      default:
        return null;
    }
  }
  
  public String toString(){
	  return Character.toString(this.value);
  }
}

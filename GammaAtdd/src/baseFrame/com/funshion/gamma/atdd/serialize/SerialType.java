package com.funshion.gamma.atdd.serialize;

public enum SerialType {

	INT("int", true), LONG("long", true), DOUBLE("double", true), 
	BYTE("byte", true), SHORT("short", true), 
	BOOL("bool", true),  STRING("string", true),
	CLASS("class", false), LIST("list", false), MAP("map", false);

	public final String name;
	public final boolean isRawType;
	public static final String ClassPrefix = "class@";
	private SerialType(String name, boolean isRaw){
		this.name = name;
		this.isRawType = isRaw;
	}
	@Override
	public String toString(){
		return name;
	}
	public String toString(Class<?>cls){
		if(this.isRawType || this == LIST || this == MAP){
			return toString();
		}else{
			return  ClassPrefix +  cls.getName() ;
		}
	}
	public Object parseStringValue(String toParse) throws Exception{
		if(!this.isRawType){
			throw new Exception(this + " not rawType, can not parse from String value");
		}
		if(toParse == null){
			throw new Exception("Value not Set!");
		}
		if(ThriftSerializeTool.NullDescriptor.equalsIgnoreCase(toParse)){
			return null;
		}
		switch(this){
		case INT:
			return Integer.parseInt(toParse);
		case LONG:
			return Long.parseLong(toParse);
		case DOUBLE:
			return Double.parseDouble(toParse);
		case BYTE:
			return Byte.parseByte(toParse);
		case SHORT:
			return Short.parseShort(toParse);
		case BOOL:
			return Boolean.parseBoolean(toParse);
		case STRING:
			return toParse;
		default :
			throw new Exception(this + " not support parse value from single String ");
		}
	}
	
	public static Class<?>className(SerialType t, String name) throws Exception{
		if(t != CLASS){
			throw new Exception("Error: only for CLASS");
		}
		return Class.forName(name.substring(ClassPrefix.length()));
	}
	public static SerialType valueOfString(String str) throws Exception{
		if(str.startsWith(ClassPrefix)){
			return CLASS;
		}
		SerialType st[] = values();
		for(SerialType s : st){
			if(s.name.equalsIgnoreCase(str)){
				return s;
			}
		}
		throw new Exception("unknown SerialType define '" + str + "'");
	}

	public static void main(String[]args) throws Exception{
		SerialType st[] = values();
		for(SerialType s : st){
			System.out.println(s);
		}

		System.out.println(valueOfString("Int"));

	}
	public Class<?> getRealClass() throws Exception {
		if(!this.isRawType){
			throw new Exception(this + " not rawType, can not parse from String value");
		}
		switch(this){
		case INT:
			return int.class;
		case LONG:
			return long.class;
		case DOUBLE:
			return double.class;
		case BYTE:
			return byte.class;
		case SHORT:
			return short.class;
		case BOOL:
			return boolean.class;
		case STRING:
			return String.class;
		default :
			throw new Exception(this + " not support parse value from single String ");
		}
	}

	public static SerialType getType(Class<?>cls){
		SerialType type = null;
		if(cls == int.class || cls == Integer.class){
			type = SerialType.INT;
		}else if(cls == Boolean.class || cls == boolean.class){
			type =  SerialType.BOOL;
		}else if(cls == Byte.class || cls == byte.class){
			type = SerialType.BYTE;
		}else if(cls == Short.class || cls == short.class){
			type = SerialType.SHORT;
		}else if(cls == Long.class || cls == long.class){
			type = SerialType.LONG;
		}else if(cls == Double.class || cls == double.class){
			type = SerialType.DOUBLE;
		}else if(cls == String.class){
			type = SerialType.STRING;
		}else if(java.util.List.class.isAssignableFrom(cls)){
			type = SerialType.LIST;
		}else if(java.util.Map.class.isAssignableFrom(cls)){
			type = SerialType.MAP;
		}else{
			type = SerialType.CLASS;
		}
		return type;
	}
}

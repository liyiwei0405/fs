package com.funshion.gamma.atdd.serialize;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThriftDeserializeTool {
	
	public static List<DeserializedFieldInfo> deserializeObjects(DefineIterator itr) throws Exception{
		List<DeserializedFieldInfo> ret = new ArrayList<DeserializedFieldInfo>();
		while(itr.hasNext()){
			String line = itr.getCurrentLine();
			if(DefineReader.validLile(line)){
				DeserializedFieldInfo info = deserialize(itr);
				ret.add(info);
			}else{
				continue;
			}
		}
		return ret;
	}
	private static DeserializedFieldInfo deserialize(DefineIterator itr) throws Exception{
		DefineReader reader = new DefineReader(itr);
		try{
			DeserializedFieldInfo ret = parse(reader.next(), reader, true);
			return ret;
		}catch(Exception e){
			throw new Exception("error near line " + itr.lineNum + ":'" + itr.getCurrentLine() + "'\nStack info:" + e.getMessage(), e);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static DeserializedFieldInfo parse(String crtLine, DefineReader reader, boolean parseField) throws Exception{
		StringTokenlizer tokenlizer = new StringTokenlizer(crtLine);
		String typeName = tokenlizer.nextNotEmptyToken();
		SerialType sType = SerialType.valueOfString(typeName);
		if(sType == null){
			throw new Exception("format error:" + typeName + " not a valid Type! at line " + reader.lineInfo());
		}
		String fieldName = null;
		String nextToken = null;
		if(parseField){
			fieldName = tokenlizer.nextNotEmptyToken();
			if(fieldName == null){
				fieldName = "";
			}else{
				if(fieldName.equals("{")){
					fieldName = "";
					nextToken = "{";
				}else if(fieldName.equals(":")){
					fieldName = "";
					nextToken = ":";
				}
			}
		}
		final DeserializedFieldInfo fi ;
		if(nextToken == null){
			nextToken = tokenlizer.nextNotEmptyToken();
		}

		if(":".equals(nextToken)){//direct value, link @Igr
			String leftValues = null;
			leftValues = tokenlizer.left().trim();
			if(ThriftSerializeTool.IgnoreCompare.equalsIgnoreCase(leftValues)){
				fi = new DeserializedFieldInfo(fieldName, null);
				fi.setIgnoreCompare();
				return fi;
			}else if(ThriftSerializeTool.NullDescriptor.equalsIgnoreCase(leftValues)){
				fi = new DeserializedFieldInfo(fieldName, null);
				return fi;
			}else{
				if(sType.isRawType){
					return new DeserializedFieldInfo(fieldName, sType.parseStringValue(leftValues));
				}else if(sType == SerialType.CLASS){
					String className = typeName.substring(SerialType.CLASS.name.length() + 1);
					Class cls = Class.forName(className);
					if(cls.isEnum()){
						Object v = Enum.valueOf(cls, leftValues);
						return new DeserializedFieldInfo(fieldName, v);
					}else{
						throw new Exception("error! expect enum type, but get " + className);
					}
				}else {
					throw new Exception("error! expect ':" + ThriftSerializeTool.NullDescriptor + "', but get ':" + leftValues + "'" );
				}
			}
		}

		if(sType == SerialType.CLASS){

			String className = typeName.substring(SerialType.CLASS.name.length() + 1);
			Class cls = Class.forName(className);
			final Object retIns = cls.newInstance();
			fi = new DeserializedFieldInfo(fieldName, retIns);
			expect("expect has next token", nextToken, "{");
			expect("expect nextNotEmptyToken", tokenlizer.nextNotEmptyToken(), null);

			boolean parseOk = false;
			while(reader.hasNext()){
				String nextLine = reader.next();
				if(nextLine.equals("}")){
					parseOk = true;
					break;
				}
				DeserializedFieldInfo fieldValue =  parse(nextLine, reader, true);
				Field f = cls.getField(fieldValue.name);
				if(!fieldValue.isIgnoreCompare()){
					f.set(retIns, fieldValue.value);
				}
				fi.regMask(fieldValue.name, fieldValue);
			}
			if(!parseOk){
				throw new Exception("Error: not found class define end for " + fieldName + ":" + className);
			}
			return fi;

		}else if(sType == SerialType.LIST){

			expect("expect  reader.hasNext()", true, reader.hasNext());
			boolean igrLstOrder = false;
			if(reader.next().equalsIgnoreCase(ThriftSerializeTool.IgnoreListOrder)){
				igrLstOrder = true;
				expect("expect  reader.hasNext()", true, reader.hasNext());
			}
			expect("expect reader.next() is '['", reader.next(), "[");

			List lstInstance = new ArrayList();
			fi = new DeserializedFieldInfo(fieldName, lstInstance);
			fi.setIgnoreListOrder(igrLstOrder);
			boolean parseOk = false;

			int idx = 0;
			while(reader.hasNext()){
				String nextLine = reader.next();

				if(nextLine.equals("]")){
					parseOk = true;
					break;
				}
				DeserializedFieldInfo fieldValue =  parse(nextLine, reader, false);
				lstInstance.add(fieldValue.value);

				fi.regMaskIndex(idx, fieldValue);
				idx ++;
			}
			if(!parseOk){
				throw new Exception("Error: not found List end for " + fieldName);
			}
			return fi;
		}else if(sType == SerialType.MAP){

			expect("expect  reader.hasNext()", true, reader.hasNext());
			expect("expect reader.next() is '['", reader.next(), "[");

			Map lstInstance = new HashMap();
			fi = new DeserializedFieldInfo(fieldName, lstInstance);
			boolean parseOk = false;

//			int idx = 0;
			while(reader.hasNext()){
				String nextLine = reader.next();

				if(nextLine.equals("]")){
					parseOk = true;
					break;
				}
				DeserializedFieldInfo mapKey =  parse(nextLine, reader, false);
				expect("expect  reader.hasNext()", true, reader.hasNext());
				expect("expect map's Entry linkor!", reader.next(), ThriftSerializeTool.POINTTO);
				expect("expect  reader.hasNext()", true, reader.hasNext());
				nextLine = reader.next();
				DeserializedFieldInfo ValueKey =  parse(nextLine, reader, false);
				lstInstance.put(mapKey.value, ValueKey.value);

//				fi.regMaskIndex(idx, fieldValue);
//				idx ++;
			}
			if(!parseOk){
				throw new Exception("Error: not found List end for " + fieldName);
			}
			return fi;
		}else{
			throw new Exception("unsupport type " + sType + ", version mismatch ?");
		}
	}

	private static void expect(String info, Object real, Object expect) throws Exception {
		if(expect == null && real == null){
			return ;
		}
		if((expect == null && real != null) || !expect.equals(real)){
			throw new Exception(info + " expect " + expect + "', but get '" + real  +"'");
		}
	}

}

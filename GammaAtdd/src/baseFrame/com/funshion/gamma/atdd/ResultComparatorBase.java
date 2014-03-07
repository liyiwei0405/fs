package com.funshion.gamma.atdd;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.funshion.gamma.atdd.serialize.*;
import com.funshion.search.utils.LogHelper;
@SuppressWarnings("rawtypes")
public abstract class ResultComparatorBase implements ResultComparator{
	static final LogHelper log = new LogHelper("cmp");
	private static boolean printResult = true;
	@SuppressWarnings("serial")
	public static class CompareFailException extends Exception{
		public static final String failExceptionFormat = "%s \nexpect '%s' \n  real '%s' \nfor INPUT: %s";
		public CompareFailException(Object input, Object real, Object expect){
			this("failed compare!",
					input,
					real,
					expect);
		}
		public CompareFailException(String errorMsg, Object input, Object real, Object expect){
			super(
					String.format(failExceptionFormat, 
							errorMsg,
							expect, real, input)
					);
		}
	}

	protected void message(String messageHead, Object input, Object real, Object expect){
		if(isPrintResult()){
			log.info("%s! for input '%s', , real '%s', expect '%s'",
					messageHead, input, real, expect);
		}
	}

	public boolean isPrintResult() {
		return printResult;
	}

	protected static void fail(String errorMsg, Object input, Object real, Object expect) throws CompareFailException{
		throw new CompareFailException(errorMsg, input, real, expect);
	}

	@SuppressWarnings("unchecked")
	protected void assertListEquals(String errorMsg, Object input, List real, List expect,
			DeserializedFieldInfo cmpMask) throws Exception{
		if(cmpMask != null && cmpMask.isIgnoreCompare()){
			return;
		}
		if(checkIsNull(errorMsg, input, real, expect)){
			message("list is null", input, expect, real);
		}
		if(real.size() != expect.size()){
			message("list size mismatch: " +
					real.size() + "!=" + expect.size(), input, expect, real);
		}
		boolean igrLstOrder = false;
		if(cmpMask != null){
			igrLstOrder = cmpMask.isIgnoreListOrder();
		}
		if(igrLstOrder){
			Set sExpect = new HashSet();
			Set sReal = new HashSet();
			sExpect.addAll(expect);
			sReal.addAll(real);
			assertSetEquals(errorMsg, input, sReal, sExpect, cmpMask);
		}else{
			for(int x = 0; x < Math.min(expect.size(), real.size()); x ++){
				assertEquals(errorMsg, "@index_" + x,
						input,
						real.get(x),
						expect.get(x),
						cmpMask == null ? null : cmpMask.getMask(x)
						);
				message("index " + x + " match", input, expect.get(x), real.get(x));
			}
		}
	}
	protected void assertMapEquals(String errorMsg, Object input, Map real, Map expect,
			DeserializedFieldInfo cmpMask) throws Exception{
		if(cmpMask != null && cmpMask.isIgnoreCompare()){
			return;
		}
		if(checkIsNull(errorMsg, input, real, expect)){
			message("map is null", input, expect, real);
		}
		if(real.size() != expect.size()){
			message("list size mismatch: " +
					real.size() + "!=" + expect.size(), input, expect, real);
		}
		Set realKeySet = real.keySet();
		Set expectKeySet = expect.keySet();
		assertSetEquals(errorMsg, input, realKeySet, expectKeySet,
				cmpMask);
		for(Object o : expectKeySet){
			assertEquals(errorMsg, "@key_" + o,
					input,
					real.get(o),
					expect.get(o),
					null
					);
			message("key " + o + " match", input, expect.get(o), real.get(o));
		}
	}
	private void assertSetEquals(String errorMsg, Object input, Set realKeySet,
			Set expectKeySet, DeserializedFieldInfo cmpMask) throws Exception {

		if(cmpMask != null && cmpMask.isIgnoreCompare()){
			return;
		}
		if(checkIsNull(errorMsg, input, realKeySet, expectKeySet)){
			message("map is null", input, realKeySet, expectKeySet);
		}
		if(realKeySet.size() != expectKeySet.size()){
			message("list size mismatch: " +
					realKeySet.size() + "!=" + expectKeySet.size(), input, expectKeySet, realKeySet);
		}
		for(Object o : expectKeySet){
			if(!realKeySet.contains(o)){
				fail(errorMsg + " [keySet mismatch] expect contains " + o + ", but real has no this element in " + realKeySet , input, realKeySet, expectKeySet);
			}
		}
	}

	protected boolean checkIsNull(String errorMsg, Object input, Object real, Object expect) throws Exception{
		if(expect == null){
			if(real != null){
				fail(errorMsg, input, real, expect);
			}else{
				return true;
			}
		}
		if(real == null){
			if(expect != null){
				fail(errorMsg, input, real, expect);
			}else{
				return true;
			}
		}
		return false;
	}
	protected void assertRawEquals(String errorMsg, Object input, Object real, Object expect) throws Exception{
		if(!expect.equals(real)){
			fail(errorMsg, input, real, expect);
		}
	}
	protected void assertEquals(String errorMsg, String fieldName, Object input, Object real, Object expect, DeserializedFieldInfo cmpMask) throws Exception{
		if(cmpMask != null && cmpMask.isIgnoreCompare()){
			return;
		}
		if(fieldName != null && fieldName.length() > 0){
			errorMsg += "-->" + fieldName;
		}
		if(checkIsNull(errorMsg, input, real, expect)){
			return;
		}
		SerialType sExpect = SerialType.getType(expect.getClass());
		SerialType sReal = SerialType.getType(real.getClass());
		if(sExpect == sReal){
			if(sExpect.isRawType){
				if(sExpect == SerialType.STRING){
					//string skip \r and \n
					String expectStr = ((String)expect).replace("\r", "").replace("\n", "").trim();
					String realStr = ((String)real).replace("\r", "").replace("\n", "").trim();
					assertRawEquals(errorMsg, input, realStr, expectStr);
				}else{
					assertRawEquals(errorMsg, input, real, expect);
				}
			}else{
				if(sExpect == SerialType.LIST){//redirect to list-compare api
					assertListEquals(errorMsg, input, (List)real, (List)expect, cmpMask);
				}else if(sExpect == SerialType.MAP){
					assertMapEquals(errorMsg, input, (Map)real, (Map)expect, cmpMask);
				}else{
					if(expect.getClass().isEnum()){
						if(real != expect){
							fail(errorMsg + " [Enum MISMATCH] expect " + expect + ", but real type is " + real, input, real, expect);
						}else{
							message(fieldName + " Match", input, real, expect);
						}
					}else{
						Field[] realFields = real.getClass().getFields();
						Field[] expectFields = expect.getClass().getFields();
						//check fields matches
						Map<String, Field>mapRealFields = new HashMap<String, Field>();
						for(Field f : realFields){
							mapRealFields.put(f.getName(), f);
						}
						for(Field fExpect : expectFields){
							String expectFieldName = fExpect.getName();
							Field fReal = mapRealFields.get(expectFieldName);

							if(fReal == null){
								fail(errorMsg + " [FIELD NOT FOUND] " + expectFieldName , input, real, expect);
							}
							assertEquals(errorMsg, expectFieldName, input, fReal.get(real), fExpect.get(expect),
									cmpMask == null ? null : cmpMask.getMask(expectFieldName));
							mapRealFields.remove(expectFieldName);
						}
						if(mapRealFields.size() > 0){
							fail(errorMsg + " [FIELD TOO MUCH ]" + mapRealFields.keySet() , input, real, expect);
						}	
					}
				}
			}
		}else{
			fail(errorMsg + " [TYPE MISMATCH] expect " + expect.getClass().getName() + ", but real type is " + real , input, real, expect);
		}
	}

	public static void disablePrintResult() {
		printResult = false;
	}

	public static void enablePrintResult() {
		printResult = true;
	}
}

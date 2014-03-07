package com.funshion.luc.defines;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.index.IndexReader;

import com.funshion.search.ConfUtils;
import com.funshion.search.analyzers.RMMSegmentTokenizer;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.KeyValuePair;
import com.funshion.search.utils.LogHelper;

public class ITableDefine {
	public static final LogHelper log = new LogHelper("itab");
	public static ITableDefine instance;
	static{
		try {
			instance = new ITableDefine();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private final HashMap<String, IFieldDefine> fieldDefineMap = new HashMap<String, IFieldDefine>();
	private final HashMap<String, TokenStreamComponents> reusableComponents = new HashMap<String, TokenStreamComponents>();
	private final HashMap<String, Class<?>> tokenizerClasses = new HashMap<String, Class<?>>();

	public final Class<?> fsSearchThriftServerClass;
	private final Class<?> fsSearcherClass;
	private final Class<?>[] pIdx;
	private final Class<?> atddTest;
	private final FullTextQueryMaker fullTextQueryMaker;
	private final LikeQueryMaker likeQueryMaker;
	private final Class<?> indexActionDetectorClass;

	public final File cfgFile;
	public final IFieldDefine idField;
	public static final String RELEVENCE = "fttRel";
	ArrayList<IFieldDefine>fieldNames = new ArrayList<IFieldDefine>();
	private  ITableDefine() throws Exception {
		String cfgNameCrt = System.getProperty("$path_of_cfgLuc_property");
		if(cfgNameCrt == null){
			this.cfgFile = ConfUtils.getConfFile("cfgLuc.conf");
		}else{
			this.cfgFile = ConfUtils.getConfFile(cfgNameCrt);
		}
		ConfigReader cr = new ConfigReader(cfgFile, "fieldAlias");
		ArrayList<KeyValuePair<String, String>> lst = cr.getAllConfig();

		HashSet<String>lucNameSet = new HashSet<String>();
		for(KeyValuePair<String, String> pair : lst){
			String tokens[] = pair.value.split(":");
			final String key = pair.key.toUpperCase();
			for(int x = 0; x < tokens.length; x ++){
				tokens[x] = tokens[x].trim();
			}
			if(lucNameSet.contains(key)){
				throw new Exception("multi fields use Character '" + tokens[0] + "' as fieldName");
			}
			lucNameSet.add(key);

			AnaType aType = AnaType.fromString(tokens[0]);
			boolean store = !"0".equals(tokens[1]);
			boolean index = !"0".equals(tokens[2]);
			IFieldDefine fd = new IFieldDefine(key, aType, store, index);
			this.fieldDefineMap.put(key, fd);
		}

		cr = new ConfigReader(cfgFile, "field_index_info");
		String idFieldName = cr.getValue("idField");
		if(idFieldName == null){
			throw new Exception("idField is not set in config " + cr);
		}
		idField = this.getFieldDefine(idFieldName);
		if(idField == null){
			throw new Exception("idField is not set! for idField name '" + idFieldName +"'");
		}
		String strMk = cr.getValue("fullTextQueryMaker");
		if(strMk == null){
			this.fullTextQueryMaker = null;
		}else{
			this.fullTextQueryMaker = (FullTextQueryMaker) Class.forName(strMk).newInstance();
		}

		String strLikeQueryMaker = cr.getValue("likeQueryMaker");
		if(strLikeQueryMaker == null){
			this.likeQueryMaker = null;
		}else{
			this.likeQueryMaker = (LikeQueryMaker) Class.forName(strLikeQueryMaker.trim()).newInstance();
		}

		fsSearchThriftServerClass = Class.forName(cr.getValue("fsSearchThriftServerClass"));
		fsSearcherClass = Class.forName(cr.getValue("fsSearcher"));
		atddTest = Class.forName(cr.getValue("atddTestClass"));

		String pIdxClassNames = cr.getValue("pendIndexerClasses");
		ArrayList<Class<?>>clses = new ArrayList<Class<?>>();
		if(pIdxClassNames != null && pIdxClassNames.length() > 0){
			String pIdxClassName[] = pIdxClassNames.split(";");
			for(String x : pIdxClassName){
				x = x.trim();
				if(x.length() == 0){
					continue;
				}
				Class<?> c = Class.forName(x);
				clses.add(c);
			}
		}
		pIdx = new Class<?>[clses.size()];
		clses.toArray(pIdx);

		String indexActionDetectClassName = cr.getValue("indexActionDetect");
		if(indexActionDetectClassName != null && indexActionDetectClassName.length() > 0){
			indexActionDetectorClass = Class.forName(indexActionDetectClassName);
		}else{
			this.indexActionDetectorClass = AllIndexDetector.class;
			log.info("use default IndexDetector %s", this.indexActionDetectorClass);
		}


		cr = new ConfigReader(cfgFile, "tokenizer");
		ArrayList<KeyValuePair<String, String>> tokens = cr.getAllConfig();

		for(KeyValuePair<String, String>keyv : tokens){
			IFieldDefine  type = this.getFieldDefine(keyv.key);
			if(type == null){
				throw new Exception("can not find type for IFieldDefine  with alia " + keyv.key);
			}
			this.tokenizerClasses.put(type.fieldName, Class.forName(keyv.value));
		}
		for(IFieldDefine x : fieldDefineMap.values()){
			if(x != null){
				fieldNames.add(x);
			}
		}
		Collections.sort(fieldNames, new Comparator<IFieldDefine>(){

			@Override
			public int compare(IFieldDefine o1, IFieldDefine o2) {
				return o1.fieldName.compareTo(o2.fieldName);
			}
		});

	}

	public Collection<IFieldDefine> getFieldDefines(){
		return fieldNames;
	}
	public FullTextQueryMaker getFullTextQueryMaker() {
		return this.fullTextQueryMaker;
	}
	public LikeQueryMaker getLikeQueryMaker() {
		return likeQueryMaker;
	}
	public TokenStreamComponents getReusableComponents(String ch) {
		return reusableComponents.get(ch);
	}

	public TokenStreamComponents createComponents(String c, Reader reader){
		try{
			Class<?> cls = tokenizerClasses.get(c);
			if(cls == null){
				IFieldDefine fdef = fieldDefineMap.get(c.toUpperCase());
				if(fdef.aType == AnaType.eStr){
					Tokenizer tz = new SplitTokenizer(reader);
					return new TokenStreamComponents(tz, tz);
				}
				RMMSegmentTokenizer tokenizer = new RMMSegmentTokenizer(reader);
				return new TokenStreamComponents(tokenizer, tokenizer);
			}
			Constructor<?> cons = cls.getConstructor(Reader.class);
			final Tokenizer tokenizer = (Tokenizer) cons.newInstance(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public void setReusableComponents(String c,
			TokenStreamComponents components) {
		reusableComponents.put(c, components);
	}

	public boolean isScoreField(String c) {
		return fieldDefineMap.get(c.toUpperCase()).scoreSim();
	}

	public boolean isNumField(String c) {
		return fieldDefineMap.get(c.toUpperCase()).aType.isNumType();
	}

	public AnaType getAnaType(String ch) {
		return fieldDefineMap.get(ch.toUpperCase()).aType;
	}
	public IFieldDefine getFieldDefine(String fieldName) {
		return fieldDefineMap.get(fieldName.toUpperCase());
	}

	public PendIndexer[] newPendIndexerInstances() throws InstantiationException, IllegalAccessException {
		PendIndexer[] idexes = new PendIndexer[this.pIdx.length];
		for(int x = 0; x < idexes.length; x ++){
			idexes[x] = (PendIndexer) this.pIdx[x].newInstance();
		}
		return idexes;
	}

	public AbstractSearcher newAbstractSearcher(IndexReader ir) throws Exception{
		return (AbstractSearcher) fsSearcherClass.getConstructor(org.apache.lucene.index.IndexReader.class).newInstance(ir);
	}

	public AtddTestor newAtddTestor() throws Exception {
		return (AtddTestor) atddTest.newInstance();
	}

	public ConfigReader getIndexConfig() throws IOException {
		return new ConfigReader(cfgFile, "search-indexer");
	}

	public void registerTokenlizer(String fieldName,
			Class<?> cls) {
		tokenizerClasses.put(fieldName, cls);
	}

	public IndexActionDetector getDetector() throws Exception{
		return (IndexActionDetector) indexActionDetectorClass.newInstance();
	}
}

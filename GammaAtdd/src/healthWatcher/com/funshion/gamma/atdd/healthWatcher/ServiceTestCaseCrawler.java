package com.funshion.gamma.atdd.healthWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bmtech.htmls.parser.Node;
import com.bmtech.htmls.parser.Parser;
import com.bmtech.htmls.parser.Tag;
import com.bmtech.htmls.parser.filters.HasAttributeFilter;
import com.bmtech.htmls.parser.filters.NodeClassFilter;
import com.bmtech.htmls.parser.tags.LinkTag;
import com.bmtech.htmls.parser.util.NodeList;
import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.gamma.atdd.QueryReturnValue;
import com.funshion.gamma.atdd.serialize.DefineIterator;
import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;
import com.funshion.gamma.atdd.serialize.ThriftDeserializeTool;
import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.LogHelper;
import com.funshion.utils.http.HttpCrawler;

public class ServiceTestCaseCrawler {
	static final LogHelper log = new LogHelper("methodTestcaseLoad");
	final String svcName;
	URL baseUrl;
	File testCaseFolder;
	final String suffixName = ".testcase";

	ServiceTestCaseCrawler(final String svcName, final File testCaseFolder){
		this.svcName = svcName;
		this.testCaseFolder = testCaseFolder;
	}

	ServiceTestCaseCrawler(final String svcName, final URL baseUrl){
		this.svcName = svcName;
		this.baseUrl = baseUrl;
	}

	public List<NamedGammaTestCase> loadTestCasesFromDisk(Method method, File baseDir) throws Exception {
		List<NamedGammaTestCase> ret = new ArrayList<NamedGammaTestCase>();
		File dir = new File(baseDir, ThriftSerializeTool.methodStringName(method));
		if(!dir.exists() || !dir.isDirectory()){
			return ret;
		}
		File[] testCaseFolders = dir.listFiles();
		for(File testCaseFolder : testCaseFolders){
			String folderName = testCaseFolder.getName();
			if(!folderName.toLowerCase().endsWith(suffixName) || 
					folderName.length() <= suffixName.length()){
				log.warn("testcase name must end with '%s', skip '%s', for service %s",
						suffixName, folderName, this.svcName);
				continue;
			}

			NamedGammaTestCase tc = getTestCaseFromDisk(testCaseFolder);
			if(tc != null){
				ret.add(tc);
			}
		}

		return ret;
	}

	public List<NamedGammaTestCase> loadTestCases(Method method, URL baseUrl) throws Exception {
		List<NamedGammaTestCase> ret = new ArrayList<NamedGammaTestCase>();
		URL url = new URL(baseUrl, 
				URLEncoder.encode(ThriftSerializeTool.methodStringName(method), Charsets.UTF8_STR));
		if(!testFolderExists(url)){
			return ret;
		}
		HttpCrawler crl = new HttpCrawler(url);
		try{
			crl.connect();
			String html = crl.getString(Charsets.UTF8_CS);
			//<td><i class="icon-folder-open icon-spaced"></i> <a href="/search.git/tree/master/GammaAtdd/config/atdd/conf-VodInfoService.cfg-testcases/%5BGETINFOBYPLAYIDLIST%5D/99999.testcase/">99999.testcase</a></td>

			HasAttributeFilter fltTd = new HasAttributeFilter("class", "icon-folder-open icon-spaced");
			NodeClassFilter lnkFlt = new NodeClassFilter(LinkTag.class);
			Parser p = new Parser(html);
			NodeList nl = p.parse(fltTd);
			final String suffixName = ".testcase";
			for(int x = 0; x < nl.size(); x ++){
				Node n = nl.elementAt(x);
				Tag t = (Tag) n.getParent();
				LinkTag lt = (LinkTag) t.getChildren().extractAllNodesThatMatch(lnkFlt).elementAt(0);
				final String link = lt.getLink();
				if(!link.endsWith("/")){
					log.warn("get a folder but not end with '/':%s",
							link);
					continue;
				}
				String tmpLnk = link.substring(0, link.length() - 1);
				int pos = tmpLnk.lastIndexOf('/');
				String folderNameRaw = tmpLnk.substring(pos + 1).trim();
				String folderName = URLDecoder.decode(folderNameRaw, Charsets.UTF8_STR);
				if(!folderName.toLowerCase().endsWith(suffixName) || 
						folderName.length() <= suffixName.length()){
					log.warn("testcase name must end with '%s', skip '%s', for service %s",
							suffixName, folderName, this.svcName);
					continue;
				}
				URL subUrl = new URL(url, link);
				final String testCaseName = folderName.substring(0, folderName.length() - suffixName.length());

				NamedGammaTestCase tc = getTestCase(testCaseName, subUrl);
				if(tc != null){
					ret.add(tc);
				}
			}

		}finally{
			crl.close();
		}
		return ret;
	}

	public NamedGammaTestCase getTestCaseFromDisk(File testCaseFolder) throws Exception {
		if(!testCaseFolder.exists()){
			return null;
		}
		final String testCaseName = testCaseFolder.getName().substring(0, testCaseFolder.getName().length() - suffixName.length());


		File inputFile = new File(testCaseFolder, "input.txt");
		File expectFile = new File(testCaseFolder, "expect.txt");

		String txtParas = getRawTxtInfoFromDisk(inputFile);
		DefineIterator paraItr = new DefineIterator(txtParas.split("\n"));
		List<DeserializedFieldInfo> fis = ThriftDeserializeTool.deserializeObjects(paraItr);
		QueryParas paraValue = QueryParas.fromDeserializedFieldInfoList(fis);

		String txtReturn = getRawTxtInfoFromDisk(expectFile);
		DefineIterator returnItr = new DefineIterator(txtReturn.split("\n"));
		List<DeserializedFieldInfo> returnValues = ThriftDeserializeTool.deserializeObjects(returnItr);
		QueryReturnValue returns = QueryReturnValue.fromDeserializedFieldInfoList(returnValues);

		String md5 = NamedGammaTestCase.md5Flag(txtParas, txtReturn);

		return new NamedGammaTestCase(testCaseName, md5, paraValue, returns);
	}

	public NamedGammaTestCase getTestCase(String folderName, final URL methodFolderUrl) throws Exception {
		String rawUrl = methodFolderUrl.toString().replaceFirst("\\.git/tree/", ".git/raw/");
		URL rewriteFolderUrl = new URL(rawUrl);
		if(!testFolderExists(rewriteFolderUrl)){
			return null;
		}
		URL parasUrl = new URL(rewriteFolderUrl, "input.txt");
		URL returnUrl = new URL(rewriteFolderUrl, "expect.txt");

		String txtParas = getRawTxtInfo(parasUrl);
		DefineIterator paraItr = new DefineIterator(txtParas.split("\n"));
		List<DeserializedFieldInfo> fis = ThriftDeserializeTool.deserializeObjects(paraItr);
		QueryParas paraValue = QueryParas.fromDeserializedFieldInfoList(fis);

		String txtReturn = getRawTxtInfo(returnUrl);
		DefineIterator returnItr = new DefineIterator(txtReturn.split("\n"));
		List<DeserializedFieldInfo> returnValues = ThriftDeserializeTool.deserializeObjects(returnItr);
		QueryReturnValue returns = QueryReturnValue.fromDeserializedFieldInfoList(returnValues);

		String md5 = NamedGammaTestCase.md5Flag(txtParas, txtReturn);

		return new NamedGammaTestCase(folderName, md5, paraValue, returns);
	}

	public String getRawTxtInfoFromDisk(File file) throws Exception{
		FileReaderCharSet reader = new FileReaderCharSet(file, "utf-8");
		int fileLen = (int)file.length();
		char[] chars = new char[fileLen];
		reader.read(chars);
		reader.close();
		return String.valueOf(chars);
	}
	
	class FileReaderCharSet extends InputStreamReader{
		public FileReaderCharSet(File file, String charSetName) throws Exception {
			super(new FileInputStream(file), charSetName);
		}
	}
	
	public String getRawTxtInfo(URL url) throws IOException{
		HttpCrawler crl = new HttpCrawler(url);
		crl.connect();
		String ret = crl.getString(Charsets.UTF8_CS).trim();
		return ret;
	}

	private boolean testFolderExists(URL url) throws IOException{
		HttpCrawler crl = new HttpCrawler(url);
		try {
			crl.connect();
			crl.getString();
			return crl.getHttpCode() == 200;
		} catch (IOException e) {
			if(crl.getHttpCode() > 0 && crl.getHttpCode() != 200){
				return false;
			}else{
				throw e;
			}
		}finally{
			crl.close();
		}
	}

	public Map<Method, List<NamedGammaTestCase>> loadServiceTestCasesFromDisk() throws Exception {
		File baseDir = new File(testCaseFolder, svcName);
		if(!baseDir.exists()){
			log.warn("testcase folder doesn't exist, mkdir: %s", baseDir);
			if(! baseDir.mkdir()){
				throw new Exception("mkdir failed!");
			}
		}
		Class<?> c = Class.forName(svcName + AbstractThriftService.IfaceDepector);
		Method ms[] = c.getMethods();

		Map<Method, List<NamedGammaTestCase>>map = new HashMap<Method, List<NamedGammaTestCase>>();
		for(Method m : ms){
			try{
				List<NamedGammaTestCase> ret = loadTestCasesFromDisk(m, baseDir);
				if(ret != null && ret.size() > 0){
					map.put(m, ret);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error(e, "when load method %s", e);
			}
		}
		return map;
	}

	/**
	 * if put into map, means the testcases is ok loaded, and should refresh the method's testcases
	 * if not put into map, there is something error when load testcases, method's testcases should not be refreshed
	 * @return
	 * @throws Exception
	 */
	public Map<Method, List<NamedGammaTestCase>> loadServiceTestCases() throws Exception {
		URL url = new URL(baseUrl, svcName + "/");
		if(!testFolderExists(url)){
			throw new Exception("folder is not exists! test by url:" + url);
		}
		Class<?> c = Class.forName(svcName + AbstractThriftService.IfaceDepector);
		Method ms[] = c.getMethods();

		Map<Method, List<NamedGammaTestCase>>map = new HashMap<Method, List<NamedGammaTestCase>>();
		for(Method m : ms){
			try{
				List<NamedGammaTestCase> ret = loadTestCases(m, url);
				map.put(m, ret);
			}catch(Exception e){
				e.printStackTrace();
				log.error(e, "when load method %s", e);
			}
		}
		return map;
	}

	public static void main(String[] args) throws Exception {
		final String svcName = "com.funshion.gamma.atdd.vodInfo.thrift.VodInfoService";
		URL url = new URL("http://git.funshion.com/search.git/tree/master/testCase4atdd/testCases/innerTest/");
		ServiceTestCaseCrawler loader = new ServiceTestCaseCrawler(svcName, url);
		Map<Method, List<NamedGammaTestCase>> map = loader.loadServiceTestCases();
		for(Iterator<Entry<Method, List<NamedGammaTestCase>>> itr = map.entrySet().iterator();
				itr.hasNext();){
			Entry<Method, List<NamedGammaTestCase>> e = itr.next();
			Method m = e.getKey();

			System.out.println("testcase size:" + e.getValue().size());
			System.out.println();
			Iterator<NamedGammaTestCase> caseItr = e.getValue().iterator();
			System.out.println("Method: " + m);
			while(caseItr.hasNext()){
				System.out.println(caseItr.next());
			}
		}
	}
}

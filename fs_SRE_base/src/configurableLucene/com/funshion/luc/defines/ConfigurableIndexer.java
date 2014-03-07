package com.funshion.luc.defines;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.funshion.luc.defines.IndexActionDetector.ActionType;
import com.funshion.search.IndexableRecord;
import com.funshion.search.utils.LogHelper;

public class ConfigurableIndexer {
	public static final LogHelper log = new LogHelper("cindexer");

	final Directory diskDir, ramDir;
	final IndexWriter diskIw, ramIw;
	final PendIndexer[]pIdxs;
	public ConfigurableIndexer(Directory dirDisk, Directory dirRam, ITableDefine itab) throws IOException, InstantiationException, IllegalAccessException{
		this.diskDir = dirDisk;
		this.ramDir = dirRam;
		IndexWriterConfig ic = new IndexWriterConfig(Version.LUCENE_42, new IAnalyzer());
		ic.setMaxBufferedDocs(100000);
		diskIw = new IndexWriter(diskDir, ic);
		ramIw = new IndexWriter(ramDir, ic);
		pIdxs = itab.newPendIndexerInstances();
	}

	protected void indexRecord(IndexableRecord irec, boolean memory) throws IOException{
		Document doc = new Document();
		irec.indexRecord(doc, this.pIdxs);
		//		System.out.println(doc);
		//		com.funcom.funshion.search.Consoler.readString(":");
		if(memory){
			ramIw.addDocument(doc);
		}else{
			diskIw.addDocument(doc);
		}
	}

	public void index(Iterator<IndexableRecord> itr, IndexActionDetector det) throws IOException{
		int cnt = 0;

		if(det == null){
			while(itr.hasNext()){
				cnt ++;
				IndexableRecord rec = itr.next();
				try{
					indexRecord(rec, false);
				}catch(Exception e){
					e.printStackTrace();
					log.error(e, "when index record %s", rec);
				}
			}
			diskIw.forceMerge(1);
		}else{//iteractive
			while(itr.hasNext()){
				cnt ++;
				IndexableRecord rec = itr.next();

				try{
					ActionType action = det.checkType(rec);
					if(action == ActionType.SKIP){
						log.debug("skip index record %s", rec);
					}else if(action == ActionType.UPDATE){
						Document doc = new Document();
						rec.indexRecord(doc, this.pIdxs);
						diskIw.deleteDocuments(action.delTerm);
						ramIw.updateDocument(action.delTerm, doc);
					}else if(action == ActionType.DEL){
						diskIw.deleteDocuments(action.delTerm);
						ramIw.deleteDocuments(action.delTerm);
					}else if(action == ActionType.ADD){
						indexRecord(rec, true);
					}else{
						throw new Exception("unknown actiontype " + action + " for record " + rec);
					}
				}catch(Exception e){
					e.printStackTrace();
					log.error(e, "when index record %s", rec);
				}
			}
			if(cnt > 0){
				long st = System.nanoTime();
				diskIw.forceMerge(8);
				if(log.logger.isInfoEnabled()){
					long ed = System.nanoTime();
					log.info("disk merge8 used time %s ms", (ed - st) / 10000/ 100.0);
				}
				st = System.nanoTime();
				ramIw.forceMerge(8);
				if(log.logger.isInfoEnabled()){
					long ed = System.nanoTime();
					log.info("ram merge8 used time %s ms", (ed - st) / 10000/ 100.0);
				}
			}
		}
		log.warn("total indexed record %s", cnt);
		if(cnt > 0){
			long st = System.nanoTime();
			diskIw.commit();
			if(log.logger.isInfoEnabled()){
				long ed = System.nanoTime();
				log.info("disk commit used time %s ms", (ed - st) / 10000/ 100.0);
			}

			st = System.nanoTime();
			ramIw.commit();
			if(log.logger.isInfoEnabled()){
				long ed = System.nanoTime();
				log.info("ram commit used time %s ms", (ed - st) / 10000/ 100.0);
			}
		}
		log.info("index has successfully done! to index directory %s", this.diskIw.getDirectory());
	}

	public void close() throws IOException{
		diskIw.close();
		ramIw.close();
	}
}

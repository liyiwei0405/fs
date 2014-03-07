package com.funshion.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.IOUtils;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Md5;
import com.funshion.search.utils.TCPClient;
import com.funshion.search.utils.systemWatcher.MessageClient;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class ChgSynClient extends Thread{
	public static final LogHelper log = new LogHelper("chSynClient");
	final IndexableFs fs;
	private final int queryItv;
	private final String serverIp;
	private final int serverDaemonPort;
	protected final int ver = 1;
	public ChgSynClient(ConfigReader cr, IndexableFs fs) throws IOException{
		queryItv = cr.getInt("queryItv", 1000);
		serverIp = cr.getValue("serverIp");
		this.serverDaemonPort = cr.getInt("serverDaemonPort");
		this.fs = fs;
	}
	public void run(){
		while(true){
			try {
				query();
			} catch (Exception e) {
				log.error(e, "when syn querys");
				e.printStackTrace();
			}finally{
				try {
					Thread.sleep(queryItv);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void query() throws Exception{
		MessageClient clt = new MessageClient(serverIp, serverDaemonPort);
		try{
			QueryMessage msg = MessageClient.emptyQuery("crtDatum");
			AnswerMessage ans = clt.queryMsg(msg);
			log.debug("crtDatum query result %s", ans);
			if(WatcherAction.isStatusOK(ans)){
				final int serverPort = Integer.parseInt(ans.answerBody.get(0));
				String newDirName = ans.answerBody.get(1);
				int fileNum = Integer.parseInt(ans.answerBody.get(2));
				int synIndex = fs.checkChg(newDirName, fileNum);
			
				if(synIndex >= 0){
					log.info("fetching %s's %s'th chgLog", newDirName, synIndex);
					try {
						DatumFile f = fetchFile(serverPort, newDirName, synIndex);
						log.info("fetching %s's %s'th chgLog OK, file is %s, len %s", newDirName, synIndex,
								f, f.file.length());
						if(f.isMain){
							fs.switchChgDir(f);
						}else{
							fs.putDatumFile(f);
						}
					} catch (IOException e) {
						log.error(e, "when fetch file " + newDirName + "'s " + synIndex + "'th chgLog");
						e.printStackTrace();
					}
				}else{
					log.debug("no new datas from server %s:%s",
							this.serverIp, this.serverDaemonPort);
				}
			}else{
				log.error("bad answer status! msg is %s", ans);
			}

		}finally{
			clt.close();
		}
	}

	
	public DatumFile fetchFile(int port, String datumDirName, int index) throws IOException{
		TCPClient clt = new TCPClient(serverIp, port, 3000);//configurable timeout
		try{
			Socket skt = clt.getSocket();
			InputStream ips = skt.getInputStream();
			OutputStream ops = skt.getOutputStream();
			ChgTransQueryHeadStruct stct = new ChgTransQueryHeadStruct(datumDirName, index);
			stct.writeTo(ops);
			ops.flush();
			int status = IOUtils.readInt(ips);
			if(status != 0){
				throw new IOException("ask for " + datumDirName + ":" + index + ", get status " + status);
			}
			ChgTransAnswerHeadStruct answer = new ChgTransAnswerHeadStruct(ips);
			if(!answer.dirName.equals(datumDirName) || (answer.index != index)){
				log.error("strange! ask for '%s':'%s', but get '%s':'%s'", datumDirName, index, answer.dirName, answer.index);
				throw new IOException("Strange ERROR for fetch file! query and answer mismatch!");
			}

			File tmpToWrite = fs.prepareTmpFile();
			FileOutputStream fos = new FileOutputStream(tmpToWrite);
			byte[]buf = new byte[1024 * 32];
			Md5 md5 = new Md5();
			
			
			try{
				int len = 0;
				while(true){
//					long st = System.currentTimeMillis();
					int readed = ips.read(buf);
					if(readed == -1){
						break;
					}
					len += readed;
//					long used = System.currentTimeMillis() - st;
					fos.write(buf, 0, readed);
					md5.append(buf, 0, readed);
					if(len >= answer.fileLen){
						break;
					}
				}
			}finally{
				fos.close();
			}

			File dir = fs.getNewDir(datumDirName);//prepare dir if not exists
			if(tmpToWrite.length() != answer.fileLen){
				throw new IOException("file len mismatch, expect = " + answer.fileLen + ", but get " + tmpToWrite.length());
			}
			log.debug("fileLen check match!");
			byte[]md5Bytes = md5.finish();
			if(md5Bytes.length == answer.md5.length){
				for(int x = 0; x < md5Bytes.length; x ++){
					if(md5Bytes[x] != answer.md5[x]){
						throw new IOException("md5 value mismatch!" + Md5.byte2Hex(answer.md5) + "!=" + Md5.byte2Hex(md5Bytes));
					}
				}
			}else{
				throw new IOException("md5 Len mismatch!" + answer.md5 + "!=" + md5Bytes.length);
			}
			log.debug("md5 check match!");
			
			File newFile = new File(dir, answer.dfileName);
			if(newFile.exists()){
				log.warn("STRANGE, file has already exsit! try deleting %s", newFile);
				newFile.delete();
			}
			if(!tmpToWrite.renameTo(newFile)){
				throw new IOException("rename Fail! from " + tmpToWrite.getAbsolutePath() + " to " + newFile);
			}
			if(newFile.exists() && newFile.length() == answer.fileLen){//double-check!
				log.debug("file rename to %s ok!", newFile);
			}else{
				throw new IOException("rename Fail! from " + tmpToWrite.getAbsolutePath() + " to " + newFile);
			}
			newFile.setWritable(false);
			return new DatumFile(newFile, md5Bytes, index == 0);
		}finally{
			clt.close();
		}
	}
}

package com.funshion.search.segment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.Consoler;

public class RMMSegment extends Segment{
	private final HashMap<String, Short> map = new HashMap<String, Short>();
	private final boolean reverse = true;
	private RMMSegment() {
		load(ConfUtils.getConfFile("segment/lexicon"), map, reverse);
	}
	/**
	 * get a instance of of this class
	 * @return
	 */
	public static  RMMSegment instance = new RMMSegment();

	public TokenHandler segment(String source){
		return Segment.segment(source, map, reverse);
	}
	
	public class CoupleResult{
		public final TokenHandler handler;
		public final Set<String>set;
		CoupleResult(TokenHandler handler, Set<String>set){
			this.handler = handler;
			this.set = set;
			this.handler.setCursor(0);
		}
		public Set<String>toQuerySet(){
			Set<String>set = new HashSet<String>();
			set.addAll(this.set);
			while(handler.hasNext()){
				String next = handler.next();
				set.add(next);
			}
			return set;
		}
	}
	/**
	 * FIXME
	 * 此方法不能正确处理具有标点隔开的情况（可能增加误匹配）
	 * @param source
	 * @return 
	 */
	public CoupleResult noCoverSegment(String source){
		Set<String>set = new HashSet<String>();
		TokenHandler hdl = this.segment(source);
		while(hdl.hasNext()){
			String next = hdl.next();
			int crtCursor = hdl.getCursor();
			//			if(next.length() < 2){
			//				continue;
			//			}
			if(next.charAt(0) < 128){
				continue;
			}


			StringBuffer sb = new StringBuffer();
			sb.append(next);
			for(int x = 0; x < 4; x ++){
				if(hdl.hasNext()){
					sb.append(hdl.next());
				}
				if(sb.length() > 4){
					break;
				}
			}
			String newStr = sb.toString();
			for(int x = 0; x < next.length(); x ++){
				for(int y = x + 2; y < newStr.length(); y ++){
					String sub = newStr.substring(x, y);
					Short type = map.get(sub);
					if(type == null){
						continue;
					}else if(type == state_can){
						set.add(sub);
					}else if(type == state_is){
						set.add(sub);
						break;
					}else if(type == state_may){
						continue;
					}
				}
			}
			hdl.setCursor(crtCursor);
		}
		return new CoupleResult(hdl, set);
	}
	
	public static void main(String[]args) throws UnsupportedEncodingException{
		while(true){
			String ipt = Consoler.readString("\n:");
			System.out.println(instance.noCoverSegment(ipt).toQuerySet());
		}
	}
}

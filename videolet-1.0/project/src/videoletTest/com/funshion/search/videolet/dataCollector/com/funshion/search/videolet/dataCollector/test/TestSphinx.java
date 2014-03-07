package com.funshion.search.videolet.dataCollector.com.funshion.search.videolet.dataCollector.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;
import org.sphx.api.SphinxWordInfo;

import com.funshion.search.utils.Consoler;

public class TestSphinx {
	
	static int maxMatches = 0;
	public static void main(String[]a) throws IOException {
		maxMatches = Consoler.readInt("maxMatches：", 100);
		TestSphinx tp = new TestSphinx();
		while(true){
			String word = Consoler.readString(":");
			try {
				tp.test(word);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public static void test(String word) throws SphinxException{
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		map.put("c", 20);
		map.put("d", 1);
		
		SphinxClient clt = new SphinxClient();
		clt.setServer ("192.168.16.108", 3618);
//		clt.setServer ("192.168.130.37", 23518);
		clt.setFieldWeights(null);
//		clt.setConnectTimeout(1000);
		clt.setMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
		clt.setLimits (0, maxMatches, maxMatches );
		clt.setFilter("isdel", 0, false);
//		clt.set
		clt.setSortMode (SphinxClient.SPH_SORT_EXTENDED, "@weight desc");
		
		String q = word;
		SphinxResult res = clt.query(q.toString());
		if ( res==null )
		{
			System.err.println ( "Error: " + clt.getLastError() );
			System.exit ( 1 );
		}
		if (clt.getLastWarning()!=null && clt.getLastWarning().length()>0 )
			System.out.println ( "WARNING: " + clt.getLastWarning() + "\n" );
		
		/* print me out */
		System.out.println ( "Query '" + q + "' retrieved " + res.total + " of " + res.totalFound + " matches in " + res.time + " sec." );
		System.out.println ( "Query stats:" );
		for ( int i=0; i<res.words.length; i++ )
		{
			SphinxWordInfo wordInfo = res.words[i];
			System.out.println ( "\t'" + wordInfo.getWord() + "' found " + wordInfo.getHits() + " times in " + wordInfo.getDocs() + " documents" );
		}

		System.out.println ( "\nMatches:" );
		for ( int i=0; i<res.getMatches().size(); i++ )
		{
			SphinxMatch info = res.getMatches().get(i);
			System.out.print ( (i+1) + ". id=" + info.getDocId() + ", weight=" + info.getWeight() );

			if ( res.attrNames==null || res.attrTypes==null )
				continue;

			for ( int a=0; a<res.attrNames.length; a++ )
			{
				System.out.print ( ", " + res.attrNames[a] + "=" );

				if ( res.attrTypes[a]==SphinxClient.SPH_ATTR_MULTI)
				{
					System.out.print ( "(" );
					long[] attrM = (long[]) info.getAttribute(a);
					if ( attrM!=null )
						for ( int j=0; j<attrM.length; j++ )
					{
						if ( j!=0 )
							System.out.print ( "," );
						System.out.print ( attrM[j] );
					}
					System.out.print ( ")" );

				} else
				{
					switch ( res.attrTypes[a] )
					{
						case SphinxClient.SPH_ATTR_INTEGER:
						case SphinxClient.SPH_ATTR_ORDINAL:
						case SphinxClient.SPH_ATTR_FLOAT:
						case SphinxClient.SPH_ATTR_BIGINT:
							/* ints, longs, floats, strings.. print as is */
							System.out.print ( info.getAttribute(a) );
							break;

						case SphinxClient.SPH_ATTR_TIMESTAMP:
							Long iStamp = (Long) info.getAttribute(a);
							/**
							Date date = new Date ( iStamp.longValue()*1000 );
							System.out.print ( date.toString() ); **/
							System.out.print (iStamp);
							break;

						default:
							System.out.print ( "(unknown-attr-type=" + res.attrTypes[a] + ")" );
					}
				}
			}

			System.out.println();
		}
	}
}

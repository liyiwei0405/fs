package test;

import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.QueryVideolet.Iface;
import com.funshion.search.videolet.thrift.QueryVideolet.query_args;
import com.funshion.search.videolet.thrift.QueryVideolet.query_result;
import com.funshion.search.videolet.thrift.VideoletSearchResult;

public class VideoletThriftClient extends org.apache.thrift.TServiceClient implements Iface {

	public static class Factory implements org.apache.thrift.TServiceClientFactory<VideoletThriftClient> {
		public Factory() {}
		public VideoletThriftClient getClient(org.apache.thrift.protocol.TProtocol prot) {
			return new VideoletThriftClient(prot);
		}
		public VideoletThriftClient getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
			return new VideoletThriftClient(iprot, oprot);
		}
	}

	public VideoletThriftClient(org.apache.thrift.protocol.TProtocol prot)
	{
		super(prot, prot);
	}

	public VideoletThriftClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
		super(iprot, oprot);
	}

	public VideoletSearchResult query(QueryStruct qs) throws org.apache.thrift.TException
	{
		send_query(qs);
		return recv_query();
	}

	public void send_query(QueryStruct qs) throws org.apache.thrift.TException
	{
		query_args args = new query_args();
		args.setQs(qs);
		sendBase("query", args);
	}

	public VideoletSearchResult recv_query() throws org.apache.thrift.TException
	{
		query_result result = new query_result();
		receiveBase(result, "query");
		if (result.isSetSuccess()) {
			return result.success;
		}
		throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "query failed: unknown result");
	}

}
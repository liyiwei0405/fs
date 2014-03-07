package com.funshion.search.media.search.atdd;

import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.retrieve.media.thrift.MediaSearchService.Client;
import com.funshion.retrieve.media.thrift.MediaSearchService.Iface;
import com.funshion.retrieve.media.thrift.MediaSearchService.retrive1_args;
import com.funshion.retrieve.media.thrift.MediaSearchService.retrive1_result;


public class MediaSearchThriftClient extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
        public Factory() {}
        @Override
		public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
          return new Client(prot);
        }
        @Override
		public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
          return new Client(iprot, oprot);
        }
      }

      public MediaSearchThriftClient(org.apache.thrift.protocol.TProtocol prot)
      {
        super(prot, prot);
      }

      public MediaSearchThriftClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        super(iprot, oprot);
      }

      @Override
	public MediaRetrieveResult retrive1(RetrieveStruct qs) throws org.apache.thrift.TException
      {
        send_retrive1(qs);
        return recv_retrive1();
      }
      

      public void send_retrive1(RetrieveStruct qs) throws org.apache.thrift.TException
      {
        retrive1_args args = new retrive1_args();
        args.setQs(qs);
        sendBase("retrive1", args);
      }

      public MediaRetrieveResult recv_retrive1() throws org.apache.thrift.TException
      {
        retrive1_result result = new retrive1_result();
        receiveBase(result, "retrive1");
        if (result.isSetSuccess()) {
          return result.success;
        }
        throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "retrive1 failed: unknown result");
      }

    }
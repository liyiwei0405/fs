package com.funshion.gamma.media;

import java.util.List;

import org.apache.thrift.TException;

import com.funshion.gamma.media.thrift.FieldGroupType;
import com.funshion.gamma.media.thrift.MediaListV2;
import com.funshion.gamma.media.thrift.MediaV2;
import com.funshion.gamma.media.thrift.MediaWithClientList;

public class MediaServerImpl implements com.funshion.gamma.media.thrift.MediaServiceV2.Iface
 {

	@Override
	public MediaV2 getMediaById(int mediaId, FieldGroupType type)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MediaV2> getMediaListByIds(List<Integer> mediaIds,
			FieldGroupType type) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaListV2 getMediaListByType(String displayType,
			FieldGroupType type, int pageSize, int pageIndex) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaListV2 getMediaListByDate(String modifyDate,
			FieldGroupType type, int pageSize, int pageIndex) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaWithClientList getUpdatedMediasOrderByModifyTime(
			String modifyDate, int limit, String order) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaWithClientList getMediasStateOnClient(List<Integer> mediaIds,
			String clientType) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaWithClientList getMediasState(List<Integer> mediaIds)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.funshion.retrieve.media.portableCondition;

import java.util.List;

import com.funshion.retrieve.media.thrift.Token;

public abstract class PortalFSCondition {
	public final String field;
	public PortalFSCondition(String field){
		this.field = field;
	}
	
	public abstract void appendToList(List<Token>list, final ConjunctType connType) throws Exception;
	
}

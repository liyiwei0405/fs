package com.funshion.fsql.expression.ast;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.funshion.fsql.expression.FsqlInterpreter;
import com.funshion.fsql.expression.Token;
import com.funshion.fsql.expression.TokenType;
import com.funshion.luc.defines.AnaType;
import com.funshion.luc.defines.FullTextQueryMaker;
import com.funshion.luc.defines.IFieldDefine;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.luc.defines.LikeQueryMaker;
import com.funshion.search.analyzers.CharacterFormat;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;

public class ItemConditionNode extends QueryMakeableConditionNode{
	static final String IntT = "IntT", LongT = "LongT", FloatT = "FloatT";
	@SuppressWarnings("rawtypes")
	static NumericRangeQuery makeRange(String type, String field,
			Object low, Object high, boolean incLow, boolean incHigh) throws Exception{
		if(type == IntT){
			return NumericRangeQuery.newIntRange(field, 
					(Integer)low, (Integer)high, incLow, incHigh);
		}else if(type == LongT){
			return NumericRangeQuery.newLongRange(field, 
					(Long)low, (Long)high, incLow, incHigh);
		}else if(type == FloatT){
			return NumericRangeQuery.newFloatRange(field, 
					(Float)low, (Float)high, incLow, incHigh);
		}else{
			throw new Exception("unkonw type " + type);
		}
	}
	private FieldNameNode fieldName;
	private Token relation;
	private FiledValueNode fieldValue;

	public ItemConditionNode(int position, FieldNameNode fieldName,
			Token relation, FiledValueNode valueNode) {
		super(position);
		this.fieldName = fieldName;
		this.fieldValue = valueNode; 
		this.relation = relation;
	}

	@Override
	public Query toQueryElement(FsqlInterpreter interpreter) throws Exception {
		AnaType aType = ITableDefine.instance.getAnaType(fieldName.name);
		if(aType == null){
			throw new Exception("unkown field " + fieldName.name);
		}
		Query q;
		IFieldDefine fdef = ITableDefine.instance.getFieldDefine(this.fieldName.name.toLowerCase());
		final String qName = fdef.fieldName;
		final String qValue = this.fieldValue.getValue().toString();
		if(aType.isNumType()){
			if(this.relation.type == TokenType.EQUAL){
				BytesRef br = new BytesRef();
				NumericUtils.intToPrefixCoded(Integer.parseInt(qValue), 0, br);
				Term term = new Term(qName, br);
				q = new TermQuery(term);
			}else{
				Object var;
				String numType;
				if(aType == AnaType.aFloat){
					var = Float.parseFloat(qValue);
					numType = FloatT;
				}else if(aType == AnaType.aInt){
					var = Integer.parseInt(qValue);
					numType = IntT;
				}else if(aType == AnaType.aLong){
					var = Long.parseLong(qValue);
					numType = LongT;
				}else {
					throw new Exception("not number type " + aType);
				}
				if(this.relation.type == TokenType.LESS_EQUAL){
					q = makeRange(numType, qName, null, var, false, true);
				}else if(this.relation.type == TokenType.LESS_THEN){
					q = makeRange(numType, qName, null, var, false, false);
				}else if(this.relation.type == TokenType.GREATER_EQUAL){
					q = makeRange(numType, qName, var, null, true, false);
				}else if(this.relation.type == TokenType.GREATER_THEN){
					q = makeRange(numType, qName, var, null, false, false);
				}else{
					throw new Exception("when compile to BooleanQuery, INT store not support OperType " + this.relation.type);
				}
			}
		}else{
			if(aType == AnaType.aText){
				String toSearch = F2JConvert.instance.convert(qValue);
				toSearch = CharacterFormat.rewriteString(toSearch);
				if(this.relation.type == TokenType.SEARCH){
					FullTextQueryMaker ins = ITableDefine.instance.getFullTextQueryMaker();
					q = ins.makeTitleQuery(fdef.fieldName, toSearch);
				}else if(this.relation.type == TokenType.LIKE){
					LikeQueryMaker ins = ITableDefine.instance.getLikeQueryMaker();
					q = ins.makeLikeQuery(fdef.fieldName, toSearch);
				}else{
					throw new Exception("when compile to BooleanQuery, aText is not support for '" + this + "'");
				}
			}else if(aType == AnaType.aStr || aType == AnaType.eStr){
				String toSearch = qValue.toString().toLowerCase().trim();
				if(this.relation.type == TokenType.EQUAL){
					Term term = new Term(qName, toSearch);
					q = new TermQuery(term);
				}else{
					throw new Exception("when compile to BooleanQuery, TXT store not support OperType " + this);
				}
			}else{
				throw new Exception("not support " + aType + " for "+ this);
			}
		}
		return q;
	}

	public FieldNameNode getFieldName() {
		return fieldName;
	}

	public void setFieldName(FieldNameNode fieldName) {
		this.fieldName = fieldName;
	}

	public Token getRelation() {
		return relation;
	}

	public void setRelation(Token relation) {
		this.relation = relation;
	}

	public FiledValueNode getValueNode() {
		return fieldValue;
	}

	public void setValueNode(FiledValueNode valueNode) {
		this.fieldValue = valueNode;
	}

	@Override
	public String toString(){
		return this.myConjunct + fieldName.name + " " +  relation.type  + " " + fieldValue.getValue() + super.appendString();
	}

	public void setValue(String value) {
		int pos = -1;
		if(fieldValue != null){
			pos = fieldValue.position;
		}
		fieldValue = new FiledValueNode(pos, value);
	}
}

package com.funshion.fsql.expression;
public class OrderInfo{
	public final String filed;
	public final boolean asc;
	OrderInfo(Token field, boolean asc){
		String txt = field.text;
		//FIXME to real order??
		this.filed = txt;
		this.asc = asc;
	}
	@Override
	public String toString(){
		return "OrderInfo:" + this.filed + " " + (asc ? "asc" : "desc");
	}
	
}
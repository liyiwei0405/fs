namespace java com.funshion.search.utils.systemWatcher.message
namespace php dev.utility.search

struct QueryMessage{
	1:string messageName
	2:string ienv
	3:list<string>messageBody
}
struct AnswerMessage{
	1:i16 answerStatus
	2:i32 actionStatus
	3:i64 serverSeq
	4:list<string>answerBody

}
service MessageService{
	AnswerMessage queryMsg(1:QueryMessage qm)
}

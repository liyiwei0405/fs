package com.bmtech.htmls.parser.util;

/**
 * Support for chained exceptions in code that predates Java 1.4.
 * A chained exception can use a Throwable argument to reference
 * a lower level exception. The chained exception provides a
 * stack trace that includes the message and any throwable
 * exception included as an argument in the chain.
 *
 * For example:
 *
 *   ApplicationException: Application problem encountered;
 *   ProcessException: Unable to process document;
 *   java.io.IOException: Unable to open 'filename.ext'
 *     at ChainedExceptionTest.openFile(ChainedExceptionTest.java:19)
 *     at ChainedExceptionTest.processFile(ChainedExceptionTest.java:27)
 *     at ChainedExceptionTest.application(ChainedExceptionTest.java:40)
 *     at ChainedExceptionTest.main(ChainedExceptionTest.java:52)
 *
 * Represents the output from two nested exceptions. The outside
 * exception is a subclass of ChainedException called
 * ApplicationException, which includes a throwable reference.
 * The throwable reference is also a subclass of ChainedException,
 * called ProcessException, which in turn includes a reference to
 * a standard IOException. In each case, the message is increasingly
 * specific about the nature of the problem. The end user may only
 * see the application exception, but debugging is greatly
 * enhanced by having more details in the stack trace.
 *
 * @author Claude Duguay
 **/

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;

public class ChainedException
  extends Exception
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
protected Throwable throwable;

  public ChainedException() {}

  public ChainedException(String message)
  {
    super(message);
  }

  public ChainedException(Throwable throwable)
  {
    this.throwable = throwable;
  }

  public ChainedException(String message, Throwable throwable)
  {
    super(message);
    this.throwable = throwable;
  }

  public String[] getMessageChain()
  {
    Vector<String> list = getMessageList();
    String[] chain = new String[list.size()];
    list.copyInto (chain);
    return chain;
  }

  public Vector<String> getMessageList()
  {
    Vector<String> list = new Vector<String>();
    list.addElement(getMessage());
    if (throwable != null)
    {
      if (throwable instanceof ChainedException)
      {
        ChainedException chain = (ChainedException)throwable;
        Vector<String> sublist = chain.getMessageList ();
        for (int i = 0; i < sublist.size (); i++)
            list.addElement ((String) sublist.elementAt (i));
      }
      else
      {
        String message = throwable.getMessage();
        if (message != null && !message.equals(""))
        {
          list.addElement (message);
        }
      }
    }
    return list;
  }

  public Throwable getThrowable()
  {
    return throwable;
  }

  public void printStackTrace()
  {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream out)
  {
    synchronized (out)
    {
      if (throwable != null)
      {
        out.println(getClass().getName() +
          ": " + getMessage() + ";");
        throwable.printStackTrace(out);
      }
      else
      {
        super.printStackTrace(out);
      }
    }
  }

  public void printStackTrace(PrintWriter out)
  {
    synchronized (out)
    {
      if (throwable != null)
      {
        out.println(getClass().getName() +
          ": " + getMessage() + ";");
        throwable.printStackTrace(out);
      }
      else
      {
        super.printStackTrace(out);
      }
    }
  }
}


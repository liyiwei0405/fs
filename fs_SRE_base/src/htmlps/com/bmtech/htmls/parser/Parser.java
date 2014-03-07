// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2004 Somik Raha
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/Parser.java,v $
// $Author: liying $
// $Date: 2012/07/31 06:46:36 $
// $Revision: 1.2 $
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//

package com.bmtech.htmls.parser;

import java.io.Serializable;

import com.bmtech.htmls.parser.lexer.Lexer;
import com.bmtech.htmls.parser.lexer.Page;
import com.bmtech.htmls.parser.util.DefaultParserFeedback;
import com.bmtech.htmls.parser.util.IteratorImpl;
import com.bmtech.htmls.parser.util.NodeIterator;
import com.bmtech.htmls.parser.util.NodeList;
import com.bmtech.htmls.parser.util.ParserException;
import com.bmtech.htmls.parser.util.ParserFeedback;
import com.bmtech.htmls.parser.visitors.NodeVisitor;



/**
 * The main parser class.
 * This is the primary class of the HTML Parser library. It provides
 * constructors that take a {@link #Parser(String) String},
 * a {@link #Parser(URLConnection) URLConnection}, or a
 * {@link #Parser(Lexer) Lexer}.  In the case of a String, 
 * a check is made to see if the first non-whitespace character is a &lt;, in
 * which case it is assumed to be HTML. Otherwise an
 * attempt is made to open it as a URL, and if that fails it assumes it is a
 * local disk file. If you want to parse a String after using the
 * {@link #Parser() no-args} constructor, use 
 * {@link #setInputHTML setInputHTML()}, or you can use {@link #createParser}.
 * <p>The Parser provides access to the contents of the
 * page, via a {@link #elements() NodeIterator}, a
 * {@link #parse(NodeFilter) NodeList} or a
 * {@link #visitAllNodesWith NodeVisitor}.
 * <p>Typical usage of the parser is:
 * <code>
 * <pre>
 * Parser parser = new Parser ("http://whatever");
 * NodeList list = parser.parse (null);
 * // do something with your list of nodes.
 * </pre>
 * </code></p>
 * <p>What types of nodes and what can be done with them is dependant on the
 * setup, but in general a node can be converted back to HTML and it's
 * children (enclosed nodes) and parent can be obtained, because nodes are
 * nested. See the {@link Node} interface.</p>
 * <p>For example, if the URL contains:<br>
 * <code>
 * {@.html
 * <html>
 * <head>
 * <title>Mondays -- What a bad idea.</title>
 * </head>
 * <body BGCOLOR="#FFFFFF">
 * Most people have a pathological hatred of Mondays...
 * </body>
 * </html>}
 * </code><br>
 * and the example code above is used, the list contain only one element, the
 * {@.html <html>} node.  This node is a {@link com.bmtech.htmls.parser.tags tag},
 * which is an object of class
 * {@link com.bmtech.htmls.parser.tags.Html Html} if the default {@link NodeFactory}
 * (a {@link PrototypicalNodeFactory}) is used.</p>
 * <p>To get at further content, the children of the top
 * level nodes must be examined. When digging through a node list one must be
 * conscious of the possibility of whitespace between nodes, e.g. in the example
 * above:
 * <code>
 * <pre>
 * Node node = list.elementAt (0);
 * NodeList sublist = node.getChildren ();
 * System.out.println (sublist.size ());
 * </pre>
 * </code>
 * would print out 5, not 2, because there are newlines after {@.html <html>},
 * {@.html </head>} and {@.html </body>} that are children of the HTML node
 * besides the {@.html <head>} and {@.html <body>} nodes.</p>
 * <p>Because processing nodes is so common, two interfaces are provided to
 * ease this task, {@link com.bmtech.htmls.parser.filters filters}
 * and {@link com.bmtech.htmls.parser.visitors visitors}.
 */
public class Parser
    implements
        Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2791921111635846306L;

	// Please don't change the formatting of the version variables below.
    // This is done so as to facilitate ant script processing.

    /**
     * The floating point version number ({@value}).
     */
    public static final double
    VERSION_NUMBER = 1.6
    ;

    /**
     * The type of version ({@value}).
     */
    public static final String
    VERSION_TYPE = "Release Build"
    ;

    /**
     * The date of the version ({@value}).
     */
    public static final String
    VERSION_DATE = "Jun 10, 2006"
    ;

    // End of formatting

    /**
     * The display version ({@value}).
     */
    public static final String VERSION_STRING =
            "" + VERSION_NUMBER
            + " (" + VERSION_TYPE + " " + VERSION_DATE + ")";

    /**
     * Feedback object.
     */
    protected ParserFeedback mFeedback;

    /**
     * The html lexer associated with this parser.
     */
    protected Lexer mLexer;

    /**
     * A quiet message sink.
     * Use this for no feedback.
     */
    public static final ParserFeedback DEVNULL =
        new DefaultParserFeedback (DefaultParserFeedback.QUIET);

    /**
     * A verbose message sink.
     * Use this for output on <code>System.out</code>.
     */
    public static final ParserFeedback STDOUT = new DefaultParserFeedback ();


    //
    // Static methods
    //

    /**
     * Return the version string of this parser.
     * @return A string of the form:
     * <pre>
     * "[floating point number] ([build-type] [build-date])"
     * </pre>
     */
    public static String getVersion ()
    {
        return (VERSION_STRING);
    }

    /**
     * Return the version number of this parser.
     * @return A floating point number, the whole number part is the major
     * version, and the fractional part is the minor version.
     */
    public static double getVersionNumber ()
    {
        return (VERSION_NUMBER);
    }





    /**
     * Creates the parser on an input string.
     * @param html The string containing HTML.
     * @param charset <em>Optional</em>. The character set encoding that will
     * be reported by {@link #getEncoding}. If charset is <code>null</code>
     * the default character set is used.
     * @return A parser with the <code>html</code> string as input.
     * @exception IllegalArgumentException if <code>html</code> is <code>null</code>.
     */
    public static Parser createParser (String html, String charset)
    {
        Parser ret;

        if (null == html)
            throw new IllegalArgumentException ("html cannot be null");
        ret = new Parser (new Lexer (new Page (html, charset)));

        return (ret);
    }

    //
    // Constructors
    //

    /**
     * Zero argument constructor.
     * The parser is in a safe but useless state parsing an empty string.
     * Set the lexer or connection using {@link #setLexer}
     * or {@link #setConnection}.
     * @see #setLexer(Lexer)
     * @see #setConnection(URLConnection)
     */
    public Parser ()
    {
        this (new Lexer (new Page ("")), DEVNULL);
    }

    /**
     * Construct a parser using the provided lexer and feedback object.
     * This would be used to create a parser for special cases where the
     * normal creation of a lexer on a URLConnection needs to be customized.
     * @param lexer The lexer to draw characters from.
     * @param fb The object to use when information,
     * warning and error messages are produced. If <em>null</em> no feedback
     * is provided.
     */
    public Parser (Lexer lexer, ParserFeedback fb)
    {
        setFeedback (fb);
        setLexer (lexer);
        setNodeFactory (new PrototypicalNodeFactory ());
    }

    /**
     * Creates a Parser object with the location of the resource (URL or file)
     * You would typically create a DefaultHTMLParserFeedback object and pass
     * it in.
     * @see #Parser(URLConnection,ParserFeedback)
     * @param resource Either a URL, a filename or a string of HTML.
     * The string is considered HTML if the first non-whitespace character
     * is a &lt;. The use of a url or file is autodetected by first attempting
     * to open the resource as a URL, if that fails it is assumed to be a file
     * name.
     * A standard HTTP GET is performed to read the content of the URL.
     * @param feedback The HTMLParserFeedback object to use when information,
     * warning and error messages are produced. If <em>null</em> no feedback
     * is provided.
     * @throws ParserException If the URL is invalid.
     */
    public Parser (String resource, ParserFeedback feedback)
        throws
            ParserException
    {
        setFeedback (feedback);
        setResource (resource);
        setNodeFactory (new PrototypicalNodeFactory ());
    }

    /**
     * Creates a Parser object with the location of the resource (URL or file).
     * A DefaultHTMLParserFeedback object is used for feedback.
     * @param resource Either HTML, a URL or a filename (autodetects).
     * @throws ParserException If the resourceLocn argument does not resolve
     * to a valid page or file.
     * @see #Parser(String,ParserFeedback)
     */
    public Parser (String resource) throws ParserException
    {
        this (resource, STDOUT);
    }

    /**
     * Construct a parser using the provided lexer.
     * A feedback object printing to {@link #STDOUT System.out} is used.
     * This would be used to create a parser for special cases where the
     * normal creation of a lexer on a URLConnection needs to be customized.
     * @param lexer The lexer to draw characters from.
     */
    public Parser (Lexer lexer)
    {
        this (lexer, STDOUT);
    }


    /**
     * Set the html, a url, or a file.
     * @param resource The resource to use.
     * @exception IllegalArgumentException if <code>resource</code> is <code>null</code>.
     * @exception ParserException if a problem occurs in connecting.
     */
    public void setResource (String resource)
        throws
            ParserException
    {
            setLexer (new Lexer (new Page (resource)));

    }
 

    /**
     * Set the encoding for the page this parser is reading from.
     * @param encoding The new character set to use.
     * @throws ParserException If the encoding change causes characters that
     * have already been consumed to differ from the characters that would
     * have been seen had the new encoding been in force.
     * @see com.bmtech.htmls.parser.util.EncodingChangeException
     * @see #getEncoding
     */
    public void setEncoding (String encoding)
        throws
            ParserException
    {
        getLexer ().getPage ().setEncoding (encoding);
    }

    /**
     * Get the encoding for the page this parser is reading from.
     * This item is set from the HTTP header but may be overridden by meta
     * tags in the head, so this may change after the head has been parsed.
     * @return The encoding currently in force.
     * @see #setEncoding
     */
    public String getEncoding ()
    {
        return (getLexer ().getPage ().getEncoding ());
    }

    /**
     * Set the lexer for this parser.
     * The current NodeFactory is transferred to (set on) the given lexer,
     * since the lexer owns the node factory object.
     * It does not adjust the <code>feedback</code> object.
     * @param lexer The lexer object to use.
     * @see #setNodeFactory
     * @see #getLexer
     * @exception IllegalArgumentException if <code>lexer</code> is <code>null</code>.
     */
    public void setLexer (Lexer lexer)
    {
        NodeFactory factory;
        String type;

        if (null == lexer)
            throw new IllegalArgumentException ("lexer cannot be null");
        // move a node factory that's been set to the new lexer
        factory = null;
        if (null != getLexer ())
            factory = getLexer ().getNodeFactory ();
        if (null != factory)
            lexer.setNodeFactory (factory);
        mLexer = lexer;
        // warn about content that's not likely text
        type = mLexer.getPage ().getContentType ();
        if (type != null && !type.startsWith ("text"))
            getFeedback ().warning (
                "URL "
                + mLexer.getPage ().getUrl ()
                + " does not contain text");
    }

    /**
     * Returns the lexer associated with the parser.
     * @return The current lexer.
     * @see #setLexer
     */
    public Lexer getLexer ()
    {
        return (mLexer);
    }

    /**
     * Get the current node factory.
     * @return The current lexer's node factory.
     * @see #setNodeFactory
     */
    public NodeFactory getNodeFactory ()
    {
        return (getLexer ().getNodeFactory ());
    }

    /**
     * Set the current node factory.
     * @param factory The new node factory for the current lexer.
     * @see #getNodeFactory
     * @exception IllegalArgumentException if <code>factory</code> is <code>null</code>.
     */
    public void setNodeFactory (NodeFactory factory)
    {
        if (null == factory)
            throw new IllegalArgumentException ("node factory cannot be null");
        getLexer ().setNodeFactory (factory);
    }

    /**
     * Sets the feedback object used in scanning.
     * @param fb The new feedback object to use. If this is null a
     * {@link #DEVNULL silent feedback object} is used.
     * @see #getFeedback
     */
    public void setFeedback (ParserFeedback fb)
    {
        if (null == fb)
            mFeedback = DEVNULL;
        else
            mFeedback = fb;
    }

    /**
     * Returns the current feedback object.
     * @return The feedback object currently being used.
     * @see #setFeedback
     */
    public ParserFeedback getFeedback()
    {
        return (mFeedback);
    }

    //
    // Public methods
    //

    /**
     * Reset the parser to start from the beginning again.
     * This assumes support for a reset from the underlying
     * {@link com.bmtech.htmls.parser.lexer.Source} object.
     * <p>This is cheaper (in terms of time) than resetting the URL, i.e.
     * <pre>
     * parser.setURL (parser.getURL ());
     * </pre>
     * because the page is not refetched from the internet.
     * <em>Note: the nodes returned on the second parse are new
     * nodes and not the same nodes returned on the first parse. If you
     * want the same nodes for re-use, collect them in a NodeList with
     * {@link #parse(NodeFilter) parse(null)} and operate on the NodeList.</em>
     */
    public void reset ()
    {
        getLexer ().reset ();
    }

    /**
     * Returns an iterator (enumeration) over the html nodes.
     * {@link com.bmtech.htmls.parser.nodes Nodes} can be of three main types:
     * <ul>
     * <li>{@link com.bmtech.htmls.parser.nodes.TagNode TagNode}</li>
     * <li>{@link com.bmtech.htmls.parser.nodes.TextNode TextNode}</li>
     * <li>{@link com.bmtech.htmls.parser.nodes.RemarkNode RemarkNode}</li>
     * </ul>
     * In general, when parsing with an iterator or processing a NodeList,
     * you will need to use recursion. For example:
     * <code>
     * <pre>
     * void processMyNodes (Node node)
     * {
     *     if (node instanceof TextNode)
     *     {
     *         // downcast to TextNode
     *         TextNode text = (TextNode)node;
     *         // do whatever processing you want with the text
     *         System.out.println (text.getText ());
     *     }
     *     if (node instanceof RemarkNode)
     *     {
     *         // downcast to RemarkNode
     *         RemarkNode remark = (RemarkNode)node;
     *         // do whatever processing you want with the comment
     *     }
     *     else if (node instanceof TagNode)
     *     {
     *         // downcast to TagNode
     *         TagNode tag = (TagNode)node;
     *         // do whatever processing you want with the tag itself
     *         // ...
     *         // process recursively (nodes within nodes) via getChildren()
     *         NodeList nl = tag.getChildren ();
     *         if (null != nl)
     *             for (NodeIterator i = nl.elements (); i.hasMoreElements (); )
     *                 processMyNodes (i.nextNode ());
     *     }
     * }
     *
     * Parser parser = new Parser ("http://www.yahoo.com");
     * for (NodeIterator i = parser.elements (); i.hasMoreElements (); )
     *     processMyNodes (i.nextNode ());
     * </pre>
     * </code>
     * @throws ParserException If a parsing error occurs.
     * @return An iterator over the top level nodes (usually {@.html <html>}).
     */
    public NodeIterator elements () throws ParserException
    {
        return (new IteratorImpl (getLexer (), getFeedback ()));
    }

    /**
     * Parse the given resource, using the filter provided.
     * This can be used to extract information from specific nodes.
     * When used with a <code>null</code> filter it returns an
     * entire page which can then be modified and converted back to HTML
     * (Note: the synthesis use-case is not handled very well; the parser
     * is more often used to extract information from a web page).
     * <p>For example, to replace the entire contents of the HEAD with a
     * single TITLE tag you could do this:
     * <pre>
     * NodeList nl = parser.parse (null); // here is your two node list
     * NodeList heads = nl.extractAllNodesThatMatch (new TagNameFilter ("HEAD"))
     * if (heads.size () > 0) // there may not be a HEAD tag
     * {
     *     Head head = heads.elementAt (0); // there should be only one
     *     head.removeAll (); // clean out the contents
     *     Tag title = new TitleTag ();
     *     title.setTagName ("title");
     *     title.setChildren (new NodeList (new TextNode ("The New Title")));
     *     Tag title_end = new TitleTag ();
     *     title_end.setTagName ("/title");
     *     title.setEndTag (title_end);
     *     head.add (title);
     * }
     * System.out.println (nl.toHtml ()); // output the modified HTML
     * </pre>
     * @return The list of matching nodes (for a <code>null</code>
     * filter this is all the top level nodes).
     * @param filter The filter to apply to the parsed nodes,
     * or <code>null</code> to retrieve all the top level nodes.
     * @throws ParserException If a parsing error occurs.
     */
    public NodeList parse (NodeFilter filter) throws ParserException
    {
        NodeIterator e;
        Node node;
        NodeList ret;

        ret = new NodeList ();
        for (e = elements (); e.hasMoreNodes (); )
        {
            node = e.nextNode ();
            if (null != filter)
                node.collectInto (ret, filter);
            else
                ret.add (node);
        }

        return (ret);
    }

    /**
     * Apply the given visitor to the current page.
     * The visitor is passed to the <code>accept()</code> method of each node
     * in the page in a depth first traversal. The visitor
     * <code>beginParsing()</code> method is called prior to processing the
     * page and <code>finishedParsing()</code> is called after the processing.
     * @param visitor The visitor to visit all nodes with.
     * @throws ParserException If a parse error occurs while traversing
     * the page with the visitor.
     */
    public void visitAllNodesWith (NodeVisitor visitor) throws ParserException
    {
        Node node;
        visitor.beginParsing();
        for (NodeIterator e = elements(); e.hasMoreNodes(); )
        {
            node = e.nextNode();
            node.accept(visitor);
        }
        visitor.finishedParsing();
    }

    /**
     * Initializes the parser with the given input HTML String.
     * @param inputHTML the input HTML that is to be parsed.
     * @throws ParserException If a error occurs in setting up the
     * underlying Lexer.
     * @exception IllegalArgumentException if <code>inputHTML</code> is <code>null</code>.
     */
    public void setInputHTML (String inputHTML)
        throws
            ParserException
    {
        if (null == inputHTML)
            throw new IllegalArgumentException ("html cannot be null");
        if (!"".equals (inputHTML))
            setLexer (new Lexer (new Page (inputHTML)));
    }

    /**
     * Extract all nodes matching the given filter.
     * @see Node#collectInto(NodeList, NodeFilter)
     * @param filter The filter to be applied to the nodes.
     * @throws ParserException If a parse error occurs.
     * @return A list of nodes matching the filter criteria,
     * i.e. for which the filter's accept method
     * returned <code>true</code>.
     */
    public NodeList extractAllNodesThatMatch (NodeFilter filter)
        throws
            ParserException
    {
        NodeIterator e;
        NodeList ret;

        ret = new NodeList ();
        for (e = elements (); e.hasMoreNodes (); )
            e.nextNode ().collectInto (ret, filter);

        return (ret);
    }

  

}

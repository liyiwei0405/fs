// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2004 Somik Raha
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/tags/BaseHrefTag.java,v $
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

package com.bmtech.htmls.parser.tags;

import com.bmtech.htmls.parser.lexer.Page;
import com.bmtech.htmls.parser.nodes.TagNode;
import com.bmtech.htmls.parser.util.ParserException;

/**
 * BaseHrefTag represents an &lt;Base&gt; tag.
 * It extends a basic tag by providing an accessor to the HREF attribute.
 */
public class BaseHrefTag
    extends
        TagNode
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The set of names handled by this tag.
     */
    private static final String[] mIds = new String[] {"BASE"};

    /**
     * Create a new base tag.
     */
    public BaseHrefTag ()
    {
    }

    /**
     * Return the set of names handled by this tag.
     * @return The names to be matched that create tags of this type.
     */
    public String[] getIds ()
    {
        return (mIds);
    }

    /**
     * Get the value of the <code>HREF</code> attribute, if any.
     * @return The <code>HREF</code> value, with the leading and trailing whitespace removed, if any.
     */
    public String getBaseUrl()
    {
        String base;

        base = getAttribute ("HREF");
        if (base != null && base.length() > 0)
            base = base.trim ();
        base = (null == base) ? "" : base;
        
        return (base);
    }

    /**
     * Set the value of the <code>HREF</code> attribute.
     * @param base The new <code>HREF</code> value.
     */
    public void setBaseUrl (String base)
    {
        setAttribute ("HREF", base);
    }

    /**
     * Perform the meaning of this tag.
     * This sets the base URL to use for the rest of the page.
     * @exception ParserException If setting the base URL fails.
     */
    public void doSemanticAction () throws ParserException
    {
        Page page;
        
        page = getPage ();
        if (null != page)
            page.setBaseUrl (getBaseUrl ());
    }
}

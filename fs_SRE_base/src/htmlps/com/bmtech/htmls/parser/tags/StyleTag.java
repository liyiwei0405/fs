// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2004 Somik Raha
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/tags/StyleTag.java,v $
// $Author: liying $
// $Date: 2012/07/31 06:46:37 $
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

import com.bmtech.htmls.parser.scanners.StyleScanner;

/**
 * A StyleTag represents a &lt;style&gt; tag.
 */
public class StyleTag extends CompositeTag
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The set of names handled by this tag.
     */
    private static final String[] mIds = new String[] {"STYLE"};

    /**
     * The set of end tag names that indicate the end of this tag.
     */
    private static final String[] mEndTagEnders = new String[] {"BODY", "HTML"};

    /**
     * Create a new style tag.
     */
    public StyleTag ()
    {
        setThisScanner (new StyleScanner ());
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
     * Return the set of end tag names that cause this tag to finish.
     * @return The names of following end tags that stop further scanning.
     */
    public String[] getEndTagEnders ()
    {
        return (mEndTagEnders);
    }

    /**
     * Get the style data in this tag.
     * @return The HTML of the children of this tag.
     */
    public String getStyleCode ()
    {
        return (getChildrenHTML ());
    }

    public String toPlainTextString(){
    	return "";
    }
    /**
     * Print the contents of the style node.
     * @return A string suitable for debugging or a printout.
     */
    public String toString()
    {
        String guts;
        StringBuffer ret;
        
        ret = new StringBuffer ();

        guts = toHtml ();
        guts = guts.substring (1, guts.length () - 1);
        ret.append ("Style node :\n");
        ret.append (guts);
        ret.append ("\n");

        return (ret.toString ());
    }
}

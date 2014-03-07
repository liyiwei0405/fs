// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2003 Derrick Oswald
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/filters/NodeClassFilter.java,v $
// $Author: liying $
// $Date: 2012/07/31 06:46:38 $
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

package com.bmtech.htmls.parser.filters;

import com.bmtech.htmls.parser.Node;
import com.bmtech.htmls.parser.NodeFilter;

/**
 * This class accepts all tags of a given class.
 */
public class NodeClassFilter implements NodeFilter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The class to match.
     */
    protected Class<?> mClass;

    /**
     * Creates a NodeClassFilter that accepts Html tags.
     */
    public NodeClassFilter ()
    {
        this (com.bmtech.htmls.parser.tags.Html.class);
    }

    /**
     * Creates a NodeClassFilter that accepts tags of the given class.
     * @param cls The class to match.
     */
    public NodeClassFilter (Class<?> cls)
    {
        mClass = cls;
    }

    /**
     * Get the class to match.
     * @return Returns the class.
     */
    public Class<?> getMatchClass ()
    {
        return (mClass);
    }

    /**
     * Set the class to match.
     * @param cls The node class to match.
     */
    public void setMatchClass (Class<?> cls)
    {
        mClass = cls;
    }

    /**
     * Accept nodes that are assignable from the class provided in
     * the constructor.
     * @param node The node to check.
     * @return <code>true</code> if the node is the right class,
     * <code>false</code> otherwise.
     */
    public boolean accept (Node node)
    {
        return ((null != mClass) && mClass.isAssignableFrom (node.getClass ()));
    }
}
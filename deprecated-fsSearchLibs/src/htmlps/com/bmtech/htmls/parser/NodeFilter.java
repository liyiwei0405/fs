// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2003 Derrick Oswald
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/NodeFilter.java,v $
// $Author: liying $
// $Date: 2012/07/31 06:46:35 $
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

/**
 * Implement this interface to select particular nodes.
 */
public interface NodeFilter
    extends
        Serializable,
        Cloneable
{
    /**
     * Predicate to determine whether or not to keep the given node.
     * The behaviour based on this outcome is determined by the context
     * in which it is called. It may lead to the node being added to a list
     * or printed out. See the calling routine for details.
     * @return <code>true</code> if the node is to be kept, <code>false</code>
     * if it is to be discarded.
     * @param node The node to test.
     */
    boolean accept (Node node);
}

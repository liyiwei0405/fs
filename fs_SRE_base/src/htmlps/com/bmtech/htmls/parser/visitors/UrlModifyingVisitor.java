// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2004 Joshua Kerievsky
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/visitors/UrlModifyingVisitor.java,v $
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

package com.bmtech.htmls.parser.visitors;

import com.bmtech.htmls.parser.Node;
import com.bmtech.htmls.parser.Remark;
import com.bmtech.htmls.parser.Tag;
import com.bmtech.htmls.parser.Text;
import com.bmtech.htmls.parser.tags.CompositeTag;
import com.bmtech.htmls.parser.tags.ImageTag;
import com.bmtech.htmls.parser.tags.LinkTag;

public class UrlModifyingVisitor extends NodeVisitor {
    private String linkPrefix;
    private StringBuffer modifiedResult;

    public UrlModifyingVisitor(String linkPrefix) {
        super(true,true);
        this.linkPrefix =linkPrefix;
        modifiedResult = new StringBuffer();
    }

    public void visitRemarkNode (Remark remarkNode)
    {
        modifiedResult.append (remarkNode.toHtml());
    }

    public void visitStringNode(Text stringNode)
    {
        modifiedResult.append (stringNode.toHtml());
    }

    public void visitTag(Tag tag)
    {
        if (tag instanceof LinkTag)
            ((LinkTag)tag).setLink(linkPrefix + ((LinkTag)tag).getLink());
        else if (tag instanceof ImageTag)
            ((ImageTag)tag).setImageURL(linkPrefix + ((ImageTag)tag).getImageURL());
        // process only those nodes that won't be processed by an end tag,
        // nodes without parents or parents without an end tag, since
        // the complete processing of all children should happen before
        // we turn this node back into html text
        if (null == tag.getParent ()
            && (!(tag instanceof CompositeTag) || null == ((CompositeTag)tag).getEndTag ()))
            modifiedResult.append(tag.toHtml());
    }

    public void visitEndTag(Tag tag)
    {
        Node parent;
        
        parent = tag.getParent ();
        // process only those nodes not processed by a parent
        if (null == parent)
            // an orphan end tag
            modifiedResult.append(tag.toHtml());
        else
            if (null == parent.getParent ())
                // a top level tag with no parents
                modifiedResult.append(parent.toHtml());
    }

    public String getModifiedResult() {
        return modifiedResult.toString();
    }
}

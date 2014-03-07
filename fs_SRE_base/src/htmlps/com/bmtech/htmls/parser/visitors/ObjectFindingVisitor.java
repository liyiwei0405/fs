// HTMLParser Library $Name:  $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2004 Joshua Kerievsky
//
// Revision Control Information
//
// $Source: /liying/bmt_base/src/htmlps/com/bmtech/htmls/parser/visitors/ObjectFindingVisitor.java,v $
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
import com.bmtech.htmls.parser.Tag;
import com.bmtech.htmls.parser.util.NodeList;

public class ObjectFindingVisitor extends NodeVisitor {
    private Class<?> classTypeToFind;
    private NodeList tags;

    public ObjectFindingVisitor(Class<?> classTypeToFind) {
        this(classTypeToFind,true);
    }

    public ObjectFindingVisitor(Class<?> classTypeToFind,boolean recurse) {
        super(recurse, true);
        this.classTypeToFind = classTypeToFind;
        this.tags = new NodeList();
    }

    public int getCount() {
        return (tags.size ());
    }

    public void visitTag(Tag tag) {
        if (tag.getClass().equals(classTypeToFind))
            tags.add(tag);
    }

    public Node[] getTags() {
        return tags.toNodeArray();
    }
}
/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.contacts;

import java.util.*;

import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

class FoldersTreeModel implements TreeModel
{
    private ContactsStoring storing;
    private Strings strings;
    private StoredContactsFolder cachedFolder;
    private StoredContactsFolder[] cache;

    public FoldersTreeModel(ContactsStoring storing, Strings strings)
    {
	this.storing = storing;
	this.strings = strings;
	if (storing == null)
	    throw new NullPointerException("emailStoring may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public Object getRoot()
    {
	try {
	    return storing.getRootFolder(); 
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    @Override public boolean isLeaf(Object node)
    {
	if (node == null || !(node instanceof ContactsFolder))
	    return true;
	final StoredContactsFolder folder = (StoredContactsFolder)node;
	try {
	    StoredContactsFolder[] folders = storing.getFolders(folder);
	    return folders == null || folders.length < 1;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return true;
	}
    }

    @Override public void beginChildEnumeration(Object node)
    {
	if (node == null || !(node instanceof ContactsFolder))
	    return;
	final StoredContactsFolder folder = (StoredContactsFolder)node;
	try {
	    cachedFolder = folder;
	    cache = storing.getFolders(folder);
	    if (cache == null)
		cache = new StoredContactsFolder[0];
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    cachedFolder = null;
	    cache = null;
	}
    }

    public int getChildCount(Object parent)
    {
	if (parent == null || !(parent instanceof ContactsFolder))
	    return 0;
	final ContactsFolder folder = (ContactsFolder)parent;
	if (cachedFolder == null || !cachedFolder.equals(folder))
	    return 0;
	return cache.length;
    }

    @Override public Object getChild(Object parent, int index)
    {
	if (parent == null || !(parent instanceof ContactsFolder))
	    return null;
	final ContactsFolder folder = (ContactsFolder)parent;
	if (cachedFolder == null || !cachedFolder.equals(folder))
	    return null;
	return index < cache.length?cache[index]:null;
    }

    public void endChildEnumeration(Object node)
    {
	cachedFolder = null;
	cache = null;
    }
}

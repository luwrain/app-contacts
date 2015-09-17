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

import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

class FoldersCachedTreeModelSource implements CachedTreeModelSource
{
    private ContactsStoring storing;
    private Strings strings;

    public FoldersCachedTreeModelSource(ContactsStoring storing, Strings strings)
    {
	this.storing = storing;
	this.strings = strings;
	if (storing == null)
	    throw new NullPointerException("storing may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public Object getRoot()
    {
	try {
	    final StoredContactsFolder root = storing.getFoldersRoot();
	    if (root == null)
		return null;
	    return new FolderWrapper(root, strings.folderTitle(root.getTitle()));
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    @Override public Object[] getChildObjs(Object obj)
    {
	if (obj == null || !(obj instanceof FolderWrapper))
	    return new Object[0];
	final FolderWrapper wrapper = (FolderWrapper)obj;
	try {
	    final StoredContactsFolder[] folders = storing.getFolders(wrapper.folder());
	    if (folders == null)
		return new Object[0];
	    final FolderWrapper[] wrappers= new FolderWrapper[folders.length];
	    for(int i = 0;i < folders.length;++i)
		wrappers[i] = new FolderWrapper(folders[i], strings.folderTitle(folders[i].getTitle()));
	    return wrappers;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return new Object[0];
	}
    }
}

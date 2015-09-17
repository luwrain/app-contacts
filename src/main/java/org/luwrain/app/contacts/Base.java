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

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.pim.contacts.*;

class Base
{
    static private final String SHARED_OBJECT_NAME = "luwrain.pim.contacts";


    private Luwrain luwrain;
    private Strings strings;
    private ContactsStoring storing;
    private FoldersCachedTreeModelSource foldersCachedSource;
    private TreeModel foldersModel;

    public boolean init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	final Object obj = luwrain.getSharedObject(SHARED_OBJECT_NAME);
	if (obj == null || !(obj instanceof org.luwrain.pim.contacts.Factory))
	    return false;
	final org.luwrain.pim.contacts.Factory factory = (org.luwrain.pim.contacts.Factory)obj;
	final Object obj2 = factory.createContactsStoring();
	if (obj2 == null || !(obj2 instanceof ContactsStoring))
	    return false;
	storing = (ContactsStoring)obj2;
	return true;
    }

    public TreeModel getFoldersModel()
    {
	if (foldersModel != null)
	    return foldersModel;
	foldersCachedSource = new FoldersCachedTreeModelSource(storing, strings);
	foldersModel = new CachedTreeModel(foldersCachedSource);
	return foldersModel;
    }

    public boolean openFolder(StoredContactsFolder folder)
    {
	return false;
    }

    public boolean insertIntoTree()
    {
	final String folderTitle = strings.insertIntoTreePopupValueFolder();
	final String contactTitle = strings.insertIntoTreePopupValueContact();
	final Object res = Popups.fixedList(luwrain, strings.insertIntoTreePopupName(), new String[]{folderTitle, contactTitle}, 0);
	return true;
    }
}

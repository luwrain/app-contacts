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
import org.luwrain.pim.contacts.*;

class Base
{
    private Luwrain luwrain;
    private Strings strings;
    private ContactsStoring storing;
    private FoldersTreeModel foldersModel;

    public void init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	//	mailStoring = luwrain.getPimManager().getMailStoring();
    }

    public FoldersTreeModel getFoldersModel()
    {
	if (foldersModel != null)
	    return foldersModel;
	foldersModel = new FoldersTreeModel(storing, strings);
	return foldersModel;
    }

    public boolean openFolder(StoredContactsFolder folder)
    {
	return false;
    }
}

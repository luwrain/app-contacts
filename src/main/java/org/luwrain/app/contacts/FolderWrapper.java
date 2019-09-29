/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.pim.contacts.*;

//Needed for localized titles;
class FolderWrapper
{
    private ContactsFolder folder;
    private String title;

    FolderWrapper(ContactsFolder folder, String title)
    {
	this.folder = folder;
	this.title = title;
	NullCheck.notNull(folder, "folder");
	NullCheck.notNull(title, "title");
    }

    ContactsFolder folder()
    {
	return folder;
    }

    public String title()
    {
	return title;
    }

    @Override public String toString()
    {
	return title;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof FolderWrapper))
	    return false;
	final FolderWrapper wrapper = (FolderWrapper)o;
	return folder.equals(wrapper.folder);
    }
}

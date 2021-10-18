/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

final class FoldersAppearance implements ListArea.Appearance<Object>
{
    private final App app;

    FoldersAppearance(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
		NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");

	if (item instanceof ContactsFolder)
	{
	    final ContactsFolder f = (ContactsFolder)item;
	    app.getLuwrain().setEventResponse(DefaultEventResponse.listItem(Sounds.DOC_SECTION, f.getTitle(), Suggestions.CLICKABLE_LIST_ITEM));
	    return;
	}

		if (item instanceof Contact)
	{
	    final Contact c = (Contact)item;
	    app.getLuwrain().setEventResponse(DefaultEventResponse.listItem(c.getTitle(), Suggestions.CLICKABLE_LIST_ITEM));
	    return;
	}

		
    }

    @Override public String getScreenAppearance(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	return item.toString();
    }

    @Override public int getObservableLeftBound(Object item)
    {
		NullCheck.notNull(item, "item");
		return (item instanceof ContactsFolder)?2:0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	NullCheck.notNull(item, "item");
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length() + ((item instanceof ContactsFolder)?2:0);
    }
}

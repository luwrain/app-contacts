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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.app.base.*;

final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;
    private ContactsStoring storing = null;
    private ContactsFolder foldersRoot = null;

    App()
    {
	super(Strings.NAME, Strings.class, "luwrain.contacts");
    }

    @Override protected boolean onAppInit() throws Exception
    {
	this.storing = org.luwrain.pim.Connections.getContactsStoring(getLuwrain(), true);
	this.foldersRoot = storing.getFolders().getRoot();
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return true;
    }

    void ensureEverythingSaved()
    {
	/*
	if (!base.hasCurrentContact())
	    return;
	base.saveForm(valuesArea);
	base.saveNotes(notesArea);
	*/
    }

    @Override protected AreaLayout getDefaultAreaLayout()
    {
	return mainLayout.getAreaLayout();
    }

    /*
    @Override public void closeApp()
    {
	ensureEverythingSaved();
	luwrain.closeApp();
    }
    */

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    ContactsStoring getStoring()
    {
	return this.storing;
    }

    ContactsFolder getFoldersRoot()
    {
	return this.foldersRoot;
    }
    
}

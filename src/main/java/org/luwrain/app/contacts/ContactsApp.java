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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

public class ContactsApp implements Application, Actions
{
    private static final String STRINGS_NAME = "luwrain.contacts";

    private Luwrain luwrain;
    private Base base = new Base();
    private Strings strings;

    private TreeArea foldersArea;
    private FormArea valuesArea;
    private EditArea notesArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	base.init(luwrain, strings);
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public void openContact(StoredContact contact)
    {
    }

    private void createAreas()
    {
	final Actions a = this;
	final Strings s = strings;
	foldersArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				   base.getFoldersModel(),
				   strings.foldersAreaName()){
		private Strings strings = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoValues();
			    return true;
			default:
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    if (obj != null && obj instanceof StoredContact)
			actions.openContact((StoredContact)obj);
		}
	    };

	//FIXME:
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
    }

    @Override public void gotoFolders()
    {
	luwrain.setActiveArea(foldersArea);
    }

    @Override public void gotoValues()
    {
	luwrain.setActiveArea(valuesArea);
    }

    public void gotoNotes()
    {
	luwrain.setActiveArea(valuesArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}

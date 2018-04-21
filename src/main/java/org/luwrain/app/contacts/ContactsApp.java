/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class ContactsApp implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private TreeArea foldersArea = null;
    private FormArea valuesArea = null;
    private EditArea notesArea = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	if (!base.hasStoring())
	    return new InitResult(InitResult.Type.FAILURE);
	this.actions = new Actions(luwrain, strings, base);
	createAreas();
	return new InitResult();
    }

    private void createAreas()
    {
	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.environment = new DefaultControlEnvironment(luwrain);
	treeParams.model = base.getFoldersModel();
	treeParams.name = 				   strings.foldersAreaName();

	foldersArea = new TreeArea(treeParams) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    luwrain.setActiveArea(valuesArea);
			    return true;
			case INSERT:
			    return actions.insertIntoTree(foldersArea);
			case DELETE:
			    return actions.deleteFromTree(foldersArea, valuesArea, notesArea);
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
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    NullCheck.notNull(obj, "obj");
		    actions.openContact(ContactsApp.this, obj, valuesArea, notesArea);
		}
	    };

	valuesArea = new FormArea(new DefaultControlEnvironment(luwrain), strings.valuesAreaName()){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    gotoNotes();
			    return true;
			case INSERT:
			    return actions.insertValue(valuesArea);
			case DELETE:
			    return actions.deleteValue();
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
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };

	final EditArea.Params editParams = new EditArea.Params();
	editParams.context = new DefaultControlEnvironment(luwrain);
	editParams.name = strings.notesAreaName();

	notesArea = new EditArea(editParams){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    luwrain.setActiveArea(foldersArea);
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
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
    }

    void gotoNotes()
    {
	luwrain.setActiveArea(notesArea);
    }

    @Override public void closeApp()
    {
	ensureEverythingSaved();
	luwrain.closeApp();
    }

    void ensureEverythingSaved()
    {
	if (!base.hasCurrentContact())
	    return;
	base.saveForm(valuesArea);
	base.saveNotes(notesArea);
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }
}

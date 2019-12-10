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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

final class App implements Application, MonoApp
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;

    private ListArea foldersArea = null;
    private FormArea valuesArea = null;
    private EditArea notesArea = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	if (!base.hasStoring())
	    return new InitResult(InitResult.Type.FAILURE);
	this.actions = new Actions(base);
	createAreas();
	return new InitResult();
    }

    private void createAreas()
    {
	this.foldersArea = new ListArea(base.createFoldersListParams((area, index, obj)->{
		    NullCheck.notNull(area, "area");
		    NullCheck.notNull(obj, "obj");
		    actions.openContact(App.this, obj, valuesArea, notesArea);
		    return true;
		})) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    return AreaLayoutHelper.activateNextArea(luwrain, getAreaLayout(), this);
			case INSERT:
			    return actions.insertIntoTree(foldersArea);
			case DELETE:
			    return actions.deleteFromTree(foldersArea, valuesArea, notesArea);
			default:
			    return super.onInputEvent(event);
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };

	this.valuesArea = new FormArea(new DefaultControlContext(luwrain), strings.valuesAreaName()){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    return AreaLayoutHelper.activateNextArea(luwrain, getAreaLayout(), this);
			case BACKSPACE:
			    return AreaLayoutHelper.activatePrevArea(luwrain, getAreaLayout(), this);
			case INSERT:
			    return actions.insertValue(valuesArea);
			case DELETE:
			    return actions.deleteValue();
			default:
			    return super.onInputEvent(event);
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };

	final EditArea.Params editParams = new EditArea.Params();
	editParams.context = new DefaultControlContext(luwrain);
	editParams.name = strings.notesAreaName();
	this.notesArea = new EditArea(editParams){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    return AreaLayoutHelper.activateNextArea(luwrain, getAreaLayout(), this);
			default:
			    return super.onInputEvent(event);
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    void ensureEverythingSaved()
    {
	if (!base.hasCurrentContact())
	    return;
	base.saveForm(valuesArea);
	base.saveNotes(notesArea);
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
    }

    @Override public void closeApp()
    {
	ensureEverythingSaved();
	luwrain.closeApp();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}

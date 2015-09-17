/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{new Command(){
		@Override public String getName()
		{
		    return "contacts";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("contacts");
		}
	    }};
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[]{new Shortcut() {
		@Override public String getName()
		{
		    return "contacts";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    return new Application[]{new ContactsApp()};
		}
	    }};
    }
}
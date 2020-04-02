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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.pim.contacts.*;

final class Base
{
    final Luwrain luwrain;
final Strings strings;
    final ContactsStoring storing;

    private final ContactsFolder foldersRoot;
    private ContactsFolder[] folders = new ContactsFolder[0];
    private Contact currentContact = null;

Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	this.storing = org.luwrain.pim.Connections.getContactsStoring(luwrain , true);
	ContactsFolder root = null;
		try {
root = storing.getFolders().getRoot();
	}
	catch (Exception e)
	{
	    luwrain.crash(e);
    }
		this.foldersRoot = root;
    }

    boolean hasStoring()
    {
	return storing != null;
    }

    boolean hasCurrentContact()
    {
	return currentContact != null;
    }

    boolean openFolder(ContactsFolder folder)
    {
	return false;
    }

    boolean insertIntoTree(ContactsFolder insertInto)
    {
	NullCheck.notNull(insertInto, "insertInto");
	final String folderTitle = strings.insertIntoTreePopupValueFolder();
	final String contactTitle = strings.insertIntoTreePopupValueContact();
	final Object res = Popups.fixedList(luwrain, strings.insertIntoTreePopupName(), new String[]{folderTitle, contactTitle});
	if (res == folderTitle)
	    return insertFolder(insertInto);
	    if (res == contactTitle)
		return insertContact(insertInto);
return false;
    }

    private boolean insertFolder(ContactsFolder insertInto)
    {
	final String name = Popups.simple(luwrain, "Имя новой группы контактов", "Введите имя новой группы:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    luwrain.message("Новая группа контактов не может быть создана с пустым именем", Luwrain.MessageType.ERROR);
	    return false;
	}
	try {
	    final ContactsFolder f = new ContactsFolder();
	    f.setTitle(name);
	    f.setOrderIndex(0);
	    storing.getFolders().save(insertInto, f);
	    return true;
	}
	catch(Exception e)
	{
	    luwrain.crash(e);
	    return false;
	}
    }

    private boolean insertContact(ContactsFolder insertInto)
    {
	final String name = Popups.simple(luwrain, "Имя нового контакта", "Введите имя нового контакта:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    luwrain.message("Новый контакт не может быть создан с пустым именем", Luwrain.MessageType.ERROR);
	    return false;
	}
	try {
	    final Contact c = new Contact();
	    c.setTitle(name);
	    storing.getContacts().save(insertInto, c);
	    return true;
	}
	catch(Exception e)
	{
	    luwrain.crash(e);
	    return false;
	}
    }

    void setCurrentContact(Contact contact)
    {
	NullCheck.notNull(contact, "contact");
	currentContact = contact;
    }
    }

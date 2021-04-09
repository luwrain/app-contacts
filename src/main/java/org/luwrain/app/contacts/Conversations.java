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
import org.luwrain.pim.contacts.*;
import org.luwrain.popups.*;

final class Conversations
{
    private final App app;
    private final Luwrain luwrain;
    private final Strings strings;

    Conversations(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.luwrain = app.getLuwrain();
	this.strings = app.getStrings();
    }

    String newContactName()
    {
	return Popups.textNotEmpty(luwrain, "Новый контакт", "Имя нового контакта:", "");
    }

    String newFolderName()
    {
	return Popups.textNotEmpty(luwrain, "Новая группа", "Имя новой группы контактов:", "");
    }

    ContactValue.Type newContactValueType()
    {
	final String mailTitle = "Электронная почта";
	final String phoneTitle = "Телефон";
	final String addressTitle = "Адрес";
	final String birthdayTitle = "Дата рождения";
	final String skypeTitle = "Skype";
	final Object res = Popups.fixedList(luwrain, "Тип нового значения:", new String[]{mailTitle, phoneTitle, addressTitle, birthdayTitle, skypeTitle});
	if (res == null)
	    return null;
	final ContactValue.Type type;
	if (res == mailTitle)
	    return ContactValue.Type.MAIL;
	if (res == addressTitle)
	    return ContactValue.Type.ADDRESS;
	if (res == birthdayTitle)
	    return ContactValue.Type.BIRTHDAY;
	if (res == skypeTitle)
	    return ContactValue.Type.SKYPE;
	return null;//Should never happen
    }
}

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

    void fillValuesArea(FormArea area)
    {
	NullCheck.notNull(area, "area");
	area.clear();
	if (currentContact == null)
	    return;
	try {
	    area.addEdit("name", "Имя:", currentContact.getTitle(), null, true);
	    int counter = 1;
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.MAIL)
		    area.addEdit("mail" + (counter++), "Электронная почта:", v.value, v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.MOBILE_PHONE)
		    area.addEdit("mobile" + (counter++), "Мобильный телефон:", v.value, v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.GROUND_PHONE)
		    area.addEdit("ground" + (counter++), "Телефон:", v.value, v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.ADDRESS)
		    area.addEdit("address" + (counter++), "Адрес:", v.value, v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.BIRTHDAY)
		    area.addEdit("birthday" + (counter++), "Дата рождения:", v.value, v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.type == ContactValue.SKYPE)
		    area.addEdit("skype" + (counter++), "Skype:", v.value, v, true);
	}
	catch (Exception e)
	{
	    e.printStackTrace(); 
	    luwrain.message("Во время получения данных контакта произошла непредвиденная ошибка", Luwrain.MessageType.ERROR);
	}
    }

    //Returns true on success saving, shows all corresponding error message;
    boolean saveForm(FormArea area)
    {
	if (currentContact == null)
	    return false;
	final LinkedList<ContactValue> values = new LinkedList<ContactValue>();
	for(int i = 0;i < area.getItemCount();++i)
	{
	    final Object obj = area.getItemObjOnLine(i);
	    if (obj == null || !(obj instanceof ContactValue))
		continue;
	    final ContactValue value = (ContactValue)obj;
	    value.value = area.getEnteredText(i);
	    if (!value.value.trim().isEmpty())
		values.add(value);
	}
	try {
	    currentContact.setValues(values.toArray(new ContactValue[values.size()]));
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время сохранения введённых изменений произошла непредвиденная ошибка", Luwrain.MessageType.ERROR);
	    return false;
	}
	return true;
    }

    //Returns true if new value is really added, shows all corresponding error messages;
    boolean insertValue()
    {
	if (currentContact == null)
	    return false;
	final String mailTitle = "Электронная почта";
	final String mobileTitle = "Мобильный телефон";
	final String phoneTitle = "Телефон";
	final String addressTitle = "Адрес";
	final String birthdayTitle = "Дата рождения";
	final String skypeTitle = "Skype";
	final Object res = Popups.fixedList(luwrain, "Выберите тип нового значения:", new String[]{
		mailTitle,
		mobileTitle,
		phoneTitle,
		addressTitle,
		birthdayTitle,
		skypeTitle,
});
	if (res == null)
	    return false;
	int type;
	if (res == mailTitle)
	    type = ContactValue.MAIL; else
	    if (res == mobileTitle)
		type = ContactValue.MOBILE_PHONE; else
		if (res == phoneTitle)
		    type = ContactValue.GROUND_PHONE; else
		    if (res == addressTitle)
			type = ContactValue.ADDRESS; else
			if (res == birthdayTitle)
			    type = ContactValue.BIRTHDAY; else
			    if (res == skypeTitle)
				type = ContactValue.SKYPE; else
				return false;//Should never happen
	try {
	    final ContactValue[] oldValues = currentContact.getValues();
	    final ContactValue[] newValues = new ContactValue[oldValues.length + 1];
	    for(int i = 0;i < oldValues.length;++i)
		newValues[i] = oldValues[i];
	    newValues[newValues.length - 1] = new ContactValue(type, "", false);
	    currentContact.setValues(newValues);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время добавления нового значения произошла непредвиденная ошибка", Luwrain.MessageType.ERROR);
	    return false;
	}
	return true;
    }

    boolean fillNotesArea(EditAreaOld area)
    {
	String value;
	try {
	    value = currentContact.getNotes();
	    }
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время получения комментария произошла непредвиденная ошибка:" + e.getMessage());
	    return false;
	}
	area.setLines(value.split("\n", -1));
	return true;
    }

    boolean saveNotes(EditAreaOld area)
    {
	if (currentContact == null)
	    return true;
	final StringBuilder b = new StringBuilder();
	final int count = area.getLineCount();
	if (count > 0)
	{
	    b.append(area.getLine(0));
	    for(int i = 1;i < count;++i)
		b.append("\n" + area.getLine(i));
	}
	try {
	    currentContact.setNotes(b.toString());
	    return true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время сохранения комментария произошла непредвиденная ошибка:" + e.getMessage(), Luwrain.MessageType.ERROR);
	    return false;
	}
    }

    boolean deleteFolder(ContactsFolder folder)
    {
	try {
	    if (folder.isRoot())
	    {
		luwrain.message("Корневая группа контактов не может быть удалена", Luwrain.MessageType.ERROR);
		return false;
	    }
	    final Contact[] contacts = storing.getContacts().load(folder);
	    final ContactsFolder[] subfolders = storing.getFolders().load(folder);
	    if (contacts != null && contacts.length > 0)
	    {
		luwrain.message("Выделенная группа содержит контакты и не может быть удалена", Luwrain.MessageType.ERROR);
		return false;
	    }
	    if (subfolders != null && subfolders.length > 0)
	    {
		luwrain.message("Выделенная группа содержит вложенные группы и не может быть удалена", Luwrain.MessageType.ERROR);
		return false;
	    }
	    final YesNoPopup popup = new YesNoPopup(luwrain, "Удаление группы контактов", "Вы действительно хотите удалить группу контактов \"" + folder.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
	    luwrain.popup(popup);
	    if (popup.wasCancelled() || !popup.result())
		return false;
	    storing.getFolders().delete(folder);
	    return true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время попытки удаления группы контактов произошла непредвиденная ошибка:" + e.getMessage(), Luwrain.MessageType.ERROR);
	    return false;
	}
    }

    boolean deleteContact(Contact contact)
    {
	try {
	    final YesNoPopup popup = new YesNoPopup(luwrain, "Удаление группы контактов", "Вы действительно хотите удалить контакт \"" + contact.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
	    luwrain.popup(popup);
	    if (popup.wasCancelled() || !popup.result())
		return false;
	    storing.getContacts().delete(contact);
	    currentContact = null;//FIXME:maybe only if currentContact == contact
	    return true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время попытки удаления контакта произошла непредвиденная ошибка:" + e.getMessage(), Luwrain.MessageType.ERROR);
	    return false;
	}
    }

    ListArea.Params createFoldersListParams(ListArea.ClickHandler clickHandler)
    {
	NullCheck.notNull(clickHandler, "clickHandler");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new FoldersListModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = strings.foldersAreaName();
	return params;
    }

    final class FoldersListModel implements ListArea.Model
{
    @Override public int getItemCount()
    {
	return folders.length;
    }
    @Override public Object getItem(int index)
    {
	return folders[index];
    }
    @Override public void refresh()
    {
    }
    }
    }

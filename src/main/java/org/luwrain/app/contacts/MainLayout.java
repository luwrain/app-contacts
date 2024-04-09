/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.edit.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.app.base.*;
import org.luwrain.popups.*;

final class MainLayout extends LayoutBase implements ListArea.ClickHandler
{
    private final App app;
    private final ContactsFolders folders;
    private final Contacts contacts;

    final ListArea foldersArea;
    final FormArea valuesArea;
    final EditArea notesArea;

    private ContactsFolder openedFolder = null;
    private Contact openedContact = null;
    private final List items = new ArrayList();

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.openedFolder = app.getStoring().getFolders().getRoot();
	this.folders = app.getStoring().getFolders();
	this.contacts = app.getStoring().getContacts();
	updateItems();

	this.foldersArea = new ListArea(listParams((params)->{
		    params.model = new ListUtils.ListModel(items);
		    params.appearance = new FoldersAppearance(app);
		    params.name = app.getStrings().foldersAreaName();
		    params.clickHandler = this;
		}));
	final Actions foldersActions = actions(
					       action("new-folder", "Новая группа", new InputEvent(InputEvent.Special.INSERT, EnumSet.of(InputEvent.Modifiers.SHIFT)), this::actNewFolder),
					       action("new-contact", "Новый контакт", new InputEvent(InputEvent.Special.INSERT), this::actNewContact)
					       );

	this.valuesArea = new FormArea(getControlContext(), app.getStrings().valuesAreaName());
	final Actions valuesActions = actions(
					      action("new-value", "Добавить новое значение", new InputEvent(InputEvent.Special.INSERT), this::actNewValue)
					      );

	this.notesArea = new EditArea(editParams((params)->{
		    params.name = app.getStrings().notesAreaName();
		    params.appearance = new EditUtils.DefaultEditAreaAppearance(getControlContext());
		}));
	final Actions notesActions = actions();

	setAreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, foldersActions, valuesArea, valuesActions, notesArea, notesActions);
    }

    @Override public boolean onListClick(ListArea area, int index, Object obj)
    {
	if (obj == null || !(obj instanceof Contact))
	    return false;
	ensureEverythingSaved();
	this.openedContact = (Contact)obj;
	fillValuesArea(valuesArea);
	fillNotesArea(notesArea);
	setActiveArea(valuesArea);
	return true;
    }

    private boolean actNewFolder()
    {
	final String name = app.getConv().newFolderName();
	if (name == null || name.trim().isEmpty())
	    return true;
	final ContactsFolder f = new ContactsFolder();
	f.setTitle(name.trim());
	folders.save(openedFolder, f);
	updateItems();
	return true;
    }

    private boolean actNewContact()
    {
	final String name = app.getConv().newContactName();
	if (name == null || name.trim().isEmpty())
	    return true;
	final Contact c = new Contact();
	c.setTitle(name);
	contacts.save(openedFolder, c);
	updateItems();
	return true;
    }

    private boolean actNewValue()
    {
	if (openedContact == null)
	    return false;
	final ContactValue.Type type = app.getConv().newContactValueType();
	if (type == null)
	    return true;
	final ContactValue newValue = new ContactValue();
	newValue.setType(type);
	final ContactValue[] oldValues = openedContact.getValues();
	final ContactValue[] newValues = Arrays.copyOf(oldValues, oldValues.length + 1);
	newValues[newValues.length - 1] = newValue;
	openedContact.setValues(newValues);
	return true;
    }

    private void updateItems()
    {
	items.clear();
	items.addAll(Arrays.asList(folders.load(openedFolder)));
	items.addAll(Arrays.asList(contacts.load(openedFolder)));
    }

    private void fillValuesArea(FormArea area)
    {
	NullCheck.notNull(area, "area");
	area.clear();
	if (openedContact == null)
	    return;
	area.addEdit("name", "Имя:", openedContact.getTitle(), null, true);
	int counter = 1;
	for(ContactValue v: openedContact.getValues())
	    if (v.getType() == ContactValue.Type.MAIL)
		area.addEdit("mail" + (counter++), "Электронная почта:", v.getValue(), v, true);
	for(ContactValue v: openedContact.getValues())
	    if (v.getType() == ContactValue.Type.PHONE)
		area.addEdit("mobile" + (counter++), "Мобильный телефон:", v.getValue(), v, true);
	for(ContactValue v: openedContact.getValues())
	    if (v.getType() == ContactValue.Type.ADDRESS)
		area.addEdit("address" + (counter++), "Адрес:", v.getValue(), v, true);
	for(ContactValue v: openedContact.getValues())
	    if (v.getType() == ContactValue.Type.BIRTHDAY)
		area.addEdit("birthday" + (counter++), "Дата рождения:", v.getValue(), v, true);
	for(ContactValue v: openedContact.getValues())
	    if (v.getType() == ContactValue.Type.SKYPE)
		area.addEdit("skype" + (counter++), "Skype:", v.getValue(), v, true);
    }

    private void saveForm(FormArea area)
    {
	if (openedContact == null)
	    return;
	final List<ContactValue> values = new ArrayList<ContactValue>();
	for(int i = 0;i < area.getItemCount();++i)
	{
	    final Object obj = area.getItemObj(i);
	    if (obj == null || !(obj instanceof ContactValue))
		continue;
	    final ContactValue value = (ContactValue)obj;
	    value.setValue(area.getEnteredText(i));
	    if (!value.getValue().trim().isEmpty())
		values.add(value);
	}
	openedContact.setValues(values.toArray(new ContactValue[values.size()]));
	return;
    }

    private boolean fillNotesArea(EditArea area)
    {
	String value;
	value = openedContact.getNotes();
	area.setText(value.split("\n", -1));
	return true;
    }

    private boolean saveNotes(EditArea area)
    {
	if (openedContact == null)
	    return true;
	final StringBuilder b = new StringBuilder();
	final int count = area.getLineCount();
	if (count > 0)
	{
	    b.append(area.getLine(0));
	    for(int i = 1;i < count;++i)
		b.append("\n" + area.getLine(i));
	}
	openedContact.setNotes(b.toString());
	return true;
    }

    private boolean deleteFolder(ContactsFolder folder)
    {
	if (folder.isRoot())
	{
	    app.getLuwrain().message("Корневая группа контактов не может быть удалена", Luwrain.MessageType.ERROR);
	    return false;
	}
	final Contact[] contacts = app.getStoring().getContacts().load(folder);
	final ContactsFolder[] subfolders = app.getStoring().getFolders().load(folder);
	if (contacts != null && contacts.length > 0)
	{
	    app.getLuwrain().message("Выделенная группа содержит контакты и не может быть удалена", Luwrain.MessageType.ERROR);
	    return false;
	}
	if (subfolders != null && subfolders.length > 0)
	{
	    app.getLuwrain().message("Выделенная группа содержит вложенные группы и не может быть удалена", Luwrain.MessageType.ERROR);
	    return false;
	}
	final YesNoPopup popup = new YesNoPopup(app.getLuwrain(), "Удаление группы контактов", "Вы действительно хотите удалить группу контактов \"" + folder.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
	app.getLuwrain().popup(popup);
	if (popup.wasCancelled() || !popup.result())
	    return false;
	app.getStoring().getFolders().delete(folder);
	return true;
    }

    private boolean deleteContact(Contact contact)
    {
	final YesNoPopup popup = new YesNoPopup(app.getLuwrain(), "Удаление группы контактов", "Вы действительно хотите удалить контакт \"" + contact.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
	app.getLuwrain().popup(popup);
	if (popup.wasCancelled() || !popup.result())
	    return false;
	app.getStoring().getContacts().delete(contact);
	openedContact = null;//FIXME:maybe only if currentContact == contact
	return true;
    }

    /*
    //Returns false if the area must issue an error beep
    boolean insertValue(FormArea valuesArea)
    {
	NullCheck.notNull(valuesArea, "valuesArea");
	if (!base.hasCurrentContact())
	    return false;
	if (!base.saveForm(valuesArea))
	    return true;
	if (!base.insertValue())
	    return true;
	base.fillValuesArea(valuesArea);
	return true;
    }
    */

    void ensureEverythingSaved()
    {
	if (openedContact == null)
	    return;
	saveForm(valuesArea);
	saveNotes(notesArea);
    }
}

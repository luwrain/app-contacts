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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.app.base.*;
import org.luwrain.popups.*;

final class MainLayout extends LayoutBase implements ListArea.ClickHandler
{
    private final App app;
    private final ContactsFolders folders;
    private final Contacts contacts;

    private final ListArea foldersArea;
    private final FormArea valuesArea;
    private final EditArea notesArea;

    private ContactsFolder openedFolder = null;
    private final List items = new ArrayList();
    private Contact currentContact = null;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.openedFolder = app.getStoring().getFolders().getRoot();
	this.folders = app.getStoring().getFolders();
	this.contacts = app.getStoring().getContacts();
	updateItems();

	final Actions foldersActions;
	{
	    final ListArea.Params params = new ListArea.Params();
	    params.context = getControlContext();
	    params.model = new ListUtils.ListModel(items);
	    params.appearance = new FoldersAppearance(app);
	    params.name = app.getStrings().foldersAreaName();
	    params.clickHandler = this;
	    this.foldersArea = new ListArea(params);
	    foldersActions = actions(
				     action("new-folder", "Новая группа", new InputEvent(InputEvent.Special.INSERT, EnumSet.of(InputEvent.Modifiers.SHIFT)), this::actNewFolder),
				     action("new-contact", "Новый контакт", new InputEvent(InputEvent.Special.INSERT), this::actNewContact)
				     );
	}

	final Actions valuesActions;
	{
	    this.valuesArea = new FormArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().valuesAreaName());
	    valuesActions = actions();
	}

	final Actions notesActions;
	{
	    final EditArea.Params params = new EditArea.Params();
	    params.context = getControlContext();
	    params.name = app.getStrings().notesAreaName();
	    params.appearance = new EditUtils.DefaultEditAreaAppearance(getControlContext());
	    this.notesArea = new EditArea(params);
	    notesActions = actions();
	}

	setAreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, foldersActions, valuesArea, valuesActions, notesArea, notesActions);
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

    private void updateItems()
    {
	items.clear();
	items.addAll(Arrays.asList(folders.load(openedFolder)));
	items.addAll(Arrays.asList(contacts.load(openedFolder)));
    }


    void fillValuesArea(FormArea area)
    {
	NullCheck.notNull(area, "area");
	area.clear();
	if (currentContact == null)
	    return;
	area.addEdit("name", "Имя:", currentContact.getTitle(), null, true);
	int counter = 1;
	for(ContactValue v: currentContact.getValues())
	    if (v.getType() == ContactValue.Type.MAIL)
		area.addEdit("mail" + (counter++), "Электронная почта:", v.getValue(), v, true);
	for(ContactValue v: currentContact.getValues())
	    if (v.getType() == ContactValue.Type.PHONE)
		area.addEdit("mobile" + (counter++), "Мобильный телефон:", v.getValue(), v, true);
	for(ContactValue v: currentContact.getValues())
	    if (v.getType() == ContactValue.Type.ADDRESS)
		area.addEdit("address" + (counter++), "Адрес:", v.getValue(), v, true);
	for(ContactValue v: currentContact.getValues())
	    if (v.getType() == ContactValue.Type.BIRTHDAY)
		area.addEdit("birthday" + (counter++), "Дата рождения:", v.getValue(), v, true);
	for(ContactValue v: currentContact.getValues())
	    if (v.getType() == ContactValue.Type.SKYPE)
		area.addEdit("skype" + (counter++), "Skype:", v.getValue(), v, true);
    }

    //Returns true on success saving, shows all corresponding error message;
    boolean saveForm(FormArea area)
    {
	if (currentContact == null)
	    return false;
	final LinkedList<ContactValue> values = new LinkedList<ContactValue>();
	for(int i = 0;i < area.getItemCount();++i)
	{
	    final Object obj = area.getItemObj(i);
	    if (obj == null || !(obj instanceof ContactValue))
		continue;
	    final ContactValue value = (ContactValue)obj;
	    //FIXME:	    value.setValue(area.getEnteredText(i));
	    if (!value.getValue().trim().isEmpty())
		values.add(value);
	}
	currentContact.setValues(values.toArray(new ContactValue[values.size()]));
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
	final Object res = Popups.fixedList(app.getLuwrain(), "Выберите тип нового значения:", new String[]{
		mailTitle,
		mobileTitle,
		phoneTitle,
		addressTitle,
		birthdayTitle,
		skypeTitle,
	    });
	if (res == null)
	    return false;
	final ContactValue.Type type;
	if (res == mailTitle)
	    type = ContactValue.Type.MAIL; else
	    if (res == mobileTitle)
		type = ContactValue.Type.PHONE; else
		    if (res == addressTitle)
			type = ContactValue.Type.ADDRESS; else
			if (res == birthdayTitle)
			    type = ContactValue.Type.BIRTHDAY; else
			    if (res == skypeTitle)
				type = ContactValue.Type.SKYPE; else
				return false;//Should never happen
	final ContactValue[] oldValues = currentContact.getValues();
	final ContactValue[] newValues = new ContactValue[oldValues.length + 1];
	for(int i = 0;i < oldValues.length;++i)
	    newValues[i] = oldValues[i];
	newValues[newValues.length - 1] = new ContactValue(type, "", false);
	currentContact.setValues(newValues);
    return true;
}

boolean fillNotesArea(EditArea area)
{
    String value;
    value = currentContact.getNotes();
area.setLines(value.split("\n", -1));
return true;
}

boolean saveNotes(EditArea area)
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
    currentContact.setNotes(b.toString());
    return true;
}

boolean deleteFolder(ContactsFolder folder)
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

boolean deleteContact(Contact contact)
{
    final YesNoPopup popup = new YesNoPopup(app.getLuwrain(), "Удаление группы контактов", "Вы действительно хотите удалить контакт \"" + contact.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
    app.getLuwrain().popup(popup);
    if (popup.wasCancelled() || !popup.result())
	return false;
    app.getStoring().getContacts().delete(contact);
    currentContact = null;//FIXME:maybe only if currentContact == contact
    return true;
}

    /*
    boolean insertIntoTree(ListArea foldersArea)
    {
	NullCheck.notNull(foldersArea, "foldersArea");
	final Object selected = foldersArea.selected();
	if (selected == null || !(selected instanceof FolderWrapper))
	    return false;
	final FolderWrapper wrapper = (FolderWrapper)selected;
	if (!base.insertIntoTree(wrapper.folder()))
	    return true;
	foldersArea.refresh();
	return true;
    }

    boolean deleteFromTree(ListArea foldersArea, FormArea valuesArea, EditArea notesArea)
    {
	NullCheck.notNull(foldersArea, "foldersArea");
	NullCheck.notNull(valuesArea, "valuesArea");
	NullCheck.notNull(notesArea, "notesArea");
	final Object selected = foldersArea.selected();
	if (selected == null ||  (
				  !(selected instanceof FolderWrapper) && !(selected instanceof Contact)))
	    return false;
	if (selected instanceof FolderWrapper)
	{
	    final FolderWrapper wrapper = (FolderWrapper)selected;
	    if (base.deleteFolder(wrapper.folder()))
		foldersArea.refresh();
	    return true;
	}
	if (selected instanceof Contact)
	{
	    final Contact contact = (Contact)selected;
	    if (base.deleteContact(contact))
	    {
		foldersArea.refresh();
		valuesArea.clear();
		notesArea.clear();
	    }
	    return true;
	}
	return false;
    }
    */

@Override public boolean onListClick(ListArea area, int index, Object obj)
    {
    /*
	NullCheck.notNull(app, "app");
	NullCheck.notNull(valuesArea, "valuesArea");
	NullCheck.notNull(notesArea, "notesArea");
	if (obj == null || !(obj instanceof Contact))
	    return;
	app.ensureEverythingSaved();
	base.setCurrentContact((Contact)obj);
	base.fillValuesArea(valuesArea);
	base.fillNotesArea(notesArea);
	luwrain.setActiveArea(valuesArea);
    */
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

    boolean deleteValue()
    {
	//Currently the user can leave the value empty to delete it on saving
	return false;
    }
    */

    boolean insertIntoTree(ContactsFolder insertInto)
    {
	NullCheck.notNull(insertInto, "insertInto");
	final String folderTitle = app.getStrings().insertIntoTreePopupValueFolder();
	final String contactTitle = app.getStrings().insertIntoTreePopupValueContact();
	final Object res = Popups.fixedList(app.getLuwrain(), app.getStrings().insertIntoTreePopupName(), new String[]{folderTitle, contactTitle});
	if (res == folderTitle)
	    return insertFolder(insertInto);
	    if (res == contactTitle)
		return insertContact(insertInto);
return false;
    }

    private boolean insertFolder(ContactsFolder insertInto)
    {
	final String name = Popups.simple(app.getLuwrain(), "Имя новой группы контактов", "Введите имя новой группы:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    app.getLuwrain().message("Новая группа контактов не может быть создана с пустым именем", Luwrain.MessageType.ERROR);
	    return false;
	}
	try {
	    final ContactsFolder f = new ContactsFolder();
	    f.setTitle(name);
	    f.setOrderIndex(0);
	    app.getStoring().getFolders().save(insertInto, f);
	    return true;
	}
	catch(Exception e)
	{
	    app.getLuwrain().crash(e);
	    return false;
	}
    }

    private boolean insertContact(ContactsFolder insertInto)
    {
	final String name = Popups.simple(app.getLuwrain(), "Имя нового контакта", "Введите имя нового контакта:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    app.getLuwrain().message("Новый контакт не может быть создан с пустым именем", Luwrain.MessageType.ERROR);
	    return false;
	}
	try {
	    final Contact c = new Contact();
	    c.setTitle(name);
	    app.getStoring().getContacts().save(insertInto, c);
	    return true;
	}
	catch(Exception e)
	{
	    app.getLuwrain().crash(e);
	    return false;
	}
    }

    void setCurrentContact(Contact contact)
    {
	NullCheck.notNull(contact, "contact");
	currentContact = contact;
    }


}

	


package org.luwrain.app.contacts;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.pim.contacts.*;

class Base
{
    static private final String SHARED_OBJECT_NAME = "luwrain.pim.contacts";

    private Luwrain luwrain;
    private Strings strings;
    private ContactsStoring storing;
    private StoredContact currentContact = null;
    private TreeModelSource treeModelSource;
    private TreeModel foldersModel;

    boolean init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	final Object obj = luwrain.getSharedObject(SHARED_OBJECT_NAME);
	if (obj == null || !(obj instanceof org.luwrain.pim.contacts.Factory))
	    return false;
	final org.luwrain.pim.contacts.Factory factory = (org.luwrain.pim.contacts.Factory)obj;
	final Object obj2 = factory.createContactsStoring();
	if (obj2 == null || !(obj2 instanceof ContactsStoring))
	    return false;
	storing = (ContactsStoring)obj2;
	return true;
    }

    boolean hasCurrentContact()
    {
	return currentContact != null;
    }

    TreeModel getFoldersModel()
    {
	if (foldersModel != null)
	    return foldersModel;
	treeModelSource = new TreeModelSource(storing, strings);
	foldersModel = new CachedTreeModel(treeModelSource);
	return foldersModel;
    }

    boolean openFolder(StoredContactsFolder folder)
    {
	return false;
    }

    boolean insertIntoTree(StoredContactsFolder insertInto)
    {
	NullCheck.notNull(insertInto, "insertInto");
	final String folderTitle = strings.insertIntoTreePopupValueFolder();
	final String contactTitle = strings.insertIntoTreePopupValueContact();
	final Object res = Popups.fixedList(luwrain, strings.insertIntoTreePopupName(), new String[]{folderTitle, contactTitle}, 0);
	if (res == folderTitle)
	    return insertFolder(insertInto);
	    if (res == contactTitle)
		return insertContact(insertInto);
return false;
    }

    private boolean insertFolder(StoredContactsFolder insertInto)
    {
	final String name = Popups.simple(luwrain, "Имя новой группы контактов", "Введите имя новой группы:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    luwrain.message("Новая группа контактов не может быть создана с пустым именем", Luwrain.MESSAGE_ERROR);
	    return false;
	}
	final ContactsFolder f = new ContactsFolder();
	f.title = name;
	f.orderIndex = 0;
	try {
	    storing.saveFolder(insertInto, f);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время добавления новой группы контактов произошла неожиданная ошибка", Luwrain.MESSAGE_ERROR);
	    return false;
	}
	return true;
    }

    private boolean insertContact(StoredContactsFolder insertInto)
    {
	final String name = Popups.simple(luwrain, "Имя нового контакта", "Введите имя нового контакта:", "");
	if (name == null)
	    return false;
	if (name.trim().isEmpty())
	{
	    luwrain.message("Новый контакт не может быть создан с пустым именем", Luwrain.MESSAGE_ERROR);
	    return false;
	}
	final Contact c = new Contact();
	c.title = name;
	try {
	    storing.saveContact(insertInto, c);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время добавления нового контакта произошла неожиданная ошибка", Luwrain.MESSAGE_ERROR);
	    return false;
	}
	return true;
    }

    void setCurrentContact(StoredContact contact)
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
	    luwrain.message("Во время получения данных контакта произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);
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
	    values.add(value);
	}
	try {
	    currentContact.setValues(values.toArray(new ContactValue[values.size()]));
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    luwrain.message("Во время сохранения введённых изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);
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
}, 0);
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
	    luwrain.message("Во время добавления нового значения произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);
	    return false;
	}
	return true;
    }

    void fillNotesArea(EditArea area)
    {
    }

    void saveNotes(EditArea area)
    {
    }


}

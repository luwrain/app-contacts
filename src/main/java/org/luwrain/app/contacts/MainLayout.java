
package org.luwrain.app.contacts;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.popups.*;

final class MainLayout
{
    private final App app;
    private final ListArea foldersArea;
    private final FormArea valuesArea;
    private final EditArea notesArea;

    private ContactsFolder[] folders = new ContactsFolder[0];
    private Contact currentContact = null;

    MainLayout(App app)
    {
	this.app = app;
	this.foldersArea = new ListArea(createFoldersListParams((area, index, obj)->{
		    NullCheck.notNull(area, "area");
		    NullCheck.notNull(obj, "obj");
		    //		    actions.openContact(App.this, obj, valuesArea, notesArea);
		    return true;
		})) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
			return super.onSystemEvent(event);
		}
	    };

	this.valuesArea = new FormArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().valuesAreaName()){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
			return super.onSystemEvent(event);
		    }
	    };

	final EditArea.Params editParams = new EditArea.Params();
	editParams.context = new DefaultControlContext(app.getLuwrain());
	editParams.name = app.getStrings().notesAreaName();
	this.notesArea = new EditArea(editParams){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
			return super.onSystemEvent(event);
		}
	    };
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
		if (v.getType() == ContactValue.MAIL)
		    area.addEdit("mail" + (counter++), "Электронная почта:", v.getValue(), v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.getType() == ContactValue.MOBILE_PHONE)
		    area.addEdit("mobile" + (counter++), "Мобильный телефон:", v.getValue(), v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.getType() == ContactValue.GROUND_PHONE)
		    area.addEdit("ground" + (counter++), "Телефон:", v.getValue(), v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.getType() == ContactValue.ADDRESS)
		    area.addEdit("address" + (counter++), "Адрес:", v.getValue(), v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.getType() == ContactValue.BIRTHDAY)
		    area.addEdit("birthday" + (counter++), "Дата рождения:", v.getValue(), v, true);
	    for(ContactValue v: currentContact.getValues())
		if (v.getType() == ContactValue.SKYPE)
		    area.addEdit("skype" + (counter++), "Skype:", v.getValue(), v, true);
	}
	catch (Exception e)
	{
	    app.getLuwrain().crash(e);
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
	    final Object obj = area.getItemObj(i);
	    if (obj == null || !(obj instanceof ContactValue))
		continue;
	    final ContactValue value = (ContactValue)obj;
	    //FIXME:	    value.setValue(area.getEnteredText(i));
	    if (!value.getValue().trim().isEmpty())
		values.add(value);
	}
	try {
	    currentContact.setValues(values.toArray(new ContactValue[values.size()]));
	}
	catch(Exception e)
	{
	    app.getLuwrain().crash(e);
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
	    app.getLuwrain().crash(e);
	    return false;
	}
	return true;
    }

    boolean fillNotesArea(EditArea area)
    {
	String value;
	try {
	    value = currentContact.getNotes();
	    }
	catch(Exception e)
	{
	    app.getLuwrain().crash(e);
	    return false;
	}
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
	try {
	    currentContact.setNotes(b.toString());
	    return true;
	}
	catch(Exception e)
	{
	    app.getLuwrain().crash(e);
	    	    return false;
	}
    }

    boolean deleteFolder(ContactsFolder folder)
    {
	try {
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
	catch(Exception e)
	{
	    e.printStackTrace();
	    app.getLuwrain().message("Во время попытки удаления группы контактов произошла непредвиденная ошибка:" + e.getMessage(), Luwrain.MessageType.ERROR);
	    return false;
	}
    }

    boolean deleteContact(Contact contact)
    {
	try {
	    final YesNoPopup popup = new YesNoPopup(app.getLuwrain(), "Удаление группы контактов", "Вы действительно хотите удалить контакт \"" + contact.getTitle() + "\"?", false, Popups.DEFAULT_POPUP_FLAGS);
	    app.getLuwrain().popup(popup);
	    if (popup.wasCancelled() || !popup.result())
		return false;
	    app.getStoring().getContacts().delete(contact);
	    currentContact = null;//FIXME:maybe only if currentContact == contact
	    return true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    app.getLuwrain().message("Во время попытки удаления контакта произошла непредвиденная ошибка:" + e.getMessage(), Luwrain.MessageType.ERROR);
	    return false;
	}
    }



AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
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

    void openContact(App app, Object obj, FormArea valuesArea, EditArea notesArea)
    {
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
    }

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

        boolean openFolder(ContactsFolder folder)
    {
	return false;
    }

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



        ListArea.Params createFoldersListParams(ListArea.ClickHandler clickHandler)
    {
	NullCheck.notNull(clickHandler, "clickHandler");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new FoldersListModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = app.getStrings().foldersAreaName();
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

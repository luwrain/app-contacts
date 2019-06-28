
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

final class Actions
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;

    Actions(Base base)
    {
	NullCheck.notNull(base, "base");
	this.luwrain = base.luwrain;
	this.strings = base.strings;
	this.base = base;
    }

    boolean insertIntoTree(TreeArea foldersArea)
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

    boolean deleteFromTree(TreeArea foldersArea, FormArea valuesArea, EditArea notesArea)
    {
	NullCheck.notNull(foldersArea, "foldersArea");
	NullCheck.notNull(valuesArea, "valuesArea");
	NullCheck.notNull(notesArea, "notesArea");
	final Object selected = foldersArea.selected();
	if (selected == null ||  (
				  !(selected instanceof FolderWrapper) && !(selected instanceof StoredContact)))
	    return false;
	if (selected instanceof FolderWrapper)
	{
	    final FolderWrapper wrapper = (FolderWrapper)selected;
	    if (base.deleteFolder(wrapper.folder()))
		foldersArea.refresh();
	    return true;
	}
	if (selected instanceof StoredContact)
	{
	    final StoredContact contact = (StoredContact)selected;
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
	if (obj == null || !(obj instanceof StoredContact))
	    return;
	app.ensureEverythingSaved();
	base.setCurrentContact((StoredContact)obj);
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
}

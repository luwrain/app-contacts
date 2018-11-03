
package org.luwrain.app.contacts;

import org.luwrain.core.NullCheck;
import org.luwrain.pim.contacts.StoredContactsFolder;

//Needed for localized titles;
class FolderWrapper
{
    private StoredContactsFolder folder;
    private String title;

    FolderWrapper(StoredContactsFolder folder, String title)
    {
	this.folder = folder;
	this.title = title;
	NullCheck.notNull(folder, "folder");
	NullCheck.notNull(title, "title");
    }

    StoredContactsFolder folder()
    {
	return folder;
    }

    public String title()
    {
	return title;
    }

    @Override public String toString()
    {
	return title;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof FolderWrapper))
	    return false;
	final FolderWrapper wrapper = (FolderWrapper)o;
	return folder.equals(wrapper.folder);
    }
}

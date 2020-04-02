
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.pim.contacts.*;

//Needed for localized titles;
class FolderWrapper
{
    private ContactsFolder folder;
    private String title;

    FolderWrapper(ContactsFolder folder, String title)
    {
	this.folder = folder;
	this.title = title;
	NullCheck.notNull(folder, "folder");
	NullCheck.notNull(title, "title");
    }

    ContactsFolder folder()
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

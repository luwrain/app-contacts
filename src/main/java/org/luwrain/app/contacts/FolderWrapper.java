
package org.luwrain.app.contacts;

import org.luwrain.pim.contacts.StoredContactsFolder;

class FolderWrapper
{
    private StoredContactsFolder folder;
    private String title;

    public FolderWrapper(StoredContactsFolder folder, String title)
    {
	this.folder = folder;
	this.title = title;
	if (folder == null)
	    throw new NullPointerException("folder may not be null");
	if (title == null)
	    throw new NullPointerException("title may not be null");
    }

    public StoredContactsFolder folder()
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

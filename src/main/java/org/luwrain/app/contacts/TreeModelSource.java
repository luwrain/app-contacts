
package org.luwrain.app.contacts;

import org.luwrain.core.NullCheck;
import org.luwrain.pim.contacts.*;

class TreeModelSource implements org.luwrain.controls.CachedTreeModelSource
{
    private ContactsStoring storing;
    private Strings strings;

    public TreeModelSource(ContactsStoring storing, Strings strings)
    {
	this.storing = storing;
	this.strings = strings;
	NullCheck.notNull(storing, "storing");
	NullCheck.notNull(strings, "strings");
    }

    @Override public Object getRoot()
    {
	try {
	    final StoredContactsFolder root = storing.getFolders().getRoot();
	    if (root == null)
		return null;
	    return new FolderWrapper(root, root.getTitle());
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    @Override public Object[] getChildObjs(Object obj)
    {
	if (obj == null || !(obj instanceof FolderWrapper))
	    return new Object[0];
	final FolderWrapper wrapper = (FolderWrapper)obj;
	try {
	    StoredContactsFolder[] folders = storing.getFolders().load(wrapper.folder());
	    StoredContact[] contacts = storing.getContacts().load(wrapper.folder());
	    if (folders == null)
		folders = new StoredContactsFolder[0];
	    if (contacts == null)
		contacts = new StoredContact[0];
	    final FolderWrapper[] wrappers= new FolderWrapper[folders.length];
	    for(int i = 0;i < folders.length;++i)
		wrappers[i] = new FolderWrapper(folders[i], folders[i].getTitle());
	    final Object[] res = new Object[wrappers.length + contacts.length];
	    for(int i = 0;i < wrappers.length;++i)
		res[i] = wrappers[i];
	    for(int i = 0;i < contacts.length;++i)
		res[wrappers.length + i] = contacts[i];
	    return res;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return new Object[0];
	}
    }
}

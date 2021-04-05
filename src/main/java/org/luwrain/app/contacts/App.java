
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.app.base.*;

final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;
    private ContactsStoring storing = null;
    private ContactsFolder foldersRoot = null;

    App()
    {
	super(Strings.NAME, Strings.class, "luwrain.contacts");
    }

    @Override protected boolean onAppInit() throws Exception
    {
	this.storing = org.luwrain.pim.Connections.getContactsStoring(getLuwrain(), true);
	this.foldersRoot = storing.getFolders().getRoot();
	this.mainLayout = new MainLayout(this);
	return true;
    }

    void ensureEverythingSaved()
    {
	/*
	if (!base.hasCurrentContact())
	    return;
	base.saveForm(valuesArea);
	base.saveNotes(notesArea);
	*/
    }

    @Override protected AreaLayout getDefaultAreaLayout()
    {
	return mainLayout.getAreaLayout();
    }

    /*
    @Override public void closeApp()
    {
	ensureEverythingSaved();
	luwrain.closeApp();
    }
    */

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    ContactsStoring getStoring()
    {
	return this.storing;
    }

    ContactsFolder getFoldersRoot()
    {
	return this.foldersRoot;
    }
    
}

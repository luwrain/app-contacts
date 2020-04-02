
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;
import org.luwrain.template.*;

final class App extends AppBase<Strings> implements MonoApp
{
    App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override protected boolean onAppInit()
    {
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
	return null;//new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
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
}

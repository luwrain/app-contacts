
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

public class ContactsApp implements Application, Actions
{
    static private final String STRINGS_NAME = "luwrain.contacts";

    private Luwrain luwrain;
    private Base base = new Base();
    private Strings strings;

    private TreeArea foldersArea;
    private FormArea valuesArea;
    private EditArea notesArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain, strings))
	    return false;
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public boolean deleteFromTree()
    {
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

    @Override public boolean deleteValue()
    {
	//Currently the user can leave the value empty to delete it on saving
	return false;
    }




    @Override public void openContact(Object obj)
    {
	if (obj == null || !(obj instanceof StoredContact))
	    return;
	ensureEverythingSaved();
	base.setCurrentContact((StoredContact)obj);
	base.fillValuesArea(valuesArea);
	base.fillNotesArea(notesArea);
	gotoValues();
    }

    @Override public boolean insertIntoTree()
    {
	final Object selected = foldersArea.selected();
	if (selected == null || !(selected instanceof FolderWrapper))
	    return false;
	final FolderWrapper wrapper = (FolderWrapper)selected;
	if (!base.insertIntoTree(wrapper.folder()))
	    return true;
	foldersArea.refresh();
	return true;
    }

    //Returns false if the area must issue an error beep;
    @Override public boolean insertValue()
    {
	if (!base.hasCurrentContact())
	    return false;
	if (!base.saveForm(valuesArea))
	    return true;
	if (!base.insertValue())
	    return true;
	base.fillValuesArea(valuesArea);
	return true;
    }

    private void createAreas()
    {
	final Actions actions = this;
	final Strings s = strings;

	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.environment = new DefaultControlEnvironment(luwrain);
	treeParams.model = base.getFoldersModel();
	treeParams.name = 				   strings.foldersAreaName();

	foldersArea = new TreeArea(treeParams) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    actions.gotoValues();
			    return true;
			case INSERT:
			    return actions.insertIntoTree();
			case DELETE:
			    return actions.deleteFromTree();
			default:
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    NullCheck.notNull(obj, "obj");
		    actions.openContact(obj);
		}
	    };

	valuesArea = new FormArea(new DefaultControlEnvironment(luwrain), strings.valuesAreaName()){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    actions.gotoNotes();
			    return true;
			case INSERT:
			    return actions.insertValue();
			case DELETE:
			    return actions.deleteValue();
			default:
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };

	notesArea = new EditArea(new DefaultControlEnvironment(luwrain), strings.notesAreaName()){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    actions.gotoFolders();
			    return true;
			default:
			    return super.onKeyboardEvent(event);
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
    }

    @Override public void gotoFolders()
    {
	luwrain.setActiveArea(foldersArea);
    }

    @Override public void gotoValues()
    {
	luwrain.setActiveArea(valuesArea);
    }

    public void gotoNotes()
    {
	luwrain.setActiveArea(notesArea);
    }

    @Override public void closeApp()
    {
	ensureEverythingSaved();
	luwrain.closeApp();
    }

    private void ensureEverythingSaved()
    {
	if (!base.hasCurrentContact())
	    return;
	base.saveForm(valuesArea);
	base.saveNotes(notesArea);
    }
}

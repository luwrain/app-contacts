
package org.luwrain.app.contacts;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.contacts.*;

final class MainLayout
{
    private final App app;
    private ListArea foldersArea = null;
    private FormArea valuesArea = null;
    private EditArea notesArea = null;

        private ContactsFolder[] folders = new ContactsFolder[0];

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

AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, valuesArea, notesArea);
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

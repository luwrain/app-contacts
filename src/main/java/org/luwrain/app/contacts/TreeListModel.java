
package org.luwrain.app.contacts;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class TreeListModel implements ListArea.Model
{
    protected final TreeArea.Model treeModel;
    protected Object current = null;

    TreeListModel(TreeArea.Model treeModel)
    {
	NullCheck.notNull(treeModel, "treeModel");
	this.treeModel = treeModel;
	this.current = treeModel.getRoot();
    }

    @Override public int getItemCount()
    {
	return 0;
    }
    
    @Override public Object getItem(int index)
    {
	return null;
    }

	@Override public void refresh()
    {
    }

    protected Object[] buildList(Object buildFor)
    {
	NullCheck.notNull(buildFor, "buildFor");
	final List firstLevelList = new LinkedList();
	treeModel.beginChildEnumeration(buildFor);
	final int firstLevelCount = treeModel.getChildCount(buildFor);
	for(int i = 0;i < firstLevelCount;++i)
	{
	    final Object o = treeModel.getChild(buildFor, i);
	    if (o != null)
		firstLevelList.add(o);
	}
		treeModel.endChildEnumeration(buildFor);

		return null;
    }
}

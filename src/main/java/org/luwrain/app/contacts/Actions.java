
package org.luwrain.app.contacts;

//import org.luwrain.pim.contacts.StoredContact;

interface Actions
{
    void gotoFolders();
    void gotoValues();
    void gotoNotes();
    void openContact(Object obj);
    boolean insertIntoTree();
    boolean insertValue();
    void closeApp();
    boolean deleteFromTree();
    boolean deleteValue();
}

package com.rim.samples.device.helloworlddemo;

import java.util.Enumeration;

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.blackberry.api.pdap.BlackBerryContactList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class CleanContacts extends UiApplication
{
	/**
	 * Entry point for application
	 * @param args Command line arguments (not used)
	 */ 
	public static void main(String[] args)
	{
		// Create a new instance of the application and make the currently
		// running thread the application's event dispatch thread.
		CleanContacts theApp = new CleanContacts();       
		theApp.enterEventDispatcher();
	}


	/**
	 * Creates a new HelloWorldDemo object
	 */
	public CleanContacts()
	{        
		// Push a screen onto the UI stack for rendering.
		pushScreen(new HelloWorldScreen());
	}    
}

final class HelloWorldScreen extends MainScreen
{
	final LabelField label = new LabelField();
	final ButtonField btn = new ButtonField("Start", ButtonField.CONSUME_CLICK);

	boolean closing = false;
	int cleaned = 0;

	HelloWorldScreen(){
		
		setTitle("Clean Contacts");
		add(label);
		add(btn);

		btn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				CleanContactsAction();
			}
		});
	}
	
		
	public void close()
	{
		closing = true;
		super.close();
	}   

	public void updateLabel(final String str){
		UiApplication.getUiApplication().invokeAndWait(new Runnable() {
			public void run() {
				label.setText(str);
			}
		});		
	}
	
	public void CleanContactsAction()
	{
		label.setText("starting..."); 

		new Thread(new Runnable() {
			
			public void run() {
				
				try {
					BlackBerryContactList contacts = (BlackBerryContactList) PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);

					Enumeration enumeration = contacts.itemsByName("Unnamed");

					while (enumeration.hasMoreElements()) {
						BlackBerryContact item = (BlackBerryContact) enumeration.nextElement();
						contacts.removeContact(item);

						updateLabel(Integer.toString(++cleaned));

						if (closing) {
							return;
						}

					}
					contacts.close();

				} catch (PIMException e) {
					e.printStackTrace();
				}

				updateLabel("done, removed " + cleaned + " contacts");
				
			}
		}).start();
	}   

}

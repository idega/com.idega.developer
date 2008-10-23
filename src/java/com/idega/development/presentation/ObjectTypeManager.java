package com.idega.development.presentation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.development.presentation.comp.BundleComponentFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.PresentationUtil;

/**
 * @author gimmi
 */
public class ObjectTypeManager extends Block {

	private String PARAMETER_TYPE = "otm_pt";
	private String PARAMETER_NAME = "otm_pn";
	private String PARAMETER_REQUIRED_SUPER_CLASS = "otm_rpc";
	private String PARAMETER_REQ_INTERFACES = "otm_ri";
	private String PARAMETER_FINAL_REFLECTION_CLASS = "otm_frc";
	private String PARAMETER_METHOD_START_FILERS = "otm_msf";
	
	private String PARAMETER_OBJECT_TYPE_ID = "otm_oti";
	private String PARAMETER_DELETE_OBJECT_TYPE_ID = "otm_doti";
	private String PARAMETER_CHECK_IC_OBJECT = "otm_cio";
	private ICObjectType objectTypeToEdit = null;

	public ObjectTypeManager() {
	}

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = iwc.getIWMainApplication().getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		String otPk = iwc.getParameter(this.PARAMETER_OBJECT_TYPE_ID);
		if (otPk != null) {
			try {
				ICObjectTypeHome otHome = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
				this.objectTypeToEdit = otHome.findByPrimaryKey(otPk);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}
			
		if (iwc.isParameterSet(this.PARAMETER_DELETE_OBJECT_TYPE_ID)) {
			handleDelete(iwc);			
		}
			
		if (iwc.isParameterSet(this.PARAMETER_TYPE)) {
			handleInsert(iwc);	
		}
		
		if (iwc.isParameterSet(this.PARAMETER_CHECK_IC_OBJECT)) {
			listObjectUsingType(iwc, iwc.getParameter(this.PARAMETER_CHECK_IC_OBJECT));
		} else {
			drawMenu(iwc);
		}
 	}
 	
 	private void handleInsert(IWContext iwc){
 		String name = iwc.getParameter(this.PARAMETER_NAME);
 		String type = iwc.getParameter(this.PARAMETER_TYPE);
 		String rSuper = iwc.getParameter(this.PARAMETER_REQUIRED_SUPER_CLASS);
 		String rInter = iwc.getParameter(this.PARAMETER_REQ_INTERFACES);
 		String refl = iwc.getParameter(this.PARAMETER_FINAL_REFLECTION_CLASS);
 		String filters = iwc.getParameter(this.PARAMETER_METHOD_START_FILERS);
 		
 		if (!name.equals("") && !name.equals("")) { 
 		
			ICObjectTypeHome otHome;
			try {
				otHome = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
		 		ICObjectType ot = null;
		 		if (this.objectTypeToEdit == null) {
		 			ot = otHome.create();
		 		} else {
		 			ot = this.objectTypeToEdit;
		 		}
		 		ot.setName(name);
		 		ot.setType(type);
		 		if (rSuper != null) {
		 			if (rSuper.equals("")) {
		 				ot.setRequiredSuperClassName(null);
		 			} else {
						RefactorClassRegistry.forName(rSuper);
		 				ot.setRequiredSuperClassName(rSuper);
		 			}
		 		}
				if (rInter != null) {
					if ( rInter.equals("") ) {
						ot.setRequiredInterfacesString(null);
					} else {
						Vector vector = ot.seperateStringIntoVector(rInter);
						if (vector != null) {
							Iterator iter = vector.iterator();
							String className;
							while (iter.hasNext()) {
								className = (String) iter.next();
								RefactorClassRegistry.forName(className);
							}
						}
			 			ot.setRequiredInterfacesString(rInter);
					}
				}
				if (refl != null) {
					if (refl.equals("")) {
						ot.setFinalReflectionClassName(null);
					} else {
						RefactorClassRegistry.forName(refl);
		 				ot.setFinalReflectionClassName(refl);
					}
				}
				if (filters != null) {
		 			ot.setMethodStartFiltersString(filters);
				}
		 		ot.store();
		 		this.objectTypeToEdit = null;
			} catch (IDOLookupException e) {
				add(IWDeveloper.getText("ObjectType was not created ("+e.getMessage()+")"));
				e.printStackTrace();
			} catch (CreateException e) {
				add(IWDeveloper.getText("ObjectType was not created ("+e.getMessage()+")"));
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				add(IWDeveloper.getText("ObjectType was not created, could not find class "+e.getMessage()+Text.BREAK));
				e.printStackTrace();
			}
	 		
 		} else {
 			add(IWDeveloper.getText("ObjectType was not created"));
 		}
		BundleComponentFactory.getInstance().refreshCache();

 	}
 	
 	private void handleDelete(IWContext iwc) {
 		String[] idsToDelete = iwc.getParameterValues(this.PARAMETER_DELETE_OBJECT_TYPE_ID);
 		if (idsToDelete != null && idsToDelete.length > 0) {
 			try {
				ICObjectHome idoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
				ICObjectTypeHome idotHome = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
				ICObjectType oType;
				Collection coll;
				Link link;
				for (int i = 0; i < idsToDelete.length; i++) {
					coll = idoHome.findAllByObjectType( idsToDelete[i] );
					if (coll == null || coll.isEmpty()) {
						oType = idotHome.findByPrimaryKey(idsToDelete[i]);
						try {
							oType.remove();
						} catch (RemoveException e) {
							add(IWDeveloper.getText("Cannot delete "+idsToDelete[i]+" ("+e.getMessage()+")"+Text.BREAK));
						}
					} else {
						link = new Link(IWDeveloper.getText("these"));
						link.addParameter(this.PARAMETER_CHECK_IC_OBJECT, idsToDelete[i]);
						link.maintainParameter(IWDeveloper.actionParameter, iwc);
						link.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc);
						
						add(IWDeveloper.getText("Not deleting "+idsToDelete[i]+" because its still being used by "));
						add(link);
						add(IWDeveloper.getText(" objects"+Text.BREAK));
					}
				}
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			}
 		}
		BundleComponentFactory.getInstance().refreshCache();
 	}

	private void listObjectUsingType(IWContext iwc, String icObjectTypePk) {
		try {
			Table table = new Table();
			int row = 1;
			ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			ICObject object;
			Collection coll = icoHome.findAllByObjectType(icObjectTypePk);
			if (coll != null && !coll.isEmpty()) {
				table.mergeCells(1, row, 2, row);
				table.add(IWDeveloper.getText("ObjectType "+icObjectTypePk+" is used by the following objects"+Text.BREAK), 1, row);
				Iterator iter = coll.iterator();
				while (iter.hasNext()) {
					++row;
					object = icoHome.findByPrimaryKey(iter.next());
					table.add(object.getName()+Text.BREAK, 1, row);
					table.add(object.getClassName()+Text.BREAK, 2, row);
				}
				
			} else {
				table.add(IWDeveloper.getText("ObjectType "+icObjectTypePk+" is not used by IC_OBJECT"+Text.BREAK), 1, row);
			}
			add(table);
			Link back = new Link(IWDeveloper.getText("Back"));
			back.maintainParameter(IWDeveloper.actionParameter, iwc);
			back.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc);
			add(back);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void drawMenu(IWContext iwc) {
		Collection objectTypes = null;
		ICObjectTypeHome otHome = null;
		
		try {
			otHome = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			objectTypes = otHome.findAll();
		
			Form form = new Form();
			form.maintainParameter(IWDeveloper.actionParameter);
			form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
			Table table = new Table();
			
			form.add(table);
			int row = 1;
			
			table.add(IWDeveloper.getText("Name"), 1, row);
			table.add(IWDeveloper.getText("Type"), 2, row);
			table.add(IWDeveloper.getText("Req. Superclass"), 3, row);
			table.add(IWDeveloper.getText("Req. Interfaces*"), 4, row);
			table.add(IWDeveloper.getText("Final reflection class"), 5, row);
			table.add(IWDeveloper.getText("Method start filters*"), 6, row);
			table.add(IWDeveloper.getText("Delete"), 7, row);
			Iterator iter = objectTypes.iterator();
			ICObjectType objectType;
			CheckBox delete;
			Link link;
			while (iter.hasNext()) {
				objectType = otHome.findByPrimaryKey(iter.next().toString());
				link = new Link(objectType.getName());
				link.addParameter(this.PARAMETER_OBJECT_TYPE_ID, objectType.getPrimaryKey().toString());
				link.maintainParameter(IWDeveloper.actionParameter, iwc);
				link.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME, iwc);
				delete = new CheckBox(this.PARAMETER_DELETE_OBJECT_TYPE_ID, objectType.getPrimaryKey().toString());
				table.add(link, 1, ++row);
				table.add(objectType.getType(), 2, row);
				if (objectType.getRequiredSuperClassName() != null) {
					table.add(objectType.getRequiredSuperClassName(), 3, row);
				}
				if (objectType.getRequiredInterfacesString() != null) {
					table.add(objectType.getRequiredInterfacesString(), 4, row);
				}
				if (objectType.getFinalReflectionClassName() != null) {
					table.add(objectType.getFinalReflectionClassName(), 5, row);
				}
				if (objectType.getMethodStartFiltersString() != null) {
					table.add(objectType.getMethodStartFiltersString(), 6, row);
				}
				/*
				String[] filt = objectType.getMethodStartFilters();
				if (filt == null) {
					System.out.println("Filters for "+objectType.getName()+" == null");
				} else {
					System.out.println("Filters for "+objectType.getName()+" = "+filt.length);
					for (int i = 0; i < filt.length; i++) {
						System.out.println("Filter "+(i+1)+" = "+filt[i]);
					}
				}
				*/
				table.add(delete, 7, row);
			}
			
			TextInput name = new TextInput(this.PARAMETER_NAME);
			TextInput type = new TextInput(this.PARAMETER_TYPE);
			TextInput rSuper = new TextInput(this.PARAMETER_REQUIRED_SUPER_CLASS);
			TextInput rInterf = new TextInput(this.PARAMETER_REQ_INTERFACES);
			TextInput refl = new TextInput(this.PARAMETER_FINAL_REFLECTION_CLASS);
			TextInput filters = new TextInput(this.PARAMETER_METHOD_START_FILERS);
			SubmitButton submit = new SubmitButton("Save");

			if (this.objectTypeToEdit != null) {
				name.setContent(this.objectTypeToEdit.getName());
				type.setContent(this.objectTypeToEdit.getType());
				if (this.objectTypeToEdit.getRequiredSuperClassName() != null) {
					rSuper.setContent(this.objectTypeToEdit.getRequiredSuperClassName());
				}
				if (this.objectTypeToEdit.getRequiredInterfacesString() != null) {
					rInterf.setContent(this.objectTypeToEdit.getRequiredInterfacesString());
				}
				if (this.objectTypeToEdit.getFinalReflectionClassName() != null) {
					refl.setContent(this.objectTypeToEdit.getFinalReflectionClassName());
				}
				if (this.objectTypeToEdit.getMethodStartFiltersString() != null) {
					filters.setContent(this.objectTypeToEdit.getMethodStartFiltersString());
				}
				
				table.add(new HiddenInput(this.PARAMETER_OBJECT_TYPE_ID, this.objectTypeToEdit.getPrimaryKey().toString()));
				submit = new SubmitButton("Update");
			}
			
			table.add(name, 1, ++row);
			table.add(type, 2, row);
			table.add(rSuper, 3, row);
			table.add(rInterf, 4, row);
			table.add(refl, 5, row);
			table.add(filters, 6, row);
			
			table.add(submit, 1, ++row);
			table.mergeCells(1, row, 7, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
			
			table.add(IWDeveloper.getText("* Comme delimited, if more than one"), 1, ++row);
			table.mergeCells(1, row, 7, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_LEFT);
			
			add(form);
		} catch (IDOLookupException e) {
			e.printStackTrace();
			add(IWDeveloper.getText("Error getting object types"));
		} catch (FinderException e) {
			e.printStackTrace();
			add(IWDeveloper.getText("Error getting object types"));
		}
			
		
	}

}

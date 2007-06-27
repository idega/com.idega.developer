package com.idega.development.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOSessionBean;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;

public class FilesManagerBusinessBean extends IBOSessionBean implements FilesManagerBusiness {

	private static final long serialVersionUID = -4600940859804313580L;
	
	public boolean copyFilesToSlide() {
		ICFileHome icFileHome = null;
		try {
			icFileHome = (ICFileHome) getIDOHome(ICFile.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (icFileHome == null) {
			return false;
		}
		
		ICFile root = null;
		try {
			root = icFileHome.findRootFolder();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		
		return copyFiles(root);
	}
	
	private boolean copyFiles(ICFile root) {
		if (root == null) {
			return false;
		}
		
		Collection files = root.getChildren();
		if (files == null) {
			System.out.println("ROOT folder has no children, nothing to copy, returning");
			return true;	//	Nothing to copy
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		IWSlideService slide = null;
		try {
			slide = (IWSlideService) IBOLookup.getServiceInstance(iwc, IWSlideService.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		if (slide == null) {
			return false;
		}
		
		Object o = null;
		ICFile file = null;
		boolean result = true;
		for (Iterator it = files.iterator(); (it.hasNext() && result);) {
			o = it.next();
			if (o instanceof ICFile) {
				file = (ICFile) o;
				if (file.isLeaf()) {
					result = copyFile(file, slide, DeveloperConstants.OLD_FILES_FOLDER_FOR_OTHER_FILES);
				}
				else {
					result = copyFilesFromFolder(file, slide, DeveloperConstants.OLD_FILES_FOLDER);
				}
			}
		}
		
		return result;
	}
	
	private boolean copyFilesFromFolder(ICFile folder, IWSlideService slide, String basePath) {
		Collection files = folder.getChildren();
		if (files == null) {
			System.out.println("Folder " + folder.getName() + " has no files");
			return true;	//	Nothing to copy
		}
		
		System.out.println("Copying files (" + files.size() + ") from folder " + folder.getName());
		
		Object o = null;
		ICFile file = null;
		boolean result = true;
		for (Iterator it = files.iterator(); (it.hasNext() && result);) {
			o = it.next();
			if (o instanceof ICFile) {
				file = (ICFile) o;
				if (file.isLeaf()) {
					result = copyFile(file, slide, basePath);
				}
				else {
					basePath = new StringBuffer(basePath).append(file.getName()).append(CoreConstants.SLASH).toString();
					result = copyFilesFromFolder(file, slide, basePath);
				}
			}
		}
		
		return result;
	}
	
	private boolean copyFile(ICFile file, IWSlideService slide, String basePath) {
		try {
			System.out.println("Copying file " + file.getName() + " to folder " + basePath);
			return slide.uploadFileAndCreateFoldersFromStringAsRoot(basePath, file.getName(), file.getFileValue(), file.getMimeType(),true);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

}

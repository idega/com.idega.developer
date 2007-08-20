package com.idega.development.business;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	
	private List<String> copiedFiles = null;
	
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
		
		Collection files = null;
		try {
			files = icFileHome.findAllDescendingOrdered();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		
		copiedFiles = new ArrayList<String>();
		
		return copyFiles(files);
	}
	
	private boolean copyFiles(Collection files) {
		if (files == null) {
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
		
		Iterator filesIterator = files.iterator();
		Object o = null;
		ICFile file = null;
		boolean result = true;
		for (Iterator it = filesIterator; (it.hasNext() && result);) {
			o = it.next();
			if (o instanceof ICFile) {
				file = (ICFile) o;
				if (file.isLeaf()) {
					result = copyFile(file, slide, DeveloperConstants.OLD_FILES_FOLDER_FOR_OTHER_FILES, true);
				}
				else if (file.isFolder() || !file.isLeaf()) {
					result = copyFilesFromFolder(file, slide, DeveloperConstants.OLD_FILES_FOLDER);
				}
			}
		}
		
		return result;
	}
	
	private boolean copyFilesFromFolder(ICFile folder, IWSlideService slide, String basePath) {
		Collection files = folder.getChildren();
		if (files == null) {
			return true;	//	Nothing to copy
		}
		
		Iterator filesIterator = files.iterator();
		Object o = null;
		ICFile file = null;
		boolean result = true;
		for (Iterator it = filesIterator; (it.hasNext() && result);) {
			o = it.next();
			if (o instanceof ICFile) {
				file = (ICFile) o;
				if (file.isLeaf()) {	//	File
					result = copyFile(file, slide, basePath, false);
				}
				else if (file.isFolder() || !file.isLeaf()) {	// Folder or file with children
					basePath = new StringBuffer(basePath).append(file.getName()).append(CoreConstants.SLASH).toString();
					result = copyFilesFromFolder(file, slide, basePath);
				}
			}
		}
		
		return result;
	}
	
	private boolean copyFile(ICFile file, IWSlideService slide, String basePath, boolean checkName) {
		String name = file.getName();
		if (name == null) {
			name = "Untitled";
		}
		
		if (checkName) {
			int index = 1;
			String tempName = name;
			while (copiedFiles.contains(tempName)) {
				tempName = new StringBuffer().append(index).append("_").append(name).toString();
				index++;
			}
			name = tempName;
			copiedFiles.add(name);
		}
		
		InputStream stream = file.getFileValue();
		if (stream == null) {
			return true;	//	Skipping
		}
		
		boolean result = true;
		try {
			result = slide.uploadFileAndCreateFoldersFromStringAsRoot(basePath, name, stream, file.getMimeType(), true);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeStream(stream);
		}
		if (result) {
			file.setFileUri(new StringBuffer(CoreConstants.WEBDAV_SERVLET_URI).append(basePath).append(name).toString());
			file.store();
		}
		return result;
	}
	
	private void closeStream(InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

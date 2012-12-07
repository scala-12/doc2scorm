package com.ipoint.coursegenerator.core.utils.manifest;

import java.util.Collection;
import java.util.Iterator;

import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationsType;

import com.ipoint.coursegenerator.core.utils.TransliterationTool;

public class OrganizationProcessor {

    public OrganizationProcessor() {
	super();

    }

    /**
     * Create Organization for Manifest
     * 
     * @param manifest
     */
    public static void createOrganization(ManifestType manifest) {
	manifest.addNewOrganizations().setDefault("");
	manifest.getOrganizations().addNewOrganization();
	OrganizationType organization = OrganizationType.Factory.newInstance();
	organization.addNewMetadata();
	// organization.setIdentifier(course.getSysName());
	// organization.setTitle(course.getName());
	// OrganizationType[] ota = manifest.getOrganizations()
	// .getOrganizationArray();
	// createItems(organization);
	// ota[0] = organization;
	// manifest.getOrganizations().setOrganizationArray(ota);
    }

    /**
     * Create Items Recousively
     * 
     * @param itemType
     * @param sco
     */
    public static ItemType createItem(ItemType parentItem, String scoName, String resid, String itemid) {	
	ItemType item = parentItem.addNewItem();
	item.setIdentifier(itemid);
	item.setTitle(scoName);
	if (resid != null)
	    item.setIdentifierref(resid);
	return item;
    }

    public static ItemType createItem(OrganizationsType organizations, String scoName,
	    String resid, String itemid) {
	ItemType item = organizations.getOrganizationArray(0).addNewItem();
	if (resid != null)
	    item.setIdentifierref(resid);
	item.setTitle(scoName);
	item.setIdentifier(itemid);
	return item;
    }
}

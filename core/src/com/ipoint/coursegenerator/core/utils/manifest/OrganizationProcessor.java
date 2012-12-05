package com.ipoint.coursegenerator.core.utils.manifest;

import java.util.Collection;
import java.util.Iterator;

import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationsType;

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
    public static void createItem(ItemType itemType, String scoName, String iref) {
	itemType.setIdentifier(scoName);
	itemType.setTitle(scoName);
	if (iref != null)
	    itemType.setIdentifierref(iref);
    }

    public static ItemType createItem(OrganizationsType organizations, String scoName,
	    String iref) {
	ItemType item = organizations.getOrganizationArray(0).addNewItem();
	if (iref != null)
	    item.setIdentifierref(iref);
	item.setTitle(scoName);
	item.setIdentifier(scoName);
	return item;
    }
}

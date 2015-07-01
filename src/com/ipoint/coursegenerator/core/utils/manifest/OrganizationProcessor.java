package com.ipoint.coursegenerator.core.utils.manifest;

import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
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
	public static void createOrganization(ManifestType manifest,
			String courseName) {
		manifest.addNewOrganizations().setDefault("");
		OrganizationType organization = manifest.getOrganizations()
				.addNewOrganization();
		organization.setTitle(courseName);
		organization.setIdentifier(courseName.replaceAll(" ", "_").replaceAll(
				"[\\W&&[^-]]", "")
				+ "_" + java.util.UUID.randomUUID());
		organization.addNewMetadata();
	}

	/**
	 * Create Items Recousively
	 * 
	 * @param itemType
	 * @param sco
	 */
	public static ItemType createItem(ItemType parentItem, String scoName,
			String resid, String itemid) {
		ItemType item = parentItem.addNewItem();
		
		return addItemInfo(item, scoName, resid, itemid);
	}
	
	public static ItemType createItem(OrganizationsType organizations,
			String scoName, String resid, String itemid) {
		ItemType item = organizations.getOrganizationArray(0).addNewItem();

		return addItemInfo(item, scoName, resid, itemid);
	}
	
	private static ItemType addItemInfo(ItemType item, String scoName,
			String resid, String itemid) {
		item.setIdentifier(itemid);
		item.setTitle(scoName);
		if (resid != null)
			item.setIdentifierref(resid);
		return item;
	}

	public static ItemType insertBeforeLast(XmlComplexContentImpl parentItem,
			String scoName, String resid, String itemid) {
		ItemType item = null;
		if (parentItem instanceof OrganizationType) {
			OrganizationType parent = (OrganizationType) parentItem;
			if (parent.sizeOfItemArray() > 1) {
				item = parent.insertNewItem(parent.sizeOfItemArray() - 1);
			} else {
				item = parent.insertNewItem(0);
			}
		} else if (parentItem instanceof ItemType) {
			ItemType parent = (ItemType) parentItem;
			if (parent.sizeOfItemArray() > 1) {
				item = parent.insertNewItem(parent.sizeOfItemArray() - 1);
			} else {
				item = parent.insertNewItem(0);
			}
		}
		if (item != null) {
			item.setIdentifier(itemid);
			item.setTitle(scoName);
			if (resid != null)
				item.setIdentifierref(resid);
			return item;
		} else
			return null;
	}

}

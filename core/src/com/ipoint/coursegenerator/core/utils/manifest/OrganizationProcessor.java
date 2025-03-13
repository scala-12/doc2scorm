package com.ipoint.coursegenerator.core.utils.manifest;

import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.OrganizationType;

import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.structure.ModelTreeNode;
import com.ipoint.coursegenerator.core.utils.Tools;

public class OrganizationProcessor {

	private final ManifestType manifest;
	private final String courseName;
	private final String sysName;

	public OrganizationProcessor(ManifestType manifest, String courseName, String sysName) {
		this.manifest = manifest;
		this.courseName = Tools.removeExtraSpaces(courseName);
		this.sysName = ((null == sysName) || sysName.isEmpty()) ? Tools.generateSystemName(courseName) : sysName;

		this.manifest.addNewOrganizations().setDefault(this.sysName);
		this.manifest.getOrganizations().addNewOrganization();

		createOrganization();
	}

	private OrganizationType createOrganization() {
		OrganizationType organization = OrganizationType.Factory.newInstance();
		organization.addNewMetadata();
		organization.setIdentifier(this.sysName);
		organization.setTitle(this.courseName);

		OrganizationType[] ota = this.manifest.getOrganizations().getOrganizationArray();
		ota[0] = organization;
		this.manifest.getOrganizations().setOrganizationArray(ota);

		return organization;
	}

	private OrganizationType getOrganization() {
		OrganizationType organization = this.manifest.getOrganizations().getOrganizationArray()[0];
		if (organization.getIdentifier() == null) {
			organization = createOrganization();
		}

		return organization;
	}

	public void createItem(ModelTreeNode node) {
		ItemType itemType = getOrganization().addNewItem();
		itemType.setIdentifier("RES_" + node.getSystemName());
		itemType.setTitle(node.getTitle());

		AbstractPage<?> page = node.getPage();

		if (null != page) {
			if (page instanceof TestingPage) {
				TestingPage testingPage = (TestingPage) page;
				itemType.addNewItem().newCursor().insertElementWithText("datafromlms",
						"http://www.adlnet.org/xsd/adlcp_rootv1p2", testingPage.getLaunchData());
				itemType.removeItem(0);
				if (testingPage.getMaxTimeAllowed() != null) {
					itemType.addNewItem().newCursor().insertElementWithText("maxtimeallowed",
							"http://www.adlnet.org/xsd/adlcp_rootv1p2", testingPage.getMaxTimeAllowed().toString());
					itemType.removeItem(0);
				}
			}

			itemType.setIdentifierref("REF_" + node.getSystemName());
		}
	}
}

package com.ipoint.coursegenerator.core.utils.manifest;

import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.MetadataType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourcesType;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.structure.ModelTreeNode;

public class ResourcesProcessor {

	private final ManifestType manifest;

	public ResourcesProcessor(ManifestType manifest) {
		this.manifest = manifest;

		createResources();
	}

	private ResourcesType getResources() {
		return ((this.manifest.getResources() == null) || (manifest.getResources().getResourceArray() == null))
				? createResources() : this.manifest.getResources();
	}

	private ResourcesType createResources() {
		return this.manifest.addNewResources();
	}

	public void createItem(ModelTreeNode node) {
		AbstractPage<?> page = node.getPage();
		if (null != page) {
			ResourceType resourse = this.getResources().addNewResource();
			resourse.setIdentifier("REF_" + node.getSystemName());
			resourse.setType("webcontent");

			Node attNode = resourse.getDomNode().getOwnerDocument()
					.createAttributeNS("http://www.adlnet.org/xsd/adlcp_rootv1p2", "scormType");
			attNode.setNodeValue("sco");
			resourse.getDomNode().getAttributes().setNamedItem(attNode);

			String href = node.getPageLocation();
			resourse.setHref(href);

			MetadataType mt = MetadataType.Factory.newInstance();
			mt.setSchema("ADL SCORM");
			mt.setSchemaversion("2004 4th Edition");
			resourse.setMetadata(mt);
			resourse.addNewFile().setHref(href);
		}
	}

}

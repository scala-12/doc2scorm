package com.ipoint.coursegenerator.core.utils.manifest;

import org.imsproject.xsd.imscpRootv1P1P2.ManifestDocument;


public class ManifestProcessor {

	public ManifestProcessor() {
		super();
	}

	/**
	 * Create root element is Manifest
	 * 
	 * @param manifestDocument
	 */
	public void createManifest(ManifestDocument manifestDocument) {
		manifestDocument.addNewManifest();
		manifestDocument.getManifest().setIdentifier(
				"SingleSharableResource_MulitipleFileManifest");
		manifestDocument.getManifest().setVersion("1.1");
	}

}

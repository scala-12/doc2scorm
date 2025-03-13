package com.ipoint.coursegenerator.core.utils.manifest;

import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;

public class MetadataProcessor {

	/**
	 * MetadataProcessor Constructor
	 */
	public MetadataProcessor() {
		super();
	}

	/**
	 * Add metadata to Manifest
	 * 
	 * @param manifest
	 */
	public void createMetadata(ManifestType manifest) {
		manifest.addNewMetadata();
	}

}

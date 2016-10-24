package com.boundlessgeo.doitt.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShapeFileCheckService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<String> fields = Arrays.asList("the_geom","SegmentID","Street","SeqNum",
			"NodeIDFrom","NodeIDTo","PhysicalID","BikeLane","BikeDir","Impediment","Notes",
			"FromStreet","ToStreet","NewRt_Y_N","BikeRtCond","TraffCond","gid");
	private String crsName = "NAD_1983_StatePlane_New_York_Long_Island_FIPS_3104_Feet";
	public void openShapeFile(String shp){
		final HashMap<String, Serializable> params = new HashMap<>(3);
		final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
		List<String>missingFields = new ArrayList<String>();
		missingFields.addAll(fields);
		List<String>extraFields = new ArrayList<String>();
		try {
			URL shpurl = new File(shp).toURI().toURL();
			params.put(ShapefileDataStoreFactory.URLP.key, shpurl);
			ShapefileDataStore dataStore = (ShapefileDataStore) factory.createDataStore(params);
			String typeName = dataStore.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source= dataStore
			        .getFeatureSource(typeName);
			SimpleFeatureType existingFeatureType = source.getSchema();
			String crs = existingFeatureType.getCoordinateReferenceSystem().getName().toString();
			if(crsName.equalsIgnoreCase(crs))
				logger.warn("Coordinate Reference System is correct");
			else
				logger.warn("Coordinate Reference System is incorrect. It is: " + crs + " but should be "+crsName);
			List<AttributeDescriptor> ads = existingFeatureType.getAttributeDescriptors();
			Iterator<AttributeDescriptor> itAD = ads.iterator();
			while(itAD.hasNext()){
				AttributeDescriptor ad = itAD.next();
				String fieldName = ad.getLocalName();
				if(!fields.contains(fieldName))
					extraFields.add(fieldName);
				if(fields.contains(fieldName))
					missingFields.remove(fieldName);
			}
			if(!extraFields.isEmpty()){
				Iterator<String>itEF = extraFields.iterator();
				while(itEF.hasNext()){
					String ef = itEF.next();
					logger.warn("Extra field found: " + ef);
				}
			}else{
				logger.warn("No extra fields found");
			}
			if(!missingFields.isEmpty()){
				Iterator<String>itMF = missingFields.iterator();
				while(itMF.hasNext()){
					String mf = itMF.next();
					logger.warn("Missing field: " + mf);
				}
			}else{
				logger.warn("All required fields found");
			}
           
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());

		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
	}

}

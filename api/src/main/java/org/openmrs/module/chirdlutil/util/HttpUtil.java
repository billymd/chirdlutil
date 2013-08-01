/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chirdlutil.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Utility class for handling HTTP calls.
 *
 * @author Steve McKee
 */
public class HttpUtil {

	/**
	 * Performs an HTTP POST.
	 * 
	 * @param url The URL for posting the data.
	 * @param postData The data to post.
	 * @param connectionTimeout The connection timeout.
	 * @param readTimeout The read timeout.
	 * @return String containing the result of the HTTP POST.
	 * @throws IOException
	 */
	public static String post(String url, String postData, int connectionTimeout, int readTimeout) throws IOException {
		URL aURL = new java.net.URL(url);
		
		// Make the connection
		HttpURLConnection aConnection = (java.net.HttpURLConnection) aURL.openConnection();
		aConnection.setConnectTimeout(connectionTimeout);
		aConnection.setReadTimeout(readTimeout);
		aConnection.setDoOutput(true);
		aConnection.setDoInput(true);
		aConnection.setRequestMethod("POST");
		aConnection.setAllowUserInteraction(false);
		
		// POST the data
		OutputStreamWriter streamToAuthorize = new java.io.OutputStreamWriter(aConnection.getOutputStream());
		streamToAuthorize.write(postData);
		streamToAuthorize.flush();
		streamToAuthorize.close();
		
		// Process the result
		InputStream resultStream = aConnection.getInputStream();
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		IOUtil.bufferedReadWrite(resultStream, responseStream);
		
		return responseStream.toString();
	}
}

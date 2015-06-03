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
package org.openmrs.module.chirdlutil.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * This class reads a csv file and reassigns mlm priorities accordingly. It expects name and
 * new_priority column. The name is the token name of the rule
 */
public class ConvertRules {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args == null || args.length < 2) {
				return;
			}
			File[] parentDirectories = new File[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				try {
					parentDirectories[i] = new File(args[i]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String outputDirectory = args[args.length - 1];
			updateMLMs(parentDirectories, outputDirectory);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Look for each mlm file listed in the csv file and update its priority
	public static void updateMLMs(File[] files, String outputDirectory) throws FileNotFoundException, IOException {
		
		//look for the mlm file
		for (File file : files) {
			
			if (!file.getAbsolutePath().equals(outputDirectory)) {
				if (file.isDirectory()) {
					updateMLMs(file.listFiles(), outputDirectory);
				} else {
					processFile(file, outputDirectory);
				}
			}
		}
		
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param file
	 */
	private static void processFile(File file, String outputPath) {
		String oldFileName = file.getPath();
		
		if (!oldFileName.endsWith("mlm")) {
			return;
		}
		String directory = file.getParentFile().getName();
		
		File outputDirectory = new File(outputPath + "/" + directory);
		
		outputDirectory.mkdirs();
		
		String newFileName = outputDirectory + "\\" + file.getName();
		
		System.out.println("Converting " + oldFileName + "...");
		Pattern p = null;
		Matcher m = null;
		boolean matches = false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(oldFileName));
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));
			String line = null;
			Boolean inLogicSection = false;
			String result = "";
			String extraVariables = "";
			
			while ((line = reader.readLine()) != null) {
				
				if (line.trim().length() == 0) {
					writer.write(line);
					writer.flush();
					continue;
				}
				
				//add value for validation
				if (line.toLowerCase().indexOf("validation:") > -1) {
					line = "Validation: testing;;";
				}
				
				//remove everything but actual date from date field
				if (line.toLowerCase().indexOf("date:") > -1) {
					int index = line.indexOf("T");
					if (index > 0) {
						line = line.substring(0, index) + ";;";
					}
				}
				
				//make sure endif has a semicolon
				if (line.toLowerCase().indexOf("endif") > -1) {
					int index = line.indexOf(";");
					if (index == -1) {
						line = line + ";";
					}
				}
				
				p = Pattern.compile("(.+)(\\|\\|\\s+)(\\w+\\s+)(\\|\\|\\s+=)(.+)");
				m = p.matcher(line);
				matches = m.matches();
				
				//convert vertical pipes to proper assignment
				if (matches) {
					
					line = m.replaceFirst("$1$3:=$5");
					line = line + "\n" + "endif;";
				}
				
				p = Pattern.compile("\\s+[Cc][Oo][Nn][Cc][Ll][Uu][Dd][Ee]\\s+");
				m = p.matcher(line);
				matches = m.find();
				
				//add endif after conclude
				if (matches) {
					
					p = Pattern.compile(".*[Ee][Ll][Ss][Ee]\\s+([Cc][Oo][Nn][Cc][Ll][Uu][Dd][Ee]\\s+.*)");
					m = p.matcher(line);
					matches = m.matches();
					
					//convert Else conclude <boolean>; endif; --> conclude <boolean>;
					if (matches) {
						
						line = m.replaceFirst("$1");
					} else {
						
						line = line + "\n" + "endif;";
					}
				}
				
				//remove age_min and move to Data section
				if (line.toLowerCase().indexOf("age_min:") > -1) {
					extraVariables += line.replaceFirst(":", ":=").replace(";;", ";") + "\n";
					line = "";
				}
				
				//remove age max and move to Data section
				if (line.toLowerCase().indexOf("age_max:") > -1) {
					extraVariables += line.replaceFirst(":", ":=").replace(";;", ";") + "\n";
					line = "";
				}
				
				//replace "is in" with "in"
				line = line.replaceAll("is in", "in");
				
				if (line.toLowerCase().indexOf("logic:") > -1) {
					inLogicSection = true;
				}
				
				if (line.toLowerCase().indexOf("action:") > -1) {
					inLogicSection = false;
				}
				
				if (inLogicSection) {
					//look for calls in the logic section
					p = Pattern.compile("(.*)([cC][aA][lL][lL]\\s+)");
					m = p.matcher(line);
					matches = m.find();
					
					//look for calls that already have a variable assignment
					Pattern p2 = Pattern.compile(".+:=\\s*[Cc][Aa][Ll][Ll]\\s+.+");
					Matcher m2 = p2.matcher(line);
					boolean matches2 = m2.find();
					
					//make sure calls have an assignment variable in the logic section
					if (matches && !matches2) {
						
						line = m.replaceFirst("temp:=$2");
					}
				}
				
				p = Pattern.compile("(\\w+\\s*=\\s*)(no)");
				m = p.matcher(line);
				matches = m.find();
				
				//make sure reserved word "no" has quotes around it
				if (matches) {
					
					line = m.replaceAll("$1\"$2\"");
				}
				
				//fix missing semicolon after Explanation
				if (line.indexOf("Explanation:") > -1) {
					line = line.replaceAll(";", "");
					line = line + ";;";
				}
				
				p = Pattern.compile("\\{(.*)\\}");
				m = p.matcher(line);
				matches = m.find();
				
				//make sure datasource has from
				if (matches) {
					
					if (!m.group(1).toLowerCase().contains("from")) {
						line = m.replaceFirst("{$1 from CHICA}");
					}
				}
				
				p = Pattern.compile("Priority:\\s*(\\w*)\\s*");
				m = p.matcher(line);
				matches = m.find();
				
				//remove blank priorities
				if (matches) {
					if (m.group(1).trim().length() == 0) {
						line = "";
					}
				}
				
				p = Pattern.compile("\\s*If.*'.*then");
				m = p.matcher(line);
				matches = m.find();
				
				//replace single quotes with double quotes in If statements
				if (matches) {
					line = line.replaceAll("'", "\"");
				}
				
				result += line + "\n";
			}
			
			//add rule type to Data section
			if (result.indexOf("PSF") > -1) {
				extraVariables += "Rule_Type:= PSF;\n";
			} else {
				
				if (result.indexOf("PWS") > -1) {
					extraVariables += "Rule_Type:= PWS;\n";
				}
			}
			
			//Add firstname to Data section, if needed
			if (result.indexOf("firstname") > -1) {
				extraVariables += "firstname:= call firstName;\n";
			}
			
			//Add Gender to Data section, if needed
			if (result.indexOf("Gender") > -1 || result.indexOf("hisher") > -1) {
				extraVariables += "Gender:= read Last {gender from person};\n";
			}
			//Add hisher to Data section, if needed
			if (result.indexOf("hisher") > -1) {
				//only add if not already there
				p = Pattern.compile("hisher\\s*:=");
				m = p.matcher(result);
				if (!m.find()) {
					extraVariables += "If (Gender = M) then hisher := \"his\";\n"+
							"endif;\n"+
							"If (Gender = F) then hisher := \"her\";\n"+
							"endif;\n";
				}
			}
			int index = result.indexOf("Data:");
			result = result.substring(0, index + 5) + "\n" + extraVariables + "\n"
			        + result.substring(index + 5, result.length());
			
			//remove empty If then statements
			p = Pattern.compile("If[^\\n\\r]+?then\\s+?endif", Pattern.DOTALL);
			m = p.matcher(result);
			while (m.find()) {
				result = m.replaceFirst("");
				m = p.matcher(result);
			}
			
			writer.write(result);
			writer.flush();
			writer.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.util;

import java.io.Serializable;

public class DocsBean implements Serializable
{
 private String docName;
 private String docOwner;
 private String docEntryDate;
 private String docLastUpdatedDate;
public DocsBean(String docName, String docOwner, String docEntryDate,
		String docLastUpdatedDate) {
	super();
	this.docName = docName;
	this.docOwner = docOwner;
	this.docEntryDate = docEntryDate;
	this.docLastUpdatedDate = docLastUpdatedDate;
}
public String toString()
{
return docName;	
}
public String getDocName() {
	return docName;
}
public void setDocName(String docName) {
	this.docName = docName;
}
public String getDocOwner() {
	return docOwner;
}
public void setDocOwner(String docOwner) {
	this.docOwner = docOwner;
}
public String getDocEntryDate() {
	return docEntryDate;
}
public void setDocEntryDate(String docEntryDate) {
	this.docEntryDate = docEntryDate;
}
public String getDocLastUpdatedDate() {
	return docLastUpdatedDate;
}
public void setDocLastUpdatedDate(String docLastUpdatedDate) {
	this.docLastUpdatedDate = docLastUpdatedDate;
}
 
}
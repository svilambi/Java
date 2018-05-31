package com.util;

import java.io.Serializable;

public class NotesBean implements Serializable
{
	private String noteSubject;
	private String noteText ;
	private String ownerData ;						
	private String noteOwner;
	private String noteEntryDate;
	private boolean isPrivate;
	public NotesBean(String noteSubject, String noteText, String ownerData,
			String noteOwner,String noteEntryDate) {
		super();
		this.noteSubject = noteSubject;
		this.noteText = noteText;
		this.ownerData = ownerData;
		this.noteOwner = noteOwner;
		this.noteEntryDate = noteEntryDate;
	}
	public String toString()
	{
		return noteEntryDate;	
	}
	public NotesBean() {
		// TODO Auto-generated constructor stub
	}
	public NotesBean(String noteSubject, String noteText, String ownerData,
			String noteOwner, String noteEntryDate, boolean isPrivate) {
		// TODO Auto-generated constructor stub		
		this.noteSubject = noteSubject;
		this.noteText = noteText;
		this.ownerData = ownerData;
		this.noteOwner = noteOwner;
		this.noteEntryDate = noteEntryDate;
		this.isPrivate = isPrivate;
	}
	public String getNoteSubject() {
		return noteSubject;
	}
	public void setNoteSubject(String noteSubject) {
		this.noteSubject = noteSubject;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	public String getOwnerData() {
		return ownerData;
	}
	public void setOwnerData(String ownerData) {
		this.ownerData = ownerData;
	}
	public String getNoteOwner() {
		return noteOwner;
	}
	public void setNoteOwner(String noteOwner) {
		this.noteOwner = noteOwner;
	}
	public String getNoteEntryDate() {
		return noteEntryDate;
	}
	public void setNoteEntryDate(String noteEntryDate) {
		this.noteEntryDate = noteEntryDate;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
}

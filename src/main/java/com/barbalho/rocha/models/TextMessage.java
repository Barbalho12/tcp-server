package com.barbalho.rocha.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "textmessage")
public class TextMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "data")
	private String data;

	@Column(name = "datetime")
	private Date datetime;

	public TextMessage() {

	}

	public TextMessage(final String data, final Date datetime) {
		this.data = data;
		this.datetime = datetime;
	}

	public TextMessage(final String data) {
		this.data = data;
		this.datetime = new Date();
	}

	public String getData() {
		return this.data;
	}

	public void setData(final String data) {
		this.data = data;
	}

	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(final Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		return "TextMessage [id=" + id + ", data=" + data + ", datetime=" + datetime + "]";
	}

}
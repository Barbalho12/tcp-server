package com.barbalho.rocha.models;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "age")
	private int age;

	@Column(name = "weight")
	private int weight;

	@Column(name = "height")
	private int height;

	@Column(name = "name_size")
	private int nameSize;

	@Column(name = "name")
	private String name;

	public User() {

	}

	public User(int age, int weight, int height, int nameSize, String name) {
		this.age = age;
		this.weight = weight;
		this.height = height;
		this.nameSize = nameSize;
		this.name = name;
	}

	public User(byte[] bytes) {
		this.age = (int) (bytes[0] & 0xff);
		this.weight = (int) (bytes[1] & 0xff);
		this.height = (int) (bytes[2] & 0xff);
		this.nameSize = (int) (bytes[3] & 0xff);
		this.name = new String(Arrays.copyOfRange(bytes, 4, this.nameSize + 4));
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[nameSize + 4];
		bytes[0] = (byte) age;
		bytes[1] = (byte) weight;
		bytes[2] = (byte) height;
		bytes[3] = (byte) nameSize;

		for (int i = 0, bi = 4; i < nameSize; i++, bi++) {
			bytes[bi] = (byte) name.charAt(i);
		}
		return bytes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNameSize() {
		return nameSize;
	}

	public void setNameSize(int nameSize) {
		this.nameSize = nameSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", age=" + age + ", weight=" + weight + ", height=" + height + ", nameSize="
				+ nameSize + ", name=" + name + "]";
	}

}
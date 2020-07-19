package com.barbalho.rocha;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.barbalho.rocha.exceptions.ProtocolException;

@DisplayName("ProtocolTest")
public class ProtocolTest {

	@DisplayName("Tests the creation of a message following the protocol")
	@Test
	public void createMessageTest() {
		byte[] textMessage = "Hello World".getBytes();
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x10, (byte) 0xA1, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F, 0x72, 0x6C,
				0x64, (byte) 0xDC, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
		
	}
	
	@DisplayName("Tests the creation of an extensive message following the protocol")
	@Test
	public void createMessageBigTest() {
		byte[] textMessage = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut quis porta risus. Nunc porttitor "
				+ "velit consequat neque convallis molestie. Sed et vivamus.").getBytes();
		
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, (byte) 0x9B, (byte) 0xA1, 
				0x4c, 0x6f, 0x72, 0x65, 0x6d, 0x20, 0x69, 0x70, 0x73, 0x75, 0x6d, 0x20, 0x64, 0x6f, 0x6c, 0x6f, 0x72, 0x20, 
				0x73, 0x69, 0x74, 0x20, 0x61, 0x6d, 0x65, 0x74, 0x2c, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x65, 0x63, 0x74, 0x65, 
				0x74, 0x75, 0x72, 0x20, 0x61, 0x64, 0x69, 0x70, 0x69, 0x73, 0x63, 0x69, 0x6e, 0x67, 0x20, 0x65, 0x6c, 0x69, 
				0x74, 0x2e, 0x20, 0x55, 0x74, 0x20, 0x71, 0x75, 0x69, 0x73, 0x20, 0x70, 0x6f, 0x72, 0x74, 0x61, 0x20, 0x72, 
				0x69, 0x73, 0x75, 0x73, 0x2e, 0x20, 0x4e, 0x75, 0x6e, 0x63, 0x20, 0x70, 0x6f, 0x72, 0x74, 0x74, 0x69, 0x74, 
				0x6f, 0x72, 0x20, 0x76, 0x65, 0x6c, 0x69, 0x74, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x65, 0x71, 0x75, 0x61, 0x74, 
				0x20, 0x6e, 0x65, 0x71, 0x75, 0x65, 0x20, 0x63, 0x6f, 0x6e, 0x76, 0x61, 0x6c, 0x6c, 0x69, 0x73, 0x20, 0x6d, 
				0x6f, 0x6c, 0x65, 0x73, 0x74, 0x69, 0x65, 0x2e, 0x20, 0x53, 0x65, 0x64, 0x20, 0x65, 0x74, 0x20, 0x76, 0x69, 
				0x76, 0x61, 0x6d, 0x75, 0x73, 0x2e,
				(byte) 0x43, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
		
	}
	
	@DisplayName("Tests the creation of a message with special characters following the protocol")
	@Test
	public void createMessageEspecialCharTest() {
		// Teste fora do escopo do projeto
		byte[] textMessage = "Cabeça de dragão".getBytes(Charset.forName("ISO-8859-1"));
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x15, (byte) 0xA1, 0x43,0x61,0x62,0x65, (byte)0xe7,0x61,0x20,0x64,0x65,0x20,0x64,0x72,0x61,0x67, (byte)0xe3,0x6f,0x1C, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
	}
	
	@DisplayName("Tests the creation of an empty message following the protocol")
	@Test
	public void createMessageEmptyTest() {
		byte[] textMessage = "".getBytes();
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x05, (byte) 0xA1, (byte) 0x2F, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
	}

	@DisplayName("Tests the CRC byte validation")
	@Test
	public void validateCRCTest() {
		byte expectedCRC = (byte) 0xC6;
		byte[] message = { 0x0A, 0x09, 0x01, 0x31, 0x32, 0x33, 0x34, expectedCRC, 0x0D };
		byte[] dataOfMessage = { 0x31, 0x32, 0x33, 0x34 };

		try {
			Protocol.validateCRC(message[1], message[2], dataOfMessage, expectedCRC);
		} catch (Exception e) {
			Assertions.fail("validateCRC fail");
		}

		byte invalidCRC = (byte) 0xC4;
		Assertions.assertThrows(ProtocolException.class, () -> {
			Protocol.validateCRC(message[1], message[2], dataOfMessage, invalidCRC);
		});

	}

	@DisplayName("Tests the INIT byte validation")
	@Test
	public void validateINITTest() {

		byte expectedINIT = 0x0A;
		try {
			Protocol.validateINIT(expectedINIT);
		} catch (Exception e) {
			Assertions.fail("validateINIT fail");
		}

		byte invalidINIT = 0x1A;
		Assertions.assertThrows(ProtocolException.class, () -> {
			Protocol.validateINIT(invalidINIT);
		});

	}

	@DisplayName("Tests the validation of the END byte")
	@Test
	public void validateENDTest() {

		byte expectedEND = 0x0D;
		try {
			Protocol.validateEND(expectedEND);
		} catch (Exception e) {
			Assertions.fail("validateEND fail");
		}

		byte invalidEND = 0x1D;
		Assertions.assertThrows(ProtocolException.class, () -> {
			Protocol.validateEND(invalidEND);
		});

	}

}

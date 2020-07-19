package com.barbalho.rocha;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.barbalho.rocha.exceptions.ProtocolException;

@DisplayName("ProtocolTest")
public class ProtocolTest {

	@DisplayName("Testa a criação de mensagem seguindo o protocolo")
	@Test
	public void createMessageTest() {
		byte[] textMessage = "Hello World".getBytes();
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x10, (byte) 0xA1, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F, 0x72, 0x6C,
				0x64, (byte) 0xDC, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
		
	}
	
	@DisplayName("Testa a criação de mensagem com caracteres especiais seguindo o protocolo")
	@Test
	public void createMessageEspecialCharTest() {
		// Teste fora do escopo do projeto
		byte[] textMessage = "Cabeça de dragão".getBytes(Charset.forName("ISO-8859-1"));
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x15, (byte) 0xA1, 0x43,0x61,0x62,0x65, (byte)0xe7,0x61,0x20,0x64,0x65,0x20,0x64,0x72,0x61,0x67, (byte)0xe3,0x6f,0x1C, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
	}
	
	@DisplayName("Testa a criação de mensagem vazia seguindo o protocolo")
	@Test
	public void createMessageEmptyTest() {
		byte[] textMessage = "".getBytes();
		byte frame = (byte) 0xA1;
		byte[] message = Protocol.createMessage(textMessage, frame);
		byte[] expectedMessage = { 0x0A, 0x05, (byte) 0xA1, (byte) 0x2F, 0x0D };
		Assertions.assertArrayEquals(expectedMessage, message);
	}

	@DisplayName("Testa a validação do byte CRC")
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

	@DisplayName("Testa a validação do byte INIT")
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

	@DisplayName("Testa a validação do byte END")
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

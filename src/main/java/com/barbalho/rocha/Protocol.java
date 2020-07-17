package com.barbalho.rocha;

public class Protocol {
    public static final int INIT = 0;
	public static final int BYTES = 1;
	public static final int FRAME = 2;
	public static final int START_DATA = 3;

	public static final byte INIT_VALUE = 0x0A;
	public static final byte END_VALUE = 0x0D;

	public static final byte TEXT_FRAME = (byte) 0xA1;
	public static final byte USER_FRAME = (byte) 0xA2;
	public static final byte TIME_FRAME = (byte) 0xA3;
    public static final byte ACK_FRAME = (byte) 0xA0;
    
    public static final byte [] ACK = {INIT_VALUE, 0x05, ACK_FRAME, 0x28, END_VALUE};
    
}
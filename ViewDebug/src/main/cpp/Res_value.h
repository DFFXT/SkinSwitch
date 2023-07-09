#pragma once
struct  Res_value
{
	enum {
		TYPE_FLOAT = 0x04,
		TYPE_DIMENSION = 0x05,
		TYPE_FRACTION = 0x06,
	};
	enum {
		COMPLEX_UNIT_SHIFT = 0,
		COMPLEX_UNIT_PX = 0,
		        // TYPE_DIMENSION: Value is Device Independent Pixels.
		        COMPLEX_UNIT_DIP = 1,
		        // TYPE_DIMENSION: Value is a Scaled device independent Pixels.
		        COMPLEX_UNIT_SP = 2,
		        // TYPE_DIMENSION: Value is in points.
		        COMPLEX_UNIT_PT = 3,
		        // TYPE_DIMENSION: Value is in inches.
		        COMPLEX_UNIT_IN = 4,
		        // TYPE_DIMENSION: Value is in millimeters.
		        COMPLEX_UNIT_MM = 5,
				COMPLEX_UNIT_FRACTION = 0,
				COMPLEX_UNIT_FRACTION_PARENT = 1,

		COMPLEX_RADIX_SHIFT = 4,
		COMPLEX_RADIX_MASK = 0x3,

		COMPLEX_RADIX_23p0 = 0,
		COMPLEX_RADIX_16p7 = 1,
		COMPLEX_RADIX_8p15 = 2,
		COMPLEX_RADIX_0p23 = 3,
		COMPLEX_MANTISSA_SHIFT = 8,
		COMPLEX_MANTISSA_MASK = 0xffffff
	};
	int data;
	int dataType;
};


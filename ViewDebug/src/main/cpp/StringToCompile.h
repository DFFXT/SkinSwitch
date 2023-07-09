#pragma once
#include <iostream>
#include <stdint.h>
#include "Res_value.h"
using namespace std;

inline int isspace16(char16_t c) {
    return (c < 0x0080 && isspace(c));
}

struct unit_entry
{
    const char* name;
    size_t len;
    uint8_t type;
    uint32_t unit;
    float scale;
};
static const unit_entry unitNames[] = {
    { "px", strlen("px"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_PX, 1.0f },
    { "dip", strlen("dip"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_DIP, 1.0f },
    { "dp", strlen("dp"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_DIP, 1.0f },
    { "sp", strlen("sp"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_SP, 1.0f },
    { "pt", strlen("pt"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_PT, 1.0f },
    { "in", strlen("in"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_IN, 1.0f },
    { "mm", strlen("mm"), Res_value::TYPE_DIMENSION, Res_value::COMPLEX_UNIT_MM, 1.0f },
    { "%", strlen("%"), Res_value::TYPE_FRACTION, Res_value::COMPLEX_UNIT_FRACTION, 1.0f / 100 },
    { "%p", strlen("%p"), Res_value::TYPE_FRACTION, Res_value::COMPLEX_UNIT_FRACTION_PARENT, 1.0f / 100 },
    { NULL, 0, 0, 0, 0 }
};


static bool parse_unit(const char* str, Res_value* outValue,
    float* outScale, const char** outEnd)
{
    const char* end = str;
    while (*end != 0 && !isspace((unsigned char)*end)) {
        end++;

    }
    const size_t len = end - str;

    const char* realEnd = end;
    while (*realEnd != 0 && isspace((unsigned char)*realEnd)) {
        realEnd++;

    }
    if (*realEnd != 0) {
        return false;

    }

    const unit_entry* cur = unitNames;
    while (cur->name) {
        if (len == cur->len && strncmp(cur->name, str, len) == 0) {
            outValue->dataType = cur->type;
            outValue->data = cur->unit << Res_value::COMPLEX_UNIT_SHIFT;
            *outScale = cur->scale;
            *outEnd = end;
            //printf("Found unit %s for %s\n", cur->name, str);
            return true;

        }
        cur++;

    }

    return false;
}



bool stringToFloat(const char * s, size_t len, Res_value* outValue)
{
    while (len > 0 && isspace16(*s)) {
        s++;
        len--;

    }

    if (len <= 0) {
        return false;

    }

    char buf[128];
    int i = 0;
    while (len > 0 && *s != 0 && i < 126) {
        if (*s > 255) {
            return false;

        }
        buf[i++] = *s++;
        len--;

    }

    if (len > 0) {
        return false;

    }
    if ((buf[0] < '0' || buf[0] > '9') && buf[0] != '.' && buf[0] != '-' && buf[0] != '+') {
        return false;

    }

    buf[i] = 0;
    const char* end;
    float f = strtof(buf, (char**)&end);

    if (*end != 0 && !isspace((unsigned char)*end)) {
        // Might be a unit...
        float scale;
        if (parse_unit(end, outValue, &scale, &end)) {
            f *= scale;
            const bool neg = f < 0;
            if (neg) f = -f;
            uint64_t bits = (uint64_t)(f * (1 << 23) + .5f);
            uint32_t radix;
            uint32_t shift;
            if ((bits & 0x7fffff) == 0) {
                // Always use 23p0 if there is no fraction, just to make
                // things easier to read.
                radix = Res_value::COMPLEX_RADIX_23p0;
                shift = 23;

            }
            else if ((bits & 0xffffffffff800000LL) == 0) {
                // Magnitude is zero -- can fit in 0 bits of precision.
                radix = Res_value::COMPLEX_RADIX_0p23;
                shift = 0;

            }
            else if ((bits & 0xffffffff80000000LL) == 0) {
                // Magnitude can fit in 8 bits of precision.
                radix = Res_value::COMPLEX_RADIX_8p15;
                shift = 8;

            }
            else if ((bits & 0xffffff8000000000LL) == 0) {
                // Magnitude can fit in 16 bits of precision.
                radix = Res_value::COMPLEX_RADIX_16p7;
                shift = 16;

            }
            else {
                // Magnitude needs entire range, so no fractional part.
                radix = Res_value::COMPLEX_RADIX_23p0;
                shift = 23;

            }
            int32_t mantissa = (int32_t)(
                (bits >> shift) & Res_value::COMPLEX_MANTISSA_MASK);
            if (neg) {
                mantissa = (-mantissa) & Res_value::COMPLEX_MANTISSA_MASK;

            }
            outValue->data |=
                (radix << Res_value::COMPLEX_RADIX_SHIFT)
                | (mantissa << Res_value::COMPLEX_MANTISSA_SHIFT);
            //printf("Input value: %f 0x%016Lx, mult: %f, radix: %d, shift: %d, final: 0x%08x\n",
            //       f * (neg ? -1 : 1), bits, f*(1<<23),
            //       radix, shift, outValue->data);
            return true;

        }
        return false;

    }

    while (*end != 0 && isspace((unsigned char)*end)) {
        end++;

    }

    if (*end == 0) {
        if (outValue) {
            outValue->dataType = outValue->TYPE_FLOAT;
            *(float*)(&outValue->data) = f;
            return true;

        }

    }

    return false;
}

/*
int main() {
    Res_value resValue;
    const char16_t* str = u"1.5dp";
    stringToFloat(str, 5, &resValue);
    std::cout << resValue.data << endl;
    return 0;
}*/

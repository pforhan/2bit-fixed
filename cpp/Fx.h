#include <cstdint>
#include <algorithm>

using intfx = int32_t;

intfx toFx(int8_t wholePart, uint8_t fractionalPart = 0) {
    return (static_cast<intfx>(wholePart) << 8) | static_cast<intfx>(fractionalPart);
}

int8_t getWholePart(intfx fixedPointNumber) {
    return static_cast<int8_t>((fixedPointNumber >> 8) & 0xFF);
}

uint8_t getFractionalPart(intfx fixedPointNumber) {
    return static_cast<uint8_t>(fixedPointNumber & 0xFF);
}

intfx addFx(intfx a, intfx b) {
    return a + b;
}

intfx subtractFx(intfx a, intfx b) {
    return a - b;
}

intfx multiplyFx(intfx a, intfx b) {
    return (a * b) >> 8;
}

intfx divideFx(intfx a, intfx b) {
    uint8_t fractionalPart = static_cast<uint8_t>(((a % b) << 8) / b) & 0xFF;
    return toFx(static_cast<int8_t>(a / b), fractionalPart);
}

intfx sqrtFx(intfx raw) {
    if (raw < 0) {
        return 0;
    }
    intfx n = (raw + 1) << 8;
    intfx guess = 256;
    for (int i = 1; i <= 15; ++i) {
        intfx newGuess = (guess + n / guess) >> 1;
        if (newGuess == guess) {
            break;
        }
        guess = newGuess;
    }
    int8_t whole = static_cast<int8_t>(guess >> 8);
    uint8_t fractional = static_cast<uint8_t>(guess % 256);
    return toFx(whole, fractional);
}

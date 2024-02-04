package twobitfixed

typealias intfx = Int

object Fx {
  @JvmStatic
  fun main(args: Array<String>) {
  }
}

fun intfx.fxToString(): String {
  val fractionalPart = getFractionalPart(this)
  val zFilled = "%03d".format((fractionalPart.toFloat() / 256 * 1000).toInt())
  return "${getWholePart(this)}:$fractionalPart(.$zFilled)"
}

fun intfx.fxToFloat(): Float {
  return getWholePart(this) + (getFractionalPart(this).toFloat() / 256)
}

fun toFx(wholePart: Byte, fractionalPart: UByte = 0u): intfx {
  // minor: can this be done without toInt?
  return wholePart.toInt() shl 8 or fractionalPart.toInt()
}

fun getWholePart(fixedPointNumber: intfx): Byte {
  return (fixedPointNumber shr 8 and 0xFF).toByte()
}

fun getFractionalPart(fixedPointNumber: intfx): UByte {
  return (fixedPointNumber and 0xFF).toUByte()
}

fun addFx(a: intfx, b: intfx): intfx = a + b

fun subtractFx(a: intfx, b: intfx): intfx = a - b

fun multiplyFx(a: intfx, b: intfx): intfx = (a * b) shr 8

fun divideFx(a: intfx, b: intfx): intfx {
  // Fractional remainder explanation, as provided by AI:
  // 1. shl 8 performs a left bit shift by 8 positions, effectively multiplying the remainder by 256.
  // 2. / b performs integer division to scale the remainder down.
  // 3. and 0xFF masks the lower 8 bits, ensuring the result is within the byte range (0-255).
  val fractionalPart = ((((a % b) shl 8) / b) and 0xFF).toUByte()
  return toFx((a / b).toByte(), fractionalPart)
}

fun sqrtFx(raw: intfx): intfx {
  // Handle negative input
  if (raw < 0) {
    // This could be a throw but 0 is better for an embedded system.
    return 0
  }

  // shift left by a byte to effectively eliminate the fractional part
  // add 1/256th in to help deal with an off-by-one error
  val n = (raw + 1) shl 8

  // Initial guess for the square root
  var guess = 256

  // Iterate using the Babylonian method to refine the guess
  // Iterate up to 15 times to find a guess
  for (i in 1..15) {
    val newGuess = (guess + n / guess) shr 1
    if (newGuess == guess) {
      break // Converged to a solution
    }
    guess = newGuess
  }

  // Extract integer and decimal parts of the approximation
  // drop the bottom 8 bits of the guess to get back to scrap the extra byte we added above
  val whole = guess shr 8
  // Grab the remainder for the decimal value.
  val fractional = guess % 256

  // println("sqrt of ${raw.fxToString()} = whol $whole frac $fractional")

  return toFx(whole.toByte(), fractional.toUByte())
}

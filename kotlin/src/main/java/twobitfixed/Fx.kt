package twobitfixed

import kotlin.math.abs

typealias intfx = Int

object Fx {
  @JvmStatic
  fun main(args: Array<String>) {
  }
}

fun intfx.fxToString(): String {
  val fractionalPart = getFractionalPart(this)
  return "${getWholePart(this)}:$fractionalPart(.${(fractionalPart.toFloat() / 256 * 1000).toUInt()})"
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

fun addFx(a: intfx, b: intfx): intfx {
  return a + b
}

fun subtractFx(a: intfx, b: intfx): intfx {
  return a - b
}

fun multiplyFx(a: intfx, b: intfx): intfx {
  return ((a * b) shr 8).toInt()
  // val wholeA = getWholePart(a)
  // val fracA = getFractionalPart(a)
  // val wholeB = getWholePart(b)
  // val fracB = getFractionalPart(b)
  // println("wA $wholeA fA $fracA wB $wholeB fB $fracB")
  //
  // // Sign-extend whole parts to 32 bits for accurate multiplication
  // val signedWholeA = wholeA.toLong()
  // val signedWholeB = wholeB.toLong()
  //
  // // Perform multiplications with intermediate results using 64-bit integers
  // val product1 = signedWholeA * signedWholeB
  // val product2 = multiplyWholeAndFraction(wholeA, fracB)
  // val product3 = multiplyWholeAndFraction(wholeB, fracA)
  // val product4 = multiplyFractions(fracA, fracB)
  //
  // val combined = (product1 shl 8) + product2 + product3 + product4
  // println("p1 $product1 p2 $product2 p3 $product3 p4 $product4 comb $combined altcomb $altcomb")
}

// fun multiplyWholeAndFraction(whole: Byte, frac: UByte): intfx =
//   whole * frac.toShort()
//
// fun multiplyFractions(fracA: UByte, fracB: UByte): intfx {
//   // Convert bytes to unsigned integers for accurate multiplication
//   val a = fracA.toShort()
//   val b = fracB.toShort()
//
//   return (a * b shr 8).toInt()
// }

fun divideFx(a: intfx, b: intfx): intfx {
  // println("div a $a b $b intDiv ${a/b} intRem ${a%b}")

  // Fractional remainder explanation, as provided by AI:
  // 1. shl 8 performs a left bit shift by 8 positions, effectively multiplying the remainder by 256.
  // 2. / b performs integer division to scale the remainder down.
  // 3. and 0xFF masks the lower 8 bits, ensuring the result is within the byte range (0-255).
  val fractionalPart = ((((a % b) shl 8) / b) and 0xFF).toUByte()

  return toFx((a / b).toByte(), fractionalPart)
}

fun sqrtFx(n: intfx): intfx {
  // Handle negative input
  if (n < 0) {
    throw IllegalArgumentException("Square root of negative numbers is not defined")
  }

  // Initial guess for the square root
  var guess = 1

  // Iterate using the Babylonian method to refine the guess
  for (i in 1..15) {
    val newGuess = (guess + n / guess) shr 1
    // println(" sqrt raw n $n guess $guess newG $newGuess")
    if (newGuess == guess) {
      break // Converged to a solution
    }
    guess = newGuess
  }

  // guess at this point is for the whole value, and is somehow 16 times the expected value...
  // it's not wrong but it isn't what we're looking for.

  // Extract integer and decimal parts of the approximation
  val quotient = guess shr 4 // TODO why is div by 16 needed?
  // Grab the remainder for the decimal value.
  val remainder = guess % 16
  val decimalApproximation = remainder shl 4 // TODO this means we can only hit 1/16 accuracy

  // ai orig: val decimalApproximation = ((n - guess * guess) shl 8) / guess and 0xFF

  // println("sqrt ${n.fxToString()} quo $quotient frac $decimalApproximation")
  return toFx(quotient.toByte(), decimalApproximation.toUByte())
}

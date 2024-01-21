package twobitfixed

import kotlin.math.abs

typealias intfx = Int

object TestBed {

  @JvmStatic
  fun main(args: Array<String>) {
    // TODO remove

    val testOneHalf = toFx(1, 128u)
    val testTwoQuarter = toFx(2, 64u)
    val testOne = toFx(1)
    val testHalf = toFx(0, 128u)
    val testTwo = toFx(2)
    val testThree = toFx(3)
    val testThreeThreeQuarter = toFx(3, 192u)
    val testFour = toFx(4)
    val testFourHalf = toFx(4, 128u)
    val testSix = toFx(6)
    val testNine = toFx(9)

    expect(25, subtractFx(testOneHalf, testHalf), testOne, "1.5 - .5")
    expect(26, subtractFx(testOneHalf, testHalf), testOne,"1.5 - .5")
    expect(27, addFx(testOneHalf, testHalf), testTwo,"1.5 + .5")
    expect(28, addFx(testOneHalf, testOneHalf), testThree,"1.5 + 1.5")
    expect(29, addFx(testTwoQuarter, testTwoQuarter), testFourHalf,"2.25 + 2.25")
    expect(30, addFx(testTwoQuarter, testOneHalf), testThreeThreeQuarter,"2.25 + 1.5")
    expect(31, multiplyFx(testOne, testOne), testOne,"1 x 1")
    expect(32, multiplyFx(testOneHalf, testOneHalf), testTwoQuarter,"1.5 x 1.5")
    expect(33, multiplyFx(testTwo, testTwo), testFour,"2 x 2")
    expect(34, multiplyFx(testTwo, testThree), testSix,"2 x 3")
    expect(35, multiplyFx(testThree, testThree), testNine,"3 x 3")
    if (true) return
    expect(36, divideFx(testOneHalf, testOne), testOneHalf,"1.5 / 1")
    expect(37, divideFx(testFour, testTwo), testTwo,"4 / 2")
    expect(38, sqrtFx(testNine), testThree,"sqrt 9")
    expect(39, sqrtFx(testFour), testTwo,"sqrt 4")
    println(toString(sqrtFx(testTwo)))
  }

  fun expect(line: Int, calculated: intfx, expected: intfx, message: String = "") {
    if (calculated != expected) println("$line: $message, -- expected ${toString(expected)} but was ${toString(calculated)}")
  }

  fun toString(value: intfx): String {
    return "${getWholePart(value)}:${(getFractionalPart(value).toFloat() / 256 * 1000).toUInt()}"
  }

  fun toFx(wholePart: Byte, fractionalPart: UByte): intfx {
    // minor: can this be done without toInt?
    return (wholePart.toInt() shl 8) or fractionalPart.toInt()
  }

  fun toFx(wholePart: Byte): intfx {
    return wholePart.toInt() shl 8
  }

  fun getWholePart(fixedPointNumber: intfx): Byte {
    return (fixedPointNumber shr 8 and 0xFF).toByte()
  }

  fun getFractionalPart(fixedPointNumber: intfx): UByte {
    return (fixedPointNumber and 0xFF).toUByte()
  }

  fun addFx(a: intfx, b: intfx): intfx {
    return a + b // (a shl 8) or b
  }

  fun subtractFx(a: intfx, b: intfx): intfx {
    return a - b
  }

  fun multiplyFx(a: intfx, b: intfx): intfx {
    val wholeA = getWholePart(a)
    val fracA = getFractionalPart(a)
    val wholeB = getWholePart(b)
    val fracB = getFractionalPart(b)
    println("wA $wholeA fA $fracA wB $wholeB fB $fracB")

    // Sign-extend whole parts to 32 bits for accurate multiplication
    val signedWholeA = wholeA.toLong()
    val signedWholeB = wholeB.toLong()

    // Perform multiplications with intermediate results using 64-bit integers
    val product1 = signedWholeA * signedWholeB
    val product2 = signedWholeA * fracB.toShort()
    val product3 = fracA.toShort() * signedWholeB
    val product4 = fracA * fracB

    // Combine products, shift, and mask to extract whole and fractional parts
    val combined = (product1 shl 32) + (product2 shl 16) + (product3 shl 16) + product4.toLong()
    val wholeResult = (combined shr 32).toShort()
    val fracResult = (combined shr 16).toUShort()

    println("p1 $product1 p2 $product2 p3 $product3 p4 $product4 comb $combined WR $wholeResult FR $fracResult")

    return toFx(wholeResult.toByte(), fracResult.toUByte())
    // still-broken version:
    // val wholeResult = wholeA * wholeB
    // // make use of the fractional parts; frac1 and frac2 may be larger than 1
    // val frac1 = wholeA * fracB
    // val frac2 = wholeB * fracA
    // // TODO probably need to adjust this value as in floating math it would shrink
    // val frac3 = fracA * fracB
    //
    // val product = combineProducts(wholeResult, frac2, frac3, frac1)
    // println("${toString(a)} * ${toString(b)} = $product or ${toString(product)}")
    // return product
    // end still-broken
  }

  // fun combineProducts(wholeProduct: Int, fractionalProduct1: Int, fractionalProduct2: Int fractionalProduct3: Int): intfx {
  //   val combinedWholeProduct = wholeProduct1 + wholeProduct2
  //   val combinedFractionalProduct = fractionalProduct1 + fractionalProduct2
  //
  //   val wholePart = combinedWholeProduct shr 8 // Shift right by 8 bits to get the whole part
  //   val fractionalPart = combinedFractionalProduct and 0xFF // Mask off the whole part and extract the fractional part
  //
  //   println("CWP $combinedWholeProduct CFP $combinedFractionalProduct WP $wholePart FP $fractionalPart WP or FP ${wholePart or fractionalPart} ")
  //   return wholePart or fractionalPart // Combine the whole and fractional parts into a short
  // }

  fun multiplyWholeAnd(whole: Byte, frac: UByte): Short {
    // Multiply as integers, preserving precision
    val product = whole * frac.toShort()

    // Extract whole and fractional parts using shifts
    val wholeResult = (product shr 8).toByte()  // Shift right 8 bits for division by 256
    val fracResult = (product and 0xFF).toUByte()  // Mask lower 8 bits for fractional part

    // Round fractional part if necessary
    if (fracResult >= 128) {
      wholeResult++
      fracResult = 0
    }

    return Pair(wholeResult, fracResult)
  }

  fun divideFx(a: intfx, b: intfx): intfx {
    if (b == 0) { // handle zero divisor case
      return 0 // INT_MIN // return minimum value for signed integers
    }
    val wholeA = getWholePart(a).toInt()
    val fracA = getFractionalPart(a).toByte()
    val wholeB = getWholePart(b).toInt()
    val fracB = getFractionalPart(b).toByte()
    val wholeResult = (wholeA shl 8) / wholeB
    val fracResult =
      (((fracA.toInt() shl 8) / wholeB) + (((fracA.toInt() shl 8) % wholeB) / wholeB) + (((wholeA % wholeB) * 256) / wholeB)).toByte()
    return (wholeResult shl 8) or fracResult.toInt()
  }
  // fun divideFx(a: intfx, b: intfx): intfx {
  //   // if dividing by 1 just return a
  //   if (b == 1 shl 8) return a;
  //
  //   // Divide the integer parts
  //   val wholePart = getWholePart(a) / getWholePart(b)
  //
  //   // Check for division by zero
  //   val fractional2 = getFractionalPart(b).toByte()
  //   if (fractional2.toInt() == 0) {
  //     return 0
  //   }
  //
  //   // Divide the fractional parts
  //   val fractionalPart: Int = (getFractionalPart(a).toInt() shl 8) / fractional2
  //
  //   // Combine the integer and fractional parts into a fixed-point number
  //   return toFx(wholePart.toByte(), fractionalPart.toUByte())
  //   // if (b == 0) { // handle zero divisor case
  //   //   return Int.MIN_VALUE // return minimum value for signed integers
  //   // }
  //   // return (a shr 8) / b // use right shift and division operators
  // }

  fun sqrtFx(x: intfx): intfx {

    // Extract the integer part of the fixed-point number
    val wholePart = getWholePart(x).toInt()

    // Extract the fractional part of the fixed-point number
    val fractionalPart = getFractionalPart(x).toInt()

    // Initialize the guess for the square root
    var guess = toFx(wholePart.toByte(), 0u)

    // Iterate until the guess is close enough to the actual square root
    for (i in 0..9) {
      // Calculate the square of the guess
      val square = multiplyFx(guess, guess)

      // Check if the square is close enough to the fixed-point number
      if (abs(subtractFx(square, x)) < 1) {
        break
      }

      // Refine the guess by averaging the guess and the quotient of the
      // fixed-point number divided by the guess
      if (guess == 0) continue
      guess = divideFx(addFx(guess, x), guess)
    }

    return guess

    // if (x == Int.MIN_VALUE) { // handle negative or zero input case
    //   return Int.MAX_VALUE // return maximum value for signed integers
    // }
    // val fx = x shr 8 // extract the integer part of x
    // val fs = x and 0xFF // extract the fractional part of x
    // if (fs == fs shr 8) { // check if fs is zero or one bit long
    //   return fx // no need to compute square root, just return integer part of x
    // }
    //
    // // TODO: compute square root of x
    // return 0
  }

}
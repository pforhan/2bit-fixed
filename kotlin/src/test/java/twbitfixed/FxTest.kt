package twbitfixed

import org.junit.Assert.assertEquals
import org.junit.Test
import twobitfixed.addFx
import twobitfixed.divideFx
import twobitfixed.fxToString
import twobitfixed.getFractionalPart
import twobitfixed.getWholePart
import twobitfixed.intfx
import twobitfixed.multiplyFx
import twobitfixed.sqrtFx
import twobitfixed.subtractFx
import twobitfixed.toFx

class FxTest {

  val valTwo256ths = toFx(0, 2u)
  val val240_256ths = toFx(0, 240u)
  val valHalf = toFx(0, 128u)
  val valOne = toFx(1)
  val valOneAndHalf = toFx(1, 128u)
  val valTwo = toFx(2)
  val valTwoAndQuarter = toFx(2, 64u)
  val valThree = toFx(3)
  val valThreeAndThreeQuarter = toFx(3, 192u)
  val valFour = toFx(4)
  val valFourAndHalf = toFx(4, 128u)
  val valSix = toFx(6)
  val valNine = toFx(9)
  val val120 = toFx(120)

  @Test
  fun test_toFx() {
    assertEquals("1 should be 256", 256, valOne)
    assertEquals("17 should be something", toFx(17), 17 shl 8)
    assertEquals("1.25 should be something", 1 shl 8 or 64, toFx(1, 64u))
    assertEquals("10.25 should be something", 10 shl 8 or 64, toFx(10, 64u))
    // todo should have some negatives in here
  }

  @Test
  fun test_getWholePart() {
    wholeRoundTrip(127)
    wholeRoundTrip(0)
    wholeRoundTrip(1)
    wholeRoundTrip(-17)
    wholeRoundTrip(-1)
    wholeRoundTrip(-128)
  }

  private fun wholeRoundTrip(value: Byte) {
    assertEquals("whole round trip", getWholePart(toFx(value)), value)
    assertEquals("whole round trip", getWholePart(toFx(value, 1u)), value)
    assertEquals("whole round trip", getWholePart(toFx(value, 133u)), value)
    assertEquals("whole round trip", getWholePart(toFx(value, 255u)), value)
  }

  @Test
  fun test_getFractionalPart() {
    fracRoundTrip(0u)
    fracRoundTrip(1u)
    fracRoundTrip(55u)
    fracRoundTrip(127u)
    fracRoundTrip(255u)
  }

  private fun fracRoundTrip(value: UByte) {
    assertEquals("frac round trip", getFractionalPart(toFx(0, value)), value)
    assertEquals("frac round trip", getFractionalPart(toFx(1, value)), value)
    assertEquals("frac round trip", getFractionalPart(toFx(127, value)), value)
    assertEquals("frac round trip", getFractionalPart(toFx(-1, value)), value)
    assertEquals("frac round trip", getFractionalPart(toFx(-128, value)), value)
  }

  @Test
  fun test_addFx() {
    expect(addFx(valOneAndHalf, valHalf), valTwo, "1.5 + .5")
    expect(addFx(valOneAndHalf, valOneAndHalf), valThree, "1.5 + 1.5")
    expect(addFx(valTwoAndQuarter, valTwoAndQuarter), valFourAndHalf, "2.25 + 2.25")
    expect(addFx(valTwoAndQuarter, valOneAndHalf), valThreeAndThreeQuarter, "2.25 + 1.5")
  }

  @Test
  fun test_subtractFx() {
    expect(subtractFx(valOneAndHalf, valHalf), valOne, "1.5 - .5")
    expect(subtractFx(valOneAndHalf, valHalf), valOne, "1.5 - .5")
  }

  @Test
  fun test_multiplyFx() {
    expect(multiplyFx(valOne, valOne), valOne, "1 x 1")
    expect(multiplyFx(valOneAndHalf, valOneAndHalf), valTwoAndQuarter, "1.5 x 1.5")
    expect(multiplyFx(valTwo, valTwo), valFour, "2 x 2")
    expect(multiplyFx(valTwo, valThree), valSix, "2 x 3")
    expect(multiplyFx(valThree, valThree), valNine, "3 x 3")
    expect(multiplyFx(val120, valTwo256ths), val240_256ths, "120 x 2/256")
  }

  // @Test
  // fun test_multiplyWholeAndFraction() {
  //   // divide by 2:
  //   assertEquals("", toFx(0, 128u), multiplyWholeAndFraction(1, 128u))
  //   assertEquals("", toFx(1), multiplyWholeAndFraction(2, 128u))
  //   assertEquals("", toFx(2), multiplyWholeAndFraction(4, 128u))
  //   assertEquals("", toFx(8), multiplyWholeAndFraction(16, 128u))
  //   // divide by 4:
  //   assertEquals("", toFx(0, 64u), multiplyWholeAndFraction(1, 64u))
  //   assertEquals("", toFx(0, 128u), multiplyWholeAndFraction(2, 64u))
  //   assertEquals("", toFx(1), multiplyWholeAndFraction(4, 64u))
  //   assertEquals("", toFx(4), multiplyWholeAndFraction(16, 64u))
  //
  //   // Divide by about 7:
  //   assertEquals("", toFx(4, 236u), multiplyWholeAndFraction(35, 36u))
  // }
  //
  // @Test
  // fun test_multiplyFractions() {
  //   assertEquals(toFx(0, 18u), multiplyFractions(19u, 255u))
  //   assertEquals(toFx(0, 64u), multiplyFractions(128u, 128u))
  //   assertEquals(toFx(0, 125u), multiplyFractions(250u, 128u))
  //   assertEquals(toFx(0, 254u), multiplyFractions(255u, 255u))
  // }

  @Test
  fun test_divideFx() {
    // Divide by ones:
    expect(divideFx(valOne, valOne), valOne, "1 / 1")
    expect(divideFx(valOneAndHalf, valOne), valOneAndHalf, "1.5 / 1")
    expect(divideFx(valTwo, valOne), valTwo, "2 / 1")
    expect(divideFx(valFour, valOne), valFour, "4 / 1")
    expect(divideFx(val120, valOne), val120, "120 / 1")

    // others
    expect(divideFx(valThree, valTwo), valOneAndHalf, "3 / 2")
    expect(divideFx(val120, valFour), toFx(30), "120 / 4")
    expect(divideFx(val120, valNine), toFx(13, 85u), "120 / 9 should be 13:85")
    expect(divideFx(val240_256ths, valTwo256ths), val120, "240/256 / 2/256")
    expect(divideFx(valOne, valTwo), valHalf, "1 / 2")
    expect(divideFx(valFour, valTwo), valTwo, "4 / 2")
  }

  @Test
  fun test_sqrtFx() {
    // Try all possible positive values
    for (i in 1..127) {
      for (fr in 0u..255u) {
        sqrtRoundtrip(toFx(i.toByte(), fr.toUByte()))
      }
    }
  }

  private fun sqrtRoundtrip(startingValue: intfx) {
    val square = multiplyFx(startingValue, startingValue)
    val calculated = sqrtFx(square)
    val message = "sqrt of ${startingValue.fxToString()}^2 =[${square.fxToString()}]"
    expect(calculated, startingValue, message)
  }

  private fun expect(calculated: intfx, expected: intfx, message: String) =
    assertEquals(
      "$message, -- expected ${expected.fxToString()} but was ${calculated.fxToString()}",
      expected,
      calculated,
    )
}

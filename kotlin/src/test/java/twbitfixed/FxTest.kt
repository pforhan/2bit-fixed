package twbitfixed

import org.junit.Assert.assertEquals
import org.junit.Test
import twobitfixed.addFx
import twobitfixed.divideFx
import twobitfixed.fxToString
import twobitfixed.intfx
import twobitfixed.multiplyFx
import twobitfixed.sqrtFx
import twobitfixed.subtractFx
import twobitfixed.toFx

class FxTest {

  val valHalf = toFx(0, 128u)
  val valOne = toFx(1)
  val valOneHalf = toFx(1, 128u)
  val valTwo = toFx(2)
  val valTwoQuarter = toFx(2, 64u)
  val valThree = toFx(3)
  val valThreeThreeQuarter = toFx(3, 192u)
  val valFour = toFx(4)
  val valFourHalf = toFx(4, 128u)
  val valSix = toFx(6)
  val valNine = toFx(9)

  @Test
  fun test_toFx() {
    assertEquals("1 should be 256", toFx(1), 256)
    assertEquals("17 should be something", toFx(17), 17 shl 8)
    assertEquals("10.25 should be something", toFx(10, 64u), 17 shl 8 and 64)
  }

  @Test
  fun test_getWholePart() {
  }

  @Test
  fun test_getFractionalPart() {
  }

  @Test
  fun test_addFx() {
    expect(addFx(valOneHalf, valHalf), valTwo, "1.5 + .5")
    expect(addFx(valOneHalf, valOneHalf), valThree, "1.5 + 1.5")
    expect(addFx(valTwoQuarter, valTwoQuarter), valFourHalf, "2.25 + 2.25")
    expect(addFx(valTwoQuarter, valOneHalf), valThreeThreeQuarter, "2.25 + 1.5")
  }

  @Test
  fun test_subtractFx() {
    expect(subtractFx(valOneHalf, valHalf), valOne, "1.5 - .5")
    expect(subtractFx(valOneHalf, valHalf), valOne, "1.5 - .5")
  }

  @Test
  fun test_multiplyFx() {
    expect(multiplyFx(valOne, valOne), valOne, "1 x 1")
    expect(multiplyFx(valOneHalf, valOneHalf), valTwoQuarter, "1.5 x 1.5")
    expect(multiplyFx(valTwo, valTwo), valFour, "2 x 2")
    expect(multiplyFx(valTwo, valThree), valSix, "2 x 3")
    expect(multiplyFx(valThree, valThree), valNine, "3 x 3")
  }

  @Test
  fun test_multiplyWholeAndFraction() {
  }

  @Test
  fun test_divideFx() {
    expect(divideFx(valOneHalf, valOne), valOneHalf, "1.5 / 1")
    expect(divideFx(valFour, valTwo), valTwo, "4 / 2")
  }

  @Test
  fun test_sqrtFx() {
    expect(sqrtFx(valNine), valThree, "sqrt 9")
    expect(sqrtFx(valFour), valTwo, "sqrt 4")
    println(sqrtFx(valTwo).fxToString())
  }

  private fun expect(calculated: intfx, expected: intfx, message: String) {
    // TODO can probably use straight up junit here -- maybe using fxToString and string compare?
    assertEquals(
      "$message, -- expected ${expected.fxToString()} but was ${calculated.fxToString()}",
      expected,
      calculated
    )
  }
}
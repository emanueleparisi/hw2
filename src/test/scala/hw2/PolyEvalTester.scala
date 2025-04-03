package hw2

import scala.math._

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class PolyEvalTester extends AnyFlatSpec with ChiselScalatestTester {
  val width = 8
  def testPolyEvalOut(n: Int): Unit = {
    val coefs = Seq.fill(n)(1)
    test(new PolyEval(coefs, width)) { dut =>
      def f(x: BigInt): BigInt = (x.pow(coefs.length) - 1) / (x - 1)

      dut.io.x.poke(2.U)
      dut.io.out.expect(f(2).U)

      dut.io.x.poke(5.U)
      dut.io.out.expect(f(5).U)

      dut.io.x.poke(13.U)
      dut.io.out.expect(f(13).U)
    }
  }

  behavior of "PolyEval"
  it should "correctly calculate output for deg(2) poly" in {
    testPolyEvalOut(3)
  }

  it should "correctly calculate output for deg(3) poly" in {
    testPolyEvalOut(4)
  }

  it should "correctly calculate output for deg(4) poly" in {
    testPolyEvalOut(5)
  }

  it should "correctly calculate output for deg(5) poly" in {
    testPolyEvalOut(6)
  }
}

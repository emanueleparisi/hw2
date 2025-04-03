package hw2

import chisel3._


/**
  * 
  * @param coefs : in ascending exponent order -> Seq(1, 2, 3) == 1 + 2x + 3x^2
  * @param width : the width of x
  */
class PolyEval(coefs: Seq[Int], width: Int) extends Module {
  require (coefs.length > 0, "coefs must be non-empty")

  def helper(c: Seq[Int]): UInt = {
    if (c.isEmpty) {
      0.U
    } else {
      c.head.U + io.x * helper(c.drop(1))
    }
  }
  
  val io = IO(new Bundle {
    val x      = Input(UInt(width.W))
    val out    = Output(UInt())
  })

  io.out := helper(coefs)
}

package hw2

import chisel3._
import chisel3.util.log2Ceil


class SineWave(val period: Int, val amplitude: Int) {
  require(period > 0)
  val B: Double = (2.0 * math.Pi) / period.toDouble

  def apply(index: Int): Int = (amplitude.toDouble * math.sin(B * index)).toInt
}


/**
  *
  * @param s : SineWave (internally contains period & amplitude)
  * ________________________________
  * @field stride:  UInt      (Input)
  * @field en:      Bool      (Input)
  * @field out:     SInt      (Output)
  */
class SineWaveGenIO (sw: SineWave) extends Bundle {
  val stride = Input(UInt(log2Ceil(sw.period).W))
  val en = Input(Bool())
  val out = Output(SInt(log2Ceil(2*sw.amplitude+1).W))
}


/**
  * 
  * @param s : SineWave (internally contains period)
  */
class SineWaveGen(sw: SineWave) extends Module {
  val io = IO(new SineWaveGenIO(sw))

  val lut = VecInit(Seq.tabulate(sw.period)(i => sw(i).S))
  val cnt = RegInit(0.U(log2Ceil(sw.period).W))
  val nxt = Wire(UInt(log2Ceil(sw.period).W))

  nxt := cnt + io.stride
  when(io.en) {
    cnt := nxt
    when(nxt >= sw.period.U) {
      cnt := nxt - sw.period.U
    }
  }
  io.out := lut(cnt)
}


object Main extends App {
  println(getVerilogString(new SineWaveGen(new SineWave(3, 128))))
}
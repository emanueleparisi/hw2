package hw2

import chisel3._
import chisel3.util._


object CipherState extends ChiselEnum {
  val empty, ready, encrypted, decrypted = Value
}

class XORCipherCmds extends Bundle {
  val clear      = Input(Bool())
  val loadKey    = Input(Bool())
  val loadAndEnc = Input(Bool())
  val decrypt    = Input(Bool())
}


/**
  * @param width :    Int
  * @field in:        UInt           (Input) - payload or key
  * @field cmds:      XORCipherCmds  (Input)
  * @field out:       UInt           (Output)
  * @field full:      Bool           (Output)
  * @field encrypted: Bool           (Output)
  * @field state:     CipherState    (Output) - visible for testing
  */
class XORCipherIO(width: Int) extends Bundle {
  val in = Input(UInt(width.W))
  val cmds = new XORCipherCmds()
  val out = Output(UInt(width.W))
  val full = Output(Bool())
  val encrypted = Output(Bool())
  val state = Output(CipherState())
}


/**
  * @param width Int
  */
class XORCipher(width: Int) extends Module {
  val io = IO(new XORCipherIO(width))

  val data = RegInit(0.U(width.W))
  val key = RegInit(0.U(width.W))
  val state = RegInit(CipherState.empty)

  io.full := state =/= CipherState.empty
  io.encrypted := state === CipherState.encrypted

  when(io.cmds.clear) {
    data := 0.U
    key := 0.U
  } .elsewhen(io.cmds.loadKey) {
    key := io.in
  } .elsewhen(io.cmds.loadAndEnc) {
    data := io.in ^ key
  } .elsewhen(io.cmds.decrypt) {
    data := io.in ^ key
  }
  switch(state) {
    is(CipherState.empty) {
      when(io.cmds.loadKey) {
        state := CipherState.ready
      }
    }
    is(CipherState.ready) {
      when(io.cmds.clear) {
        state := CipherState.empty
      } .elsewhen(io.cmds.loadAndEnc) {
        state := CipherState.encrypted
      } .elsewhen(io.cmds.decrypt) {
        state := CipherState.ready
      }
    }
    is(CipherState.encrypted) {
      when(io.cmds.clear) {
        state := CipherState.empty
      } .elsewhen(io.cmds.decrypt) {
        state := CipherState.decrypted
      }
    }
    is(CipherState.decrypted) {
      when(io.cmds.clear) {
        state := CipherState.empty
      } .elsewhen(io.cmds.loadAndEnc) {
        state := CipherState.ready
      }
    }
  }
  io.out := data
  io.state := state
}

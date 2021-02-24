package com.lucianomolinari.http4splayground.io_book.chap3

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object DebugExample extends IOApp {

  val hello = IO("hello").debug
  val world = IO("world").debug

  // val seq = (hello, world).mapN((h, w) => s"$h $w").debug
  val par = (hello, world).parMapN((h, w) => s"$h $w").debug

  def run(args: List[String]): IO[ExitCode] = {
    par.as(ExitCode.Success)
  }
}

package com.lucianomolinari.http4splayground.io_book.chap3

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object ParMapNErrors extends IOApp {

  val ok  = IO("hi").debug
  val ko1 = IO.raiseError[String](new RuntimeException("oh!")).debug
  val ko2 = IO.raiseError[String](new RuntimeException("noes!")).debug

  val e1 = (ok, ko1).parTupled.void //.parMapN((_, _) => ())
  val e2 = (ko1, ok).parTupled.void //.parMapN((_, _) => ())
  val e3 = (ko1, ko2).parTupled.void //.parMapN((_, _) => ())

  def run(args: List[String]): IO[ExitCode] = {
    e1.attempt.debug *>
      IO("----").debug *>
      e2.attempt.debug *>
      IO("----").debug *>
      e3.attempt.debug *>
      IO(ExitCode.Success)
  }

}

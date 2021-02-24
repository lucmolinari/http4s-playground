package com.lucianomolinari.http4splayground.io_book.chap4

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object Start extends IOApp {

  val task = IO("task").debug

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- task.start
      _ <- IO("task was started").debug
    } yield ExitCode.Success

  }

}

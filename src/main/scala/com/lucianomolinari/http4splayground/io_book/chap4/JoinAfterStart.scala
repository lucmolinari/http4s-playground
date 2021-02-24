package com.lucianomolinari.http4splayground.io_book.chap4

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._
import scala.concurrent.duration._

object JoinAfterStart extends IOApp {

  val task = IO.sleep(2.seconds) *> IO("task").debug

  def run(args: List[String]): IO[ExitCode] =
    for {
      fiber <- task.start
      _     <- IO("pre-join").debug
      _     <- fiber.join.debug
      _     <- IO("post-join").debug
    } yield ExitCode.Success

}

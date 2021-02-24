package com.lucianomolinari.http4splayground.io_book.chap4

import cats.effect.IOApp
import cats.effect.IO
import cats.effect.ExitCode

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.implicits._
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._
import scala.concurrent.duration._

object Cancel extends IOApp {

  val task = IO("task").debug *> IO.never

  def run(args: List[String]): IO[ExitCode] =
    for {
      fiber <- task.onCancel(IO("i was cancelled").debug.void).start
      _     <- IO("pre-cancel").debug
      // _ <- fiber.join
      _     <- fiber.cancel
      _     <- IO("canceled").debug
    } yield ExitCode.Success

}

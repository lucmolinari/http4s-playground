package com.lucianomolinari.http4splayground.io_book.chap5

import cats.effect.{ExitCode, IO, IOApp, _}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object Shifting extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO("one").debug
      _ <- IO.shift
      _ <- IO("two").debug
      _ <- IO.shift
      _ <- IO("three").debug
    } yield ExitCode.Success

}

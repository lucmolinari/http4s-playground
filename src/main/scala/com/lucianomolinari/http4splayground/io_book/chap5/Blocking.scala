package com.lucianomolinari.http4splayground.io_book.chap5

import cats.effect.{ExitCode, IO, IOApp, _}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object Blocking extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      withBlocker(blocker).as(ExitCode.Success)
    }

  def withBlocker(blocker: Blocker): IO[Unit] =
    for {
      _ <- IO("on default").debug
      _ <- blocker.blockOn(IO("on blocker").debug)
      _ <- IO("where am I?").debug
    } yield ()

}

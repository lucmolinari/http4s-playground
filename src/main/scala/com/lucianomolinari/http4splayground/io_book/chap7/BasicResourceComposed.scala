package com.lucianomolinari.http4splayground.io_book.chap7

import cats.effect._
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

import scala.concurrent.duration._

object BasicResourceComposed extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (stringResource, intResource).tupled
      .use { case (s, i) =>
        IO(s"$s is so cool").debug *> IO(s"$i is also cool").debug
      }
      .as(ExitCode.Success)

  val stringResource: Resource[IO, String] =
    Resource.make(
      IO("> acquiring stringResource").debug *> IO("String")
    )(_ => IO("< releaseing stringResource").debug.void)

  val intResource: Resource[IO, Int] =
    Resource.make(
      IO("> acquiring intResource").debug *> IO(99)
    )(_ => IO("< releaseing intResource").debug.void)

}

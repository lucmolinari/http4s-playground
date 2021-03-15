package com.lucianomolinari.http4splayground.io_book.chap7

import cats.effect._
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object BasicResource extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    stringResource
      .use(_ => IO.raiseError(new RuntimeException("oh noes!")))
      .attempt
      .debug
      // .use { s =>
      //   IO(s"$s is so cool!").debug
      // }
      .as(ExitCode.Success)

  val stringResource: Resource[IO, String] =
    Resource.make(
      IO("> acquiring stringResource").debug *> IO("String")
    )(_ => IO("< releaseing stringResource").debug.void)

}

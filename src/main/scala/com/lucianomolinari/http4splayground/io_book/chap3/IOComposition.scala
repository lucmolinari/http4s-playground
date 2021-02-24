package com.lucianomolinari.http4splayground.io_book.chap3

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object IOComposition extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val hello = IO(println(s"${Thread.currentThread().getName()} Hello"))
    val world = IO(println(s"${Thread.currentThread().getName()} World"))

    val hw1 = for {
      _ <- hello
      _ <- world
    } yield ()

    val hw2 = (hello, world).mapN((_, _) => ())

    hw1 >> hw2 >> IO(ExitCode.Success)
  }

}

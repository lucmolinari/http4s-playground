package com.lucianomolinari.http4splayground.io_book.chap4

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object MyParMapN extends IOApp {

  def run(args: List[String]): IO[ExitCode] = ???

  def myParMapN[A, B, C](ia: IO[A], ib: IO[B])(f: (A, B) => C): IO[C] =
    for {
      fiberA <- ia.start
      fiberB <- ib.start
      a      <- fiberA.join.onError(_ => fiberB.cancel)
      b      <- fiberB.join.onError(_ => fiberA.cancel)
    } yield f(a, b)

}

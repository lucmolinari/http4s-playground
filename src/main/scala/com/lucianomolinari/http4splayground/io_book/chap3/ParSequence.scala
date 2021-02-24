package com.lucianomolinari.http4splayground.io_book.chap3

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object ParSequence extends IOApp {

  val numTasks             = 100
  val tasks: List[IO[Int]] = List.tabulate(numTasks)(task)

  def task(id: Int): IO[Int] = IO(id).debug

  def run(args: List[String]): IO[ExitCode] = {
    tasks.parSequence.debug.as(ExitCode.Success)
  }

}

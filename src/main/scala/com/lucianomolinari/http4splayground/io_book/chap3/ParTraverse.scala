package com.lucianomolinari.http4splayground.io_book.chap3

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object ParTraverse extends IOApp {

  val numTasks         = 100
  val tasks: List[Int] = List.range(0, numTasks)

  def task(id: Int): IO[Int] = IO(id).debug

  def run(args: List[String]): IO[ExitCode] = {
    tasks.parTraverse(task).debug.as(ExitCode.Success)
  }

}

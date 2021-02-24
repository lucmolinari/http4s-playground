package com.lucianomolinari.http4splayground.io_book.chap5

import cats.effect.{ExitCode, IO, IOApp, _}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

object Parallelism extends IOApp {

  val numCpus               = Runtime.getRuntime().availableProcessors()
  val tasks                 = List.range(0, numCpus * 2).parTraverse(task)
  def task(i: Int): IO[Int] = IO(i).debug

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO(s"number of CPUs: $numCpus").debug
      _ <- tasks.debug
    } yield ExitCode.Success

}

package com.lucianomolinari.http4splayground.io_book.chap4

import cats.effect.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._

import scala.concurrent.duration._

object Timeout extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- task.timeout(200.millis)
      // done <- IO.race(task, timeout)
      // _    <- done match {
      //           case Left(_)  => IO("   task won").debug
      //           case Right(_) => IO("timeout won").debug
      //         }
    } yield ExitCode.Success

  val task    = annotadedSleep("   task", 100.millis)
  val timeout = annotadedSleep("timeout", 500.millis)

  def annotadedSleep(name: String, duration: FiniteDuration): IO[Unit] =
    (
      IO(s"$name: starting").debug *>
        IO.sleep(duration) *>
        IO(s"$name: done").debug
    ).onCancel(IO(s"$name cancelled").debug.void).void

}

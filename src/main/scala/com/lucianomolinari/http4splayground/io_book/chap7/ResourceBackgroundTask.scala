package com.lucianomolinari.http4splayground.io_book.chap7

import cats.effect._
import cats.implicits._
import com.lucianomolinari.http4splayground.io_book.util.debug._
import scala.concurrent.duration._

object ResourceBackgroundTask extends IOApp {

  def run(args: List[String]): IO[ExitCode] = for {
    _ <- backgroundTask.use { _ =>
           IO.sleep(200.millis) *> IO("$s is so cool").debug
         }
    _ <- IO("done!").debug
  } yield ExitCode.Success

  val backgroundTask: Resource[IO, Unit] = {
    val loop = (IO("looping..").debug *> IO.sleep(100.millis)).foreverM

    Resource
      .make(IO("> forking backgroundTask").debug *> loop.start)(
        IO("< canceling backgroundTask").debug.void *> _.cancel
      )
      .void
  }

}

package com.lucianomolinari.http4splayground.tutorialservice

import scala.concurrent.ExecutionContext.global

import cats.effect._
import cats.implicits._
import org.http4s.EntityEncoder
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import io.circe.generic.auto._
import org.http4s.circe._
import io.circe.Decoder
import io.circe.generic.semiauto._
import io.circe.Encoder
// import org.http4s.circe.CirceEntityEncoder._

object Main1 extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name.")
  }

  case class Tweet(id: Int, message: String)

  def getTweet(tweetId: Int): IO[Tweet] = IO(Tweet(tweetId, "message 10"))

  def getPopularTweets(): IO[Seq[Tweet]] = IO(
    Seq(
      Tweet(100, "popular 1"),
      Tweet(200, "popular 2")
    )
  )

  object Tweet {
    implicit val tweetDecoder: Encoder[Tweet] = deriveEncoder[Tweet]
    implicit def tweetEntityEncoder: EntityEncoder[IO, Tweet] = jsonEncoderOf
    implicit def tweetSeqEntityEncoder: EntityEncoder[IO, Seq[Tweet]] = jsonEncoderOf
  }

  // implicit val tweetEncoder: EntityEncoder[IO, Tweet]       = ???
  // implicit val tweetsEncoder: EntityEncoder[IO, Seq[Tweet]] = ???

  val tweetService = HttpRoutes.of[IO] {
    case GET -> Root / "tweets" / IntVar(tweetId) =>
      getTweet(tweetId).flatMap(Ok(_))
    case GET -> Root / "tweets" / "popular"       => getPopularTweets().flatMap(Ok(_))
  }

  def run(args: List[String]): IO[ExitCode] = {
    val services = helloWorldService <+> tweetService

    val httpApp =
      Router("/" -> helloWorldService, "/api" -> services).orNotFound

    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}

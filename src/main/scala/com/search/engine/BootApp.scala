package com.search.engine

import akka.Done
import akka.http.scaladsl.settings.ServerSettings
import com.search.engine.config._
import com.search.engine.server.{DistinguishedServer, ShardServer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.io.StdIn


object BootApp {

  def main(args: Array[String]): Unit = {

    implicit val config: Config = ConfigFactory.load

    val shutdownSignal = waitForShutdownSignal()

    Future {
      new DistinguishedServer(shardUris, shutdownSignal)
        .startServer(
          distinguishedUri.authority.host.toString(),
          distinguishedUri.authority.port,
          ServerSettings(config))
    }

    shardUris foreach {
      shard =>
        Future {
          new ShardServer(shutdownSignal)
            .startServer(
              shard.authority.host.toString(),
              shard.authority.port,
              ServerSettings(config))
        }
    }

    Await.ready(shutdownSignal, Duration.Inf)
  }

  protected def waitForShutdownSignal(): Future[Done] = {
    val promise = Promise[Done]()
    sys.addShutdownHook {
      promise.trySuccess(Done)
    }
    Future {
      blocking {
        if (StdIn.readLine("Press RETURN to stop...\n") != null)
          promise.trySuccess(Done)
      }
    }
    promise.future
  }
}

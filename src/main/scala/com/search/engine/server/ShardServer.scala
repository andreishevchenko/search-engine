package com.search.engine.server

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.{Done, pattern}
import com.search.engine.nodes.{ShardNodeController, ShardNodeControllerComponent}
import com.search.engine.service.ShardApi
import com.search.engine.store.IndexStoreInMemory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ShardServer(shutdownSignal: Future[Done]) extends HttpApp with ShardApi with ShardNodeControllerComponent {

  private[this] lazy val indexStore = new IndexStoreInMemory

  protected[this] lazy val shardNodeController: ShardNodeController = new ShardNodeController(indexStore)

  override protected def routes: Route = shardRoute

  override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] = shutdownSignal
}
